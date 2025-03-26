package strictpvp.dupeVault.dupe

import org.bukkit.event.Listener

open class Dupe(val name: String) : Listener {
    var state: Boolean = false
    var multiple: Int = 0
    var chance: Int = 0
}