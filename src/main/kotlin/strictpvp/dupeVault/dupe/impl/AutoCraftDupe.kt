package strictpvp.dupeVault.dupe.impl

import com.destroystokyo.paper.event.server.ServerTickEndEvent
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.inventory.PrepareItemCraftEvent
import strictpvp.dupeVault.DupeVault.Companion.configManager
import strictpvp.dupeVault.Utils
import strictpvp.dupeVault.dupe.Dupe
import java.time.Duration
import java.time.LocalDateTime

class AutoCraftDupe : Dupe("AutoCraft-Dupe") {
    val dupeTask: MutableMap<Player, LocalDateTime> = mutableMapOf()

    @EventHandler
    fun onTick(e: ServerTickEndEvent) {
        if (!state) return

        // remove dupe tasks if it over 1 seconds
        for (task in dupeTask) {
            val timeDifference = Duration.between(task.value, LocalDateTime.now())
            if (timeDifference > Duration.ofSeconds(1)) {
                dupeTask.remove(task.key)
            }
        }
    }

    @EventHandler
    fun onCraft(e: PrepareItemCraftEvent) {
        if (!state) return

        dupeTask.put(e.view.player as Player, LocalDateTime.now())
    }

    @EventHandler
    fun onPickup(e: EntityPickupItemEvent) {
        if (!state) return

        if (e.entity !is Player) return // entity is player?

        val player = e.entity as Player

        if (e.item.itemStack.maxStackSize == 1 && configManager.getValueAsBool("$name.onlyStackable")) return // is stackable?
        if (!dupeTask.contains(player)) return // is player auto crafted?
        if (player.inventory.firstEmpty() == -1) return // get empty slot
        if (!Utils.getRandom(chance)) return // chance

        val inventory = player.inventory
        val itemStack = e.item.itemStack

        if (configManager.getValueAsBool("$name.allowOverStack")) {
            itemStack.amount *= multiple

            val maxStack = configManager.getValue("$name.maxStack") as Int

            while (itemStack.amount > maxStack) {
                val excessAmount = itemStack.amount - maxStack
                val splitStack = itemStack.clone().apply { amount = maxStack }

                inventory.setItem(inventory.firstEmpty(), splitStack)
                itemStack.amount = excessAmount
            }

            inventory.setItem(inventory.firstEmpty(), itemStack)
        } else {
            itemStack.amount *= multiple

            while (itemStack.amount > itemStack.maxStackSize) {
                val splitStack = itemStack.clone().apply { amount = itemStack.maxStackSize }
                inventory.setItem(inventory.firstEmpty(), splitStack)
                itemStack.amount -= itemStack.maxStackSize
            }

            inventory.setItem(inventory.firstEmpty(), itemStack)
        }

        dupeTask.remove(player)
    }
}