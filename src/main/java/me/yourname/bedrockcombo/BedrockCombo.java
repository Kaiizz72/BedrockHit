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
    private final int cpsThresholdMs = 80;
    private final double comboY = 0.32;
    private final double comboXZ = 0.15;

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
        getLogger().info("BedrockProCombo đã bật!");
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
        long now = System.currentTimeMillis();
        long last = lastHitTime.getOrDefault(attackerId, 0L);
        long diff = now - last;

        if (diff <= cpsThresholdMs) {
            Vector dir = victim.getLocation().toVector().subtract(attacker.getLocation().toVector()).normalize();
            Vector kb = dir.multiply(comboXZ).setY(comboY);
            victim.setVelocity(kb);
        }

        lastHitTime.put(attackerId, now);
    }
}
