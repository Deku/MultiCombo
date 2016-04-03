package cl.josedev.MultiCombo;

import org.bukkit.Effect;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ComboListener implements Listener {

	private MultiCombo plugin;
	
	public ComboListener(MultiCombo multiCombo) {
		this.plugin = multiCombo;
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player p = event.getPlayer();
		plugin.hitsCount.remove(p.getUniqueId());
		plugin.highestCombo.remove(p.getUniqueId());
	}
	
	@EventHandler
	public void onPlayerHit(EntityDamageByEntityEvent event) {
		Entity attacker = event.getDamager();
		Entity victim = event.getEntity();
		double dmg = event.getDamage();
		
		if (attacker instanceof Player) {
			Player player = (Player) attacker;
			
			if (plugin.hitsCount.containsKey(player.getUniqueId())) {
				// Count the hit
				plugin.manager.updateMark(player);
				
				// Check if a combo was made
				Combo combo = plugin.hitsCount.get(player.getUniqueId());
				
				// Combos can only be made on one target
				if (combo.getVictimId() == victim.getUniqueId()) {
					int hits = combo.getHitCount();

					switch (hits) {
						case 5:
							player.sendMessage(MultiCombo.TAG + plugin.getConfig().getString("messages.combo5"));
							player.getWorld().playEffect(player.getLocation(), Effect.SNOWBALL_BREAK, 10);
							player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 60, 1));
							event.setDamage(dmg * plugin.getConfig().getDouble("damageMultipliers.combo5"));
							break;
						case 10:
							player.sendMessage(MultiCombo.TAG + plugin.getConfig().getString("messages.combo10"));
							player.getWorld().playEffect(player.getLocation(), Effect.FLAME, 10);
							player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 60, 1));
							event.setDamage(dmg * plugin.getConfig().getDouble("damageMultipliers.combo10"));
							break;
						case 20:
							player.sendMessage(MultiCombo.TAG + plugin.getConfig().getString("messages.combo20"));
							player.getWorld().playEffect(player.getLocation(), Effect.FIREWORKS_SPARK, 10);
							player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 60, 1));
							event.setDamage(dmg * plugin.getConfig().getDouble("damageMultipliers.combo20"));
							break;
						case 50:
							player.sendMessage(MultiCombo.TAG + plugin.getConfig().getString("messages.combo50"));
							player.getWorld().playEffect(player.getLocation(), Effect.EXPLOSION_LARGE, 10);
							player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 60, 1));
							event.setDamage(dmg * plugin.getConfig().getDouble("damageMultipliers.combo50"));
							break;	
					}
				} else {
					// A new combo is generated
					plugin.manager.removeMark(player);
					plugin.manager.mark(player, victim);
				}
			} else {
				plugin.manager.mark(player, victim);
			}	
		}
		
		if (victim instanceof Player) {
			Player player = (Player) victim;
			
			if (plugin.hitsCount.containsKey(player.getUniqueId())) {
				plugin.manager.removeMark(player);
			}
		}
	}

}
