package me.schooltests.stprisons;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public abstract class Module {
    protected STPrisons plugin;
    protected Set<Listener> moduleListeners = new HashSet<>();
    protected Map<String, CommandExecutor> moduleCommands = new HashMap<>();

    public Module(STPrisons plugin) {
        this.plugin = plugin;
    }

    public void enable() {
        moduleListeners.forEach(listener -> Bukkit.getPluginManager().registerEvents(listener, plugin));
        moduleCommands.forEach((name, command) -> Objects.requireNonNull(plugin.getCommand(name)).setExecutor(command));
    }

    public void disable() {
        moduleListeners.forEach(HandlerList::unregisterAll);
        moduleCommands.forEach((name, command) -> Objects.requireNonNull(plugin.getCommand(name)).setExecutor(null));
    }

    public STPrisons getPlugin() {
        return plugin;
    }
}
