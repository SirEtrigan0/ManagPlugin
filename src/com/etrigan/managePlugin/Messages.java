package com.etrigan.managePlugin;
import org.bukkit.ChatColor;

public enum Messages {

	TELEPORT_PLACE("Kliknij prawym na teleport z name-tagiem w reku!"),
	TELEPORT_NAMED("Teleporter nazwany: ");
	
	private String defaultMessages;
	
	Messages(String defaultMessages){
		this.defaultMessages = defaultMessages;
		
	}
	public void setDefaultMessages(String defaultMessages) {
		String toSet = ChatColor.translateAlternateColorCodes('&', defaultMessages);
		if(toSet.equals("")) return;
		this.defaultMessages = toSet;
	}
	@Override
	public String toString() {
		return this.defaultMessages;
	}
}
