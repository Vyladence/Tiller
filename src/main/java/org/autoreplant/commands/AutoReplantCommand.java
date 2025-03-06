package org.autoreplant.commands;

import org.autoreplant.AutoReplant;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AutoReplantCommand implements CommandExecutor {

    private final AutoReplant plugin;

    public AutoReplantCommand(AutoReplant plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length >= 1) {
            if (args[0].equalsIgnoreCase("reload")) {
                // Reload Command
                if (!sender.hasPermission("autoreplant.use")) {
                    plugin.send(sender, plugin.getConfig().getString("messages.no_permission"));
                    return true;
                }

                plugin.configReload();
                plugin.send(sender, plugin.getConfig().getString("messages.reloaded"));
                return true;
            } else if (args[0].equalsIgnoreCase("toggle"))  {
                if (!(sender instanceof Player player)) {
                    sender.sendMessage("Only players can use this command!");
                    return true;
                }

                if (!player.hasPermission("autoreplant.use")) {
                    plugin.send(sender, plugin.getConfig().getString("messages.no_permission"));
                    return true;
                }

                plugin.togglePlayerState(player);

                return true;
            }
        }

        // Base command
        plugin.send(sender, String.format(plugin.getConfig().getString("messages.version-msg"), plugin.description.getVersion()));

        return true;
    }
}
