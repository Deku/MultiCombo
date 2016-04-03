package cl.josedev.MultiCombo;

import java.util.Iterator;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class ComboManager {
	private MultiCombo plugin;
	
	public ComboManager(MultiCombo plugin) {
		this.plugin = plugin;
	}
	
	public void purge() {
        Iterator<Combo> iterator = plugin.hitsCount.values().iterator();
        while (iterator.hasNext()) {
            Combo combo = iterator.next();
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
		plugin.hitsCount.put(player.getUniqueId(), new Combo(player, victim, expireTime));
		ComboUpdateTask.run(plugin, player);
	}
	
	public void updateMark(Player p) {
		Combo combo = plugin.hitsCount.get(p.getUniqueId());
		long expireTime = System.currentTimeMillis() + (plugin.getConfig().getLong("comboDuration") * 1000);
		combo.increaseHitCount();
		combo.updateExpireTime(expireTime);
		
		plugin.hitsCount.put(p.getUniqueId(), combo);
	}
	
	public void removeMark(Player player) {
		Combo combo = plugin.hitsCount.get(player.getUniqueId());
		
		if (combo != null) {
			if (combo.getHitCount() > plugin.getHighestCombo(player)) {
				plugin.highestCombo.put(player.getUniqueId(), combo.getHitCount());
			}
		}
		
		plugin.hitsCount.remove(player.getUniqueId());
	}
	
	public Combo getCombo(UUID playerId) {
        Combo combo = plugin.hitsCount.get(playerId);

        if (combo == null || combo.isExpired()) {
            return null;
        }

        return combo;
    }
	
	public boolean isMarked(UUID playerId) {
		Combo combo = plugin.hitsCount.get(playerId);
		
		return (combo != null && !combo.isExpired());
	}
}
