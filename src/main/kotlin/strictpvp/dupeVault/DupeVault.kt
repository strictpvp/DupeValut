package strictpvp.dupeVault

import org.bstats.bukkit.Metrics
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import strictpvp.dupeVault.command.ReloadConfig
import strictpvp.dupeVault.dupe.impl.VehicleQuitDupe
import strictpvp.dupeVault.manager.ConfigManager
import strictpvp.dupeVault.manager.DupeManager

class DupeVault : JavaPlugin() {
    companion object {
        lateinit var plugin: JavaPlugin
        lateinit var metrics: Metrics // bstats

        val dupeManager = DupeManager()
        val configManager = ConfigManager()

        var isFoliaServer = false
    }

    override fun onEnable() {
        logger.info("Enabling...")

        // bstats
        metrics = Metrics(this, 25250)

        plugin = this

        // check server software
        try {
            Class.forName("io.papermc.paper.threadedregions.scheduler.RegionScheduler")
            isFoliaServer = true
        } catch (ignored: ClassNotFoundException) { }

        // init dupe Manager
        dupeManager.init()

        // init configManager
        saveDefaultConfig();
        getConfig().options().copyDefaults(true);
        saveConfig();
        configManager.init()

        // init commands
        getCommand("reloadconfig")!!.setExecutor(ReloadConfig())
    }

    override fun onDisable() {
        logger.info("Disabling...")

        // bstats
        metrics.shutdown()

        // remove duped donkeys
        for (world in Bukkit.getWorlds()) {
            VehicleQuitDupe.removeDupedEntities(world.entities)
        }
    }
}
