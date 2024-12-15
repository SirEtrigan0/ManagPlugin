package com.etrigan.managePlugin;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Home implements CommandExecutor{
	private HashMap<UUID, Location> homes = new HashMap<>();
	private FileConfiguration config;
	private final ManagePlugin plugin;
	
	
	public Home (ManagePlugin plugin) {
		this.plugin = plugin;
		this.config = plugin.getConfig();
		if (config.contains("homes")) {
			for (String uuid : config.getConfigurationSection("homes").getKeys(false)) {
				homes.put(UUID.fromString(uuid), (Location) config.get("homes." + uuid));
			}
		}
		for (UUID uuid : homes.keySet()) {
			config.set("homes." + uuid.toString(), homes.get(uuid));
		}
		setupDefaultConfig();
		
	}


	private void setupDefaultConfig() {
		//FileConfiguration config = plugin.getConfig();

        config.options().copyDefaults(true);
        plugin.saveConfig();
	}
	
	
	private boolean hasDiamond(Player player) {
		for (ItemStack item : player.getInventory().getContents()) {
			if (item !=null && item.getType() == Material.DIAMOND) {
				return true;
			}		
		}
		return false;
	}
	
	private void removeDiamond(Player player) {
		ItemStack diamond = new ItemStack(Material.DIAMOND, 1);
		player.getInventory().removeItem(diamond);
	}
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("Only players can use home commands!");
			return true;
		}
		
		Player player = (Player) sender;
		UUID playerUUID = player.getUniqueId();
		
		if (command.getName().equalsIgnoreCase("sethome")) {
			homes.put(playerUUID, player.getLocation());
		    config.set("homes." + playerUUID.toString(), player.getLocation());  
		    plugin.saveConfig();  
			player.sendMessage(Messages.SET_HOME.toString());
			return true;
		}
		
		if (command.getName().equalsIgnoreCase("home")) {
			if (!homes.containsKey(playerUUID)) {
				player.sendMessage(Messages.NO_HOME.toString());
				return true;
			}
			if (!hasDiamond(player)) {
				player.sendMessage(Messages.DIAX_HOME.toString());
				return true;
			}
			
			removeDiamond(player);
			player.teleport(homes.get(playerUUID));
			player.sendMessage(Messages.WELC_HOME.toString());
			return true;
		}
		return false;
		

		
	}
	
}
