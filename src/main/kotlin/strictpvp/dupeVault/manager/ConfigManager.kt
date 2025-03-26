package strictpvp.dupeVault.manager

import org.bukkit.configuration.file.YamlConfiguration
import strictpvp.dupeVault.DupeVault
import strictpvp.dupeVault.DupeVault.Companion.dupeManager
import java.io.File

class ConfigManager {
    lateinit var config: File
    lateinit var configYaml: YamlConfiguration

    fun init() {
        config = File(DupeVault.plugin.dataFolder.path, "config.yml")

        if (config.exists())
            load()
    }

    fun load() {
        configYaml = YamlConfiguration.loadConfiguration(config)

        for (dupe in dupeManager.dupes) {
            dupe.state = configYaml.getBoolean("${dupe.name}.state")
            dupe.multiple = configYaml.getInt("${dupe.name}.multiple")
            dupe.chance = configYaml.getInt("${dupe.name}.chance")
        }
    }

    fun getValue(path: String): Any? {
        return configYaml.get(path)
    }

    fun getValueAsBool(path: String): Boolean {
        return configYaml.getBoolean(path)
    }
}