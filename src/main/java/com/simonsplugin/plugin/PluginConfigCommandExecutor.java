package com.simonsplugin.plugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;

public class PluginConfigCommandExecutor implements CommandExecutor {
    private PluginConfig pluginConfig;

    public PluginConfigCommandExecutor(PluginConfig pluginConfig){
        this.pluginConfig = pluginConfig;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(label.equals("sethardcore")){
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
        } else if (label.equals("setminerandomizer")) {
            if(sender.hasPermission("setminerandomizer.use")){
                if (args.length == 0) {
                    sender.sendMessage("Usage: /setminerandomizer <true|false>");
                    return true;
                }
                if(args[0].equals("true")) {
                    changeDoMineRandomizing(true, sender);
                    return true;
                }
                else if (args[0].equals("false")){
                    changeDoMineRandomizing(false, sender);
                    return true;
                }
                else{
                    sender.sendMessage("usage: /sethardcore <true|false>");
                    return true;
                }

            }

        }
        return true;

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
        pluginConfig.isHardcore = isHardcore;
        // Confirm the action to the player
        sender.sendMessage("Hardcore mode has been " + (isHardcore ? "enabled" : "disabled") + " in your current world.");
    }

    private void changeDoMineRandomizing (boolean bool, CommandSender sender){
        pluginConfig.doMineRandomizing = bool;
        sender.sendMessage("Randomizing has been " + (bool ?  "enabled" : "disabled"));
    }
}
