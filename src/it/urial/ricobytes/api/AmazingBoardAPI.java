package it.urial.ricobytes.api;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import it.urial.ricobytes.core.FileManager;
import it.urial.ricobytes.utils.Placeholders;
import it.urial.ricobytes.utils.Scoreboard;

public class AmazingBoardAPI {
	
	private Plugin plugin;
	private BukkitTask scores;
	private BukkitTask title;
	private Integer titleIndex = 0;
	private FileConfiguration mainConfiguration;
	
	public AmazingBoardAPI(Plugin plugin){
		this.plugin = plugin;
	}
	
	/*
	 * SCOREBOARDS FILES
	 */
	
	public void reload(){
		try {
			this.reset();
		} catch (Exception e){
			e.printStackTrace();
		}
		FileManager.setup(this.plugin);
		this.setMainConfigurationCache();
		this.refreshScores();
		this.refreshTitle();
	}
	
	public void reset() throws Exception {
		this.scores.cancel();
		this.title.cancel();
		this.titleIndex = 0;
		FileManager.scoreboards.clear();
	}
	
	public void load(String filename) throws IOException {
		if(getFilesListNames().contains(filename)){
			try {
				this.reset();
			} catch (Exception e){
				e.printStackTrace();
			}
			FileManager.setup(this.plugin);
			mainConfiguration = FileManager.scoreboards.get(filename);
			this.refreshScores();
			this.refreshTitle();
		} else {
			throw new IOException();
		}
	}
	
	public File[] getFiles(){
		return FileManager.sbFolder.listFiles();
	}
	
	public List<File> getFilesList(){
		List<File> files = new ArrayList<File>();
		
		for(int i = 0; i < this.getFiles().length; i++){
        	if(this.getFiles()[i].isFile()){
        		files.add(this.getFiles()[i]);
        	}
        }
		return files;
	}
	
	public List<String> getFilesListNames(){
		List<String> files = new ArrayList<String>();
		
		for(int i = 0; i < this.getFiles().length; i++){
        	if(this.getFiles()[i].isFile()){
        		files.add(this.getFiles()[i].getName());
        	}
        }
		return files;
	}
	
	public File getMainConfigurationFile(){
		return new File(FileManager.sbFolder + File.separator, FileManager.config.getString("Main Scoreboard"));
	}
	
	public void setMainConfigurationFile(String filename) throws IOException {
		if(getFilesListNames().contains(filename)){
			FileManager.config.set("Main Scoreboard", filename);
			FileManager.saveConfig();
		} else {
			throw new IOException();
		}
	}
	
	public void setDefaultMainConfigurationFile(){
		FileManager.config.set("Main Scoreboard", "example.yml");
		FileManager.saveConfig();
	}
	
	public FileConfiguration getMainConfiguration(){
		return FileManager.scoreboards.get(FileManager.config.getString("Main Scoreboard"));
	}
	
	public void setMainConfigurationCache(){
		mainConfiguration = FileManager.scoreboards.get(FileManager.config.getString("Main Scoreboard"));
	}
	
	public FileConfiguration getMainConfigurationCache(){
		return mainConfiguration;
	}
	
	/*
	 * SCOREBOARD
	 */
	
	public int getTitleIndex(){
		return titleIndex;
	}
	
	public void scores(Player player){
		Scoreboard sc = Scoreboard.getManager().getScoreboard(player);
		if(sc == null){
			sc = Scoreboard.getManager().setupBoard(player);
		}
		setScores(player, sc);
	}
	
	public void setTitle(Player player){
		try {
			Scoreboard sc = Scoreboard.getManager().getScoreboard(player);
			sc.setHeader(ChatColor.translateAlternateColorCodes('&', this.mainConfiguration.getStringList("Scoreboard.Title.Titles").get(titleIndex)));
			sc.update();
		} catch(Exception e){
		}
	}
	
	public void setScores(Player player, Scoreboard sc){
		Placeholders.resetSpaces();
		List<String> scL = this.mainConfiguration.getStringList("Scoreboard.Scores.Scores");
		List<String> vals = new ArrayList<String>();
		for(String str : scL){
			vals.add(ChatColor.translateAlternateColorCodes('&', Placeholders.addPlaceholders(player, ((str)))));
		}
		sc.setScores(vals);
		sc.update();
	}
	
	public void refreshTitle(){
		this.title = new BukkitRunnable(){
			@Override
			public void run(){
				if(titleIndex == mainConfiguration.getStringList("Scoreboard.Title.Titles").size()){
					titleIndex = 0;
				}
				
				for(Player players : Bukkit.getOnlinePlayers()){
					setTitle(players);
				}
				
				if(titleIndex < mainConfiguration.getStringList("Scoreboard.Title.Titles").size()){
					titleIndex+=1;
				}
			}
		}.runTaskTimer(plugin, mainConfiguration.getInt("Scoreboard.Title.Delay in Ticks"), mainConfiguration.getInt("Scoreboard.Title.Update in Ticks"));
	}
	
	public void refreshScores(){
		this.scores = new BukkitRunnable(){
			@Override
			public void run(){
				for(Player players : Bukkit.getOnlinePlayers()){
					scores(players);
				}
			}
		}.runTaskTimer(plugin, mainConfiguration.getInt("Scoreboard.Scores.Delay in Ticks"), mainConfiguration.getInt("Scoreboard.Scores.Update in Ticks"));
	}
	
	public BukkitTask getTitleTask(){
		return this.title;
	}
	
	public BukkitTask getScoresTask(){
		return this.scores;
	}
	
}
