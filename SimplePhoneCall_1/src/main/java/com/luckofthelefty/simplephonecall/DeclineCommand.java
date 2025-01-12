package com.luckofthelefty.simplephonecall;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class DeclineCommand implements CommandExecutor {

    private final CallManager callManager;
    private final RingtonePlayer ringtonePlayer;

    public DeclineCommand(CallManager callManager, RingtonePlayer ringtonePlayer) {
        this.callManager = callManager;
        this.ringtonePlayer = ringtonePlayer;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;
        UUID playerId = player.getUniqueId();

        // 1. Check if player is in ANY call at all (pending OR accepted)
        if (!callManager.hasCall(playerId)) {
            player.sendMessage("You don't have any incoming calls to decline.");
            return true;
        }

        // 2. If the call has already been accepted, instruct them to hang up instead
        if (callManager.hasActiveCall(playerId)) {
            player.sendMessage("That call has already been accepted. Use /hangup instead.");
            return true;
        }

        // 3. This is a pending call. Find the other participant
        UUID otherId = callManager.getOtherParticipant(playerId);
        if (otherId == null) {
            player.sendMessage("You don't have any incoming calls to decline.");
            return true;
        }

        // 4. Notify the other side
        Player otherPlayer = player.getServer().getPlayer(otherId);
        if (otherPlayer != null && otherPlayer.isOnline()) {
            otherPlayer.sendMessage(player.getName() + " declined your call.");
        }

        // 5. Stop the ringtone on the declining player's side
        ringtonePlayer.stopRingtone(player);

        // 6. End the call for both participants
        callManager.endCall(playerId, otherId);

        player.sendMessage("You declined the call.");
        return true;
    }
}
