package com.etrigan.managePlugin;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.Arrays;
import org.bukkit.*;

public class ObsidianMaker {
	private final ManagePlugin plugin;
	private final NamespacedKey recipeKey;
	
	public ObsidianMaker(ManagePlugin plugin) {
		this.plugin = plugin;
		this.recipeKey = new NamespacedKey(plugin, "obsidian_maker");
		
		setupDefaultConfig();
		createObsidianMakerRecipe();
	}
	
	private void setupDefaultConfig() {
		FileConfiguration config = plugin.getConfig();
		
        config.addDefault("ObsidianMaker.name", "&5Obsidian Mejker");
        config.addDefault("ObsidianMaker.lore1", Messages.OBS_LORE1.toString());
        config.addDefault("ObsidianMaker.lore2", Messages.OBS_LORE2.toString());
        config.addDefault("ObsidianMaker.amount", 1); //crafting amount
        config.addDefault("ObsidianMaker.regeneration-time", 40); // Slower than stone maker
        
        
        config.options().copyDefaults(true);
        plugin.saveConfig();
	}
	
	public ItemStack createObsidianMakerItem() {
        FileConfiguration config = plugin.getConfig();
        ItemStack obsidianMaker = new ItemStack(Material.CRYING_OBSIDIAN, 
            config.getInt("ObsidianMaker.amount"));
        
        ItemMeta meta = obsidianMaker.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', 
            config.getString("ObsidianMaker.name")));
        
        meta.setLore(Arrays.asList(
            ChatColor.translateAlternateColorCodes('&', config.getString("ObsidianMaker.lore1")),
            ChatColor.translateAlternateColorCodes('&', config.getString("ObsidianMaker.lore2"))
        ));
        
        obsidianMaker.setItemMeta(meta);
        return obsidianMaker;
    }
	 private void createObsidianMakerRecipe() {
	        ItemStack obsidianMaker = createObsidianMakerItem();
	        ShapedRecipe recipe = new ShapedRecipe(recipeKey, obsidianMaker);

	        recipe.shape("ABC", "DEF", "GHI");
	        
	        try {
	            // More expensive recipe with diamond and obsidian
	            recipe.setIngredient('A', Material.OBSIDIAN);
	            recipe.setIngredient('B', Material.DIAMOND);
	            recipe.setIngredient('C', Material.OBSIDIAN);
	            recipe.setIngredient('D', Material.DIAMOND);
	            recipe.setIngredient('E', Material.PISTON);
	            recipe.setIngredient('F', Material.DIAMOND);
	            recipe.setIngredient('G', Material.OBSIDIAN);
	            recipe.setIngredient('H', Material.PISTON);
	            recipe.setIngredient('I', Material.OBSIDIAN);

	            plugin.getServer().addRecipe(recipe);
	            plugin.getLogger().info("Successfully registered Obsidian Maker recipe!");
	        } catch (Exception e) {
	            plugin.getLogger().warning("Failed to register Obsidian Maker recipe: " + e.getMessage());
	        }
	    }

	    public static boolean isObsidianMaker(ItemStack item) {
	        if (item == null || !item.hasItemMeta()) return false;
	        ItemMeta meta = item.getItemMeta();
	        return meta.hasDisplayName() && 
	               ChatColor.stripColor(meta.getDisplayName()).equals(
	                   ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&',
	                   ManagePlugin.getPlugin(ManagePlugin.class).getConfig().getString("ObsidianMaker.name")))
	               );
	    }
	
}
