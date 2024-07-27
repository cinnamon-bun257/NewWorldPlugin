package com.simonsplugin.plugin;

import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.NamespacedKey;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AdvancementUtils {
    private final Map<Player, YamlConfiguration> playerConfigs = new ConcurrentHashMap<>();

    public void saveAdvancements(JavaPlugin plugin, Player player) {
        YamlConfiguration config = getPlayerConfig(plugin, player);
        String path = Main.getBaseWorldName(player.getWorld().getName());
        Map<String, Boolean> advancements = new ConcurrentHashMap<>();

        for (Iterator<Advancement> it = Bukkit.advancementIterator(); it.hasNext(); ) {
            Advancement advancement = it.next();
            AdvancementProgress progress = player.getAdvancementProgress(advancement);
            advancements.put(advancement.getKey().toString(), progress.isDone());
        }

        config.createSection(path, advancements);
        //config.set(path, advancements);
        savePlayerConfig(plugin, player, config);
    }

    public void loadAdvancements(JavaPlugin plugin, Player player, String worldName) {
        YamlConfiguration config = getPlayerConfig(plugin, player);
        if (config == null) {
            plugin.getLogger().warning("Configuration for player is null.");
            return;
        }

        String path = worldName.trim();
        if (!config.contains(path)) {
            plugin.getLogger().warning("Configuration does not contain path: " + path);
            return; // No advancements for this world
        }

        ConfigurationSection section = config.getConfigurationSection(path);
        if (section == null) {
            plugin.getLogger().warning("No configuration section found for path: " + path);
            plugin.getLogger().info("Available worlds in config: " + config.getKeys(false));
            return;
        }

        Map<String, Object> advancements = section.getValues(false);

        player.getWorld().setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
        try {
            clearPlayerAdvancements(player);

            for (Map.Entry<String, Object> entry : advancements.entrySet()) {
                NamespacedKey key = NamespacedKey.minecraft(entry.getKey());
                boolean isDone = (Boolean) entry.getValue();
                if (isDone && key != null) {
                    awardAdvancement(player, key);
                }
            }
        } finally {
            player.getWorld().setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, true);
        }
    }

    public void clearPlayerAdvancements(Player player) {
        for (Iterator<Advancement> it = Bukkit.advancementIterator(); it.hasNext(); ) {
            Advancement advancement = it.next();
            AdvancementProgress progress = player.getAdvancementProgress(advancement);
            for (String criteria : progress.getAwardedCriteria()) {
                progress.revokeCriteria(criteria);
            }
        }
    }

    private static void awardAdvancement(Player player, NamespacedKey key) {
        Advancement advancement = Bukkit.getAdvancement(key);
        if (advancement != null) {
            AdvancementProgress progress = player.getAdvancementProgress(advancement);
            for (String criteria : progress.getRemainingCriteria()) {
                progress.awardCriteria(criteria);
            }
        }
    }

    private YamlConfiguration getPlayerConfig(JavaPlugin plugin, Player player) {
        return playerConfigs.computeIfAbsent(player, k -> loadConfiguration(plugin, player));
    }

    private YamlConfiguration loadConfiguration(JavaPlugin plugin, Player player) {
        File advancementsFile = new File(plugin.getDataFolder(), "Advancements" + player.getUniqueId().toString() + ".yml");
        return YamlConfiguration.loadConfiguration(advancementsFile);
    }

    private void savePlayerConfig(JavaPlugin plugin, Player player, YamlConfiguration config) {
        try {
            config.save(new File(plugin.getDataFolder(), "Advancements" + player.getUniqueId().toString() + ".yml"));
        } catch (IOException e) {
            plugin.getLogger().warning("Could not save advancements data for player: " + player.getName());
        }
    }
}
