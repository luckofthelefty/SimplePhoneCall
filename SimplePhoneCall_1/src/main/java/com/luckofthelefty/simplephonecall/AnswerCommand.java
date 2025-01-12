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

        // 1. Check if target is in ANY call (pending OR accepted)
        if (!callManager.hasCall(targetId)) {
            target.sendMessage("You don't have any incoming calls to answer.");
            return true;
        }

        // 2. If the call is already accepted, let them know
        if (callManager.hasActiveCall(targetId)) {
            target.sendMessage("This call has already been accepted. Use /hangup instead.");
            return true;
        }

        // 3. Get the other participant
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

        // 4. Stop the ringtone for the target
        ringtonePlayer.stopRingtone(target);

        // 5. Mark the call as accepted for BOTH sides
        callManager.acceptCall(callerId, targetId);

        // 6. Create a private voice chat group
        String password = UUID.randomUUID().toString().substring(0, 8);
        Group group = serverApi.groupBuilder()
                .setName("Private Call: " + caller.getName() + " & " + target.getName())
                .setPassword(password)
                .setPersistent(false)
                .setType(Group.Type.ISOLATED)
                .build();

        // 7. Put both players in that group
        serverApi.getConnectionOf(caller.getUniqueId()).setGroup(group);
        serverApi.getConnectionOf(target.getUniqueId()).setGroup(group);

        // 8. Notify them
        caller.sendMessage(target.getName() + " answered the call! You are now in a private group.");
        target.sendMessage("You answered the call with " + caller.getName() + "! You are now in a private group.");

        // 9. Do NOT end the call. Let /hangup handle that later.
        return true;
    }
}
