package com.etrigan.managePlugin;
import org.bukkit.ChatColor;

public enum Messages {

	TELEPORT_PLACE("Kliknij prawym na teleport z name-tagiem w reku!"),
	TELEPORT_NAMED("Teleporter nazwany: "),
	TP_CD("Teleporter na cooldownie!"),
	SET_HOME("§aDom ustawiony"),
	NO_HOME("§cNie ustawiles/ustawilas jeszcze domu! Uzyj pierw /sethome."),
	DIAX_HOME("§cPotrzebujesz jednego diamentu zeby teleportowac sie do domu!"),
	WELC_HOME("§aWitaj w domu! §7(Oplata w wysokosci jednego diamentu zostala uiszczona)");
	
	
	
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
