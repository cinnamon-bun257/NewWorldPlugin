package com.simonsplugin.plugin;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class ChangeWorldCommandExecutor implements CommandExecutor {

    private JavaPlugin plugin;

    public ChangeWorldCommandExecutor(JavaPlugin plugin) {
        this.plugin = plugin;
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
            player.sendMessage("Usage: /changeworld <name of the world>");
            return true;
        }

        String worldName = args[0];
        LocationUtils.saveLocation(plugin, player, player.getWorld().getName(), false);
        InventoryUtils.saveInventory(plugin, player);
        LocationUtils.setLocation(plugin, player, worldName);
        InventoryUtils.setInventory(plugin, player, worldName);
        return true;

    }
}
