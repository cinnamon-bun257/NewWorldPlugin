package com.simonsplugin.plugin;


import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin{

    @Override
    public void onEnable(){
        //plugin startup
        getLogger().info("New World Plugin enabled!");
        this.getCommand("newworld").setExecutor(new NewworldCommandExecutor(this));
        this.getCommand("changeworld").setExecutor(new ChangeWorldCommandExecutor(this));
        getServer().getPluginManager().registerEvents(new QuitWorldHandler(this), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinHandler(this), this);
        getServer().getPluginManager().registerEvents(new CustomPortalListener(this), this);
        //getServer().getPluginManager().registerEvents(new AdvancementListener(this), this);

    }

    @Override
    public void onDisable(){
        getLogger().info("New World Plugin disabled");

    }
}