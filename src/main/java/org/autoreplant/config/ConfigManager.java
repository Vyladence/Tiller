package org.autoreplant.config;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.stream.Collectors;

public class ConfigManager {

    private final JavaPlugin plugin;
    private FileConfiguration config;

    // Config Boolean Storage
    private boolean defaultEnabled;
    private boolean respectFortune;
    private boolean replantUnripeCrops;
    private boolean enableItemDamage;
    private boolean unripeCropDamage;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    public void loadConfig() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        config = plugin.getConfig();

        // Load config options
        defaultEnabled = config.getBoolean("default-enabled", true);
        respectFortune = config.getBoolean("use-fortune", true);
        replantUnripeCrops = config.getBoolean("replant-unripe-crops", true);
        enableItemDamage = config.getBoolean("item-damage", false);
        unripeCropDamage = config.getBoolean("unripe-crop-item-damage", false);
    }

    public List<Material> getAllowedItems() {
        return config.getStringList("allowed-items").stream().map(Material::valueOf).collect(Collectors.toList());
    }

    public boolean isRespectFortune() {
        return respectFortune;
    }

    public boolean isDefaultEnabled() {
        return defaultEnabled;
    }

    public boolean isReplantUnripeCrops() {
        return replantUnripeCrops;
    }

    public boolean doItemDamage() {
        return enableItemDamage;
    }

    public boolean doUnripeCropDamage() {
        return unripeCropDamage;
    }
}