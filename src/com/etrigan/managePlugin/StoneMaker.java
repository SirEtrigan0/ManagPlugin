package com.etrigan.managePlugin;
import java.util.Arrays;
import org.bukkit.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.ChatColor;

public class StoneMaker implements Listener{
	private final ManagePlugin plugin;
	private final NamespacedKey recipeKey;
	
	public StoneMaker(ManagePlugin plugin) {
		this.plugin = plugin;
		this.recipeKey = new NamespacedKey(plugin, "stone_maker");
		
		setupDefaultConfig();
		createStoneMakerRecipe();
		
	}
	
	private void setupDefaultConfig() {
		FileConfiguration config = plugin.getConfig();
		
		config.addDefault("StoneMaker.name", "§6Stoniarka");
		config.addDefault("StoneMaker.lore1", "$7Postaw ten blok");
		config.addDefault("StoneMaker.lore2", "§7zeby generowac stone");
		config.addDefault("StoneMaker.amount", 1);
		config.addDefault("StoneMaker.regeneration-time", 20);
		
		//Crafting
		
	    config.addDefault("StoneMaker.crafting.TopLeft", "REDSTONE");
	    config.addDefault("StoneMaker.crafting.TopMiddle", "IRON_INGOT");
	    config.addDefault("StoneMaker.crafting.TopRight", "REDSTONE");
	    config.addDefault("StoneMaker.crafting.MiddleLeft", "IRON_INGOT");
	    config.addDefault("StoneMaker.crafting.Middle", "PISTON");  // Changed to normal piston
	    config.addDefault("StoneMaker.crafting.MiddleRight", "IRON_INGOT");
	    config.addDefault("StoneMaker.crafting.BottomLeft", "REDSTONE");
	    config.addDefault("StoneMaker.crafting.BottomMiddle", "PISTON");  // Changed to normal piston
	    config.addDefault("StoneMaker.crafting.BottomRight", "REDSTONE");
        
        config.options().copyDefaults(true);
        plugin.saveConfig();
	}
	
	public ItemStack createStoneMakerItem() {
		FileConfiguration config = plugin.getConfig();
		ItemStack stoneMaker = new ItemStack(Material.END_STONE,
				config.getInt("StoneMaker.amount"));
		
		ItemMeta meta = stoneMaker.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', 
                config.getString("StoneMaker.name")));
            
            meta.setLore(Arrays.asList(
                ChatColor.translateAlternateColorCodes('&', config.getString("StoneMaker.lore1")),
                ChatColor.translateAlternateColorCodes('&', config.getString("StoneMaker.lore2"))
            ));
            stoneMaker.setItemMeta(meta);
            return stoneMaker;
	}
	
	private void createStoneMakerRecipe() {
	    ItemStack stoneMaker = createStoneMakerItem();
	    ShapedRecipe recipe = new ShapedRecipe(recipeKey, stoneMaker);

	    // Make sure to set the shape before ingredients
	    recipe.shape("ABC", "DEF", "GHI");
	    
	    // Set ingredients with normal pistons
	    try {
	        recipe.setIngredient('A', Material.REDSTONE);
	        recipe.setIngredient('B', Material.IRON_INGOT);
	        recipe.setIngredient('C', Material.REDSTONE);
	        recipe.setIngredient('D', Material.IRON_INGOT);
	        recipe.setIngredient('E', Material.PISTON);
	        recipe.setIngredient('F', Material.IRON_INGOT);
	        recipe.setIngredient('G', Material.REDSTONE);
	        recipe.setIngredient('H', Material.PISTON);
	        recipe.setIngredient('I', Material.REDSTONE);

	        plugin.getServer().addRecipe(recipe);
	        plugin.getLogger().info("Successfully registered Stone Maker recipe!");
	    } catch (Exception e) {
	        plugin.getLogger().warning("Failed to register Stone Maker recipe: " + e.getMessage());
	    }
	}

	    public static boolean isStoneMaker(ItemStack item) {
	        if (item == null || !item.hasItemMeta()) return false;
	        ItemMeta meta = item.getItemMeta();
	        return meta.hasDisplayName() && 
	               ChatColor.stripColor(meta.getDisplayName()).equals(
	                   ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&',
	                   ManagePlugin.getPlugin(ManagePlugin.class).getConfig().getString("StoneMaker.name")))
	               );
	    }
	
	
	
}