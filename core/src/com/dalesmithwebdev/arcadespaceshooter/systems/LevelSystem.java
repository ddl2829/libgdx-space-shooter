package com.dalesmithwebdev.arcadespaceshooter.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Timer;
import com.dalesmithwebdev.arcadespaceshooter.ArcadeSpaceShooter;
import com.dalesmithwebdev.arcadespaceshooter.components.*;
import com.dalesmithwebdev.arcadespaceshooter.prefabs.EnemyFighter;
import com.dalesmithwebdev.arcadespaceshooter.prefabs.FighterBoss;
import com.dalesmithwebdev.arcadespaceshooter.prefabs.LargeMeteor;
import com.dalesmithwebdev.arcadespaceshooter.prefabs.SmallMeteor;
import com.dalesmithwebdev.arcadespaceshooter.utility.ComponentMap;
import com.dalesmithwebdev.arcadespaceshooter.utility.Rand;

public class LevelSystem extends EntitySystem {
    public static int levelNumber = 0;
    public static int levelLength;
    private boolean preppingLevel = false;

    public void update(float gameTime)
    {
        final Engine engine = this.getEngine();
        if (preppingLevel)
        {
            return;
        }
        //we know it's time to seed a new level when no meteors or enemies are left
        ImmutableArray<Entity> enemies = this.getEngine().getEntitiesFor(Family.all(EnemyComponent.class).get());
        ImmutableArray<Entity> meteors = this.getEngine().getEntitiesFor(Family.all(MeteorComponent.class).get());
        if (enemies.size() == 0 && meteors.size() == 0)
        {
            levelNumber++;
            if (levelNumber > 1)
            {
                Entity e = new Entity();
                e.add(new NotificationComponent("Level Complete!", 3000, true));
                this.getEngine().addEntity(e);
                preppingLevel = true;
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        Entity e = new Entity();
                        e.add(new NotificationComponent("Begin Level " + levelNumber, 3000, true));
                        engine.addEntity(e);

                        Timer.schedule(new Timer.Task() {
                            @Override
                            public void run() {
                                BuildLevel();
                            }
                        }, 3);
                    }
                }, 5);
            }
            else
            {
                Entity e = new Entity();
                e.add(new NotificationComponent("Use the Arrow Keys to move", 3000, true));
                this.getEngine().addEntity(e);
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
                                e.add(new NotificationComponent("Hold left shift to shield", 3000, true));
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
                }, 3);

                //Load first level
                BuildLevel();
            }
        }
    }

    private void BuildLevel()
    {
        levelLength = (int)ArcadeSpaceShooter.screenRect.height + 1000 + (500 * levelNumber);

        int startPosition = levelNumber == 1 ? (int)ArcadeSpaceShooter.screenRect.height + 1000 : (int)ArcadeSpaceShooter.screenRect.height;

        for (int l = startPosition; l < levelLength; l++)
        {
            //Initialize random meteors
            double randomAmt = 0.5 + (0.05 * levelNumber);
            if(Rand.nextFloat() * 100 < randomAmt) {
                boolean bigMeteor = Rand.nextBoolean();
                Entity newMeteor = bigMeteor ? new LargeMeteor(l) : new SmallMeteor(l);
                this.getEngine().addEntity(newMeteor);
            }

            double enemyAmount = 0.25 + (0.05 * levelNumber);
            if(Rand.nextInt(100) < enemyAmount) {
                Entity enemy = new EnemyFighter(levelNumber, l);
                this.getEngine().addEntity(enemy);
            }
        }

        Entity boss = new FighterBoss(levelNumber, levelLength);
        this.getEngine().addEntity(boss);

        preppingLevel = false;
    }

    public void draw()
    {
        ArcadeSpaceShooter.spriteBatch.setColor(Color.BLACK);
        ArcadeSpaceShooter.spriteBatch.draw(ArcadeSpaceShooter.blank, ArcadeSpaceShooter.screenRect.width - 158, 25, 150, 12);
        ImmutableArray<Entity> bossEntities = this.getEngine().getEntitiesFor(Family.all(BossEnemyComponent.class).get());
        if (bossEntities.size() > 0)
        {
            Entity boss = bossEntities.get(0);
            PositionComponent bossPosition = ComponentMap.positionComponentComponentMapper.get(boss);//(PositionComponent)boss.components[typeof(PositionComponent)];
            double pct = (levelLength - bossPosition.position.y) / levelLength;
            ArcadeSpaceShooter.spriteBatch.setColor(Color.WHITE);
            ArcadeSpaceShooter.spriteBatch.draw(ArcadeSpaceShooter.blank, ArcadeSpaceShooter.screenRect.width - 159, 26, (int)(pct * 148), 10);
        }
    }
}
