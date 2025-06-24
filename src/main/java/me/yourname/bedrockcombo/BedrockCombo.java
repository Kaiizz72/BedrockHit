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
    private final int cpsThresholdMs = 200;
    private final double comboY = 0.5;
    private final double comboXZ = 0.4;

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

        lastHitTime.put(attackerId, now);

        if (diff <= cpsThresholdMs) {
            // Huỷ knockback mặc định
            event.setCancelled(true);
            victim.damage(event.getDamage());

            // Tính knockback hướng + bay lên
            Vector dir = victim.getLocation().toVector().subtract(attacker.getLocation().toVector()).normalize();
            Vector kb = dir.multiply(comboXZ).setY(comboY);

            // Delay 1 tick để tránh bị ghi đè bởi knockback gốc
            Bukkit.getScheduler().runTaskLater(this, () -> {
                victim.setVelocity(kb);
            }, 1L);

            attacker.sendMessage("§aCombo hit!");
        }
    }
}