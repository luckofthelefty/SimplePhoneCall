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

        Player target = (Player) sender;
        UUID callerId = callManager.getCaller(target.getUniqueId());

        if (callerId == null) {
            target.sendMessage("You don't have any incoming calls to decline.");
            return true;
        }

        Player caller = target.getServer().getPlayer(callerId);

        if (caller != null && caller.isOnline()) {
            caller.sendMessage(target.getName() + " declined your call.");
        }

        // Stop the ringtone for the target
        ringtonePlayer.stopRingtone(target);

        // Remove the call request
        callManager.removeCall(target.getUniqueId());
        target.sendMessage("You declined the call.");

        return true;
    }
}