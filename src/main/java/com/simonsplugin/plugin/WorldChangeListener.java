package com.simonsplugin.plugin;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.GameRule;




public class WorldChangeListener implements Listener {
    @EventHandler
    public void onPlayerChangeWorld (PlayerChangedWorldEvent event) {
        World fromWorld = event.getFrom();
        Player player = event.getPlayer();
        World toWorld = player.getWorld();

        // Check if the worlds are different dimensions of the same world group
        if (!fromWorld.getUID().equals(toWorld.getUID())) {
            // Assuming 'world' is the base name of your world group
            copyGameRules(fromWorld, toWorld);
        }
    }


    public void copyGameRules(World sourceWorld, World targetWorld) {
        for (GameRule<?> rule : GameRule.values()) {
            Object ruleValue = sourceWorld.getGameRuleValue(rule);
            if (ruleValue != null) {
                targetWorld.setGameRule((GameRule<Object>)rule, ruleValue);
            }
        }
    }
}