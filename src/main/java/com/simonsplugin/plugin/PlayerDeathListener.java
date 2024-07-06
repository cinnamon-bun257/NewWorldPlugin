package com.simonsplugin.plugin;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class PlayerDeathListener implements Listener{
    private static JavaPlugin plugin;

    public PlayerDeathListener (JavaPlugin plugin){
        this.plugin = plugin;
    }

    public static void OnPlayerDeath (PlayerDeathEvent event){
        String worldName = event.getEntity().getWorld().getName();
        String worldNameEnd= worldName + "_the_end";
        String worldNameNether = worldName + "_nether";
        Player deadPlayer = event.getEntity();
        String deathMessage = "Player " + deadPlayer.getName() + " died";

        // Set the death message to be displayed on the death screen
        event.setDeathMessage(deathMessage);

        // Broadcast the death message to all players
        Bukkit.broadcastMessage(deathMessage);

        // Kill all players
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player != deadPlayer ) {
                String playerWorldName = player.getWorld().getName();
                if(playerWorldName.equals(worldName) || playerWorldName.equals(worldNameNether) || playerWorldName.equals(worldNameEnd)){
                    player.setHealth(0.0);
                }

            }
        }
    }

}
