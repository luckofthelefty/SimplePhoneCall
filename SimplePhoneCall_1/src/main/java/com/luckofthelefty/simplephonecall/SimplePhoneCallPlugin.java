package com.luckofthelefty.simplephonecall;

import de.maxhenkel.voicechat.api.BukkitVoicechatService;
import de.maxhenkel.voicechat.api.VoicechatServerApi;
import org.bukkit.plugin.java.JavaPlugin;

public class SimplePhoneCallPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("SimplePhoneCall plugin enabled!");

        // Initialize the CallManager
        CallManager callManager = new CallManager();
        
     // Initialize the RingtonePlayer
        RingtonePlayer ringtonePlayer = new RingtonePlayer(this);

        // Get the BukkitVoicechatService
        BukkitVoicechatService service = getServer()
                .getServicesManager()
                .load(BukkitVoicechatService.class);

        if (service != null) {
            // Register your VoicechatPlugin implementation
            service.registerPlugin(new SimplePhoneCallVoicePlugin() {
                @Override
                public void initialize(de.maxhenkel.voicechat.api.VoicechatApi api) {
                    // Register commands once the API is initialized
                    getLogger().info("Voicechat API initialized!");

                    // Retrieve the server API
                    if (api instanceof VoicechatServerApi serverApi) {
                        getLogger().info("Registering commands...");
                        getCommand("call").setExecutor(new CallCommand(callManager, ringtonePlayer));
                        getCommand("answer").setExecutor(new AnswerCommand(callManager, serverApi, ringtonePlayer));
                        getCommand("decline").setExecutor(new DeclineCommand(callManager, ringtonePlayer));
                        getCommand("hangup").setExecutor(new HangupCommand(callManager, serverApi));
                        getLogger().info("Commands registered successfully!");
                    } else {
                        getLogger().warning("Failed to cast VoicechatApi to VoicechatServerApi!");
                    }
                }

                @Override
                public String getPluginId() {
                    return "simplephonecall";
                }
            });

            getLogger().info("Successfully registered SimplePhoneCallVoicePlugin with Simple Voice Chat!");
        } else {
            getLogger().warning("Simple Voice Chat plugin not found! Make sure it's installed.");
            getPluginLoader().disablePlugin(this); // Safely disable the plugin
        }
    }

    @Override
    public void onDisable() {
        getLogger().info("SimplePhoneCall plugin disabled!");
    }
}