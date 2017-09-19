package it.urial.ricobytes.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Team;

public class Scoreboard {
	
    private static final ScoreboardManager manager;
    public static org.bukkit.scoreboard.Scoreboard board;
    private static Objective obj;
	
    static {
    	manager = new ScoreboardManager();
    }
    
    public static ScoreboardManager getManager(){
    	return manager;
    }
   
    private final String name;
    private List<String> lastUpdate = new ArrayList<String>();
    public static List<String> scores = new ArrayList<String>();
    private String header;
   
    private Scoreboard(Player player){
    	name = player == null ? null : player.getName();
        if(player != null) {
        	player.setScoreboard(board);
        	header = player.getName();
        }
    }
    
    public String getHeader(){
        return header;
    }
    
    public Scoreboard setHeader(String header){
    	this.header = header;
    	return this;
    }
    
    public Player getPlayer(){
    	return Bukkit.getPlayer(name);
    }
    
    public List<String> getScores(){
    	return scores;
    }
    
    @SuppressWarnings("static-access")
    public Scoreboard setScores(List<String> scores){
    	this.scores = new ArrayList<String>(scores);
    	return this;
    }
    
    public Scoreboard setScores(String... scores){
    	return setScores(Arrays.asList(scores));
    }
    
    public Scoreboard update(){
    	Player p = getPlayer();
    	if(p == null) return this;
    	if(scores.size() == lastUpdate.size()) {
    		int size = scores.size() - 1;
    		for(int i=0;i<=size;i++) {
    			String entry = scores.get(i);
    			String last = lastUpdate.get(i);
    			if(!entry.equals(last)) {
    				Object remove = manager.getRemovePacket(obj, last);
    				Object add = manager.getChangePacket(obj, entry, size - i);
    				ReflectUtil.sendPacket(p, remove, add);
    			}
    		}
    	} else {
    		for(int i=0;i<lastUpdate.size();i++) {
    			Object packet = manager.getRemovePacket(obj, lastUpdate.get(i));
    			ReflectUtil.sendPacket(p, packet);
    		}
    		int size = scores.size() - 1;
    		for(int i=0;i<=size;i++) {
    			Object packet = manager.getChangePacket(obj, scores.get(i), size-i);
    			ReflectUtil.sendPacket(p, packet);
    		}
    	}
    	
    	try {
    		Object packet = ReflectUtil.getNMSClass(NMSClass.PACKET_OBJECTIVE).newInstance();
    		NMSClass clazz = NMSClass.ENUM_HEALTH_DISPLAY;
    		
    		String[] keys = "a, b, c, d".split(", ");
    		Object[] values;
    		if(clazz.name != null){
    			values = new Object[]{"Board", header, ReflectUtil.getField(ReflectUtil.getNMSClass(clazz),
    							null, "INTEGER"), 2};
    		} else {
    			values = new Object[] {"Board", header, 2};
    		}
    		for(int i=0;i<values.length;i++)
    			ReflectUtil.setField(packet.getClass(), packet, keys[i], values[i]);
    		ReflectUtil.sendPacket(p, packet);
    	} catch (Exception ex) {
    		Bukkit.getLogger().info(ExceptionUtils.getStackTrace(ex));
    	}
    	this.lastUpdate = new ArrayList<String>(scores);
    	return this;
    }
    
    public void unregister() {
    	Player player = getPlayer();
    	if(player != null) {
    		manager.boards.remove(player.getUniqueId());
    		player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
    	}
    }
   
    public static class ScoreboardManager {
    	
    	private final Map<UUID, Scoreboard> boards;
    	
    	private ScoreboardManager() {
    		boards = new HashMap<UUID, Scoreboard>();
    	}
    	
    	public Scoreboard setupBoard(Player p) {
    		Scoreboard board = new Scoreboard(p);
    		boards.put(p.getUniqueId(), board);
    		return board;
    	}
    	
    	public Scoreboard getScoreboard(Player p) {
    		return boards.get(p.getUniqueId());
    	}
    	
    	public org.bukkit.scoreboard.Scoreboard getScoreboard() {
    		return board;
    	}
    	
    	public void resetTeams() {
    		Team[] teams = board.getTeams().toArray(new Team[0]);
    		for(Team team : teams) {
    			team.unregister();
    		}
    	}
           
    	private Object getChangePacket(Objective obj, String entry, int score) {
    		try {
    			Object packet = ReflectUtil.getNMSClass(NMSClass.PACKET_SCORE).newInstance();
    			NMSClass clazz = NMSClass.ENUM_ACTION;
    			String[] keys = "a, b, c, d".split(", ");
    			Object[] values;
    			if(clazz.name != null)
    				values = new Object[] {entry, obj.getName(), score,
    						ReflectUtil.getField(ReflectUtil.getNMSClass(clazz),
    								null, "CHANGE")};
    			else values = new Object[] {entry, obj.getName(), score, 0};
    			for(int i=0;i<keys.length;i++)
    				ReflectUtil.setField(packet.getClass(), packet, keys[i], values[i]);
    			return packet;
    		} catch (Exception ex) {
    			Bukkit.getLogger().info(ExceptionUtils.getStackTrace(ex));
    			return null;
    		}
    	}
           
