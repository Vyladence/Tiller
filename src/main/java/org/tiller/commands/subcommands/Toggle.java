package org.tiller.commands.subcommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.tiller.Tiller;
import org.tiller.commands.TillerSubcommand;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Toggle implements TillerSubcommand {
    private final Tiller plugin;

    public Toggle(Tiller plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "toggle";
    }

    @Override
    public String getDescription() {
        return "Toggles auto-replant on or off";
    }

    @Override
    public String getPermission() {
        return "tiller.command.toggle";
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return Stream.of("on", "off", "default")
                    .filter(opt -> opt.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command!");
            return true;
        }

        if (args.length == 0) {
            boolean newState = plugin.getPlayerManager().togglePlayerState((Player) sender, "auto-replant");
            plugin.getMessageUtil().send(sender, plugin.getConfig().getString(newState ? "messages.enabled" : "messages.disabled"));
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "on":
                plugin.getPlayerManager().setPlayerState((Player)sender, "auto-replant", true);
                plugin.getMessageUtil().send(sender, plugin.getConfig().getString("messages.enabled"));
                break;
            case "off":
                plugin.getPlayerManager().setPlayerState((Player)sender, "auto-replant", false);
                plugin.getMessageUtil().send(sender, plugin.getConfig().getString("messages.disabled"));
                break;
            case "default":
                plugin.getPlayerManager().unsetPlayerState((Player)sender, "auto-replant");
                plugin.getMessageUtil().send(sender, plugin.getConfig().getString("messages.unset"));
                break;
            default:
                sender.sendMessage("Usage: /tiller toggle [on|off|default]");
                break;
        }

        return true;
    }

}
