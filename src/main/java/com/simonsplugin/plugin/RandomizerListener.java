package com.simonsplugin.plugin;

import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class RandomizerListener implements Listener {
    private Randomizer randomizer;

    public RandomizerListener(Randomizer randomizer) {
        this.randomizer = randomizer;
    }

    public void onBlockDropItem(BlockDropItemEvent event) {
        List<Item> items = event.getItems();

        Material blockType = event.getBlockState().getType();
        ItemStack randomizedType = randomizer.getRandomizedItem(blockType);

        int totalItems = 0;
        if(items.isEmpty()) totalItems = 1;
        else{
            for (Item item : items) {
                totalItems += item.getItemStack().getAmount();
            }
        }

        // Clear the original drops
        items.clear();

        // Add new item with the original total amount
        randomizedType.setAmount(totalItems);
        event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), randomizedType);
    }
}
