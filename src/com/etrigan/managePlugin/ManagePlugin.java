package com.etrigan.managePlugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import java.util.HashMap;
import java.util.UUID;

public class ManagePlugin extends JavaPlugin {
	private HashMap<UUID, Location> homes = new HashMap<>();
	private FileConfiguration config;
	private Teleporter teleporter;
	private MakerEvents makerEvents;
	private Home home;
	
	@Override
	public void onEnable() {
		saveDefaultConfig();
		config = getConfig();
		

		new Teleporter(this);
		new StoneMaker(this);
		new ObsidianMaker(this);
		new MakerEvents(this);
		home = new Home(this);
	    getCommand("home").setExecutor(home);
	    getCommand("sethome").setExecutor(home);
		//saveConfig();

	}
	
	@Override
	public void onDisable() {
	    if (makerEvents != null) {
	        makerEvents.saveMakers();
	    }
		saveConfig();
	}
	
	
	
}