    	private Object getRemovePacket(Objective obj, String entry) {
    		Object handle = ReflectUtil.getHandle(obj);
    		try {
    			Class<?> objective = ReflectUtil.getNMSClassByName("ScoreboardObjective");
    			if(ReflectUtil.isAbove("v1_8_R1"))
    				return ReflectUtil.getNMSClass(NMSClass.PACKET_SCORE)
    						.getConstructor(String.class, objective)
    						.newInstance(entry, handle);
    			else {
    				return ReflectUtil.getNMSClass(NMSClass.PACKET_SCORE)
    						.getConstructor(String.class).newInstance(entry);
    			}
    		} catch (Exception ex) {
    			Bukkit.getLogger().info(ExceptionUtils.getStackTrace(ex));
    			return null;
    		}
    	}
           
    	public void start() {
    		board = Bukkit.getScoreboardManager().getNewScoreboard();
    		obj = board.registerNewObjective("Board", "dummy");
    		obj.setDisplaySlot(DisplaySlot.SIDEBAR);
    	}
    }
    
    private static enum NMSClass {
    	
    	PACKET_OBJECTIVE(new String[0], "PacketPlayOutScoreboardObjective"),
    	ENUM_HEALTH_DISPLAY(new String[] {"v1_8_R1"}, null, "EnumScoreboardHealthDisplay"),
    	PACKET_SCORE(new String[0], "PacketPlayOutScoreboardScore"),
    	ENUM_ACTION(new String[] {"v1_8_R1"}, null, "EnumScoreboardAction");
    	
    	String name;
    	
    	NMSClass(String[] versions, String... s) {
			if(versions.length == 0)
				name = s[0];
			for(int i=versions.length - 1;i>=0;i--) {
				if(ReflectUtil.isAbove(versions[i]))
					name = s[i + 1];
			}
		}
    }
   
    private static class ReflectUtil {
           
    	static final String CBK;
    	static final boolean subclasses;
    	
    	static {
    		String pkg = Bukkit.getServer().getClass().getPackage().getName();
    		CBK = pkg.substring(pkg.lastIndexOf('.') + 1);
    		subclasses = isAbove("v1_8_R2");
    	}
    	
    	static boolean isAbove(String version) {
    		String[] s0 = CBK.split("_");
    		int i0 = Integer.parseInt(s0[0].substring(1));
    		int j0 = Integer.parseInt(s0[1]);
    		int k0 = Integer.parseInt(s0[2].substring(1));
    		
    		String[] s1 = version.split("_");
    		
    		int i1 = Integer.parseInt(s1[0].substring(1));
    		int j1 = Integer.parseInt(s1[1]);
    		int k1 = Integer.parseInt(s1[2].substring(1));
    		return i0 > i1 ? true : i0 == i1 ? (j0 > j1 ? true : j0 == j1 ? k0 >= k1  : false) : false;
    	}
           
    	static Class<?> getNMSClassByName(String name) {
    		if(subclasses){
    			if(name.equals("EnumScoreboardAction"))
    				name = "PacketPlayOutScoreboardScore$" + name;
    			else if(name.equals("EnumScoreboardHealthDisplay"))
    				name = "IScoreboardCriteria$" + name;
    		}
    		try {
    			return Class.forName("net.minecraft.server." + CBK + "." + name);
    		} catch (ClassNotFoundException ex) {
    			Bukkit.getLogger().info(ExceptionUtils.getStackTrace(ex));
    			return null;
    		}
    	}
            
    	static Class<?> getNMSClass(NMSClass clazz) {
    		return getNMSClassByName(clazz.name);
    	}
    	
    	static Object getField(Class<?> clazz, Object instance, String field) {
    		try {
    			Field f = clazz.getDeclaredField(field);
    			f.setAccessible(true);
    			return f.get(instance);
    		} catch (Exception ex) {
    			Bukkit.getLogger().info(ExceptionUtils.getStackTrace(ex));
    			return null;
    		}
    	}
    	
    	static void setField(Class<?> clazz, Object instance, String field, Object value) {
    		try {
    			Field f = clazz.getDeclaredField(field);
    			f.setAccessible(true);
    			f.set(instance, value);
    		} catch (Exception ex) {
    			Bukkit.getLogger().info(ExceptionUtils.getStackTrace(ex));
    		}
    	}
           
        static Object getHandle(Object obj) {
        	try {
        		Method method = obj.getClass().getDeclaredMethod("getHandle");
        		method.setAccessible(true);
        		return method.invoke(obj);
        	} catch (Exception ex) {
        		Bukkit.getLogger().info(ExceptionUtils.getStackTrace(ex));
        		return null;
        	}
        }
           
        static void sendPacket(Player player, Object... packets) {
        	try {
        		Object handle = getHandle(player);
        		Object connection = handle.getClass().getDeclaredField("playerConnection").get(handle);
        		Method send = connection.getClass().getDeclaredMethod("sendPacket", getNMSClassByName("Packet"));
        		for(Object packet : packets)
        			send.invoke(connection, packet);
        	} catch (Exception ex) {
        		Bukkit.getLogger().info(ExceptionUtils.getStackTrace(ex));
        	}
        }
    }
    
    public static void disband(Player player){
    	Scoreboard.scores.remove(player);
    	Scoreboard.getManager().getScoreboard(player).unregister();
    }
    
}
