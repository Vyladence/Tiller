package org.tiller.commands.subcommands;

import org.bukkit.command.CommandSender;
import org.tiller.Tiller;
import org.tiller.commands.TillerSubcommand;

import java.util.List;

public class Reload implements TillerSubcommand {
    private final Tiller plugin;

    public Reload(Tiller plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public String getDescription() {
        return "Reloads the plugin configuration";
    }

    @Override
    public String getPermission() {
        return "tiller.command.reload";
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return List.of();
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission("tiller.command.reload")) {
            plugin.getMessageUtil().send(sender, plugin.getConfig().getString("messages.no_permission"));
            return true;
        }

        plugin.getConfigManager().loadConfig();
        plugin.getMessageUtil().send(sender, plugin.getConfig().getString("messages.reloaded"));
        return true;
    }
}
