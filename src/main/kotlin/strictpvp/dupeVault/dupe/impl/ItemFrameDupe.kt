package strictpvp.dupeVault.dupe.impl

import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.entity.ItemFrame
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityDamageEvent
import strictpvp.dupeVault.Utils
import strictpvp.dupeVault.dupe.Dupe

class ItemFrameDupe : Dupe("ItemFrame-Dupe") {
    @EventHandler(priority = EventPriority.HIGH)
    fun onAttack(e: EntityDamageEvent) {
        if (!state) return

        if (e.entity.type != EntityType.ITEM_FRAME && e.entity.type != EntityType.GLOW_ITEM_FRAME) return // check damaged entity
        val frame = e.entity as ItemFrame // item frame
        if (frame.item.type == Material.AIR) return // return if item was air
        if (!Utils.getRandom(chance)) return // chance

        for (i in 2..multiple) { // ex. (2..2 << once) (2..3 << run 2 times)
            e.entity.world.dropItemNaturally(e.entity.location, frame.item.clone()) // clone
        }
    }
}