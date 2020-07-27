package me.schooltests.stprisons;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Base configuration file for all types of configurations
 *
 * @author Itzdlg
 * @param <T> Plugin
 */
@SuppressWarnings({"unused", "WeakerAccess", "ResultIgnored", "ResultOfMethodCallIgnored", "ConstantConditions"})
public abstract class BaseConfig <T extends JavaPlugin> {
    private T plugin;
    private String fileName;
    private String path;
    private String defaultResourceName;
    private String targetSection = "";
    private final YamlConfiguration defaultConfig = new YamlConfiguration();
    private final YamlConfiguration yamlConfig = new YamlConfiguration();

    public BaseConfig(T plugin) {
        this(plugin, "plugin.yml", "", "plugin.yml");
    }

    public BaseConfig(T plugin, String fileName) {
        this(plugin, fileName, "", fileName);
    }

    public BaseConfig(T plugin, String fileName, String path) {
        this(plugin, fileName, path, fileName);
    }

    public BaseConfig(T plugin, String fileName, String path, String defaultResourceName) {
        this.plugin = plugin;
        this.fileName = fileName.endsWith(".yml") ? fileName : fileName + ".yml";
        this.defaultResourceName = defaultResourceName.endsWith(".yml") ? defaultResourceName : defaultResourceName + ".yml";
        this.path = path;
        loadConfigFile();
    }

    public T getPlugin() {
        return plugin;
    }

    public YamlConfiguration getDefaultConfig() {
        return defaultConfig;
    }

    public YamlConfiguration getYamlConfig() {
        return yamlConfig;
    }

    public final void loadConfigFile() throws ConfigurationException {
        final File file = new File(plugin.getDataFolder().toString() + File.separator + path, fileName);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            plugin.saveResource(defaultResourceName, false);
            if (!path.isEmpty()) file.renameTo(new File(plugin.getDataFolder().toString() + File.separator + path, fileName));
        }

        try {
            yamlConfig.load(file);
            defaultConfig.load(new InputStreamReader(Objects.requireNonNull(plugin.getResource(defaultResourceName.replace("\\", "/")))));
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

        postLoad();
    }

    abstract public void postLoad();

    public void setTargetSection(String configSection) {
        if (configSection != null && !configSection.isEmpty()) {
            targetSection = configSection.endsWith(".") ? configSection : configSection + ".";
        } else {
            targetSection = "";
        }
    }

    public Set<String> getDirectChildren(String node) {
        return yamlConfig.getConfigurationSection(targetSection + node).getKeys(false);
    }

    public String getStringWithArguments(String node, Map<String, String> arguments) {
        String unformatted = String.valueOf(getOrDefault(targetSection + node));
        if (unformatted == null) return "";
        for (String key : arguments.keySet())
            unformatted = unformatted.replaceAll("(?i)\\{" + key + "}", arguments.get(key));

        return unformatted;
    }

    public String getStringWithArguments(String node, String... arguments) {
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < arguments.length; i+=2) {
            if (arguments.length > i + 1)
                map.put(arguments[i], arguments[i + 1]);
        }

        return getStringWithArguments(node, map);
    }

    public Object getOrDefault(String node) {
        return yamlConfig.get(targetSection + node, defaultConfig.get(targetSection + node, null));
    }

    public String getStringOrDefault(String node) {
        return yamlConfig.getString(targetSection + node, defaultConfig.getString(targetSection + node, null));
    }

    public int getIntOrDefault(String node) {
        return yamlConfig.getInt(targetSection + node, defaultConfig.getInt(targetSection + node, 0));
    }

    public double getDoubleOrDefault(String node) {
        return yamlConfig.getDouble(targetSection + node, defaultConfig.getDouble(targetSection + node, 0));
    }

    public boolean getBooleanOrDefault(String node) {
        return yamlConfig.getBoolean(targetSection + node, defaultConfig.getBoolean(targetSection + node, true));
    }

    public List<Integer> getIntegerListOrDefault(String node) {
        List<Integer> result = yamlConfig.getIntegerList(targetSection + node);
        return result.isEmpty() ? defaultConfig.getIntegerList(targetSection + node) : result;
    }

    public List<String> getStringListOrDefault(String node) {
        List<String> result = yamlConfig.getStringList(targetSection + node);
        return result.isEmpty() ? defaultConfig.getStringList(targetSection + node) : result;
    }

    public static class ConfigurationException extends RuntimeException {
        public ConfigurationException(String message) {
            super(message);
        }
    }
}