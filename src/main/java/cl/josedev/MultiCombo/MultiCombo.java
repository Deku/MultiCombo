package cl.josedev.MultiCombo;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class MultiCombo extends JavaPlugin {
	
	public Map<UUID, HitsChain> hitsCount = new HashMap<UUID, HitsChain>();
	public Map<UUID, Integer> highestCombo = new HashMap<UUID, Integer>();
	public ComboManager manager;
	public static Integer NO_HITS = 0;
	public String TAG;
	public FileConfiguration language;
	public boolean meleeOnly;
	
	@Override
	public void onEnable() {
		this.saveDefaultConfig();
		
		copyLanguages();
		loadLanguage();
		
		this.manager = new ComboManager(this);
		this.TAG = getConfig().getString("tag");
		this.meleeOnly = getConfig().getBoolean("meleeOnly");
		
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
				p.sendMessage(TAG + language.getString("highestCombo").replace("%HITS%", String.valueOf(getHighestCombo(p))));
				
				return true;
			} else {
				sender.sendMessage(TAG + ChatColor.RED + language.getString("onlyPlayers"));
				
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
	
	private void copyLanguages() {
		String[] languages = {"en", "es"};
		
		for (String lang : languages) {
			String filename = "lang_" + lang + ".yml";
			File langFile = new File(getDataFolder(), filename);
			
			if (!langFile.exists()) {
				try {
					langFile.createNewFile();
					InputStreamReader providedLangFile = new InputStreamReader(this.getResource(filename), "UTF-8");
					
					if (providedLangFile != null) {
						YamlConfiguration  providedLang = YamlConfiguration.loadConfiguration(providedLangFile);
						providedLang.save(langFile);
					}
				} catch (IOException e) {
					getLogger().severe("Don't have the permissions to write into the folder. Aborting loading default languages!");
				}
			}
		}
	}
	
	private void loadLanguage() {
		String lang = getConfig().getString("language");
		File langFile = new File(getDataFolder(), "lang_" + lang.trim() + ".yml");
		
		if (langFile.exists()) {
			this.language = YamlConfiguration.loadConfiguration(langFile);
		} else {
			getLogger().info("Language file not found! Loading english as default...");
			
			langFile = new File(getDataFolder(), "lang_en.yml");
			this.language = YamlConfiguration.loadConfiguration(langFile);
		}
	}
}
