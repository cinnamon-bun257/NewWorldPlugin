package com.simonsplugin.plugin;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import java.io.File;
import java.io.IOException;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
public class Main extends JavaPlugin{

    @Override
    public void onEnable(){
        //plugin startup
        getLogger().info("New World Plugin enabled!");
        this.getCommand("newworld").setExecutor(new NewworldCommandExecutor(this));
        this.getCommand("changeworld").setExecutor(new ChangeWorldCommandExecutor(this));
        //getServer().getPluginManager().registerEvents(new WorldChangeListener(), this);
        getServer().getPluginManager().registerEvents(new QuitWorldHandler(this), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinHandler(this), this);


    }

    @Override
    public void onDisable(){
        getLogger().info("New World Plugin disabled");

    }
}