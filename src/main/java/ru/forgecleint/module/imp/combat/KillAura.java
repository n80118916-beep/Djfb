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
    
    // Настройки для обхода SpookyTime
    private final float range = 3.3f; 
    private final float fov = 360f; 
    private final float maxRotationSpeed = 45f; 
    
    private long lastAttackTime = 0;
    private long currentDelay = 150; 
    
    private float serverYaw = 0, serverPitch = 0;

    public KillAura() {
        super("KillAura", "Combat");
    }

    @Override
    public void onUpdate() {
        // ИСПРАВЛЕНО: world -> level (в Mojang маппингах)
        if (mc.player == null || mc.level == null) return;

        LivingEntity target = findTarget();
        
        if (target != null) {
            float[] idealRots = calculateRotations(target);
            
            serverYaw = smoothRotation(serverYaw, idealRots[0], maxRotationSpeed);
            serverPitch = smoothRotation(serverPitch, idealRots[1], maxRotationSpeed);
            
            // ИСПРАВЛЕНО: getConnection().sendPacket() -> player.connection.send()
            mc.player.connection.send(new CPlayerPacket.RotationPacket(serverYaw, serverPitch, mc.player.isOnGround()));
            
            long timeNow = System.currentTimeMillis();
            if (timeNow - lastAttackTime >= currentDelay) {
                // ИСПРАВЛЕНО: canEntityBeSeen -> canSee
                if (mc.player.canSee(target)) {
                    
                    // ИСПРАВЛЕНО: playerController.attackEntity -> gameMode.attack
                    mc.gameMode.attack(mc.player, target);
                    
                    // ИСПРАВЛЕНО: swingArm -> swing
                    mc.player.swing(Hand.MAIN_HAND);
                    
                    lastAttackTime = timeNow;
                    currentDelay = 95 + random.nextInt(40); // 7-11 CPS
                }
            }
        }
    }

    private LivingEntity findTarget() {
        LivingEntity bestTarget = null;
        double bestDist = range;

        // ИСПРАВЛЕНО: getLoadedEntities -> getEntities
        for (Entity entity : mc.level.getEntities()) {
            if (entity instanceof LivingEntity && entity != mc.player && ((LivingEntity) entity).isAlive()) {
                if (!(entity instanceof PlayerEntity)) continue;
                
                // ИСПРАВЛЕНО: getDistance -> distanceTo
                double dist = mc.player.distanceTo(entity);
                if (dist <= bestDist) {
                    // ИСПРАВЛЕНО: rotationYaw -> yRot
                    float yawDiff = Math.abs(MathHelper.wrapDegrees(getRotationsToEntity(entity)[0] - mc.player.yRot));
                    if (yawDiff <= fov / 2) {
                        bestDist = dist;
                        bestTarget = (LivingEntity) entity;
                    }
                }
            }
        }
        return bestTarget;
    }

    private float[] calculateRotations(Entity target) {
        // ИСПРАВЛЕНО: getPosX/getPosY/getPosZ -> getX/getY/getZ
        double diffX = target.getX() - mc.player.getX();
        double diffZ = target.getZ() - mc.player.getZ();
        double diffY = target.getY() + target.getEyeHeight() / 2 - (mc.player.getY() + mc.player.getEyeHeight());
        
        // ИСПРАВЛЕНО: MathHelper.sqrt_double -> Math.sqrt
        double dist = Math.sqrt(diffX * diffX + diffZ * diffZ);

        float yaw = (float) (Math.atan2(diffZ, diffX) * 180.0D / Math.PI) - 90.0F;
        float pitch = (float) -(Math.atan2(diffY, dist) * 180.0D / Math.PI);

        return new float[]{yaw, pitch};
    }

    private float smoothRotation(float current, float target, float maxDelta) {
        float delta = MathHelper.wrapDegrees(target - current);
        delta = MathHelper.clamp(delta, -maxDelta, maxDelta);
        return current + delta;
    }

    private float[] getRotationsToEntity(Entity entity) {
        return calculateRotations(entity);
    }
}
