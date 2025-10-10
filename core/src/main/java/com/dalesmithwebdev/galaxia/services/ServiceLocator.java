package com.dalesmithwebdev.galaxia.services;

/**
 * ServiceLocator - Provides centralized access to game services
 * Single Responsibility: Service lifecycle and access management
 *
 * Usage:
 *   GameStateService state = ServiceLocator.getInstance().getGameState();
 *   state.addScore(100);
 */
public class ServiceLocator {
    private static ServiceLocator instance;

    private final GameStateService gameStateService;

    private ServiceLocator() {
        this.gameStateService = new GameStateService();
    }

    /**
     * Get singleton instance of ServiceLocator
     */
    public static ServiceLocator getInstance() {
        if (instance == null) {
            instance = new ServiceLocator();
        }
        return instance;
    }

    /**
     * Reset all services (used for game restart)
     */
    public static void reset() {
        if (instance != null) {
            instance.gameStateService.reset();
        }
    }

    /**
     * Destroy and recreate service locator (used for complete reset)
     */
    public static void destroy() {
        instance = null;
    }

    /**
     * Get game state service
     */
    public GameStateService getGameState() {
        return gameStateService;
    }
}
