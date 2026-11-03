package ru.forgeclient.module.impl.combat;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import ru.forgeclient.module.Module;

import java.util.Random;

public class KillAura extends Module {

    private final Random random = new Random();
    
    // Настройки для обхода (чем меньше значения, тем легитнее)
    private final float range = 3.3f; // Дистанция удара
    private final float fov = 360f; // Угол обзора (360 = круговое зрение)
    private final float maxRotationSpeed = 45f; // Максимальная скорость поворота головы (чтобы античит не палил резкий снап)
    
    private long lastAttackTime = 0;
    private long currentDelay = 150; // Начальная задержка
    
    private float serverYaw = 0, serverPitch = 0;

    public KillAura() {
        super("KillAura", "Combat");
    }

    @Override
    public void onUpdate() {
        if (mc.player == null || mc.world == null) return;

        // Ищем цель
        LivingEntity target = findTarget();
        
        if (target != null) {
            // Вычисляем идеальные углы
            float[] idealRots = calculateRotations(target);
            
            // Плавно поворачиваем "серверную" голову к цели
            serverYaw = smoothRotation(serverYaw, idealRots[0], maxRotationSpeed);
            serverPitch = smoothRotation(serverPitch, idealRots[1], maxRotationSpeed);
            
            // Отправляем пакет поворота (Silent Rotation)
            // Сервер будет думать, что мы смотрим на цель, но на экране камера не дернется
            mc.getConnection().sendPacket(new CPlayerPacket.RotationPacket(serverYaw, serverPitch, mc.player.isOnGround()));
            
            // Проверяем, можно ли бить
            long timeNow = System.currentTimeMillis();
            if (timeNow - lastAttackTime >= currentDelay) {
                // Проверка видимости (чтобы не бить сквозь стены и не палиться)
                if (mc.player.canEntityBeSeen(target)) {
                    
                    // Наносим удар
                    mc.playerController.attackEntity(mc.player, target);
                    mc.player.swingArm(Hand.MAIN_HAND);
                    
                    lastAttackTime = timeNow;
                    
                    // Рандомизация CPS (от 90 до 130 мс = 7-11 CPS). 
                    // На SpookyTime лучше ставить чуть больше 100 мс, чтобы не спалиться.
                    currentDelay = 95 + random.nextInt(40);
                }
            }
        }
    }

    // Поиск ближайшей цели
    private LivingEntity findTarget() {
        LivingEntity bestTarget = null;
        double bestDist = range;

        for (Entity entity : mc.world.getLoadedEntities()) {
            if (entity instanceof LivingEntity && entity != mc.player && ((LivingEntity) entity).isAlive()) {
                // Ищем только игроков
                if (!(entity instanceof PlayerEntity)) continue;
                
                double dist = mc.player.getDistance(entity);
                if (dist <= bestDist) {
                    // Проверка на FOV (угол обзора)
                    float yawDiff = Math.abs(MathHelper.wrapDegrees(getRotationsToEntity(entity)[0] - mc.player.rotationYaw));
                    if (yawDiff <= fov / 2) {
                        bestDist = dist;
                        bestTarget = (LivingEntity) entity;
                    }
                }
            }
        }
        return bestTarget;
    }

    // Вычисление углов для взгляда на цель (Mojang Math)
    private float[] calculateRotations(Entity target) {
        double diffX = target.getPosX() - mc.player.getPosX();
        double diffZ = target.getPosZ() - mc.player.getPosZ();
        double diffY = target.getPosY() + target.getEyeHeight() / 2 - (mc.player.getPosY() + mc.player.getEyeHeight());
        double dist = MathHelper.sqrt(diffX * diffX + diffZ * diffZ);

        float yaw = (float) (Math.atan2(diffZ, diffX) * 180.0D / Math.PI) - 90.0F;
        float pitch = (float) -(Math.atan2(diffY, dist) * 180.0D / Math.PI);

        return new float[]{yaw, pitch};
    }

    // Плавный поворот (имитация движения мыши)
    private float smoothRotation(float current, float target, float maxDelta) {
        float delta = MathHelper.wrapDegrees(target - current);
        delta = MathHelper.clamp(delta, -maxDelta, maxDelta);
        return current + delta;
    }

    private float[] getRotationsToEntity(Entity entity) {
        return calculateRotations(entity);
    }
}
