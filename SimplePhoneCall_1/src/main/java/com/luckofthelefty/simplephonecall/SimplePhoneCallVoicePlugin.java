package com.luckofthelefty.simplephonecall;

import de.maxhenkel.voicechat.api.VoicechatApi;
import de.maxhenkel.voicechat.api.VoicechatPlugin;

public class SimplePhoneCallVoicePlugin implements VoicechatPlugin {

    private VoicechatApi voicechatApi;

    @Override
    public String getPluginId() {
        // Use a unique identifier for your plugin
        return "simplephonecall";
    }

    public void initialize(VoicechatApi api) {
        this.voicechatApi = api;

        // Example: Register a command (if applicable)
        System.out.println("SimplePhoneCallVoicePlugin initialized with API!");

        // Register custom group or event handling logic here
    }

    // Optional: Provide a way to access the API in other parts of your plugin
    public VoicechatApi getApi() {
        return voicechatApi;
    }
}