package org.tiller.commands;

import org.tiller.Tiller;
import org.tiller.commands.subcommands.Reload;
import org.tiller.commands.subcommands.Toggle;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class SubcommandRegistry {
    private final Map<String, TillerSubcommand> subcommands = new HashMap<>();

    public SubcommandRegistry(Tiller plugin) {
        register(new Reload(plugin));
        //register(new Status(plugin));
        //register(new Set(plugin));

        register(new Toggle(plugin));
        //register(new Help(this));
    }

    public void register(TillerSubcommand cmd) {
        subcommands.put(cmd.getName(), cmd);
    }

    public TillerSubcommand get(String subcommand) {
        return subcommands.get(subcommand);
    }

    public Collection<TillerSubcommand> getAll() {
        return subcommands.values();
    }
}
