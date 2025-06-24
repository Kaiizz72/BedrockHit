package me.yourname.bedrockcombo;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.UUID;

public class BedrockCombo extends JavaPlugin implements Listener {

    private final HashMap<UUID, Long> lastHitTime = new HashMap<>();
    private final HashMap<UUID, UUID> lastVictimMap = new HashMap<>();
    private final HashMap<UUID, Integer> comboCount = new HashMap<>();

    private final int cpsThresholdMs = 120;
    private final int comboRequirement = 3;
    private final double comboY = 0.21; // bay nhẹ hơn tí giống trong video
    private final double comboXZ = 0.27; // đẩy ngang rõ hơn như Hive

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
        getLogger().info("BedrockProCombo (Hive-style tuned) đã bật!");
    }

    @Override
    public void onDisable() {
        getLogger().info("BedrockProCombo đã tắt!");
    }

    @EventHandler
    public void onComboHit(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player victim)) return;
        if (!(event.getDamager() instanceof Player attacker)) return;

        UUID attackerId = attacker.getUniqueId();
        UUID victimId = victim.getUniqueId();
        long now = System.currentTimeMillis();
        long last = lastHitTime.getOrDefault(attackerId, 0L);
        long diff = now - last;

        lastHitTime.put(attackerId, now);

        UUID lastVictim = lastVictimMap.get(attackerId);
        if (!victimId.equals(lastVictim) || diff > cpsThresholdMs) {
            comboCount.put(attackerId, 1);
        } else {
            comboCount.put(attackerId, comboCount.getOrDefault(attackerId, 1) + 1);
        }

        lastVictimMap.put(attackerId, victimId);

        if (comboCount.get(attackerId) >= comboRequirement) {
            Vector dir = victim.getLocation().toVector().subtract(attacker.getLocation().toVector()).setY(0).normalize();
            Vector kb = dir.multiply(comboXZ).setY(comboY);

            Bukkit.getScheduler().runTaskLater(this, () -> {
                victim.setVelocity(kb);
            }, 1L);
        }
    }
}
