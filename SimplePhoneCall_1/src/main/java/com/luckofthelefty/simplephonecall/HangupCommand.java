package com.luckofthelefty.simplephonecall;

import de.maxhenkel.voicechat.api.VoicechatServerApi;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class HangupCommand implements CommandExecutor {

    private final CallManager callManager;
    private final VoicechatServerApi serverApi;

    public HangupCommand(CallManager callManager, VoicechatServerApi serverApi) {
        this.callManager = callManager;
        this.serverApi = serverApi;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;
        UUID callerId = callManager.getCaller(player.getUniqueId());
        UUID targetId = callManager.getTarget(player.getUniqueId());

        // Handle cases where the player is both the caller and the target (calling themselves)
        if (callerId == null && targetId == null && callManager.hasActiveCall(player.getUniqueId())) {
            callerId = player.getUniqueId();
        }

        if (callerId == null && targetId == null) {
            player.sendMessage("You are not in an active call.");
            return true;
        }

        UUID otherPlayerId = (callerId != null && !callerId.equals(player.getUniqueId())) ? callerId : targetId;
        Player otherPlayer = (otherPlayerId != null) ? player.getServer().getPlayer(otherPlayerId) : null;

        if (otherPlayer != null && otherPlayer.isOnline() && !otherPlayer.equals(player)) {
            otherPlayer.sendMessage(player.getName() + " hung up the call.");
        }

        // Remove the call and leave the voice chat group
        callManager.removeCall(player.getUniqueId());
        if (otherPlayerId != null) {
            callManager.removeCall(otherPlayerId);
        }

        // Clear the group from both players
        if (serverApi.getConnectionOf(player.getUniqueId()) != null) {
            serverApi.getConnectionOf(player.getUniqueId()).setGroup(null);
        }

        if (otherPlayerId != null && serverApi.getConnectionOf(otherPlayerId) != null) {
            serverApi.getConnectionOf(otherPlayerId).setGroup(null);
        }

        player.sendMessage("You hung up the call.");

        return true;
    }
}