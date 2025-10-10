package com.dalesmithwebdev.galaxia.utility;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import java.util.ArrayList;

/**
 * Manages sound effects for the game, including loading and playback.
 * Supports both single sounds and numbered sound variations that play randomly.
 */
public class SoundManager {
    // Explosion sounds (multiple variations)
    private static ArrayList<Sound> explosionSounds = new ArrayList<>();

    // HitHurt sounds (multiple variations)
    private static ArrayList<Sound> hitHurtSounds = new ArrayList<>();

    // Laser shoot sounds (multiple variations)
    private static ArrayList<Sound> laserShootSounds = new ArrayList<>();

    // Pickup sounds (multiple variations)
    private static ArrayList<Sound> pickupSounds = new ArrayList<>();

    // Single sounds
    private static Sound pauseSound;
    private static Sound selectSound;
    private static Sound warpInSound;

    private static float volume = 0.5f; // Default volume

    /**
     * Load all sound effects from the assets/sounds directory.
     * Call this once during game initialization.
     */
    public static void loadSounds() {
        System.out.println(">>> SoundManager: Loading sound effects...");

        // Load explosion sounds
        explosionSounds.add(Gdx.audio.newSound(Gdx.files.internal("sounds/explosion.wav")));
        explosionSounds.add(Gdx.audio.newSound(Gdx.files.internal("sounds/explosion(1).wav")));
        explosionSounds.add(Gdx.audio.newSound(Gdx.files.internal("sounds/explosion(2).wav")));
        explosionSounds.add(Gdx.audio.newSound(Gdx.files.internal("sounds/explosion(3).wav")));

        // Load hitHurt sounds
        hitHurtSounds.add(Gdx.audio.newSound(Gdx.files.internal("sounds/hitHurt.wav")));
        hitHurtSounds.add(Gdx.audio.newSound(Gdx.files.internal("sounds/hitHurt(1).wav")));
        hitHurtSounds.add(Gdx.audio.newSound(Gdx.files.internal("sounds/hitHurt(2).wav")));

        // Load laser shoot sounds
        laserShootSounds.add(Gdx.audio.newSound(Gdx.files.internal("sounds/laserShoot.wav")));
        laserShootSounds.add(Gdx.audio.newSound(Gdx.files.internal("sounds/laserShoot(1).wav")));
        laserShootSounds.add(Gdx.audio.newSound(Gdx.files.internal("sounds/laserShoot(2).wav")));

        // Load pickup sounds
        pickupSounds.add(Gdx.audio.newSound(Gdx.files.internal("sounds/pickup1.wav")));
        pickupSounds.add(Gdx.audio.newSound(Gdx.files.internal("sounds/pickup2.wav")));
        pickupSounds.add(Gdx.audio.newSound(Gdx.files.internal("sounds/pickup3.wav")));

        // Load single sounds
        pauseSound = Gdx.audio.newSound(Gdx.files.internal("sounds/pause.wav"));
        selectSound = Gdx.audio.newSound(Gdx.files.internal("sounds/select.wav"));
        warpInSound = Gdx.audio.newSound(Gdx.files.internal("sounds/warp-in.wav"));

        System.out.println(">>> SoundManager: All sounds loaded successfully!");
    }

    /**
     * Play a random explosion sound.
     */
    public static void playExplosion() {
        if (explosionSounds.isEmpty()) return;
        Sound sound = explosionSounds.get(Rand.nextInt(explosionSounds.size()));
        sound.play(volume);
    }

    /**
     * Play a random hitHurt sound.
     */
    public static void playHitHurt() {
        if (hitHurtSounds.isEmpty()) return;
        Sound sound = hitHurtSounds.get(Rand.nextInt(hitHurtSounds.size()));
        sound.play(volume);
    }

    /**
     * Play a random laser shoot sound.
     */
    public static void playLaserShoot() {
        if (laserShootSounds.isEmpty()) return;
        Sound sound = laserShootSounds.get(Rand.nextInt(laserShootSounds.size()));
        sound.play(volume);
    }

    /**
     * Play a random pickup sound.
     */
    public static void playPickup() {
        if (pickupSounds.isEmpty()) return;
        Sound sound = pickupSounds.get(Rand.nextInt(pickupSounds.size()));
        sound.play(volume);
    }

    /**
     * Play the pause sound.
     */
    public static void playPause() {
        if (pauseSound != null) {
            pauseSound.play(volume);
        }
    }

    /**
     * Play the select sound.
     */
    public static void playSelect() {
        if (selectSound != null) {
            selectSound.play(volume);
        }
    }

    /**
     * Play the warp-in sound.
     */
    public static void playWarpIn() {
        if (warpInSound != null) {
            warpInSound.play(volume);
        }
    }

    /**
     * Set the volume for all sound effects.
     * @param vol Volume level (0.0 to 1.0)
     */
    public static void setVolume(float vol) {
        volume = Math.max(0.0f, Math.min(1.0f, vol));
    }

    /**
     * Dispose of all sound resources.
     * Call this when shutting down the game.
     */
    public static void dispose() {
        System.out.println(">>> SoundManager: Disposing sound effects...");

        for (Sound sound : explosionSounds) {
            sound.dispose();
        }
        for (Sound sound : hitHurtSounds) {
            sound.dispose();
        }
        for (Sound sound : laserShootSounds) {
            sound.dispose();
        }
        for (Sound sound : pickupSounds) {
            sound.dispose();
        }

        if (pauseSound != null) pauseSound.dispose();
        if (selectSound != null) selectSound.dispose();
        if (warpInSound != null) warpInSound.dispose();

        explosionSounds.clear();
        hitHurtSounds.clear();
        laserShootSounds.clear();
        pickupSounds.clear();

        System.out.println(">>> SoundManager: All sounds disposed!");
    }
}
