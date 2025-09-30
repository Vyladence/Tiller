package org.tiller.util;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

public class MessageUtil {

    private final Plugin plugin;
    private final MiniMessage miniMessage;

    public MessageUtil(Plugin plugin) {
        this.plugin = plugin;
        this.miniMessage = MiniMessage.miniMessage();
    }

    /**
     * Parse a MiniMessage string into a Component.
     */
    public Component format(String message) {
        return miniMessage.deserialize(message);
    }

    /**
     * Send a message to a specific player.
     */
    public void send(CommandSender target, String message) {
        Audience user = (Audience) target;
        user.sendMessage(format(message));
    }
}
