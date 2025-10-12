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
    private final LevelProgressionService levelProgressionService;
    private final EntitySpawnService entitySpawnService;
    private final VirtualSpawnService virtualSpawnService;
    private final LevelLoaderService levelLoaderService;
    private final ProceduralLevelService proceduralLevelService;
    private final LevelNotificationService levelNotificationService;

    private ServiceLocator() {
        this.gameStateService = new GameStateService();
        this.levelProgressionService = new LevelProgressionService();
        this.entitySpawnService = new EntitySpawnService();
        this.virtualSpawnService = new VirtualSpawnService();
        this.levelLoaderService = new LevelLoaderService();
        this.proceduralLevelService = new ProceduralLevelService();
        this.levelNotificationService = new LevelNotificationService();
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
            instance.levelProgressionService.reset();
            instance.entitySpawnService.clearQueue();
            instance.virtualSpawnService.clear();
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

    /**
     * Get level progression service
     */
    public LevelProgressionService getLevelProgression() {
        return levelProgressionService;
    }

    /**
     * Get entity spawn service
     */
    public EntitySpawnService getEntitySpawn() {
        return entitySpawnService;
    }

    /**
     * Get level loader service
     */
    public LevelLoaderService getLevelLoader() {
        return levelLoaderService;
    }

    /**
     * Get procedural level service
     */
    public ProceduralLevelService getProceduralLevel() {
        return proceduralLevelService;
    }

    /**
     * Get level notification service
     */
    public LevelNotificationService getLevelNotification() {
        return levelNotificationService;
    }

    /**
     * Get virtual spawn service
     */
    public VirtualSpawnService getVirtualSpawn() {
        return virtualSpawnService;
    }
}
