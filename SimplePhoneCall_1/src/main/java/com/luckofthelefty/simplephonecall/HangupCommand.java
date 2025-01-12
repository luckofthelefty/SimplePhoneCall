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
        UUID playerId = player.getUniqueId();

        // 1. Check if the player is in ANY call (accepted or pending)
        if (!callManager.hasCall(playerId)) {
            player.sendMessage("You are not in a call.");
            return true;
        }

        // 2. Find the other participant
        UUID otherId = callManager.getOtherParticipant(playerId);
        if (otherId == null) {
            player.sendMessage("Could not find the other participant.");
            return true;
        }

        // 3. Notify the other participant, if online
        Player otherPlayer = player.getServer().getPlayer(otherId);
        if (otherPlayer != null && otherPlayer.isOnline()) {
            otherPlayer.sendMessage(player.getName() + " has hung up the call.");
        }

        // 4. End the call for both sides
        callManager.endCall(playerId, otherId);

        // 5. Remove them from voicechat group if needed
        if (serverApi.getConnectionOf(playerId) != null) {
            serverApi.getConnectionOf(playerId).setGroup(null);
        }
        if (serverApi.getConnectionOf(otherId) != null) {
            serverApi.getConnectionOf(otherId).setGroup(null);
        }

        player.sendMessage("You hung up the call.");
        return true;
    }
}
