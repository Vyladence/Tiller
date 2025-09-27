package org.tiller.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CommandTabCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        ArrayList<String> subcommands = new ArrayList<>();

        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (player.hasPermission("tiller.command.reload")) {
                if ("reload".startsWith(args[0])) {
                    subcommands.add("reload");
                }
            }

            if (player.hasPermission("tiller.use")) {
                if ("toggle".startsWith(args[0])) {
                    subcommands.add("toggle");
                }
            }
        }

        return subcommands;
    }
}
