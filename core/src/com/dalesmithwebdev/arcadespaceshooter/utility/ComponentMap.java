package com.dalesmithwebdev.arcadespaceshooter.utility;

import com.badlogic.ashley.core.ComponentMapper;
import com.dalesmithwebdev.arcadespaceshooter.components.*;

public class ComponentMap {
    public static ComponentMapper<BossEnemyComponent> bossEnemyComponentComponentMapper = ComponentMapper.getFor(BossEnemyComponent.class);
    public static ComponentMapper<DealsDamageComponent> dealsDamageComponentComponentMapper = ComponentMapper.getFor(DealsDamageComponent.class);
    public static ComponentMapper<EnemyComponent> enemyComponentComponentMapper = ComponentMapper.getFor(EnemyComponent.class);
    public static ComponentMapper<ExplosionComponent> explosionComponentComponentMapper = ComponentMapper.getFor(ExplosionComponent.class);
    public static ComponentMapper<HasShieldComponent> hasShieldComponentComponentMapper = ComponentMapper.getFor(HasShieldComponent.class);
    public static ComponentMapper<LaserComponent> laserComponentComponentMapper = ComponentMapper.getFor(LaserComponent.class);
    public static ComponentMapper<MeteorComponent> meteorComponentComponentMapper = ComponentMapper.getFor(MeteorComponent.class);
    public static ComponentMapper<NotificationComponent> notificationComponentComponentMapper = ComponentMapper.getFor(NotificationComponent.class);
    public static ComponentMapper<PlayerComponent> playerComponentComponentMapper = ComponentMapper.getFor(PlayerComponent.class);
    public static ComponentMapper<PositionComponent> positionComponentComponentMapper = ComponentMapper.getFor(PositionComponent.class);
    public static ComponentMapper<RenderComponent> renderComponentComponentMapper = ComponentMapper.getFor(RenderComponent.class);
    public static ComponentMapper<ShieldedComponent> shieldedComponentComponentMapper = ComponentMapper.getFor(ShieldedComponent.class);
    public static ComponentMapper<SpeedComponent> speedComponentComponentMapper = ComponentMapper.getFor(SpeedComponent.class);
    public static ComponentMapper<TakesDamageComponent> takesDamageComponentComponentMapper = ComponentMapper.getFor(TakesDamageComponent.class);
    public static ComponentMapper<HasBombsComponent> hasBombsComponentComponentMapper = ComponentMapper.getFor(HasBombsComponent.class);
    public static ComponentMapper<HasEmpComponent> hasEmpComponentComponentMapper = ComponentMapper.getFor(HasEmpComponent.class);
    public static ComponentMapper<HasLasersComponent> hasLasersComponentComponentMapper = ComponentMapper.getFor(HasLasersComponent.class);
    public static ComponentMapper<HasMissilesComponent> hasMissilesComponentComponentMapper = ComponentMapper.getFor(HasMissilesComponent.class);
    public static ComponentMapper<LaserStrengthUpgradeComponent> laserUpgradeComponentComponentMapper = ComponentMapper.getFor(LaserStrengthUpgradeComponent.class);
    public static ComponentMapper<DualLaserUpgradeComponent> dualLaserUpgradeComponentComponentMapper = ComponentMapper.getFor(DualLaserUpgradeComponent.class);
    public static ComponentMapper<DiagonalLaserUpgradeComponent> diagonalLaserUpgradeComponentComponentMapper = ComponentMapper.getFor(DiagonalLaserUpgradeComponent.class);
    public static ComponentMapper<BombUpgradeComponent> bombUpgradeComponentComponentMapper = ComponentMapper.getFor(BombUpgradeComponent.class);
    public static ComponentMapper<MissileUpgradeComponent> missileUpgradeComponentComponentMapper = ComponentMapper.getFor(MissileUpgradeComponent.class);
    public static ComponentMapper<ShieldUpgradeComponent> shieldUpgradeComponentComponentMapper = ComponentMapper.getFor(ShieldUpgradeComponent.class);
    public static ComponentMapper<EmpUpgradeComponent> empUpgradeComponentComponentMapper = ComponentMapper.getFor(EmpUpgradeComponent.class);
}
