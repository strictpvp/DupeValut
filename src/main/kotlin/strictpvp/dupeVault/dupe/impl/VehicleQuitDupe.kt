package strictpvp.dupeVault.dupe.impl

import org.bukkit.Bukkit
import org.bukkit.entity.*
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.world.ChunkUnloadEvent
import strictpvp.dupeVault.DupeVault
import strictpvp.dupeVault.DupeVault.Companion.configManager
import strictpvp.dupeVault.dupe.Dupe


class VehicleQuitDupe : Dupe("VehicleQuit-Dupe") {
    fun handleDupe(player: Player, vehicle: Entity) {
        when (vehicle.type) {
            EntityType.DONKEY -> {
                if (!configManager.getValueAsBool("$name.donkey")) return
            }
            EntityType.MULE -> {
                if (!configManager.getValueAsBool("$name.mule")) return
            }
            EntityType.LLAMA -> {
                if (!configManager.getValueAsBool("$name.llama")) return
            }
            EntityType.CHEST_BOAT -> {
                if (configManager.getValueAsBool("$name.chestBoat")) {
                    chestDupe(vehicle as ChestBoat)
                    return
                }
            }
            else -> return
        }

        val origin = vehicle as ChestedHorse
        val inventory = origin.inventory
        val viewers = inventory.viewers

        if (viewers.isEmpty()) return
        if (viewers.size == 1 && viewers[0] == player) return

        val duped = player.world.spawnEntity(player.location, vehicle.type) as ChestedHorse
        duped.isSilent = true
        duped.isInvisible = true
        duped.isTamed = true
        duped.isInvulnerable = true
        duped.setGravity(false)

        if (origin is Llama && duped is Llama) {
            duped.strength = origin.strength
        }

        val copyTask = Runnable {
            duped.isCarryingChest = true

            for (i in 0 until inventory.size) {
                val item = inventory.getItem(i)
                if (item != null) {
                    duped.inventory.setItem(i, item)
                }
            }
        }

        val viewersCopy = viewers.toList()
        val openTask = Runnable {
            for (viewer in viewersCopy) {
                if (viewer is Player && viewer.isOnline) {
                    viewer.openInventory(duped.inventory)
                }
            }
        }

        val removeTask = Runnable {
            if (!duped.isDead)
                duped.remove()
        }

        if (DupeVault.isFoliaServer) {
            Bukkit.getRegionScheduler().run(DupeVault.plugin, player.location) { task ->
                copyTask.run()
                openTask.run()
            }

            Bukkit.getRegionScheduler().runDelayed(DupeVault.plugin, player.location, {
                removeTask.run()
            }, 200L)
        } else {
            Bukkit.getScheduler().runTask(DupeVault.plugin, Runnable {
                copyTask.run()
                openTask.run()
            })

            Bukkit.getScheduler().scheduleSyncDelayedTask(DupeVault.plugin, removeTask, 200L)
        }
    }

    fun chestDupe(vehicle: ChestBoat) {
        val chest = Bukkit.createInventory(null, 27)
        for (i in 0..26) {
            chest.setItem(i, vehicle.inventory.getItem(i))
        }

        for (player in vehicle.inventory.viewers) {
            if ((player as Player).isOnline)
                player.openInventory(chest)
        }
    }

    @EventHandler
    fun onLeftServer(e: PlayerQuitEvent) {
        if (!state) return

        val vehicle = e.player.vehicle
        if (vehicle == null) return

        handleDupe(e.player, vehicle)
    }

    @EventHandler
    fun onUnloadChunk(e: ChunkUnloadEvent) {
        removeDupedEntities(e.chunk.entities.toList())
    }

    @EventHandler
    fun onClose(e: InventoryCloseEvent) {
        if (!state) return

        val entity = e.inventory.holder as? ChestedHorse ?: return
        removeDupedEntities(listOf(entity))
    }

    fun handleOpen(event: PlayerInteractEntityEvent, type: String) {
        if (!configManager.getValueAsBool("$name.$type")) return

        val player = event.player
        val entity = event.rightClicked as ChestedHorse

        if (!entity.isTamed || !entity.isCarryingChest || entity.isEmpty) return

        player.openInventory(entity.inventory)
        event.isCancelled = true
    }

    @EventHandler
    fun onPlayerInteractEntity(event: PlayerInteractEntityEvent) {
        if (!state) return
        if (!(configManager.getValueAsBool("$name.forceChestAccess"))) return

        if (!event.player.isSneaking) return

        if (event.rightClicked is ChestedHorse) {
            when (event.rightClicked) {
                is Donkey -> handleOpen(event, "donkey")
                is Mule -> handleOpen(event, "mule")
                is Llama -> handleOpen(event, "llama")
            }
        }
    }

    companion object {
        fun removeDupedEntities(entity: List<Entity>) {
            for (e in entity) {
                if (e is ChestedHorse && e.isTamed && e.isInvulnerable && e.isInvisible && e.isSilent && e.isCarryingChest && !e.isDead) {
                    e.remove()
                }
            }
        }
    }
}
