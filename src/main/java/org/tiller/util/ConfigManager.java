package org.tiller.util;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.stream.Collectors;

public class ConfigManager {

    private final JavaPlugin plugin;
    private FileConfiguration config;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    public void loadConfig() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        this.config = plugin.getConfig();
    }

    // Generic getter with type safety
    @SuppressWarnings("unchecked")
    public <T> T get(String path, Class<T> type) {
        Object value = config.get(path);
        if (value == null) return null;
        if (type.isInstance(value)) {
            return (T) value;
        }
        throw new IllegalArgumentException("Config value at " + path + " is not of type " + type.getSimpleName());
    }

    public List<Material> getAllowedItems() {
        return config.getStringList("allowed-items").stream().map(Material::valueOf).collect(Collectors.toList());
    }
}