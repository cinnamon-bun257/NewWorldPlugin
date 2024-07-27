package com.simonsplugin.plugin;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.popcraft.chunky.api.ChunkyAPI;

import java.io.File;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class NewworldCommandExecutor implements CommandExecutor {
    ChunkyAPI chunky = Bukkit.getServer().getServicesManager().load(ChunkyAPI.class);
    private boolean deletion;
    private final JavaPlugin plugin;
    private PluginConfig pluginConfig;
    private LocationUtils locationUtils;
    private  AdvancementUtils advancementUtils;
    private HealthUtils healthUtils;
    private InventoryUtils inventoryUtils;



    public NewworldCommandExecutor(JavaPlugin plugin, PluginConfig pluginConfig, LocationUtils locationUtils, AdvancementUtils advancementUtils, HealthUtils healthUtils, InventoryUtils inventoryUtils) {
        this.plugin = plugin;
        this.pluginConfig = pluginConfig;
        this.advancementUtils = advancementUtils;
        this.healthUtils = healthUtils;
        this.inventoryUtils = inventoryUtils;
        this.locationUtils = locationUtils;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be run by a player.");
            return true;
        }

        Player player = (Player) sender;
        if (!player.hasPermission("newworld.use")) {
            player.sendMessage("You do not have permission to perform this command.");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage("Usage: /newworld <new world name> <delete old world (boolean)>");
            return true;
        }

        String baseWorldName = args[0];

        if (args.length == 1) {
            deletion = true;
        } else {
            if (!args[1].equals("true") && !args[1].equals("false")) {
                player.sendMessage("Usage: /newworld <new world name> <delete old world (boolean)>");
                return true;
            }
            deletion = Boolean.parseBoolean(args[1]);
        }

        World oldWorld = player.getWorld();



        // Create new world synchronously


        WorldCreator creator = new WorldCreator(baseWorldName);
        creator.hardcore(true);
         World newWorld = creator.createWorld();

        // Create the Nether world
        WorldCreator netherCreator = new WorldCreator(baseWorldName + "_nether");
        netherCreator.environment(World.Environment.NETHER);
        World newWorldNether = netherCreator.createWorld();

        // Create the End world
        WorldCreator endCreator = new WorldCreator(baseWorldName + "_the_end");
        endCreator.environment(World.Environment.THE_END);
        World newWorldEnd = endCreator.createWorld();

            for (String rule : oldWorld.getGameRules()) {
                String value = oldWorld.getGameRuleValue(rule);
                newWorld.setGameRuleValue(rule, value);
                newWorldNether.setGameRuleValue(rule, value);
                newWorldEnd.setGameRuleValue(rule, value);


                newWorld.setTime(0);
                newWorld.setHardcore(pluginConfig.isHardcore);
            }


        assert newWorld != null;
        Location location = newWorld.getSpawnLocation();

        chunky.cancelTask(oldWorld.getName());
        chunky.cancelTask(oldWorld.getName() + "_nether");
        chunky.cancelTask(oldWorld.getName() + "_the_end");
        Bukkit.getScheduler().runTaskAsynchronously(plugin, ()->{
            chunky.startTask(baseWorldName, "square", 0, 0, 200, 200, "concentric");
            chunky.onGenerationComplete(event -> {
                plugin.getLogger().info("First Generation Complete for [" + baseWorldName + "]");
                // Save old player data, if deletion is false
                if (!deletion || oldWorld.getName().equals("world")) {
                    Bukkit.getScheduler().runTaskAsynchronously(plugin, ()-> {for (Player p : Bukkit.getServer().getOnlinePlayers()) {
                        locationUtils.saveLocation(plugin, p, false);
                        inventoryUtils.saveInventory(plugin, p);
                        advancementUtils.saveAdvancements(plugin, p);
                        healthUtils.saveHealthData(p);
                    }
                        player.sendMessage("The old world [" + oldWorld.getName() + "] has not been deleted");
                    });

                }
                for (Player p : Bukkit.getServer().getOnlinePlayers()){
                    advancementUtils.clearPlayerAdvancements(p);
                }
                locationUtils.teleportPlayersToNewWorld(plugin, location);
                chunky.startTask(baseWorldName , "square", 0, 0, 1000, 1000, "region");
                chunky.onGenerationComplete(event1 -> {
                    plugin.getLogger().info("Second Generation Complete for [" + baseWorldName + "]");
                    chunky.startTask(baseWorldName + "_nether", "square", 0, 0, 1000, 1000, "region");
                    chunky.onGenerationComplete(event2 -> {
                        plugin.getLogger().info("First Generation Complete for [" + baseWorldName + "_nether]");
                        chunky.startTask(baseWorldName + "_the_end", "square", 0, 0, 500, 500, "region");
                        chunky.onGenerationComplete(event3 -> {
                            plugin.getLogger().info("First Generation Complete for [" + baseWorldName + "_the_end]");
                            chunky.startTask(baseWorldName, "square", 0, 0, 5000, 5000, "region");
                            chunky.onGenerationComplete(event4 -> {
                                chunky.cancelTask(baseWorldName);
                            });
                        });
                    });
                });
            });
        });


        // Unload and delete old world asynchronously if necessary
        if (!oldWorld.getName().equals("world") && deletion) {
            CompletableFuture.runAsync(() -> {
                boolean unloaded = unloadWorld(oldWorld);
                World netherWorld = Bukkit.getWorld(oldWorld.getName() + "_nether");
                World endWorld = Bukkit.getWorld(oldWorld.getName() + "_the_end");
                boolean unloadedNether = netherWorld != null && unloadWorld(netherWorld);
                boolean unloadedEnd = endWorld != null && unloadWorld(endWorld);

                boolean deleted = false;
                boolean deletedNether = false;
                boolean deletedEnd = false;
                if (unloaded) {
                    deleted = deleteWorld(new File(Bukkit.getServer().getWorldContainer(), oldWorld.getName()));
                }
                if (unloadedNether) {
                    deletedNether = deleteWorld(new File(Bukkit.getServer().getWorldContainer(), oldWorld.getName() + "_nether"));
                }
                if (unloadedEnd) {
                    deletedEnd = deleteWorld(new File(Bukkit.getServer().getWorldContainer(), oldWorld.getName() + "_the_end"));
                }

                final boolean finalDeleted = deleted;
                final boolean finalDeletedNether = deletedNether;
                final boolean finalDeletedEnd = deletedEnd;
                Bukkit.getScheduler().runTask(plugin, () -> {
                    if (!finalDeleted) {
                        player.sendMessage("The old world [" + oldWorld.getName() + "] could not be deleted");
                    } else {
                        player.sendMessage("Old world [" + oldWorld.getName() + "] deleted");
                    }
                    if (!finalDeletedNether) {
                        player.sendMessage("The old world [" + oldWorld.getName() + "_nether] could not be deleted");
                    } else {
                        player.sendMessage("Old world [" + oldWorld.getName() + "_nether] deleted");
                    }
                    if (!finalDeletedEnd) {
                        player.sendMessage("The old world [" + oldWorld.getName() + "_the_end] could not be deleted");
                    } else {
                        player.sendMessage("Old world [" + oldWorld.getName() + "_the_end] deleted");
                    }
                });
            });
        }

        return true;
    }

    private boolean unloadWorld(World world) {
        if (world.getPlayers().isEmpty()) {
            world.getEntities().forEach(entity -> {
                if (!(entity instanceof Player)) {
                    entity.remove();
                }
            });
            world.save();

            try {
                // Unload the world synchronously
                return Bukkit.getScheduler().callSyncMethod(plugin, () -> Bukkit.unloadWorld(world, false)).get();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    private boolean deleteWorld(File directory) {
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (files != null) { // Some JVMs return null for empty directories
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteWorld(file);
                    } else {
                        file.delete();
                    }
                }
            }
        }
        return directory.delete();
    }
}
