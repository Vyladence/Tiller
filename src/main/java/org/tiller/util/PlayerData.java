package org.tiller.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class PlayerData {
    private final Map<String, Boolean> states = new HashMap<>();

    // Explicit setter
    public void setState(String key, boolean value) {
        states.put(key, value);
    }

    // Getter with default fallback
    public boolean getState(String key, boolean defaultValue) {
        return states.getOrDefault(key, defaultValue);
    }

    // Check if a state is explicitly set
    public boolean hasState(String key) {
        return states.containsKey(key);
    }

    // Expose all states (for saving)
    public Map<String, Boolean> getAllStates() {
        return Collections.unmodifiableMap(states);
    }
}
