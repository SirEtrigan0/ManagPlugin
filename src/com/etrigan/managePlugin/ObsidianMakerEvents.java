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
import org.bukkit.*;

public class ObsidianMakerEvents implements Listener {
    private final ManagePlugin plugin;
    private final Set<Block> obsidianMakers = new HashSet<>();

    public ObsidianMakerEvents(ManagePlugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onObsidianMakerPlace(BlockPlaceEvent event) {
        ItemStack item = event.getItemInHand();
        if (ObsidianMaker.isObsidianMaker(item)) {
            Block block = event.getBlock();
            obsidianMakers.add(block);
            
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!obsidianMakers.contains(block) || block.getType() != Material.CRYING_OBSIDIAN) {
                        this.cancel();
                        return;
                    }

                    Block above = block.getRelative(0, 1, 0);
                    if (above.getType() == Material.AIR) {
                        above.setType(Material.OBSIDIAN);
                        
                        // Add particle and sound effects for obsidian generation
                        block.getWorld().spawnParticle(Particle.LAVA, 
                            above.getLocation().add(0.5, 0.5, 0.5), 
                            5, 0.2, 0.2, 0.2, 0);
                        block.getWorld().playSound(above.getLocation(), 
                            Sound.BLOCK_LAVA_EXTINGUISH, 0.3f, 0.8f);
                    }
                }
            }.runTaskTimer(plugin, 0L, 
                plugin.getConfig().getLong("ObsidianMaker.regeneration-time"));
        }
    }

    @EventHandler
    public void onObsidianMakerBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (block.getType() == Material.CRYING_OBSIDIAN && obsidianMakers.contains(block)) {
            obsidianMakers.remove(block);
            ItemStack obsidianMaker = new ObsidianMaker(plugin).createObsidianMakerItem();
            event.setDropItems(false);
            block.getWorld().dropItemNaturally(block.getLocation(), obsidianMaker);
            
            // Break effect
            block.getWorld().spawnParticle(Particle.LAVA, 
                block.getLocation().add(0.5, 0.5, 0.5), 
                20, 0.2, 0.2, 0.2, 0);
            block.getWorld().playSound(block.getLocation(), 
                Sound.BLOCK_RESPAWN_ANCHOR_DEPLETE, 1.0f, 1.0f);
        }
    }
}