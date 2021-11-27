package com.dalesmithwebdev.arcadespaceshooter.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;
import com.dalesmithwebdev.arcadespaceshooter.ArcadeSpaceShooter;
import com.dalesmithwebdev.arcadespaceshooter.components.*;
import com.dalesmithwebdev.arcadespaceshooter.utility.ComponentMap;

import java.util.Random;

public class LevelSystem extends EntitySystem {
    public static int levelNumber = 0;
    public static int levelLength;
    private boolean preppingLevel = false;

    public void update(float gameTime)
    {

        if (preppingLevel)
        {
            return;
        }
        //we know it's time to seed a new level when no meteors or enemies are left
        ImmutableArray<Entity> enemies = ArcadeSpaceShooter.engine.getEntitiesFor(Family.all(EnemyComponent.class).get());
        ImmutableArray<Entity> meteors = ArcadeSpaceShooter.engine.getEntitiesFor(Family.all(MeteorComponent.class).get());
        if (enemies.size() == 0 && meteors.size() == 0)
        {
            levelNumber++;
            if (levelNumber > 1)
            {
                Entity e = new Entity();
                e.add(new NotificationComponent("Level Complete!", 3000, true));
                ArcadeSpaceShooter.engine.addEntity(e);
                preppingLevel = true;
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        Entity e = new Entity();
                        e.add(new NotificationComponent("Begin Level " + levelNumber, 3000, true));
                        ArcadeSpaceShooter.engine.addEntity(e);

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
                ArcadeSpaceShooter.engine.addEntity(e);
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        Entity e = new Entity();
                        e.add(new NotificationComponent("Hold the space bar to shoot", 3000, true));
                        ArcadeSpaceShooter.engine.addEntity(e);

                        Timer.schedule(new Timer.Task() {
                            @Override
                            public void run() {
                                Entity e = new Entity();
                                e.add(new NotificationComponent("Hold left shift to shield", 3000, true));
                                ArcadeSpaceShooter.engine.addEntity(e);

                                Timer.schedule(new Timer.Task() {
                                    @Override
                                    public void run() {
                                        Entity e = new Entity();
                                        e.add(new NotificationComponent("Good Luck!", 3000, true));
                                        ArcadeSpaceShooter.engine.addEntity(e);
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

    private int randomRange(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    private void BuildLevel()
    {
        //#if DEBUG
        //            Entity boss = new Entity();
        //            boss.AddComponent(new EnemyComponent(10000, 0));
        //            boss.AddComponent(new RenderComponent(Game1.instance.bossTexture));
        //            boss.AddComponent(new PositionComponent(new Vector2(Game1.instance.screenBounds.Width / 2, -100)));
        //            boss.AddComponent(new SpeedComponent(new Vector2(0, 1)));
        //            boss.AddComponent(new TakesDamageComponent(10 * levelNumber, DamageSystem.LASER));
        //            boss.AddComponent(new DealsDamageComponent(20, DamageSystem.ENEMY));
        //            boss.AddComponent(new BossEnemyComponent());
        //            world.AddEntity(boss);
        //            levelLength = -100;
        //#else

        //

        levelLength = (int)ArcadeSpaceShooter.screenRect.height + 1000 + (500 * levelNumber);

        Random rand = new Random();

        int startPosition = levelNumber == 1 ? (int)ArcadeSpaceShooter.screenRect.height + 1000 : (int)ArcadeSpaceShooter.screenRect.height;

        for (int l = startPosition; l < levelLength; l++)
        {
            //Initialize random meteors
            double randomAmt = 0.5 + (0.05 * levelNumber);
            if(rand.nextFloat() * 100 < randomAmt) {
                boolean bigMeteor = rand.nextBoolean(); //rand.Next() % 2 == 0) ? true : false;
                Entity newMeteor = new Entity();
                newMeteor.add(new MeteorComponent(bigMeteor));
                newMeteor.add(new RenderComponent(bigMeteor ? ArcadeSpaceShooter.meteorBig : ArcadeSpaceShooter.meteorSmall));
                newMeteor.add(new TakesDamageComponent(bigMeteor ? 10 : 2, DamageSystem.LASER));
                newMeteor.add(new DealsDamageComponent(bigMeteor ? 10 : 5, DamageSystem.METEOR));
                int[] speeds = new int[] { 1, 1, 1, 2, 2, 2, 3, 3, 4, 5 };
                newMeteor.add(new SpeedComponent(new Vector2(0, -speeds[rand.nextInt(speeds.length)])));
                newMeteor.add(new PositionComponent(new Vector2(rand.nextInt((int)ArcadeSpaceShooter.screenRect.width), l)));
                ArcadeSpaceShooter.engine.addEntity(newMeteor);
            }

            double enemyAmount = 0.25 + (0.05 * levelNumber);
            if(rand.nextInt(100) < enemyAmount) {
                Entity enemy = new Entity();
                enemy.add(new EnemyComponent(this.randomRange(2000, 5000), rand.nextDouble() * 10000));
                enemy.add(new RenderComponent(ArcadeSpaceShooter.enemyShip));
                enemy.add(new PositionComponent(new Vector2(this.randomRange(50, (int)ArcadeSpaceShooter.screenRect.width - 50), l)));
                enemy.add(new SpeedComponent(new Vector2(0, -1)));
                enemy.add(new TakesDamageComponent(5 + (int)(0.5 * levelNumber), DamageSystem.LASER));
                enemy.add(new DealsDamageComponent(20, DamageSystem.ENEMY));
                ArcadeSpaceShooter.engine.addEntity(enemy);
            }
        }

        Entity boss = new Entity();
        boss.add(new EnemyComponent(Math.max(1000 - (20 * levelNumber), 100), 0));
        boss.add(new RenderComponent(ArcadeSpaceShooter.bossTexture));
        boss.add(new PositionComponent(new Vector2(ArcadeSpaceShooter.screenRect.width / 2, levelLength)));
        boss.add(new SpeedComponent(new Vector2(0, -1)));
        boss.add(new TakesDamageComponent(50 + (10 * levelNumber), DamageSystem.LASER));
        boss.add(new DealsDamageComponent(20, DamageSystem.ENEMY));
        boss.add(new BossEnemyComponent(levelNumber < 2 ? 1 : levelNumber < 5 ? 2 : levelNumber < 8 ? 3 : 4));
        ArcadeSpaceShooter.engine.addEntity(boss);
//#endif
        preppingLevel = false;
    }

    public void draw()
    {
        ArcadeSpaceShooter.spriteBatch.setColor(Color.BLACK);
        ArcadeSpaceShooter.spriteBatch.draw(ArcadeSpaceShooter.blank, ArcadeSpaceShooter.screenRect.width - 158, 25, 150, 12);
        ImmutableArray<Entity> bossEntities = ArcadeSpaceShooter.engine.getEntitiesFor(Family.all(BossEnemyComponent.class).get());
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
