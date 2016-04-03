package cl.josedev.MultiCombo;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;

public class ComboUpdateTask extends BukkitRunnable {

	private static Map<UUID, Integer> tasks = new HashMap<UUID, Integer>();
	private final MultiCombo plugin;
	private final UUID playerId;
	
	public ComboUpdateTask(MultiCombo plugin, Player player) {
		this.plugin = plugin;
		this.playerId = player.getUniqueId();
	}
	
	public void run() {
		
		Player player = Bukkit.getServer().getPlayer(playerId);
		if (player == null) {
			cancel();
			return;
		}
		// let the purge handle this
		if (!player.isOnline()) {
			cancel();
			return;
		}
		
		Combo combo = plugin.getComboManager().getCombo(playerId);
		if (combo == null || combo.isExpired()) {
			plugin.getComboManager().removeMark(player);
			cancel();
			return;
		}
	}
	
	public static void run(final MultiCombo plugin, final Player player) {
		
		final BukkitScheduler sch = Bukkit.getScheduler();
		
		sch.scheduleSyncDelayedTask(plugin, new Runnable() {

			public void run() {
				if(!plugin.getComboManager().isMarked(player.getUniqueId()) || !player.isOnline()) {
					return;
				}
				
				UUID playerId = player.getUniqueId();
				Integer taskId = tasks.get(playerId);
				
				// A task is assigned to the player, or is currently being handled by the scheduler
				if (taskId != null && (sch.isQueued(taskId) || sch.isCurrentlyRunning(taskId))) {
					return;
				}
				
				taskId = new ComboUpdateTask(plugin, player).runTaskTimer(plugin, 0, 5).getTaskId();
				tasks.put(playerId, taskId);
			}
			
		});
	}
	
	public static void purgeFinished() {
        Iterator<Integer> iterator = tasks.values().iterator();
        BukkitScheduler s = Bukkit.getScheduler();

        // Loop over each task
        while (iterator.hasNext()) {
            int taskId = iterator.next();

            // Remove entry if task isn't running anymore
            if (!s.isQueued(taskId) && !s.isCurrentlyRunning(taskId)) {
                iterator.remove();
            }
        }
    }

	public static void cancelTasks(MultiCombo plugin) {
        Iterator<UUID> iterator = tasks.keySet().iterator();
        while (iterator.hasNext()) {
            UUID uuid = iterator.next();
            
            int taskId = tasks.get(uuid);
            BukkitScheduler s = Bukkit.getScheduler();

            if (s.isQueued(taskId) || s.isCurrentlyRunning(taskId)) {
                s.cancelTask(taskId);
            }

            iterator.remove();
        }
    }
}
