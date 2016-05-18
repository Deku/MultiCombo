package cl.josedev.MultiCombo;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class ComboListener implements Listener {

	private MultiCombo plugin;
	private List<Material> meleeWeapons = new ArrayList<Material>();
	
	public ComboListener(MultiCombo multiCombo) {
		this.plugin = multiCombo;
		
		List<String> sources = new ArrayList<String>();
		sources.addAll(plugin.getConfig().getStringList("meleeWhitelist"));
		
		if (sources.contains("hand")) {
			meleeWeapons.add(Material.AIR);
		}
		
		if (sources.contains("shears")) {
			meleeWeapons.add(Material.SHEARS);
		}
		
		if (sources.contains("sword")) {
			meleeWeapons.add(Material.WOOD_SWORD);
			meleeWeapons.add(Material.STONE_SWORD);
			meleeWeapons.add(Material.IRON_SWORD);
			meleeWeapons.add(Material.DIAMOND_SWORD);
		}
		
		if (sources.contains("axe")) {
			meleeWeapons.add(Material.WOOD_AXE);
			meleeWeapons.add(Material.STONE_AXE);
			meleeWeapons.add(Material.IRON_AXE);
			meleeWeapons.add(Material.DIAMOND_AXE);
		}
		
		if (sources.contains("pickaxe")) {
			meleeWeapons.add(Material.WOOD_PICKAXE);
			meleeWeapons.add(Material.STONE_PICKAXE);
			meleeWeapons.add(Material.IRON_PICKAXE);
			meleeWeapons.add(Material.DIAMOND_PICKAXE);
		}
		
		if (sources.contains("spade")) {
			meleeWeapons.add(Material.WOOD_SPADE);
			meleeWeapons.add(Material.STONE_SPADE);
			meleeWeapons.add(Material.IRON_SPADE);
			meleeWeapons.add(Material.DIAMOND_SPADE);
		}
		
		if (sources.contains("hoe")) {
			meleeWeapons.add(Material.WOOD_HOE);
			meleeWeapons.add(Material.STONE_HOE);
			meleeWeapons.add(Material.IRON_HOE);
			meleeWeapons.add(Material.DIAMOND_HOE);
		}
	}
	
	private boolean isMeleeWeapon(ItemStack item) {
		return meleeWeapons.contains(item.getType());
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
		double dmg = event.getFinalDamage();
		
		if (attacker instanceof Player) {
			Player player = (Player) attacker;
			
			if (plugin.meleeOnly) {
				ItemStack weapon = player.getInventory().getItemInMainHand();
				
				if (!isMeleeWeapon(weapon)) {
					return;
				}
			}
			
			if (plugin.hitsCount.containsKey(player.getUniqueId())) {
				// Count the hit
				plugin.manager.updateMark(player);
				
				// Get hit chain from memory
				HitsChain pcombo = plugin.hitsCount.get(player.getUniqueId());
				
				// Combos can only be made on one target
				if (pcombo.getVictimId() == victim.getUniqueId()) {
					int hits = pcombo.getHitCount();
					Combo combo = plugin.manager.combos.get(hits);
					
					// A combo was made
					if (combo != null) {
						player.sendMessage(plugin.TAG + plugin.language.getString("combo" + hits));
						player.spigot().playEffect(victim.getLocation().add(0, 1, 0),
													Effect.FLAME,
													0,
													0, 
													0, 0, 0, 
													0.2f, 
													50, 
													50);
						combo.applyEffects(player);
						event.setDamage(dmg * combo.getMultiplier());
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
