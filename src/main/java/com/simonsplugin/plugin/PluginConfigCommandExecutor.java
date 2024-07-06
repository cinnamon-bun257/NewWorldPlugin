package com.simonsplugin.plugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;

public class PluginConfigCommandExecutor implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender.hasPermission("sethardcore.use")){
            if (args.length == 0) {
                sender.sendMessage("Usage: /sethardcore <true|false>");
                return true;
            }
            if(args[0].equals("true")) {
                changeHardcore(true, sender);
                return true;
            }
            else if (args[0].equals("false")){
                changeHardcore(false, sender);
                return true;
            }
            else{
                sender.sendMessage("usage: /sethardcore <true|false>");
                return true;
            }

        }
        else{
            sender.sendMessage("you do not have permission to use this Command!");
            return true;
        }
    }
    private void changeHardcore(boolean isHardcore, CommandSender sender){
        // Check if the sender is a player
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be executed by a player.");
            return;
        }

        Player player = (Player) sender;
        // Set the hardcore mode for the world the player is in
        player.getWorld().setHardcore(isHardcore);
        // Confirm the action to the player
        sender.sendMessage("Hardcore mode has been " + (isHardcore ? "enabled" : "disabled") + " in your current world.");
    }
}
