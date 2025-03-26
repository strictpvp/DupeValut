package strictpvp.dupeVault.command

import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import strictpvp.dupeVault.DupeVault.Companion.configManager

class ReloadConfig : CommandExecutor {
    override fun onCommand(
        sender: CommandSender,
        command: Command,
        str: String,
        args: Array<out String>?
    ): Boolean {
        configManager.load()
        sender.sendMessage("${ChatColor.RED}[ReviveDupes]${ChatColor.RESET} Config Reloaded!")
        return true
    }
}