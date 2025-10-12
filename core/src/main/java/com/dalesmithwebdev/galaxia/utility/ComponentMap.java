package com.dalesmithwebdev.galaxia.utility;

import com.badlogic.ashley.core.ComponentMapper;
import com.dalesmithwebdev.galaxia.components.*;

public class ComponentMap {
    // Entity Type Mappers
    public static ComponentMapper<BossEnemyComponent> bossEnemyMapper = ComponentMapper.getFor(BossEnemyComponent.class);
    public static ComponentMapper<EnemyComponent> enemyMapper = ComponentMapper.getFor(EnemyComponent.class);
    public static ComponentMapper<PlayerComponent> playerMapper = ComponentMapper.getFor(PlayerComponent.class);
    public static ComponentMapper<MeteorComponent> meteorMapper = ComponentMapper.getFor(MeteorComponent.class);

    // Core Component Mappers
    public static ComponentMapper<PositionComponent> positionMapper = ComponentMapper.getFor(PositionComponent.class);
    public static ComponentMapper<SpeedComponent> speedMapper = ComponentMapper.getFor(SpeedComponent.class);
    public static ComponentMapper<RenderComponent> renderMapper = ComponentMapper.getFor(RenderComponent.class);

    // Combat Mappers
    public static ComponentMapper<DealsDamageComponent> dealsDamageMapper = ComponentMapper.getFor(DealsDamageComponent.class);
    public static ComponentMapper<TakesDamageComponent> takesDamageMapper = ComponentMapper.getFor(TakesDamageComponent.class);
    public static ComponentMapper<RecentlyDamagedComponent> recentlyDamagedMapper = ComponentMapper.getFor(RecentlyDamagedComponent.class);

    // Weapon Mappers
    public static ComponentMapper<LaserComponent> laserMapper = ComponentMapper.getFor(LaserComponent.class);
    public static ComponentMapper<MissileComponent> missileMapper = ComponentMapper.getFor(MissileComponent.class);
    public static ComponentMapper<BombComponent> bombMapper = ComponentMapper.getFor(BombComponent.class);

    // Capability Mappers
    public static ComponentMapper<HasLasersComponent> hasLasersMapper = ComponentMapper.getFor(HasLasersComponent.class);
    public static ComponentMapper<HasMissilesComponent> hasMissilesMapper = ComponentMapper.getFor(HasMissilesComponent.class);
    public static ComponentMapper<HasBombsComponent> hasBombsMapper = ComponentMapper.getFor(HasBombsComponent.class);
    public static ComponentMapper<HasEmpComponent> hasEmpMapper = ComponentMapper.getFor(HasEmpComponent.class);
    public static ComponentMapper<HasShieldComponent> hasShieldMapper = ComponentMapper.getFor(HasShieldComponent.class);

    // State Mappers
    public static ComponentMapper<ShieldedComponent> shieldedMapper = ComponentMapper.getFor(ShieldedComponent.class);
    public static ComponentMapper<BackgroundObjectComponent> backgroundObjectMapper = ComponentMapper.getFor(BackgroundObjectComponent.class);
    public static ComponentMapper<MeteorCollisionCooldownComponent> meteorCollisionCooldownMapper = ComponentMapper.getFor(MeteorCollisionCooldownComponent.class);

    // Upgrade Mappers
    public static ComponentMapper<LaserStrengthUpgradeComponent> laserStrengthUpgradeMapper = ComponentMapper.getFor(LaserStrengthUpgradeComponent.class);
    public static ComponentMapper<DualLaserUpgradeComponent> dualLaserUpgradeMapper = ComponentMapper.getFor(DualLaserUpgradeComponent.class);
    public static ComponentMapper<DiagonalLaserUpgradeComponent> diagonalLaserUpgradeMapper = ComponentMapper.getFor(DiagonalLaserUpgradeComponent.class);
    public static ComponentMapper<MissileUpgradeComponent> missileUpgradeMapper = ComponentMapper.getFor(MissileUpgradeComponent.class);
    public static ComponentMapper<BombUpgradeComponent> bombUpgradeMapper = ComponentMapper.getFor(BombUpgradeComponent.class);
    public static ComponentMapper<ShieldUpgradeComponent> shieldUpgradeMapper = ComponentMapper.getFor(ShieldUpgradeComponent.class);
    public static ComponentMapper<EmpUpgradeComponent> empUpgradeMapper = ComponentMapper.getFor(EmpUpgradeComponent.class);

    // Effect Mappers
    public static ComponentMapper<ExplosionComponent> explosionMapper = ComponentMapper.getFor(ExplosionComponent.class);
    public static ComponentMapper<NotificationComponent> notificationMapper = ComponentMapper.getFor(NotificationComponent.class);
}
