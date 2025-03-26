package strictpvp.dupeVault.manager

import org.bukkit.Bukkit
import strictpvp.dupeVault.DupeVault
import strictpvp.dupeVault.dupe.Dupe
import strictpvp.dupeVault.dupe.impl.*

class DupeManager {
    val dupes: MutableList<Dupe> = mutableListOf()

    fun init() {
        register(AnvilDupe())
        register(AutoCraftDupe())
        register(VehicleQuitDupe())
        register(ItemFrameDupe())
    }

    fun register(dupe: Dupe) {
        dupes.add(dupe)
        Bukkit.getPluginManager().registerEvents(dupe, DupeVault.plugin)
    }
}