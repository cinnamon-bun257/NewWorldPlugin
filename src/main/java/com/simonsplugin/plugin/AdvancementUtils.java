package com.simonsplugin.plugin;

import org.bukkit.GameRule;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;



public class AdvancementUtils {
    public static boolean isRestoring =false;
    public static void saveAdvancements(JavaPlugin plugin, Player player) {
        File advancementsFile = new File(plugin.getDataFolder(), player.getUniqueId().toString() + "_advancements.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(advancementsFile);
        String path = player.getWorld().getName();
        Map<String, Boolean> advancements = new HashMap<>();

        for (Iterator<Advancement> it = Bukkit.advancementIterator(); it.hasNext(); ) {
            Advancement advancement = it.next();
            if (player.getAdvancementProgress(advancement).isDone()) {
                advancements.put(advancement.getKey().toString(), true);
            }

            config.set(path, advancements);

            try {
                config.save(advancementsFile);
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }

    public static void loadAdvancements(JavaPlugin plugin, Player player, String worldName) {
        File advancementsFile = new File(plugin.getDataFolder(), player.getUniqueId().toString() + "_advancements.yml");
        if (!advancementsFile.exists()) {
            return; // No saved advancements to restore
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(advancementsFile);
        String path = worldName;

        if (!config.contains(path)) {
            plugin.getLogger().warning("AdvancementFile does not contain the path");
            return; // No advancements for this world
        }

        ConfigurationSection advancementsSection = config.getConfigurationSection(path);
        if (advancementsSection == null) {
            plugin.getLogger().warning("AdvancementFile with path does not contain any information");
            return; // No advancements section found
        }

        clearPlayerAdvancements(player);
        isRestoring = true;
        player.getWorld().setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false); // Disable advancement announcements
        for (String advancementKey : advancementsSection.getKeys(false)) {
            boolean isDone = advancementsSection.getBoolean(advancementKey);
            if (isDone) {
                try {
                    //plugin.getLogger().info("Processing advancement key: " + advancementKey); // Debugging log
                    NamespacedKey key = parseKey(advancementKey);
                    if (key != null) {
                        Advancement advancement = Bukkit.getAdvancement(key);
                        if (advancement != null) {
                            AdvancementProgress progress = player.getAdvancementProgress(advancement);
                            for (String criteria : progress.getRemainingCriteria()) {
                                progress.awardCriteria(criteria);
                            }
                        } else {
                            plugin.getLogger().warning("Advancement " + advancementKey + " not found for player " + player.getName());
                        }
                    } else {
                        plugin.getLogger().warning("Invalid NamespacedKey format: " + advancementKey);
                    }
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().severe("Invalid advancement key: " + advancementKey + " for player " + player.getName());
                    e.printStackTrace();
                }
            }
            isRestoring = false;
            player.getWorld().setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, true); // Re-enable advancement announcements
        }

    }
    private static NamespacedKey parseKey(String key) {
        try {
            // Split the key and ensure it's a valid NamespacedKey
            String[] parts = key.split(":");
            if (parts.length != 2) return null;
            return new NamespacedKey(parts[0], parts[1]);
        } catch (IllegalArgumentException e) {
            return null; // Return null if key format is invalid
        }
    }


    public static void clearPlayerAdvancements(Player player) {
        for (Iterator<Advancement> it = Bukkit.advancementIterator(); it.hasNext(); ) {
            Advancement advancement = it.next();
            AdvancementProgress progress = player.getAdvancementProgress(advancement);
            for (String criteria : progress.getAwardedCriteria()) {
                progress.revokeCriteria(criteria);
            }
        }
    }
}
