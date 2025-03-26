package strictpvp.dupeVault

import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import strictpvp.dupeVault.command.ReloadConfig
import strictpvp.dupeVault.dupe.impl.VehicleQuitDupe
import strictpvp.dupeVault.manager.ConfigManager
import strictpvp.dupeVault.manager.DupeManager

class DupeVault : JavaPlugin() {
    companion object {
        lateinit var plugin: JavaPlugin
        val dupeManager = DupeManager()
        val configManager = ConfigManager()

        var isFoliaServer = false
    }

    override fun onEnable() {
        logger.info("Enabling...")

        plugin = this

        try {
            Class.forName("io.papermc.paper.threadedregions.scheduler.RegionScheduler")
            isFoliaServer = true
        } catch (ignored: ClassNotFoundException) { }

        dupeManager.init()

        saveDefaultConfig();
        getConfig().options().copyDefaults(true);
        saveConfig();

        configManager.init()

        getCommand("reloadconfig")!!.setExecutor(ReloadConfig())
    }

    override fun onDisable() {
        logger.info("Disabling...")

        // remove duped donkeys
        for (world in Bukkit.getWorlds()) {
            VehicleQuitDupe.removeDupedEntities(world.entities)
        }
    }
}
