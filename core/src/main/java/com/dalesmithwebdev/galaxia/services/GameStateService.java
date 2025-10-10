package com.dalesmithwebdev.galaxia.services;

/**
 * GameStateService - Manages game state (scores, kills, pause, EMP)
 * Single Responsibility: Game state management
 */
public class GameStateService {
    private int kills = 0;
    private double playerScore = 0;
    private boolean paused = false;
    private boolean gameOverScheduled = false;
    private boolean empActive = false;
    private int empElapsedTime = 0;
    private int totalTime = 0;

    // Kills management
    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public void incrementKills(int amount) {
        this.kills += amount;
    }

    // Score management
    public double getPlayerScore() {
        return playerScore;
    }

    public void setPlayerScore(double score) {
        this.playerScore = score;
    }

    public void addScore(double score) {
        this.playerScore += score;
    }

    // Pause state
    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    // EMP state
    public boolean isEmpActive() {
        return empActive;
    }

    public void setEmpActive(boolean active) {
        this.empActive = active;
    }

    public int getEmpElapsedTime() {
        return empElapsedTime;
    }

    public void updateEmpElapsedTime(float delta) {
        this.empElapsedTime += delta;
    }

    public void resetEmpElapsedTime() {
        this.empElapsedTime = 0;
    }

    // Time tracking
    public int getTotalTime() {
        return totalTime;
    }

    public void updateTotalTime(float delta) {
        this.totalTime += delta;
    }

    // Game over state
    public boolean isGameOverScheduled() {
        return gameOverScheduled;
    }

    public void setGameOverScheduled(boolean scheduled) {
        this.gameOverScheduled = scheduled;
    }

    /**
     * Reset all game state to initial values
     */
    public void reset() {
        kills = 0;
        playerScore = 0;
        paused = false;
        gameOverScheduled = false;
        empActive = false;
        empElapsedTime = 0;
        totalTime = 0;
    }
}
