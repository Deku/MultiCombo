package cl.josedev.MultiCombo;

import java.util.UUID;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.NumberConversions;

public class HitsChain {
	
	private long hitTime = System.currentTimeMillis();
	private long expireTime;
	private UUID playerId;
	private UUID victimId;
	private int hitCount = 1;
	
	public HitsChain (Player player, Entity victim, long expireTime) {
		this.playerId = player.getUniqueId();
		this.victimId = victim.getUniqueId();
		this.expireTime = expireTime;
	}
	
	public long getHitTime() {
        return hitTime;
    }
	
	public int getHitCount() {
		return hitCount;
	}
	
	public void increaseHitCount() {
		this.hitCount = hitCount + 1;
	}

    public long getExpireTime() {
        return expireTime;
    }

    public UUID getVictimId() {
        return victimId;
    }
    
    public UUID getPlayerId() {
    	return playerId;
    }
    
    public int getDuration() {
        long currentTime = System.currentTimeMillis();
        return expireTime > currentTime ? NumberConversions.ceil((expireTime - currentTime) / 1000D) : 0;
    }

    public boolean isExpired() {
        return getDuration() < 1;
    }

	public void updateExpireTime(long newTime) {
		this.expireTime = newTime;
	}
}