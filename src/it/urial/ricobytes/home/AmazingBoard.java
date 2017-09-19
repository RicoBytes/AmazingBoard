package it.urial.ricobytes.home;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;

import it.urial.ricobytes.api.AmazingBoardAPI;
import it.urial.ricobytes.commands.AmazingBoardCmd;
import it.urial.ricobytes.core.FileManager;
import it.urial.ricobytes.events.JQ;
import it.urial.ricobytes.utils.Scoreboard;

public class AmazingBoard extends JavaPlugin {
	
	private static AmazingBoard ab;
	private AmazingBoardAPI abapi;
	
	@Override
	public void onEnable(){
		ab = this;
		
		FileManager.setup(this);
		
		Bukkit.getPluginManager().registerEvents(new JQ(), this);
		
		getCommand("amazingboard").setExecutor(new AmazingBoardCmd());
		getCommand("ab").setExecutor(new AmazingBoardCmd());
		
		abapi = new AmazingBoardAPI(this);
		if(!abapi.getFilesListNames().contains(abapi.getMainConfigurationFile().getName())){
			abapi.setDefaultMainConfigurationFile();
		}
		abapi.setMainConfigurationCache();
		
		Bukkit.getConsoleSender().sendMessage("§a##################################");
		Bukkit.getConsoleSender().sendMessage("§e§lAMAZINGBOARD §6v" + this.getDescription().getVersion());
		Bukkit.getConsoleSender().sendMessage("");
		Bukkit.getConsoleSender().sendMessage("§aScoreboards loaded: §f" + abapi.getFiles().length);
		Bukkit.getConsoleSender().sendMessage("§aMain Scoreboard: §f" + abapi.getMainConfigurationFile().getName());
		if(!Bukkit.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")){
			Bukkit.getConsoleSender().sendMessage("§aPlaceholderAPI: §cNot Loaded");
		} else if (Bukkit.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")){
			Bukkit.getConsoleSender().sendMessage("§aPlaceholderAPI: §aLoaded");
		}
		Bukkit.getConsoleSender().sendMessage("§aAPI Enabled: §a" + FileManager.config.getBoolean("API Enabled"));
		Bukkit.getConsoleSender().sendMessage("");
		Bukkit.getConsoleSender().sendMessage("§f§oMade by RicoBytes (c) Urial 2017");
		Bukkit.getConsoleSender().sendMessage("§a##################################");
		
		Scoreboard.getManager().start();
		
		Scoreboard.getManager().resetTeams();
		
		if(!FileManager.config.getBoolean("API Enabled")){
			abapi.refreshScores();
			abapi.refreshTitle();
		}
	}
	
	public void onDisable(){
		try {
			abapi.reset();
		} catch (Exception e) {
			if(!FileManager.config.getBoolean("API Enabled")){
				Bukkit.getConsoleSender().sendMessage("§e§lAMAZINGBOARD §cCould not reset informations.");
			}
		}
		
		Scoreboard.getManager().resetTeams();
		for(Player players : Bukkit.getOnlinePlayers()){
			
			players.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
			
			Scoreboard sc = Scoreboard.getManager().getScoreboard(players);
			if(sc != null){
				sc.unregister();
			}
		}
	}
	
	public static AmazingBoard getInstance(){
		return ab;
	}
	
	public AmazingBoardAPI getAPI(){
		return abapi;
	}
	
}
