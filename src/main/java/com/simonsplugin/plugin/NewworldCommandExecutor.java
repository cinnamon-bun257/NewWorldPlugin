package com.simonsplugin.plugin;
import org.bukkit.command.CommandExecutor;

import org.bukkit.Location;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.Command;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.GameMode;
import java.io.File;
import java.io.IOException;

public class NewworldCommandExecutor implements CommandExecutor {
    boolean deletion;
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

        if (args.length == 1) deletion = true;
        else {
            if(!args[1].equals("true") && !args[1].equals("false")) {
                player.sendMessage("Usage: /newworld <new world name> <delete old world (boolean)>");
                return true;
            }
            if (args[1].equals("false")) deletion = false;
        }
        String worldName = args[0];
        World oldWorld = player.getWorld();


        WorldCreator creator = new WorldCreator(worldName);
        creator.hardcore(true);
        World newWorld = creator.createWorld();
        for (String rule : oldWorld.getGameRules()) {
            String value = oldWorld.getGameRuleValue(rule);
            newWorld.setGameRuleValue(rule, value);
        }
        long currentTime = oldWorld.getTime();
        newWorld.setTime(currentTime);
        
        

        Location spawnLocation = newWorld.getSpawnLocation();
        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            // Reset health and food levels
            p.setHealth(20.0); // 20 health points is equivalent to 10 hearts
            p.setFoodLevel(20); // 20 is the max food level, fully satiated
            p.setLevel(0);
            p.setExp(0.0f);
            // Clear the player's inventory
            PlayerInventory inventory = p.getInventory();
            inventory.clear();

            // Clear any potion effects
            p.getActivePotionEffects().forEach(potionEffect ->
                    p.removePotionEffect(potionEffect.getType())
            );
            p.setGameMode(GameMode.SURVIVAL);
            p.setRespawnLocation(spawnLocation, true);
            p.teleport(spawnLocation);
        }
        if(!oldWorld.getName().equals("world") && deletion){
            boolean unloaded = unloadWorld(oldWorld, player);
            boolean deleted = false;
            if (unloaded) {
                deleted = deleteWorld(new File(Bukkit.getServer().getWorldContainer(), oldWorld.getName()));
            }

            if (!deleted ) player.sendMessage("The old world [" + oldWorld.getName() + "] could not be deleted"); else player.sendMessage("old world [" + oldWorld.getName() + "] deleted");

        }
        if(!deletion)player.sendMessage("The old world [" + oldWorld.getName() + "] has not been deleted");
        return true;
    }
    private boolean unloadWorld(World world, Player player) {
        if (world.getPlayers().isEmpty()) {

            world.getEntities().forEach(entity -> {
                if (!(entity instanceof Player)) {
                    entity.remove();
                }
            });
            world.save();
            // Attempt to unload the world
            boolean unload = Bukkit.unloadWorld(world, false);
            if (unload) {
                deleteWorld(new File(Bukkit.getServer().getWorldContainer(), world.getName()));
            }
            else return false;
        }
        return true;
    }
        private boolean deleteWorld (File directory){
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
            directory.delete();
            return true;
        }

        }

