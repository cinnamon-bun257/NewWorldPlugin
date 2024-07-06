package com.simonsplugin.plugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class CustomPortalListener implements Listener {

    private final JavaPlugin plugin;

    public CustomPortalListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerPortals (PlayerPortalEvent event) {
        plugin.getLogger().warning("onPlayerPortals used");
        Player player = event.getPlayer();
        Location fromLocation = event.getFrom();
        World fromWorld = fromLocation.getWorld();

        if (fromWorld == null) return;

        String baseWorldName = fromWorld.getName().replace("_nether", "").replace("_the_end", "");
        plugin.getLogger().warning("baseWorldName: " + baseWorldName);

        if (fromWorld.getEnvironment() == World.Environment.NORMAL) {
            // Going to the Nether or End
            if (event.getCause() == PlayerTeleportEvent.TeleportCause.NETHER_PORTAL) {
                World targetNether = Bukkit.getWorld(baseWorldName + "_nether");
                if (targetNether == null) {
                    targetNether = new WorldCreator(baseWorldName + "_nether").environment(World.Environment.NETHER).createWorld();
                }
                if (targetNether != null) {
                    Location targetLocation = findSafeLocation(targetNether, event.getTo(), player);
                    event.setTo(new Location(targetNether, fromLocation.getX()/8, 75, fromLocation.getZ()/8));
                } else {
                    plugin.getLogger().warning("Nether portal could not be crossed correctly for world: " + baseWorldName);
                }
            } else if (event.getCause() == PlayerTeleportEvent.TeleportCause.END_PORTAL) {
                World targetEnd = Bukkit.getWorld(baseWorldName + "_the_end");
                if (targetEnd == null) {
                    targetEnd = new WorldCreator(baseWorldName + "_the_end").environment(World.Environment.THE_END).createWorld();
                }
                if (targetEnd != null) {
                    Location targetLocation = new Location(targetEnd, event.getTo().getX(), event.getTo().getY(), event.getTo().getZ());
                    event.setTo(new Location(targetEnd, 100, 50, 0));
                } else {
                    plugin.getLogger().warning("End portal could not be crossed correctly for world: " + baseWorldName);
                }
            }
        } else if (fromWorld.getEnvironment() == World.Environment.NETHER) {
            // Returning from the Nether to the Overworld
            World targetOverworld = Bukkit.getWorld(baseWorldName);
            if (targetOverworld == null) {
                targetOverworld = new WorldCreator(baseWorldName).createWorld();
            }
            if (targetOverworld != null) {
                Location targetLocation = findSafeLocation(targetOverworld, fromLocation, player);
                event.setTo(new Location(targetOverworld, fromLocation.getX()*8, 70, fromLocation.getZ()*8));
            } else {
                plugin.getLogger().warning("Returning portal could not be crossed correctly for world: " + baseWorldName);
            }
        } else if (fromWorld.getEnvironment() == World.Environment.THE_END) {
            // Returning from the End to the Overworld
            World targetOverworld = Bukkit.getWorld(baseWorldName);
            if (targetOverworld == null) {
                targetOverworld = new WorldCreator(baseWorldName).createWorld();
            }
            if (targetOverworld != null) {
                Location targetLocation = player.getRespawnLocation();
                if(targetLocation == null) targetLocation= targetOverworld.getSpawnLocation();

                event.setTo(targetLocation);
            } else {
                plugin.getLogger().warning("Returning portal could not be crossed correctly for world: " + baseWorldName);
            }

        }
    }

    private Location findSafeLocation(World world, Location location, Player p) {
        if(location.getWorld().getEnvironment() == World.Environment.NORMAL && world.getEnvironment() == World.Environment.NETHER){
            return new Location(world, location.getX()/8, 60 ,location.getY()/8);
        }
        else if(location.getWorld().getEnvironment() == World.Environment.NORMAL && world.getEnvironment() == World.Environment.THE_END){
            return new Location(world, world.getSpawnLocation().getX(), world.getSpawnLocation().getY(), world.getSpawnLocation().getZ());
        }
        else if(location.getWorld().getEnvironment() == World.Environment.NETHER){
            return new Location(world, location.getX()*8, 70, location.getZ()*8);
        }
        else if (location.getWorld().getEnvironment() == World.Environment.THE_END){
            return new Location(world, p.getRespawnLocation().getX(), p.getRespawnLocation().getY(), p.getRespawnLocation().getZ());
        }

        return new Location(world, location.getX(), location.getY(), location.getZ());
    }
}