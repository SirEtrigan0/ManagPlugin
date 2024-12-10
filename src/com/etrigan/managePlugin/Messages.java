package com.etrigan.managePlugin;
import org.bukkit.ChatColor;

public enum Messages {

	TELEPORT_PLACE("Kliknij prawym na teleport z name-tagiem w reku!"),
	TELEPORT_NAMED("Teleporter nazwany: "),
	TP_CD("Teleporter na cooldownie!"),
	SET_HOME("§aDom ustawiony"),
	NO_HOME("§cNie ustawiles/ustawilas jeszcze domu! Uzyj pierw /sethome."),
	DIAX_HOME("§cPotrzebujesz jednego diamentu zeby teleportowac sie do domu!"),
	WELC_HOME("§aWitaj w domu! §7(Oplata w wysokosci jednego diamentu zostala uiszczona)"),
	TP_DESTR("!"),
	TP_ITEM_LORE1("Postaw i nazwij dwa teleporty"),
	TP_ITEM_LORE2("zeby mogly sie polaczyc"),
	TP_ITEM_LORE3("(musza miec taka sama nazwe)"),
	TP_WELC("Witaj w: "),
	OBS_LORE1("&7Postaw ten blok"),
	OBS_LORE2("&7Zeby generowac to cos ten tego tamtego!"),
	STN_LORE1("§7Postaw ten blok"),
	STN_LORE2("§7zeby generowac stone");
	
	
	
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
