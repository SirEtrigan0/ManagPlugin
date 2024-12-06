package com.etrigan.managePlugin;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.block.TileState;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.NamespacedKey;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.*;

public class MakerEvents implements Listener {
    private final ManagePlugin plugin;
    private final Set<Location> stoneMakers = new HashSet<>();
    private final Set<Location> obsidianMakers = new HashSet<>();

    public MakerEvents(ManagePlugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        loadMakers();
        startGenerationTask();
    }

    private void loadMakers() {
        // Load stone makers
        ConfigurationSection stoneSection = plugin.getConfig().getConfigurationSection("makers.stone");
        if (stoneSection != null) {
            for (String key : stoneSection.getKeys(false)) {
                String worldName = plugin.getConfig().getString("makers.stone." + key + ".world");
                double x = plugin.getConfig().getDouble("makers.stone." + key + ".x");
                double y = plugin.getConfig().getDouble("makers.stone." + key + ".y");
                double z = plugin.getConfig().getDouble("makers.stone." + key + ".z");
                World world = plugin.getServer().getWorld(worldName);
                if (world != null) {
                    Location loc = new Location(world, x, y, z);
                    if (loc.getBlock().getType() == Material.END_STONE) {
                        stoneMakers.add(loc);
                    }
                }
            }
        }

        // Load obsidian makers
        ConfigurationSection obsidianSection = plugin.getConfig().getConfigurationSection("makers.obsidian");
        if (obsidianSection != null) {
            for (String key : obsidianSection.getKeys(false)) {
                String worldName = plugin.getConfig().getString("makers.obsidian." + key + ".world");
                double x = plugin.getConfig().getDouble("makers.obsidian." + key + ".x");
                double y = plugin.getConfig().getDouble("makers.obsidian." + key + ".y");
                double z = plugin.getConfig().getDouble("makers.obsidian." + key + ".z");
                World world = plugin.getServer().getWorld(worldName);
                if (world != null) {
                    Location loc = new Location(world, x, y, z);
                    if (loc.getBlock().getType() == Material.CRYING_OBSIDIAN) {
                        obsidianMakers.add(loc);
                    }
                }
            }
        }
    }

    public void saveMakers() {
        // Clear old data
        plugin.getConfig().set("makers.stone", null);
        plugin.getConfig().set("makers.obsidian", null);

        // Save stone makers
        int stoneIndex = 0;
        for (Location loc : stoneMakers) {
            String path = "makers.stone." + stoneIndex;
            plugin.getConfig().set(path + ".world", loc.getWorld().getName());
            plugin.getConfig().set(path + ".x", loc.getX());
            plugin.getConfig().set(path + ".y", loc.getY());
            plugin.getConfig().set(path + ".z", loc.getZ());
            stoneIndex++;
        }

        // Save obsidian makers
        int obsidianIndex = 0;
        for (Location loc : obsidianMakers) {
            String path = "makers.obsidian." + obsidianIndex;
            plugin.getConfig().set(path + ".world", loc.getWorld().getName());
            plugin.getConfig().set(path + ".x", loc.getX());
            plugin.getConfig().set(path + ".y", loc.getY());
            plugin.getConfig().set(path + ".z", loc.getZ());
            obsidianIndex++;
        }

        plugin.saveConfig();
    }

    private void startGenerationTask() {
        // Stone generation
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Location loc : new HashSet<>(stoneMakers)) {
                    Block block = loc.getBlock();
                    if (block.getType() != Material.END_STONE) {
                        stoneMakers.remove(loc);
                        saveMakers();
                        continue;
                    }
                    
                    Block above = block.getRelative(0, 1, 0);
                    if (above.getType() == Material.AIR) {
                        above.setType(Material.STONE);
                    }
                }
            }
        }.runTaskTimer(plugin, 20L, 20L);

        // Obsidian generation
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Location loc : new HashSet<>(obsidianMakers)) {
                    Block block = loc.getBlock();
                    if (block.getType() != Material.CRYING_OBSIDIAN) {
                        obsidianMakers.remove(loc);
                        saveMakers();
                        continue;
                    }
                    
                    Block above = block.getRelative(0, 1, 0);
                    if (above.getType() == Material.AIR) {
                        above.setType(Material.OBSIDIAN);
                    }
                }
            }
        }.runTaskTimer(plugin, 20L, 40L);
    }

    @EventHandler
    public void onMakerPlace(BlockPlaceEvent event) {
        ItemStack item = event.getItemInHand();
        Location loc = event.getBlock().getLocation();

        if (StoneMaker.isStoneMaker(item)) {
            stoneMakers.add(loc);
            saveMakers();
           // event.getPlayer().sendMessage("§aStoniarka postawiona! Bedzie ona generowala stona bloczek nad.");
        } else if (ObsidianMaker.isObsidianMaker(item)) {
            obsidianMakers.add(loc);
            saveMakers();
           // event.getPlayer().sendMessage("§5Obsydiarka postawiona! Bedzie ona generowala obsydian bloczek nad.");
        }
    }

    @EventHandler
    public void onMakerBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Location loc = block.getLocation();

        if (block.getType() == Material.END_STONE && stoneMakers.contains(loc)) {
            stoneMakers.remove(loc);
            saveMakers();
            event.setDropItems(false);
            block.getWorld().dropItemNaturally(loc, new StoneMaker(plugin).createStoneMakerItem());
            //event.getPlayer().sendMessage("§aStoneMaker broken!");
        } else if (block.getType() == Material.CRYING_OBSIDIAN && obsidianMakers.contains(loc)) {
            obsidianMakers.remove(loc);
            saveMakers();
            event.setDropItems(false);
            block.getWorld().dropItemNaturally(loc, new ObsidianMaker(plugin).createObsidianMakerItem());
            //event.getPlayer().sendMessage("§5ObsidianMaker broken!");
        }
    }
}
