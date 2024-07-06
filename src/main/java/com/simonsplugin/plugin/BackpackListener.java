package com.simonsplugin.plugin;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class BackpackListener implements Listener {

    private final Backpack backpack;

    public BackpackListener(Backpack backpack ) {
        this.backpack = backpack;
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if(backpack.isOpened) {
            for (String worldName : backpack.getWorldInventories()) {
                if (event.getView().getTitle().equals("Backpack (" + worldName + ")")) {
                    backpack.updateInventory(worldName);
                    break;
                }
            }
            backpack.isOpened = false;
        }
    }
}