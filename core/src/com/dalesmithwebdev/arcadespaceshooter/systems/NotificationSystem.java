package com.dalesmithwebdev.arcadespaceshooter.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.dalesmithwebdev.arcadespaceshooter.ArcadeSpaceShooter;
import com.dalesmithwebdev.arcadespaceshooter.components.NotificationComponent;
import com.dalesmithwebdev.arcadespaceshooter.components.PositionComponent;
import com.dalesmithwebdev.arcadespaceshooter.components.SpeedComponent;
import com.dalesmithwebdev.arcadespaceshooter.utility.ComponentMap;

public class NotificationSystem extends EntitySystem {
    public void update(float gametime)
    {
        ImmutableArray<Entity> notifications = ArcadeSpaceShooter.engine.getEntitiesFor(Family.all(NotificationComponent.class).get());
        for (Entity notification : notifications)
        {
            NotificationComponent notificationComponent = ComponentMap.notificationComponentComponentMapper.get(notification);
            notificationComponent.elapsedTime += gametime;
            if (notificationComponent.elapsedTime > notificationComponent.maxLife)
            {
                ArcadeSpaceShooter.engine.removeEntity(notification);
                continue;
            }

            ArcadeSpaceShooter.bitmapFont.setColor(notificationComponent.color);
            if (notificationComponent.centerText)
            {
                ArcadeSpaceShooter.bitmapFont.draw(
                        ArcadeSpaceShooter.spriteBatch,
                        notificationComponent.text,
                        ArcadeSpaceShooter.screenRect.width / 2 - ArcadeSpaceShooter.measureText(notificationComponent.text) / 2,
                        ArcadeSpaceShooter.screenRect.height - ArcadeSpaceShooter.screenRect.height / 3
                );
            }
            else
            {
                if(ComponentMap.positionComponentComponentMapper.has(notification))
                {
                    PositionComponent pc = ComponentMap.positionComponentComponentMapper.get(notification);
                    ArcadeSpaceShooter.bitmapFont.draw(ArcadeSpaceShooter.spriteBatch, notificationComponent.text, pc.position.x, pc.position.y);
                }
            }
        }
    }
}
