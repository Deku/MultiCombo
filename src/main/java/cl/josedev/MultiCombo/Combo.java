package cl.josedev.MultiCombo;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class Combo {
	
	private int amount;
	private double multiplier;
	private List<PotionEffect> effects = new ArrayList<PotionEffect>();
	
	public Combo(int amount, ConfigurationSection config) {
		this.amount = amount;
		this.multiplier = config.getDouble("multiplier");
		
		for (String key : config.getConfigurationSection("effects").getKeys(false)) {
			ConfigurationSection ms = config.getConfigurationSection("effects." + key);
			String effectId = key.toUpperCase();
			int duration = ms.getInt("duration");
			int level = ms.getInt("level");
			PotionEffectType type = PotionEffectType.getByName(effectId);
			
			effects.add(new PotionEffect(type, duration * 20/*ticks*/, level));
		}
	}
	
	public int getHitsAmount() {
		return this.amount;
	}
	
	public double getMultiplier() {
		return this.multiplier;
	}
	
	public void applyEffects(Player player) {
		for (PotionEffect effect : effects) {
			player.addPotionEffect(effect);
		}
	}
}
