package com.luckofthelefty.simplephonecall;

import org.bukkit.Instrument;
import org.bukkit.Note;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class RingtonePlayer {

    // Notes for the melody (E-flat major scale)
    private final Note[] melodyNotes = {
        Note.natural(0, Note.Tone.E),   // E♭
        Note.natural(0, Note.Tone.B),   // B♭
        Note.natural(0, Note.Tone.G),   // G
        Note.natural(0, Note.Tone.B),   // B♭
        Note.natural(0, Note.Tone.E),   // E♭
        Note.natural(0, Note.Tone.B)    // B♭
    };

    // Delays between notes (in ticks) based on timing in 4/4 at quarter note = 108 BPM
    private final int[] delays = {
        11, // E♭ (Quarter note)
        6,  // B♭ (Eighth note)
        11, // G (Quarter note)
        6,  // B♭ (Eighth note)
        11, // E♭ (Quarter note)
        11  // B♭ (Quarter note)
    };

    private final JavaPlugin plugin;
    private final Map<Player, BukkitRunnable> activeRingtones = new HashMap<>();

    public RingtonePlayer(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void playRingtone(Player target) {
        if (activeRingtones.containsKey(target)) {
            return; // Ringtone is already playing for this player
        }

        BukkitRunnable ringtoneTask = new BukkitRunnable() {
            private int index = 0;
            private int elapsedTicks = 0;
            private int repeatCount = 0;

            @Override
            public void run() {
                // Stop after 30 seconds (600 ticks)
                if (repeatCount >= (30 * 20) / getTotalDelayTicks()) {
                    stopRingtone(target);
                    cancel();
                    return;
                }

                // Play the melody note
                if (elapsedTicks == 0 && index < melodyNotes.length) {
                    target.playNote(target.getLocation(), Instrument.PIANO, melodyNotes[index]);
                }

                elapsedTicks++;
                if (elapsedTicks >= delays[index]) {
                    elapsedTicks = 0;
                    index++;
                }

                // Reset sequence if complete
                if (index >= melodyNotes.length) {
                    index = 0;
                    repeatCount++;
                }
            }

            private int getTotalDelayTicks() {
                int total = 0;
                for (int delay : delays) {
                    total += delay;
                }
                return total;
            }
        };

        activeRingtones.put(target, ringtoneTask);
        ringtoneTask.runTaskTimer(plugin, 0L, 1L);
    }

    public void stopRingtone(Player target) {
        BukkitRunnable ringtoneTask = activeRingtones.remove(target);
        if (ringtoneTask != null) {
            ringtoneTask.cancel();
        }
    }
}