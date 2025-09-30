package org.tiller;

import org.tiller.commands.TillerCommand;
import org.tiller.util.BlockHandler;
import org.tiller.util.ConfigManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.tiller.util.MessageUtil;
import org.tiller.util.PlayerManager;

public class Tiller extends JavaPlugin implements Listener {
    public final PluginDescriptionFile description = this.getDescription();

    private ConfigManager configManager;
    private PlayerManager playerManager;
    private BlockHandler blockHandler;
    private MessageUtil messageUtil;

    @Override
    public void onEnable() {
        configManager = new ConfigManager(this);
        playerManager = new PlayerManager(this);
        blockHandler = new BlockHandler(this, isFolia());
        messageUtil = new MessageUtil(this);

        TillerCommand commands = new TillerCommand(this);

        // Register Command
        this.getCommand("tiller").setExecutor(commands);
        this.getCommand("tiller").setTabCompleter(commands);
        // Register Listeners
        getServer().getPluginManager().registerEvents(this, this);

        playerManager.loadPlayerStates();
        this.getLogger().info(String.format("Tiller v%s is now enabled", description.getVersion()));
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public MessageUtil getMessageUtil() {
        return messageUtil;
    }

    public BlockHandler getBlockHandler() {
        return blockHandler;
    }
    public static boolean isFolia() {
        try {
            // Folia-only class
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }



    @Override
    public void onDisable() {
        playerManager.writePlayerStates();
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        blockHandler.handleBlockBreak(event);
    }
}