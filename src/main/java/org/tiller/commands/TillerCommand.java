package org.tiller.commands;

import org.tiller.Tiller;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TillerCommand implements CommandExecutor {

    private final Tiller plugin;

    public TillerCommand(Tiller plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length >= 1) {
            if (args[0].equalsIgnoreCase("reload")) {
                // Reload Command
                if (!sender.hasPermission("tiller.use")) {
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

                if (!player.hasPermission("tiller.use")) {
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
