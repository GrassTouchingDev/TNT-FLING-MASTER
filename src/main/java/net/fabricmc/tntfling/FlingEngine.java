package com.tntflingmaster;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.*;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.Items;
import net.minecraft.util.math.Vec3;

public class FlingEngine {
    public enum Stage { IDLE, SEARCH, LIGHT, CAST, AWAIT_HOOK, TARGETING, ASCEND, APEX, RELEASE }
    public static Stage stage = Stage.IDLE;
    public static int targetTimer = 0;
    public static Entity target = null;
    public static TntEntity tnt = null;

    public static void tick(MinecraftClient client) {
        if (client.player == null || client.world == null) return;

        if (ModKeybinds.flingKey.wasPressed()) {
            stage = Stage.SEARCH;
        }

        switch (stage) {
            case SEARCH -> {
                tnt = client.world.getEntities().filter(e -> e instanceof TntEntity).map(e -> (TntEntity) e).findFirst().orElse(null);
                if (tnt != null) {
                    SlingPhysics.lookAt(client, tnt.getPos());
                    stage = Stage.LIGHT;
                }
            }
            case LIGHT -> {
                ItemHelper.switchTo(client.player, Items.FLINT_AND_STEEL);
                client.interactionManager.interactItem(client.player, client.world, client.player.getMainHandStack());
                stage = Stage.CAST;
            }
            case CAST -> {
                ItemHelper.switchTo(client.player, Items.FISHING_ROD);
                SlingPhysics.lookAt(client, tnt.getPos());
                client.interactionManager.interactItem(client.player, client.world, client.player.getMainHandStack());
                stage = Stage.AWAIT_HOOK;
            }
            case AWAIT_HOOK -> {
                if (isHooked(client)) {
                    targetTimer = 20; // Exactly 1 second for the user
                    stage = Stage.TARGETING;
                }
            }
            case TARGETING -> {
                targetTimer--;
                if (targetTimer <= 0) {
                    target = SlingPhysics.getBestTarget(client);
                    stage = Stage.ASCEND;
                }
            }
            case ASCEND -> {
                ItemHelper.switchTo(client.player, Items.MACE);
                if (target != null) client.interactionManager.attackEntity(client.player, target);
                
                // MICRO-CORRECTION: Update the aim every tick to track target movement
                Vec3 perfectPivot = SlingPhysics.calculatePerfectPivot(client.player, tnt, target);
                SlingPhysics.lookAt(client, perfectPivot);
                
                if (client.player.getVelocity().y < 0.1) stage = Stage.APEX;
            }
            case APEX -> {
                // Trigger release at the exact mathematical peak of the jump
                if (client.player.getVelocity().y <= 0.0) stage = Stage.RELEASE;
            }
            case RELEASE -> {
                ItemHelper.switchTo(client.player, Items.FISHING_ROD);
                client.interactionManager.interactItem(client.player, client.world, client.player.getMainHandStack());
                stage = Stage.IDLE;
            }
        }
    }

    private static boolean isHooked(MinecraftClient client) {
        return client.world.getEntities().filter(e -> e instanceof FishingBobberEntity).anyMatch(b -> b.squaredDistanceTo(tnt) < 2.0);
    }
}
