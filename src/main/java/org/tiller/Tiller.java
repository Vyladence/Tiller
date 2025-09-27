package org.autoreplant;

import net.md_5.bungee.api.ChatColor;
import org.autoreplant.commands.AutoReplantCommand;
import org.autoreplant.commands.CommandTabCompleter;
import org.autoreplant.config.ConfigManager;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

public class Tiller extends JavaPlugin implements Listener {

    private final HashMap<UUID, Boolean> loadedPlayers = new HashMap<>();
    private final Logger logger = Logger.getLogger("Tiller");
    public final PluginDescriptionFile description = this.getDescription();

    private ConfigManager configManager;


    @Override
    public void onEnable() {
        configManager = new ConfigManager(this);

        // Register Command
        this.getCommand("tiller").setExecutor(new TillerCommand(this));
        this.getCommand("tiller").setTabCompleter(new CommandTabCompleter());
        // Register Listeners
        getServer().getPluginManager().registerEvents(this, this);

        loadPlayerStates();
        log(String.format("Tiller v%s is now enabled", description.getVersion()));
    }

    @Override
    public void onDisable() { }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        // Check if the event was cancelled by another plugin
        if (event.isCancelled()) {
            return;
        }

        Player player = event.getPlayer();
        Block block = event.getBlock();
        ItemStack tool = player.getInventory().getItemInMainHand();

        // If player does not have permission, cancel auto replanting
        if (!player.hasPermission("tiller.use")) {
            return;
        }

        // Check if block is a plant
        if (!(block.getBlockData() instanceof Ageable cropBlock)) {
            return;
        }

        // If player is not in survival, return
        if (player.getGameMode() != GameMode.SURVIVAL) {
            return;
        }

        // Check if the correct tool is used, or if the player has the ignore tool restrictions permission
        if (!configManager.getAllowedItems().contains(tool.getType()) && !player.hasPermission("tiller.ignore_tool_restrictions")) {
            return;
        }

        // If player has auto-replant explicitly disabled, return
        if (loadedPlayers.containsKey(player.getUniqueId()) && !loadedPlayers.get(player.getUniqueId())) {
            return;
        }

        // If player has auto-replant unset and default is false, return
        if (!loadedPlayers.containsKey(player.getUniqueId()) && !configManager.isDefaultEnabled()) {
            return;
        }

        if (!configManager.isRespectFortune()) {
            // Disable the default drops
            event.setDropItems(false);

            // Drop the default amount of items, ignoring the Fortune enchantment
            for (ItemStack drop : event.getBlock().getDrops(new ItemStack(Material.AIR))) {
                event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), drop);
            }
        }

        boolean cropIsUnripe = cropBlock.getAge() < cropBlock.getMaximumAge();

        // Replant ripe crops or all crops if replanting unripe crops are enabled
        if (!cropIsUnripe || configManager.isReplantUnripeCrops()) {
            // Disable drops when replanting unripe crops
            if (cropIsUnripe) {
                event.setDropItems(false);
            }

            Material blockType = block.getType();

            Runnable replantTask = () -> {
                block.setType(blockType);
                cropBlock.setAge(0);
                block.setBlockData(cropBlock);
            };

            getServer().getScheduler().runTaskLater(this, replantTask, 1);
        }

        boolean shouldApplyItemDamage = !player.hasPermission("tiller.bypass_item_durability") &&
                configManager.doItemDamage() &&
                (!cropIsUnripe || configManager.doUnripeCropDamage());

        // Handle item damage
        if (shouldApplyItemDamage && tool.getItemMeta() instanceof Damageable damageable) {
            damageable.setDamage(damageable.getDamage() + 1);
            tool.setItemMeta(damageable);

            // Break the tool if its durability reaches zero
            if (damageable.getDamage() >= tool.getType().getMaxDurability()) {
                player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0f, 1.0f);
                player.getInventory().setItemInMainHand(null);
            }
        }
    }

    public void configReload() {
        configManager.loadConfig();
    }

    public void togglePlayerState(Player player) {
        UUID playerId = player.getUniqueId();
        boolean newState;

        // Flip current player value
        if (loadedPlayers.containsKey(playerId)) {
            newState = !loadedPlayers.get(playerId);
        } else {
            newState = !configManager.isDefaultEnabled();
        }

        savePlayerState(playerId, newState);

        String statusMessageText = getConfig().getString(newState ? "messages.enabled" : "messages.disabled");
        send(player, statusMessageText);
    }

    public void savePlayerState(UUID playerId, boolean enabled) {
        loadedPlayers.put(playerId, enabled);

        File dataFile = new File(getDataFolder(), "data.yml");
        YamlConfiguration dataConfig = YamlConfiguration.loadConfiguration(dataFile);
        dataConfig.set(playerId.toString() + ".autoReplant", enabled);

        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadPlayerStates() {
        File dataFile = new File(getDataFolder(), "data.yml");

        if (!dataFile.exists()) {
            return;
        }

        YamlConfiguration dataConfig = YamlConfiguration.loadConfiguration(dataFile);

        for (String playerId : dataConfig.getKeys(false)) {
            boolean enabled = dataConfig.getBoolean(playerId + ".autoReplant", configManager.isDefaultEnabled());

            loadedPlayers.put(UUID.fromString(playerId), enabled);
        }
    }

    public void log (String msg, String level) {
        switch (level.toLowerCase()) {
            case "warn":
            case "warning":
                logger.warning(msg);
                return;
            default:
                logger.info(msg);
        }
    }

    public void log (String msg) {
        logger.info(msg);
    }

    public void send(CommandSender target, String message) {
        if (Objects.equals(message, "")) {
            return;
        }

        String coloredMessage = ChatColor.translateAlternateColorCodes('&', message);

        target.sendMessage(coloredMessage);
    }
}