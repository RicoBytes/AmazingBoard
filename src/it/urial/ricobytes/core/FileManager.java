package it.urial.ricobytes.core;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import it.urial.ricobytes.home.AmazingBoard;

public class FileManager {

	private static File configFile;
	public static FileConfiguration config;
	
	public static File sbFolder;
	public static File[] sbFiles;
	public static LinkedHashMap<String, FileConfiguration> scoreboards = new LinkedHashMap<String, FileConfiguration>();
	
	private static Plugin plugin;
	
	private static void createConfig(Plugin p){
    	plugin = p;
    	
        try {
            if (!plugin.getDataFolder().exists()){
            	plugin.getDataFolder().mkdirs();
            }
            configFile = new File(plugin.getDataFolder(), "config.yml");
            if (!configFile.exists()) {
            	
            	configFile.createNewFile();
                
            }
            
            config = YamlConfiguration.loadConfiguration(configFile);
            
            saveConfig();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void saveConfig(){
    	
    	config.options().header("\nAmazingBoard Made By RicoBytes version " + AmazingBoard.getInstance().getDescription().getVersion() + "\n");

    	if(!(config.contains("API Enabled"))){
    		config.set("API Enabled", false);
    	}
    	if(!(config.contains("Main Scoreboard"))){
    		config.set("Main Scoreboard", "example.yml");
    	}
    	if(!(config.contains("Date Format"))){
    		config.set("Date Format", "MM/dd/yyyy");
    	}
    	
    	try {
			config.save(configFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    private static void createScoreboards(Plugin p){
    	plugin = p;
    	
        try {
        	sbFolder = new File(plugin.getDataFolder() + File.separator + "scoreboards");
        	
        	/*
        	 * CHECK IF FOLDERS EXISTS
        	 */
        	
            if(!plugin.getDataFolder().exists()){
            	plugin.getDataFolder().mkdirs();
            }
            if(!sbFolder.exists()){
            	sbFolder.mkdirs();
            }
            
            /*
             * CREATE EXAMPLE FILE
             */
            
            File example = new File(sbFolder, "example.yml");
            if (!example.exists()) {
            	example.createNewFile();
            }
            
            /*
             * LOADING ALL FILES
             */
            
            sbFiles = sbFolder.listFiles();
            
            for(int i = 0; i < sbFiles.length; i++){
            	if(sbFiles[i].isFile()){
            		scoreboards.put(sbFiles[i].getName(),
            				YamlConfiguration.loadConfiguration(new File(sbFolder.getAbsolutePath() + File.separator + sbFiles[i].getName())));
            	}
            }
            
            /*
             * SAVE ALL FILES
             */
            
            saveScoreboards();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void saveScoreboards(){
    	List<String> indexes = new ArrayList<String>(scoreboards.keySet());
    	
    	scoreboards.get("example.yml").options().header("\nTHIS IS AN EXAMPLE FILE. YOU CAN COPY IT AND PASTE WITH ANOTHER NAME AND SET ANOTHER SCOREBOARD.\nREMEMBER: 20 TICKS ARE 1 SECOND!\n");

    	if(!scoreboards.get("example.yml").contains("Scoreboard.Disabled Worlds")){
    		scoreboards.get("example.yml").set("Scoreboard.Disabled Worlds", Arrays.asList("world1", "world2"));
    	}
    	if(!scoreboards.get("example.yml").contains("Scoreboard.Title.Delay in Ticks")){
    		scoreboards.get("example.yml").set("Scoreboard.Title.Delay in Ticks", 10);
    	}
    	if(!scoreboards.get("example.yml").contains("Scoreboard.Title.Update in Ticks")){
    		scoreboards.get("example.yml").set("Scoreboard.Title.Update in Ticks", 2);
    	}
    	if(!scoreboards.get("example.yml").contains("Scoreboard.Title.Titles")){
    		scoreboards.get("example.yml").set("Scoreboard.Title.Titles", Arrays.asList("&6&l&nAMAZINGBOARD", "&e&l&nAMAZINGBOARD", "&a&l&nAMAZINGBOARD", "&2&l&nAMAZINGBOARD", "&3&l&nAMAZINGBOARD", "&b&l&nAMAZINGBOARD", "&d&l&nAMAZINGBOARD", "&5&l&nAMAZINGBOARD"));
    	}
    	if(!scoreboards.get("example.yml").contains("Scoreboard.Scores.Delay in Ticks")){
    		scoreboards.get("example.yml").set("Scoreboard.Scores.Delay in Ticks", 10);
    	}
    	if(!scoreboards.get("example.yml").contains("Scoreboard.Scores.Update in Ticks")){
    		scoreboards.get("example.yml").set("Scoreboard.Scores.Update in Ticks", 40);
    	}
    	if(!scoreboards.get("example.yml").contains("Scoreboard.Scores.Scores")){
    		scoreboards.get("example.yml").set("Scoreboard.Scores.Scores", Arrays.asList("{space}", "&6&lUSER &e%player_name%", "&6&lPLAYERS &e%server_online%", "{space}", "&7{date}", "&b&nhttps://t.me/UrialTeam"));
    	}
    	
    	try {
    		for(int i = 0; i < scoreboards.size(); i++){
        		scoreboards.get(indexes.get(i)).save(new File(sbFolder, sbFiles[i].getName()));
    		}
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    public static void setup(Plugin p){
    	createConfig(p);
    	createScoreboards(p);
    }
}
