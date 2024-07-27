package com.simonsplugin.plugin;

import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;



public class PlayerJoinHandler implements Listener{
    private JavaPlugin plugin;
    private LocationUtils locationUtils;

    public PlayerJoinHandler(JavaPlugin plugin, LocationUtils locationUtils) {
        this.plugin = plugin;
        this.locationUtils = locationUtils;
    }
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
         String baseWorldName = locationUtils.getLoginWorld(plugin, event.getPlayer());
         locationUtils.setLocation(plugin, event.getPlayer(), baseWorldName, true );
    }
}


