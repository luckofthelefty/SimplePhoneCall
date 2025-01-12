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

        // Check if the player is in a call/pending call at all
        if (!callManager.hasActiveCall(playerId)) {
            player.sendMessage("You don't have any incoming calls to decline.");
            return true;
        }

        // Find the other participant from the map
        UUID otherId = callManager.getOtherParticipant(playerId);
        if (otherId == null) {
            // Shouldn't happen if hasActiveCall(...) was true, but just in case
            player.sendMessage("You don't have any incoming calls to decline.");
            return true;
        }

        // Notify the other side
        Player otherPlayer = player.getServer().getPlayer(otherId);
        if (otherPlayer != null && otherPlayer.isOnline()) {
            otherPlayer.sendMessage(player.getName() + " declined your call.");
        }

        // Stop the ringtone on the declining player's side
        ringtonePlayer.stopRingtone(player);

        // End the call for both participants
        callManager.endCall(playerId, otherId);

        player.sendMessage("You declined the call.");

        return true;
    }
}
