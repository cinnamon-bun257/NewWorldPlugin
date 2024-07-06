package com.simonsplugin.plugin;

import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;



public class PlayerJoinHandler implements Listener{
    private JavaPlugin plugin;

    public PlayerJoinHandler(JavaPlugin plugin) {
        this.plugin = plugin;
    }
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
         String baseWorldName = LocationUtils.getLoginWorld(plugin, event.getPlayer());
         LocationUtils.setLocation(plugin, event.getPlayer(), baseWorldName, true );
    }
}


