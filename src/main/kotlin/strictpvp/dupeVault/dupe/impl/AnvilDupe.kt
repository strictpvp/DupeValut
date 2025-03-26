package strictpvp.dupeVault.dupe.impl

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.HumanEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.ItemStack
import strictpvp.dupeVault.DupeVault
import strictpvp.dupeVault.DupeVault.Companion.configManager
import strictpvp.dupeVault.Utils
import strictpvp.dupeVault.dupe.Dupe

class AnvilDupe : Dupe("Anvil-Dupe") {
    @EventHandler(priority = EventPriority.HIGH)
    fun onUseAnvil(e: InventoryClickEvent) {
        if (!state) return

        if (e.clickedInventory == null) return
        if (e.clickedInventory?.type != InventoryType.ANVIL) return
        if (e.currentItem == null) return
        if (e.rawSlot != 2) return

        if (e.whoClicked.inventory.firstEmpty() != -1 && configManager.getValueAsBool("$name.invShouldFull")) return

        val anvil = e.clickedInventory!!.location!!
        val player = e.whoClicked
        val item = e.currentItem!!.clone()

        if (DupeVault.isFoliaServer) {
            Bukkit.getRegionScheduler().runDelayed(DupeVault.plugin, player.location, {
                drop(anvil, player, item)
            }, 1L)
        } else {
            Bukkit.getScheduler().scheduleSyncDelayedTask(DupeVault.plugin, {
                drop(anvil, player, item)
            }, 1L)
        }
    }

    fun drop(anvil: Location, player: HumanEntity, item: ItemStack) {
        val type = anvil.block.type
        if ((type != Material.ANVIL && type != Material.CHIPPED_ANVIL && type != Material.DAMAGED_ANVIL) && Utils.getRandom(chance)) {
            for (i in 2..multiple) {
                val drop = player.world.dropItem(player.location, item)
                drop.velocity = player.eyeLocation.direction
            }
        }
    }
}