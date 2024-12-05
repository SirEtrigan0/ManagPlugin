package com.etrigan.managePlugin;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.HashSet;
import java.util.Set;

public class StoneMakerEvents implements Listener {
    private final ManagePlugin plugin;
    private final Set<Block> stoneMakers = new HashSet<>();

    public StoneMakerEvents(ManagePlugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onStoneMakerPlace(BlockPlaceEvent event) {
        ItemStack item = event.getItemInHand();
        if (StoneMaker.isStoneMaker(item)) {
            Block block = event.getBlock();
            stoneMakers.add(block);
            
            // Start stone generation
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!stoneMakers.contains(block) || block.getType() != Material.END_STONE) {
                        this.cancel();
                        return;
                    }

                    Block above = block.getRelative(0, 1, 0);
                    if (above.getType() == Material.AIR) {
                        above.setType(Material.STONE);
                    }
                }
            }.runTaskTimer(plugin, 0L, 
                plugin.getConfig().getLong("StoneMaker.regeneration-time"));
        }
    }

    @EventHandler
    public void onStoneMakerBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (block.getType() == Material.END_STONE && stoneMakers.contains(block)) {
            stoneMakers.remove(block);
            // Drop the Stone Maker item
            ItemStack stoneMaker = new StoneMaker(plugin).createStoneMakerItem();
            event.setDropItems(false);
            block.getWorld().dropItemNaturally(block.getLocation(), stoneMaker);
        }
    }
}