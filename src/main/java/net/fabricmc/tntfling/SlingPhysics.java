package com.tntflingmaster;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.*;
import net.minecraft.util.math.Vec3;
import java.util.Comparator;

public class SlingPhysics {
    public static Vec3 calculatePerfectPivot(PlayerEntity player, TntEntity tnt, Entity target) {
        if (target == null) return tnt.getPos();
        
        Vec3 targetPos = target.getPos().add(0, target.getHeight()/2, 0);
        Vec3 targetVel = target.getVelocity();
        Vec3 tntVel = tnt.getVelocity();
        Vec3 playerPos = player.getEyePos();

        // 1. Predict target position (Lead the target based on velocity)
        Vec3 predictedTarget = targetPos.add(targetVel.multiply(12));

        // 2. Compensate for TNT Drift
        // We subtract the TNT's current movement to "cancel" its inertia
        Vec3 driftCorrection = tntVel.multiply(-15);

        // 3. The "Sling-Shot" Alignment
        // The rod pulls TNT -> Player. 
        // To make the TNT go Player -> Target, the player must aim at a 
        // point that creates a perfectly mirrored vector.
        Vec3 slingDirection = playerPos.subtract(tnt.getPos()).normalize();
        Vec3 pivotOffset = slingDirection.multiply(-4.0);

        return predictedTarget.add(driftCorrection).add(pivotOffset);
    }

    public static Entity getBestTarget(MinecraftClient client) {
        return client.world.getEntities()
            .filter(e -> e instanceof LivingEntity && e != client.player)
            .min(Comparator.comparingDouble(e -> {
                Vec3 diff = e.getPos().subtract(client.player.getEyePos()).normalize();
                return 1.0 - diff.dot(client.player.getRotationVec());
            })).orElse(null);
    }

    public static void lookAt(MinecraftClient client, Vec3 pos) {
        Vec3 diff = pos.subtract(client.player.getEyePos());
        float yaw = (float) Math.toDegrees(Math.atan2(diff.z, diff.x)) - 90F;
        float pitch = (float) -Math.toDegrees(Math.atan2(diff.y, Math.sqrt(diff.x * diff.x + diff.z * diff.z)));
        client.player.setYaw(yaw);
        client.player.setPitch(pitch);
    }
}
