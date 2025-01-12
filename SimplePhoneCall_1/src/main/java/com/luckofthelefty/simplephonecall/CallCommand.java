package com.luckofthelefty.simplephonecall;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CallCommand implements CommandExecutor {

    private final CallManager callManager;
    private final RingtonePlayer ringtonePlayer;

    public CallCommand(CallManager callManager, RingtonePlayer ringtonePlayer) {
        this.callManager = callManager;
        this.ringtonePlayer = ringtonePlayer;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        Player caller = (Player) sender;

        if (args.length < 1) {
            caller.sendMessage("Usage: /call <playername>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if (target == null || !target.isOnline()) {
            caller.sendMessage("The player '" + args[0] + "' is not online.");
            return true;
        }

        if (callManager.hasActiveCall(target.getUniqueId())) {
            caller.sendMessage("That player is already receiving a call.");
            return true;
        }

        if (callManager.hasActiveCall(caller.getUniqueId())) {
            caller.sendMessage("You are already in a call!");
            return true;
        }

        // Handle call timeout and ringtone logic
        callManager.sendCallRequest(caller, target, () -> {
            caller.sendMessage(target.getName() + " isn't available.");
            ringtonePlayer.stopRingtone(target); // Stop ringtone on timeout
        });

        target.sendMessage(caller.getName() + " is calling you... type /answer to accept or /decline to reject.");
        caller.sendMessage("Calling " + target.getName() + "...");

        try {
            // Play the ringtone for the target
            ringtonePlayer.playRingtone(target);
        } catch (Exception e) {
            caller.sendMessage("An error occurred while playing the ringtone.");
            e.printStackTrace();
        }

        return true;
    }
}