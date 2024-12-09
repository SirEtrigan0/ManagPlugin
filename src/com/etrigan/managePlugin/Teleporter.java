package com.etrigan.managePlugin;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Teleporter implements Listener{
	private final ManagePlugin plugin;
	private final HashMap<Location, String> teleporterNames = new HashMap<>();
	private final HashMap<String, List<Location>> linkedTeleporters = new HashMap<>();
	private final HashMap<UUID, Location> namingTeleporter = new HashMap<>();
	private final NamespacedKey teleporterKey;
	private ShapedRecipe teleporterRecipe;
	
	public Teleporter(ManagePlugin plugin) {
		this.plugin = plugin;
		this.teleporterKey = new NamespacedKey(plugin, "teleporter");
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		
		plugin.saveDefaultConfig();
		loadTeleporters();
		createTeleporterRecipe();
		startAmbientEffects();
		
	}
	
	private void loadTeleporters() {
		ConfigurationSection teleportersSection = plugin.getConfig().getConfigurationSection("teleporters");
		if (teleportersSection == null) return;
		
		for (String name : teleportersSection.getKeys(false)) {
			List<String> locationStrings = teleportersSection.getStringList(name);
			List<Location> locations = new ArrayList<>();
			
			for (String locString : locationStrings) {
				String[] parts = locString.split(",");
				World world = Bukkit.getWorld(parts[0]);
				if (world != null) {
					Location loc = new Location(
							world,
							Double.parseDouble(parts[1]),
							Double.parseDouble(parts[2]),
							Double.parseDouble(parts[3])
							);
					locations.add(loc);
					teleporterNames.put(loc, name);
				}
			}
			if (!locations.isEmpty()) {
				linkedTeleporters.put(name, locations);
			}
		}
	}
	
	public void saveTeleporters() {
		
		plugin.getConfig().set("teleporters", null);
		
		for (String name : linkedTeleporters.keySet()) {
			List<String> locationStrings = new ArrayList<>();
			for (Location loc : linkedTeleporters.get(name)) {
				String locString = String.format("%s,%f,%f,%f", loc.getWorld().getName(),
						loc.getX(),
						loc.getY(),
						loc.getZ());
				locationStrings.add(locString);
			}
			plugin.getConfig().set("teleporters." + name, locationStrings);
		}
		plugin.saveConfig();
	}
	
	private void createTeleporterRecipe() {
		ItemStack teleporter = createTeleporterItem();
		
		if (teleporterRecipe != null) {
			Bukkit.removeRecipe(teleporterKey);
		}
		
		String topMaterial = plugin.getConfig().getString("recipe.top", "GOLD_INGOT");
		String middleMaterial = plugin.getConfig().getString("recipe.middle", "DIAMOND_BLOCK");
		String centerMaterial = plugin.getConfig().getString("recipe.center","ENDER_PEARL");
		
		teleporterRecipe = new ShapedRecipe(teleporterKey, teleporter);
		teleporterRecipe.shape("MMM", "MCM", "TMT");
		teleporterRecipe.setIngredient('T', Material.valueOf(topMaterial));
		teleporterRecipe.setIngredient('M', Material.valueOf(middleMaterial));
		teleporterRecipe.setIngredient('C', Material.valueOf(centerMaterial));
		
		Bukkit.addRecipe(teleporterRecipe);
	}
	
	private void startAmbientEffects() {
		Bukkit.getScheduler().runTaskTimer(plugin, () -> {
			for (List<Location> locations : linkedTeleporters.values()) {
				for (Location loc : locations) {
					if (loc.getChunk().isLoaded()) {
						World world = loc.getWorld();
						Location particleLoc = loc.clone().add(0.5, 1.0, 0.5);
						
						world.spawnParticle(Particle.PORTAL, particleLoc, 5, 0.2, 0.2, 0.2, 0);
						world.spawnParticle(Particle.PORTAL, particleLoc, 1, 0.2, 0.2, 0.2, 0);
						if (Math.random() < 0.1) {
							world.playSound(loc, Sound.BLOCK_PORTAL_AMBIENT, 0.5f, 1.5f);
						}
					}
				}
			}
			
		},20L,20L);
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Block block = event.getBlock();
		if (block.getType() == Material.END_PORTAL_FRAME) {
			Location loc = block.getLocation();
			String name = teleporterNames.get(loc);
			
			if (name !=null) {
				teleporterNames.remove(loc);
				List<Location> linked = linkedTeleporters.get(name);
				if (linked !=null) {
					linked.remove(loc);
					if (linked.isEmpty()) {
						linkedTeleporters.remove(name);
					}
				}
				saveTeleporters();
				
				block.getWorld().spawnParticle(Particle.EXPLOSION, 
						loc.clone().add(0.5, 0.5, 0.5),1);
				block.getWorld().playSound(loc, Sound.BLOCK_PORTAL_TRIGGER, 1.0f, 0.5f);
				
				event.getPlayer().sendMessage(ChatColor.RED + "Teleport Zniszczony!");
			}
				
		}
	}
	
	//Teleporter Item
	private ItemStack createTeleporterItem() {
		ItemStack item = new ItemStack(Material.END_PORTAL_FRAME);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.LIGHT_PURPLE + "Teleporter");
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.GRAY + "Postaw i nazwij dwa teleporty");
		lore.add(ChatColor.GRAY + "zeby mogly sie polaczyc");
		lore.add(ChatColor.GRAY + "(musza miec taka sama nazwe)");
		meta.setLore(lore);
		
		PersistentDataContainer container = meta.getPersistentDataContainer();
		container.set(teleporterKey, PersistentDataType.STRING, "teleporter");
		
		item.setItemMeta(meta);
		return item;
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		ItemStack item = event.getItemInHand();
		ItemMeta meta = item.getItemMeta();
		
		if (meta != null && meta.getPersistentDataContainer().has(teleporterKey, PersistentDataType.STRING)) {
			Player player = event.getPlayer();
			namingTeleporter.put(player.getUniqueId(), event.getBlock().getLocation());
			player.sendMessage(ChatColor.GREEN + Messages.TELEPORT_PLACE.toString());
			
			Location loc = event.getBlock().getLocation();
			loc.getWorld().spawnParticle(Particle.ENCHANT,
					loc.clone().add(0.5,1,0.5), 50, 0.5, 0.5, 0.5);
			loc.getWorld().playSound(loc, Sound.BLOCK_END_PORTAL_FRAME_FILL, 1.0f, 1.0f);
		}
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		
		Block block = event.getClickedBlock();
		if (block == null || block.getType() != Material.END_PORTAL_FRAME) return;
		
		Player player = event.getPlayer();
		ItemStack item = event.getItem();
		
		if (item != null && item.getType() == Material.NAME_TAG && item.hasItemMeta()) {
			event.setCancelled(true);
			String name = item.getItemMeta().getDisplayName();
			Location loc = block.getLocation();
			
			teleporterNames.put(loc, name);
			
			if (!linkedTeleporters.containsKey(name)) {
				linkedTeleporters.put(name, new ArrayList<>());
			}
			linkedTeleporters.get(name).add(loc);
			
			if (player.getGameMode() != GameMode.CREATIVE) {
				item.setAmount(item.getAmount() -1);
			}
			player.sendMessage(ChatColor.GREEN + Messages.TELEPORT_NAMED.toString()+name);
			
			Location effectLoc = loc.clone().add(0.5,1,0.5);
			World world = loc.getWorld();
			world.spawnParticle(Particle.PORTAL, effectLoc, 100,0.5,0.5,0.5);
			world.spawnParticle(Particle.END_ROD, effectLoc,20,0.2,0.2,0.2);
			world.playSound(loc, Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1.0f, 1.0f);
			
			saveTeleporters();
		}
	}
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		Location to = event.getTo();
		if (to == null) return;
		
		Block block = to.getBlock().getRelative(0,-1,0);
		if (block.getType() != Material.END_PORTAL_FRAME) return;
		
		Location teleporterLoc = block.getLocation();
		String name = teleporterNames.get(teleporterLoc);
		
		if(name==null) return;
		
		List<Location> linked = linkedTeleporters.get(name);
		if(linked==null || linked.size() <2) return;
		
		Location destination = null;
		for (Location loc : linked) {
			if(!loc.equals(teleporterLoc)) {
				destination = loc;
				break;
			}
		}
		if (destination !=null) {
			Player player = event.getPlayer();
			
			Location teleportLoc = destination.clone().add(0.5,1,0.5);
			teleportLoc.setYaw(player.getLocation().getYaw());
			teleportLoc.setPitch(player.getLocation().getPitch());
			
			World world = player.getWorld();
			
			world.spawnParticle(Particle.PORTAL, player.getLocation(), 100,0.5,1,0.5);
			world.spawnParticle(Particle.REVERSE_PORTAL, player.getLocation(), 50,0.2,0.5,0.2);
			world.playSound(player.getLocation(),Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
			
			player.teleport(teleportLoc);
			player.sendTitle(ChatColor.GOLD + "Witaj w: ",name,20,20,15);
			player.playSound(teleportLoc, Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 1f);
			
			destination.getWorld().spawnParticle(Particle.PORTAL, teleportLoc, 100,0.5,1,0.5);
			destination.getWorld().spawnParticle(Particle.END_ROD, teleportLoc, 30,0.2,0.5,0.2);
			destination.getWorld().playSound(teleportLoc,Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
			destination.getWorld().playSound(teleportLoc, Sound.BLOCK_END_PORTAL_SPAWN, 0.5f, 1.5f);
		}
		
	}
//	public Player teleportInfo(Player player) {
//		return player;
//	}
}