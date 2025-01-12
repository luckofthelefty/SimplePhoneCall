package com.luckofthelefty.simplephonecall;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CallManager {

    // Instead of mapping only target -> caller, we map both participants to each other.
    // So if A calls B, we do activeCalls.put(A, B) and activeCalls.put(B, A).
    private final Map<UUID, UUID> activeCalls = new HashMap<>();
    private final Map<UUID, Integer> callTimeoutTasks = new HashMap<>();

    /**
     * Sends a call request from caller to target with a 20-second timeout.
     */
    public void sendCallRequest(Player caller, Player target, Runnable timeoutCallback) {
        UUID callerId = caller.getUniqueId();
        UUID targetId = target.getUniqueId();

        // Put both directions in the map to track the pending call
        activeCalls.put(callerId, targetId);
        activeCalls.put(targetId, callerId);

        // Schedule timeout for 20 seconds to remove the pending call if not answered
        int taskId = Bukkit.getScheduler().scheduleSyncDelayedTask(
                Bukkit.getPluginManager().getPlugin("SimplePhoneCall"),
                () -> {
                    // If still in the map, remove it and run the callback
                    if (activeCalls.containsKey(targetId) && activeCalls.get(targetId).equals(callerId)) {
                        endCall(callerId, targetId);
                        timeoutCallback.run();
                    }
                },
                20 * 20L // 20 seconds in ticks
        );

        callTimeoutTasks.put(targetId, taskId);
    }

    /**
     * Gets the other participant in a call, or null if none.
     */
    public UUID getOtherParticipant(UUID playerId) {
        return activeCalls.get(playerId);
    }

    /**
     * Check if the player is currently in a call (pending or active).
     */
    public boolean hasActiveCall(UUID playerId) {
        return activeCalls.containsKey(playerId);
    }

    /**
     * Ends the call for both players (removes entries from the map).
     */
    public void endCall(UUID playerA, UUID playerB) {
        // Remove the mapping in both directions
        activeCalls.remove(playerA);
        activeCalls.remove(playerB);

        // Cancel any timeout tasks if they exist
        Integer taskA = callTimeoutTasks.remove(playerA);
        if (taskA != null) {
            Bukkit.getScheduler().cancelTask(taskA);
        }
        Integer taskB = callTimeoutTasks.remove(playerB);
        if (taskB != null) {
            Bukkit.getScheduler().cancelTask(taskB);
        }
    }
}
