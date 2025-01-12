package com.luckofthelefty.simplephonecall;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CallManager {

    private final Map<UUID, UUID> activeCalls = new HashMap<>();
    private final Map<UUID, Integer> callTimeoutTasks = new HashMap<>();

    public void sendCallRequest(Player caller, Player target, Runnable timeoutCallback) {
        activeCalls.put(target.getUniqueId(), caller.getUniqueId());

        // Schedule timeout for 20 seconds
        int taskId = Bukkit.getScheduler().scheduleSyncDelayedTask(
            Bukkit.getPluginManager().getPlugin("SimplePhoneCall"),
            () -> {
                if (activeCalls.containsKey(target.getUniqueId())) {
                    activeCalls.remove(target.getUniqueId());
                    timeoutCallback.run();
                }
            },
            20 * 20L // 20 seconds in ticks
        );

        callTimeoutTasks.put(target.getUniqueId(), taskId);
    }

    public UUID getCaller(UUID targetId) {
        return activeCalls.get(targetId);
    }

    public UUID getTarget(UUID callerId) {
        for (Map.Entry<UUID, UUID> entry : activeCalls.entrySet()) {
            if (entry.getValue().equals(callerId)) {
                return entry.getKey();
            }
        }
        return null;
    }

    public void removeCall(UUID targetId) {
        activeCalls.remove(targetId);

        // Cancel timeout task if it exists
        Integer taskId = callTimeoutTasks.remove(targetId);
        if (taskId != null) {
            Bukkit.getScheduler().cancelTask(taskId);
        }
    }

    public boolean hasActiveCall(UUID targetId) {
        return activeCalls.containsKey(targetId);
    }
}