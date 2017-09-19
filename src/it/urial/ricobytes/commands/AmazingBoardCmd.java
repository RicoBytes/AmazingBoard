package it.urial.ricobytes.commands;

import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import it.urial.ricobytes.core.FileManager;
import it.urial.ricobytes.home.AmazingBoard;
import it.urial.ricobytes.utils.Placeholders;

public class AmazingBoardCmd implements CommandExecutor {
	
	private AmazingBoard ab = AmazingBoard.getInstance();
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		
		if(cmd.getName().equalsIgnoreCase("amazingboard")
				|| cmd.getName().equalsIgnoreCase("ab")){
			if(sender instanceof Player){
				Player player = (Player) sender;
				
				if(player.hasPermission("amazingboard.admin")){
					
					if(FileManager.config.getBoolean("API Enabled")){
						player.sendMessage("");
						Placeholders.sendCenteredMessage(player, "§e§lAMAZINGBOARD §eHelp");
						player.sendMessage("");
						player.sendMessage(" §a/ab help");
						player.sendMessage(" §a/ab reload");
						player.sendMessage(" §a/ab list");
						player.sendMessage(" §a/ab setmain <Scoreboard File> ┃ Updated after reload");
						player.sendMessage(" §a/ab switch <Scoreboard File> ┃ Updated instantly [Reset after Reload]");
						
						player.sendMessage(" §cYou cannot run commands while is in API Mode! ");
						player.sendMessage("");
						Placeholders.sendCenteredMessage(player, "§c§oBy RicoBytes");
						player.sendMessage("");
						return false;
					}
					
					if(args.length == 0){
						player.sendMessage("");
						Placeholders.sendCenteredMessage(player, "§e§lAMAZINGBOARD §eHelp");
						player.sendMessage("");
						player.sendMessage(" §a/ab help");
						player.sendMessage(" §a/ab reload");
						player.sendMessage(" §a/ab list");
						player.sendMessage(" §a/ab setmain <Scoreboard File> ┃ Updated after reload");
						player.sendMessage(" §a/ab switch <Scoreboard File> ┃ Updated instantly [Reset after Reload]");
						player.sendMessage("");
						Placeholders.sendCenteredMessage(player, "§c§oBy RicoBytes");
						player.sendMessage("");
					}
					
					if(args.length >= 1
							&& (!(args[0].equalsIgnoreCase("help")
									|| args[0].equalsIgnoreCase("reload")
									|| args[0].equalsIgnoreCase("list")
									|| args[0].equalsIgnoreCase("setmain")
									|| args[0].equalsIgnoreCase("switch")))){
						player.sendMessage("§cInvalid argument(s)! Type /ab to see all commands!");
					}
					
					/*
					 * HELP
					 */
					
					if(args.length >= 1
							&& args[0].equalsIgnoreCase("help")){
						Bukkit.dispatchCommand(player, "ab");
					}
					
					/*
					 * RELOAD
					 */
					
					if(args.length >= 1
							&& args[0].equalsIgnoreCase("reload")){
						player.sendMessage("§e§lAMAZINGBOARD §e┃ §aReloading...");
						ab.getAPI().reload();
						player.sendMessage("§e§lAMAZINGBOARD §e┃ §aSuccessfully reloaded all files.");
						player.sendMessage("§e§lAMAZINGBOARD §e┃ §aLoaded " + ab.getAPI().getFiles().length + " scoreboard(s)");
					}
					
					/*
					 * LIST
					 */
					
					if(args.length >= 1
							&& args[0].equalsIgnoreCase("list")){
						player.sendMessage("");
						Placeholders.sendCenteredMessage(player, "§e§lAMAZINGBOARD §eHelp");
						player.sendMessage("");
						
						StringBuilder sb = new StringBuilder();
						
						for(int i = 0; i < ab.getAPI().getFiles().length; i++){
							if(ab.getAPI().getFiles()[i].isFile()){
								sb.append(ab.getAPI().getFiles()[i].getName().equals(FileManager.config.getString("Main Scoreboard")) ? "§e(Main) " + ab.getAPI().getFiles()[i].getName() : " §a" + ab.getAPI().getFiles()[i].getName());
								sb.append(", ");
							}
						}
						
		        		player.sendMessage(sb.toString().substring(0, sb.toString().length()-2));
						player.sendMessage("");
						Placeholders.sendCenteredMessage(player, "§c§oBy RicoBytes");
						player.sendMessage("");
					}
					
					/*
					 * SET MAIN
					 */
					
					if(args.length == 1
							&& args[0].equalsIgnoreCase("setmain")){
						player.sendMessage("§e§lAMAZINGBOARD §e┃ §cNot enough argument(s)");
					}
					
					if(args.length >= 2
							&& args[0].equalsIgnoreCase("setmain")){
						String filename = args[1];
						
						if(!args[1].contains(".yml")){
							player.sendMessage("§e§lAMAZINGBOARD §e┃ §aThis is not a file! You forgot the \".yml\"!");
						} else {
							try {
								ab.getAPI().setMainConfigurationFile(filename);
								player.sendMessage("§e§lAMAZINGBOARD §e┃ §aSuccessfully changed your main scoreboard to §e" + filename + "§a!");
							} catch (IOException e) {
								player.sendMessage("§e§lAMAZINGBOARD §e┃ §aThis file doesn't exist!");
							}
						}
					}
					
					/*
					 * SWITCH
					 */
					
					if(args.length == 1
							&& args[0].equalsIgnoreCase("switch")){
						player.sendMessage("§e§lAMAZINGBOARD §e┃ §cNot enough argument(s)");
					}
					
					if(args.length >= 2
							&& args[0].equalsIgnoreCase("switch")){
						String filename = args[1];
						
						if(!args[1].contains(".yml")){
							player.sendMessage("§e§lAMAZINGBOARD §e┃ §aThis is not a file! You forgot the \".yml\"!");
						} else {
							try {
								ab.getAPI().load(filename);
								player.sendMessage("§e§lAMAZINGBOARD §e┃ §aSuccessfully changed global scoreboard to §e" + filename + "§a!");
							} catch (IOException e) {
								player.sendMessage("§e§lAMAZINGBOARD §e┃ §aThis file doesn't exist!");
							}
						}
					}
					
				} else {
					player.sendMessage("§cYou cannot run this command!");
				}
			} else {
				if(args.length == 0){
					sender.sendMessage("");
					sender.sendMessage("§e§lAMAZINGBOARD §eHelp");
					sender.sendMessage("");
					sender.sendMessage(" §a/ab help");
					sender.sendMessage(" §a/ab reload");
					sender.sendMessage(" §a/ab list");
					sender.sendMessage(" §a/ab setmain <Scoreboard File> ┃ Updated after reload");
					sender.sendMessage(" §a/ab switch <Scoreboard File> ┃ Updated instantly");
					sender.sendMessage("");
					sender.sendMessage("§c§oBy RicoBlaze8");
					sender.sendMessage("");
				}
				
				/*
				 * HELP
				 */
				
				if(args.length >= 1
						&& args[0].equalsIgnoreCase("help")){
					Bukkit.dispatchCommand(sender, "ab");
				}
				
				/*
				 * RELOAD
				 */
				
				if(args.length >= 1
						&& args[0].equalsIgnoreCase("reload")){
					sender.sendMessage("§e§lAMAZINGBOARD §e┃ §aReloading...");
					ab.getAPI().reload();
					sender.sendMessage("§e§lAMAZINGBOARD §e┃ §aSuccessfully reloaded all files.");
					sender.sendMessage("§e§lAMAZINGBOARD §e┃ §aLoaded " + ab.getAPI().getFiles().length + " scoreboard(s)");
				}
				
				/*
				 * LIST
				 */
				
				if(args.length >= 1
						&& args[0].equalsIgnoreCase("list")){
					sender.sendMessage("");
					sender.sendMessage("§e§lAMAZINGBOARD §eHelp");
					sender.sendMessage("");
					
					StringBuilder sb = new StringBuilder();
					
					for(int i = 0; i < ab.getAPI().getFiles().length; i++){
						if(ab.getAPI().getFiles()[i].isFile()){
							sb.append(ab.getAPI().getFiles()[i].getName().equals(FileManager.config.getString("Main Scoreboard")) ? "§e(Main) " + ab.getAPI().getFiles()[i].getName() : " §a" + ab.getAPI().getFiles()[i].getName());
							sb.append(", ");
						}
					}
					
	        		sender.sendMessage(sb.toString().substring(0, sb.toString().length()-2));
					sender.sendMessage("");
					sender.sendMessage("§c§oBy RicoBlaze8");
					sender.sendMessage("");
				}
				
				/*
				 * SET MAIN
				 */
				
				if(args.length == 1
						&& args[0].equalsIgnoreCase("setmain")){
					sender.sendMessage("§e§lAMAZINGBOARD §e┃ §cNot enough argument(s)");
				}
				
				if(args.length >= 2
						&& args[0].equalsIgnoreCase("setmain")){
					String filename = args[1];
					
					if(!args[1].contains(".yml")){
						sender.sendMessage("§e§lAMAZINGBOARD §e┃ §aThis is not a file! You forgot the \".yml\"!");
					} else {
						try {
							ab.getAPI().setMainConfigurationFile(filename);
							sender.sendMessage("§e§lAMAZINGBOARD §e┃ §aSuccessfully changed your main scoreboard to §e" + filename + "§a!");
						} catch (IOException e) {
							sender.sendMessage("§e§lAMAZINGBOARD §e┃ §aThis file doesn't exist!");
						}
					}
				}
				
				/*
				 * SWITCH
				 */
				
				if(args.length == 1
						&& args[0].equalsIgnoreCase("switch")){
					sender.sendMessage("§e§lAMAZINGBOARD §e┃ §cNot enough argument(s)");
				}
				
				if(args.length >= 2
						&& args[0].equalsIgnoreCase("switch")){
					String filename = args[1];
					
					if(!args[1].contains(".yml")){
						sender.sendMessage("§e§lAMAZINGBOARD §e┃ §aThis is not a file! You forgot the \".yml\"!");
					} else {
						try {
							ab.getAPI().load(filename);
							sender.sendMessage("§e§lAMAZINGBOARD §e┃ §aSuccessfully changed global scoreboard to §e" + filename + "§a!");
						} catch (IOException e) {
							sender.sendMessage("§e§lAMAZINGBOARD §e┃ §aThis file doesn't exist!");
						}
					}
				}
			}
		}
		
		return true;
	}

}
