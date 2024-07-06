package com.simonsplugin.plugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BackpackCommandExecutor implements CommandExecutor {
    private final Backpack backpack;

    public BackpackCommandExecutor(Backpack backpack) {
        this.backpack = backpack;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            backpack.openBackpack(player);
            return true;
        }
        sender.sendMessage("This command can only be executed by a player.");
        return false;
    }
}
