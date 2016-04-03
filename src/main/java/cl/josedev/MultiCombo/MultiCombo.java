package cl.josedev.MultiCombo;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class MultiCombo extends JavaPlugin {
	
	public Map<UUID, Combo> hitsCount = new HashMap<UUID, Combo>();
	public Map<UUID, Integer> highestCombo = new HashMap<UUID, Integer>();
	public ComboManager manager;
	public static Integer NO_HITS = 0;
	public static String TAG = ChatColor.GOLD + "[COMBO] " + ChatColor.YELLOW;
	
	
	@Override
	public void onEnable() {
		this.manager = new ComboManager(this);
		
		getConfig().options().copyDefaults(true);
		saveConfig();
		getServer().getPluginManager().registerEvents(new ComboListener(this), this);
		
		Bukkit.getScheduler().runTaskTimer(this, new Runnable() {
            public void run() {
                getComboManager().purge();
                ComboUpdateTask.purgeFinished();
            }
        }, 3600, 3600);
		
		getLogger().info("MultiCombo activated!");
	}
	
	@Override
	public void onDisable() {
		ComboUpdateTask.cancelTasks(this);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (cmd.getName().equalsIgnoreCase("combo")) {
			if (sender instanceof Player) {
				Player p = (Player)sender;
				p.sendMessage(TAG + "Your highest combo on this session was " + getHighestCombo(p) + " hits");
				
				return true;
			} else {
				sender.sendMessage(TAG + ChatColor.RED + "Only players can use this command!");
				
				return true;
			}
		}
		
		return false;
	}
	
	public ComboManager getComboManager() {
		return manager;
	}
	
	public int getHighestCombo(Player player) {
		return highestCombo.containsKey(player.getUniqueId()) ? highestCombo.get(player.getUniqueId()) : 0; 
	}
}
