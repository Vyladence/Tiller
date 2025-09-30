package org.tiller.commands;

import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;
import org.tiller.Tiller;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class TillerCommand implements CommandExecutor, TabCompleter {

    private final Tiller plugin;
    private final SubcommandRegistry registry;

    public TillerCommand(Tiller plugin) {
        this.plugin = plugin;
        this.registry = new SubcommandRegistry(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            if (!sender.hasPermission("tiller.command")) { return false; }

            plugin.getMessageUtil().send(sender, String.format(plugin.getConfig().getString("messages.version-msg"), plugin.description.getVersion()));
            return true;
        }

        TillerSubcommand sub = registry.get(args[0]);
        if (sub == null) {
            return false;
        }

        if (sub.getPermission() != null && !sender.hasPermission(sub.getPermission())) {
            plugin.getMessageUtil().send(sender, plugin.getConfig().getString("messages.no_permission"));
            return true;
        }

        return sub.execute(sender, Arrays.copyOfRange(args, 1, args.length));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return registry.getAll().stream()
                    .filter(sub -> sub.getPermission() == null || sender.hasPermission(sub.getPermission()))
                    .map(TillerSubcommand::getName)
                    .filter(name -> name.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }

        TillerSubcommand sub = registry.get(args[0]);
        if (sub != null && (sub.getPermission() == null || sender.hasPermission(sub.getPermission()))) {
            return sub.tabComplete(sender, Arrays.copyOfRange(args, 1, args.length));
        }

        return Collections.emptyList();
    }

}
