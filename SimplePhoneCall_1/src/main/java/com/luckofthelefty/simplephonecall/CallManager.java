package com.luckofthelefty.simplephonecall;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CallManager {

    /**
     * Holds information about a call for a single player:
     *  - who they're in a call with (`otherId`),
     *  - whether it's accepted (`accepted`).
     */
    private static class CallInfo {
        private final UUID otherId;
        private boolean accepted;

        public CallInfo(UUID otherId, boolean accepted) {
            this.otherId = otherId;
            this.accepted = accepted;
        }

        public UUID getOtherId() {
            return otherId;
        }

        public boolean isAccepted() {
            return accepted;
        }

        public void setAccepted(boolean accepted) {
            this.accepted = accepted;
        }
    }

    // We'll map each player's UUID to their CallInfo
    private final Map<UUID, CallInfo> calls = new HashMap<>();

    // Keep track of scheduled tasks for call timeouts
    private final Map<UUID, Integer> callTimeoutTasks = new HashMap<>();

    /**
     * Send a call request from `caller` to `target`. This is initially "not accepted."
     */
    public void sendCallRequest(Player caller, Player target, Runnable timeoutCallback) {
        UUID callerId = caller.getUniqueId();
        UUID targetId = target.getUniqueId();

        // Mark both as having a pending call (accepted=false)
        calls.put(callerId, new CallInfo(targetId, false));
        calls.put(targetId, new CallInfo(callerId, false));

        // Schedule a 20-second timeout
        int taskId = Bukkit.getScheduler().scheduleSyncDelayedTask(
                Bukkit.getPluginManager().getPlugin("SimplePhoneCall"),
                () -> {
                    // Check if the target still has a pending call from the caller
                    CallInfo info = calls.get(targetId);
                    if (info != null && info.getOtherId().equals(callerId) && !info.isAccepted()) {
                        // The call was never accepted within 20s, end it
                        endCall(callerId, targetId);
                        timeoutCallback.run();
                    }
                },
                20L * 20 // 20 seconds in ticks
        );

        callTimeoutTasks.put(targetId, taskId);
    }

    /**
     * Accept the call for both sides (meaning it's now "active").
     */
    public void acceptCall(UUID callerId, UUID targetId) {
        // If either side has an entry, mark it accepted
        if (calls.containsKey(callerId)) {
            calls.get(callerId).setAccepted(true);
        }
        if (calls.containsKey(targetId)) {
            calls.get(targetId).setAccepted(true);
        }

        // Cancel any timeout tasks if they're still running
        Integer taskA = callTimeoutTasks.remove(callerId);
        if (taskA != null) {
            Bukkit.getScheduler().cancelTask(taskA);
        }
        Integer taskB = callTimeoutTasks.remove(targetId);
        if (taskB != null) {
            Bukkit.getScheduler().cancelTask(taskB);
        }
    }

    /**
     * True if this player has *any* call entry (pending or accepted).
     */
    public boolean hasCall(UUID playerId) {
        return calls.containsKey(playerId);
    }

    /**
     * True if this player has a call *and* it has been accepted.
     */
    public boolean hasActiveCall(UUID playerId) {
        CallInfo info = calls.get(playerId);
        return (info != null && info.isAccepted());
    }

    /**
     * Returns the UUID of the other participant in the player's call (or null if none).
     */
    public UUID getOtherParticipant(UUID playerId) {
        CallInfo info = calls.get(playerId);
        return (info == null) ? null : info.getOtherId();
    }

    /**
     * End the call for both players (removes from map and cancels timeouts).
     */
    public void endCall(UUID a, UUID b) {
        calls.remove(a);
        calls.remove(b);

        Integer taskA = callTimeoutTasks.remove(a);
        if (taskA != null) {
            Bukkit.getScheduler().cancelTask(taskA);
        }
        Integer taskB = callTimeoutTasks.remove(b);
        if (taskB != null) {
            Bukkit.getScheduler().cancelTask(taskB);
        }
    }
}
