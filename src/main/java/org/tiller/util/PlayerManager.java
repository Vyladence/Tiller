package org.tiller.util;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.tiller.Tiller;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerManager {
    Tiller plugin;
    private final HashMap<UUID, PlayerData> loadedPlayers = new HashMap<>();

    public PlayerManager(Tiller plugin) {
        this.plugin = plugin;
    }

    public PlayerData getPlayerData(UUID uuid) {
        return loadedPlayers.computeIfAbsent(uuid, id -> new PlayerData());
    }

    public boolean togglePlayerState(Player player, String path) {
        UUID playerId = player.getUniqueId();

        // Resolve current state: from memory if present, otherwise from config default
        boolean currentState = getPlayerState(playerId, path);

        // Flip it
        boolean newState = !currentState;

        // Persist
        writePlayerState(playerId, path, newState);

        return newState;
    }

    public void setPlayerState (Player player, String path, boolean state) {
        UUID playerId = player.getUniqueId();
        writePlayerState(playerId, path, state);
    }

    public void unsetPlayerState (Player player, String path) {
        UUID playerId = player.getUniqueId();
        writePlayerState(playerId, path);
    }

    // Remove/unset a state for a player
    private void writePlayerState(UUID playerId, String path) {
        File dataFile = new File(plugin.getDataFolder(), "data.yml");
        YamlConfiguration dataConfig = YamlConfiguration.loadConfiguration(dataFile);

        PlayerData data = loadedPlayers.get(playerId);
        if (data != null) {
            // Remove the state from memory
            Map<String, Boolean> states = new HashMap<>(data.getAllStates());
            states.remove(path);

            // If no states remain, remove the whole player
            if (states.isEmpty()) {
                loadedPlayers.remove(playerId);
                dataConfig.set(playerId.toString(), null);
            } else {
                // Otherwise, update the PlayerData
                data.setState(path, false); // or remove from PlayerData if you add a removeState() method
                dataConfig.set(playerId.toString() + "." + path, null);
            }
        }

        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Set a state for a player
    private void writePlayerState(UUID playerId, String path, boolean isEnabled) {
        File dataFile = new File(plugin.getDataFolder(), "data.yml");
        YamlConfiguration dataConfig = YamlConfiguration.loadConfiguration(dataFile);

        PlayerData data = loadedPlayers.computeIfAbsent(playerId, id -> new PlayerData());
        data.setState(path, isEnabled);

        dataConfig.set(playerId.toString() + "." + path, isEnabled);

        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void loadPlayerStates() {
        File dataFile = new File(plugin.getDataFolder(), "data.yml");
        if (!dataFile.exists()) return;

        YamlConfiguration dataConfig = YamlConfiguration.loadConfiguration(dataFile);

        for (String playerId : dataConfig.getKeys(false)) {
            UUID uuid = UUID.fromString(playerId);
            ConfigurationSection section = dataConfig.getConfigurationSection(playerId);
            if (section == null) continue;

            PlayerData data = new PlayerData();
            for (String key : section.getKeys(false)) {
                data.setState(key, section.getBoolean(key));
            }
            loadedPlayers.put(uuid, data);
        }
    }


    public void writePlayerStates() {
        File dataFile = new File(plugin.getDataFolder(), "data.yml");
        YamlConfiguration dataConfig = new YamlConfiguration();

        for (Map.Entry<UUID, PlayerData> entry : loadedPlayers.entrySet()) {
            String basePath = entry.getKey().toString();
            for (Map.Entry<String, Boolean> state : entry.getValue().getAllStates().entrySet()) {
                dataConfig.set(basePath + "." + state.getKey(), state.getValue());
            }
        }

        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean getPlayerState(UUID playerId, String path) {
        if (loadedPlayers.containsKey(playerId)) {
            PlayerData state = loadedPlayers.get(playerId);
            return state.getState(path, plugin.getConfigManager().get("default-enabled", Boolean.class));
        }

        return plugin.getConfigManager().get("default-enabled", Boolean.class);
    }
}
