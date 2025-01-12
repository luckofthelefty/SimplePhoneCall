# SimplePhoneCall Plugin

SimplePhoneCall is a Minecraft plugin that brings voice call functionality to your server using the Simple Voice Chat API. With this plugin, players can initiate private voice calls, answer incoming calls, decline unwanted calls, and hang up when they're done – just like in real life!

---

## 🌟 Features

- **Voice Calls**: Players can call each other privately in isolated voice groups.
- **Ringtone System**: A unique ringtone plays for the recipient while the call is ringing.
- **Answer and Decline Commands**:
  - `/answer`: Accept an incoming call and join the private voice group.
  - `/decline`: Decline an incoming call with a polite message.
- **Hangup Command**:
  - `/hangup`: Leave the call at any time and notify others in the group.
- **Automatic Timeout**: If a call isn't answered within 30 seconds, it automatically ends.

---

## 🔧 Commands

| Command        | Description                                       | Permission      |
|----------------|---------------------------------------------------|-----------------|
| `/call <name>` | Initiates a private voice call with the specified player. | None            |
| `/answer`      | Accepts an incoming call and joins the private voice group. | None            |
| `/decline`     | Declines an incoming call and notifies the caller. | None            |
| `/hangup`      | Leaves the call and disbands the voice group if you're the last participant. | None            |

---

## 🛠 Installation

1. Download the [Simple Voice Chat](https://modrinth.com/plugin/simple-voice-chat) plugin.
2. Download SimplePhoneCall from Modrinth.
3. Place both plugins into your server's `plugins` folder.
4. Restart your server.
5. Enjoy calling your friends in-game!

---

## 🔔 Ringtone System

When you receive a call, a custom ringtone plays to notify you. The ringtone repeats for 30 seconds or until the call is answered/declined.

---

## ⚙ Dependencies

- **Minecraft Spigot Server**: Version 1.21.4 or higher.
- **Simple Voice Chat**: Version 2.5.27 or higher.

---

## 🎉 How It Works

1. Use `/call <player>` to start a voice call.
2. The recipient hears a ringtone and can respond with `/answer` or `/decline`.
3. When the call is answered, a private voice group is created.
4. Use `/hangup` to leave the call at any time.

---

## 🚀 Future Updates

- Enhanced group features for multi-player calls.

---

## 🤝 Contribution

Feel free to contribute, report bugs, or suggest features via the GitHub repository. Pull requests are welcome!

---

## 📜 License

This project is licensed under the MIT License.
