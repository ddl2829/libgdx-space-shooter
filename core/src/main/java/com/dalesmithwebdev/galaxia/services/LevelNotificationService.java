package com.dalesmithwebdev.galaxia.services;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Timer;
import com.dalesmithwebdev.galaxia.components.NotificationComponent;

/**
 * LevelNotificationService - Display level-related UI messages
 * Single Responsibility: Show notifications for level events
 */
public class LevelNotificationService {

    /**
     * Show level complete message
     */
    public void showLevelComplete(Engine engine) {
        Entity e = new Entity();
        e.add(new NotificationComponent("Level Complete!", 3000, true));
        engine.addEntity(e);
    }

    /**
     * Show level start message with level name
     */
    public void showLevelStart(Engine engine, String levelName) {
        Entity e = new Entity();
        e.add(new NotificationComponent("Begin Level: " + levelName, 3000, true));
        engine.addEntity(e);
    }

    /**
     * Show level start message with level number
     */
    public void showLevelStart(Engine engine, int levelNumber) {
        Entity e = new Entity();
        e.add(new NotificationComponent("Begin Level " + levelNumber, 3000, true));
        engine.addEntity(e);
    }

    /**
     * Show tutorial messages (first level)
     */
    public void showTutorial(final Engine engine) {
        Entity e = new Entity();
        e.add(new NotificationComponent("Use the Arrow Keys to move", 3000, true));
        engine.addEntity(e);

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                Entity e = new Entity();
                e.add(new NotificationComponent("Hold the space bar to shoot", 3000, true));
                engine.addEntity(e);

                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        Entity e = new Entity();
                        e.add(new NotificationComponent("Good Luck!", 3000, true));
                        engine.addEntity(e);
                    }
                }, 3);
            }
        }, 3);
    }

    /**
     * Show all levels complete message
     */
    public void showAllLevelsComplete(Engine engine) {
        Entity e = new Entity();
        e.add(new NotificationComponent("All Levels Complete!", 3000, true));
        engine.addEntity(e);
    }

    /**
     * Schedule a notification to appear after a delay
     */
    public void scheduleNotification(final Engine engine, final String message, float delaySeconds) {
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                Entity e = new Entity();
                e.add(new NotificationComponent(message, 3000, true));
                engine.addEntity(e);
            }
        }, delaySeconds);
    }
}
