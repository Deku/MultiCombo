package cl.josedev.MultiCombo;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.configuration.ConfigurationSection;

public class ComboManager {
	private MultiCombo plugin;
	public Map<Integer, Combo> combos = new HashMap<Integer, Combo>();
	
	public ComboManager(MultiCombo plugin) {
		this.plugin = plugin;
		
		// Load combos from config
		for (String key : plugin.getConfig().getConfigurationSection("combo").getKeys(false)) {
			ConfigurationSection comboConfig = plugin.getConfig().getConfigurationSection("combo." + key);
			Integer comboAmount = Integer.parseInt(key);
			
			combos.put(comboAmount, new Combo(comboAmount, comboConfig));
		}
	}
	
	public void purge() {
        Iterator<HitsChain> iterator = plugin.hitsCount.values().iterator();
        while (iterator.hasNext()) {
            HitsChain combo = iterator.next();
            Player player = Bukkit.getServer().getPlayer(combo.getPlayerId());

            if (player != null) {
            	if (combo.isExpired()) {
            		if (player.isOnline()) {
            			// Store the combo if it's the highest
            			if (combo.getHitCount() > plugin.getHighestCombo(player)) {
            				plugin.highestCombo.put(combo.getPlayerId(), combo.getHitCount());
            			}
            		}
            		
            		iterator.remove();
            	}
            } else {
            	// If no player is found, just remove the combo
            	iterator.remove();
            }
        }
    }
	
	public void mark(Player player, Entity victim) {
		long expireTime = System.currentTimeMillis() + (plugin.getConfig().getLong("comboDuration") * 1000);
		plugin.hitsCount.put(player.getUniqueId(), new HitsChain(player, victim, expireTime));
		ComboUpdateTask.run(plugin, player);
	}
	
	public void updateMark(Player p) {
		HitsChain combo = plugin.hitsCount.get(p.getUniqueId());
		long expireTime = System.currentTimeMillis() + (plugin.getConfig().getLong("comboDuration") * 1000);
		combo.increaseHitCount();
		combo.updateExpireTime(expireTime);
		
		plugin.hitsCount.put(p.getUniqueId(), combo);
	}
	
	public void removeMark(Player player) {
		HitsChain combo = plugin.hitsCount.get(player.getUniqueId());
		
		if (combo != null) {
			if (combo.getHitCount() > plugin.getHighestCombo(player)) {
				plugin.highestCombo.put(player.getUniqueId(), combo.getHitCount());
			}
		}
		
		plugin.hitsCount.remove(player.getUniqueId());
	}
	
	public HitsChain getCombo(UUID playerId) {
        HitsChain combo = plugin.hitsCount.get(playerId);

        if (combo == null || combo.isExpired()) {
            return null;
        }

        return combo;
    }
	
	public boolean isMarked(UUID playerId) {
		HitsChain combo = plugin.hitsCount.get(playerId);
		
		return (combo != null && !combo.isExpired());
	}
}
