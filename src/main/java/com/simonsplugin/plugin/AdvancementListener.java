package com.simonsplugin.plugin;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class AdvancementListener implements Listener {

    private final JavaPlugin plugin;

    public AdvancementListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerAdvancementDone(PlayerAdvancementDoneEvent event) {
        if (AdvancementUtils.isRestoring) {
            event.getPlayer().sendMessage(""); // Send an empty message to clear the advancement notification
        }
    }
}
