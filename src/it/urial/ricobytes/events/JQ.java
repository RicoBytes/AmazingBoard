package it.urial.ricobytes.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import it.urial.ricobytes.home.AmazingBoard;
import it.urial.ricobytes.utils.Scoreboard;

public class JQ implements Listener {
	
	private AmazingBoard ab = AmazingBoard.getInstance();
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e){
		Player player = e.getPlayer();
		
		ab.getAPI().scores(player);
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e){
		Player player = e.getPlayer();
		
		Scoreboard.scores.remove(player);
		Scoreboard.getManager().getScoreboard(player).unregister();
	}
	
}
