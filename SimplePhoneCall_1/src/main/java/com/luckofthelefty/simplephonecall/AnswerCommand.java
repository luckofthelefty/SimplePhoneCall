package com.luckofthelefty.simplephonecall;

import de.maxhenkel.voicechat.api.Group;
import de.maxhenkel.voicechat.api.VoicechatServerApi;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class AnswerCommand implements CommandExecutor {

    private final CallManager callManager;
    private final VoicechatServerApi serverApi;
    private final RingtonePlayer ringtonePlayer;

    public AnswerCommand(CallManager callManager, VoicechatServerApi serverApi, RingtonePlayer ringtonePlayer) {
        this.callManager = callManager;
        this.serverApi = serverApi;
        this.ringtonePlayer = ringtonePlayer;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        Player target = (Player) sender;
        UUID targetId = target.getUniqueId();

        // Check if target is actually in a pending/active call
        if (!callManager.hasActiveCall(targetId)) {
            target.sendMessage("You don't have any incoming calls to answer.");
            return true;
        }

        // Find the other side of the call
        UUID callerId = callManager.getOtherParticipant(targetId);
        if (callerId == null) {
            target.sendMessage("You don't have any incoming calls to answer.");
            return true;
        }

        Player caller = target.getServer().getPlayer(callerId);
        if (caller == null || !caller.isOnline()) {
            target.sendMessage("The caller is no longer online.");
            callManager.endCall(targetId, callerId);
            return true;
        }

        // Stop the ringtone
        ringtonePlayer.stopRingtone(target);

        // Generate a random password for the private voice group
        String password = UUID.randomUUID().toString().substring(0, 8);

        // Create a private group
        Group group = serverApi.groupBuilder()
                .setName("Private Call: " + caller.getName() + " & " + target.getName())
                .setPassword(password)
                .setPersistent(false)
                .setType(Group.Type.ISOLATED)
                .build();

        // Add both players to the group
        serverApi.getConnectionOf(caller.getUniqueId()).setGroup(group);
        serverApi.getConnectionOf(target.getUniqueId()).setGroup(group);

        // Notify players
        caller.sendMessage(target.getName() + " answered the call! You are now in a private group.");
        target.sendMessage("You answered the call with " + caller.getName() + "! You are now in a private group.");

        // Remove the call from the CallManager for both participants
        // (If you want the call to remain tracked for an "/hangup" command, 
        // you could keep it or transition it to a separate "active calls" map.)
        callManager.endCall(targetId, callerId);

        return true;
    }
}
