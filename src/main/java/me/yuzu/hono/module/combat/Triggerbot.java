package me.yuzu.hono.module.combat;

import java.util.List;
import java.util.Optional;

import me.yuzu.hono.module.Category;
import me.yuzu.hono.module.Module;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class Triggerbot extends Module {

    private static final double ATTACK_RANGE = 4.5D;

    public Triggerbot() {
        super("Triggerbot", "Auto Attacks Entities Inside Crosshairs", Category.COMBAT, 0);
    }

    
    @Override
    public void onUpdate() {
        if (this.isToggled() && mc.player != null) {
            Entity target = getEntityInCrosshair();
            if (target != null && target instanceof LivingEntity) {
                attackEntity((LivingEntity) target);
            }
        }
    }

    private Entity getEntityInCrosshair() {
        Vec3 eyePos = mc.player.getEyePosition(1.0f);
        Vec3 lookVec = mc.player.getLookAngle().scale(ATTACK_RANGE);
        Vec3 endVec = eyePos.add(lookVec);

        AABB boundingBox = mc.player.getBoundingBox().expandTowards(lookVec).inflate(1.0f);
        List<Entity> entities = mc.level.getEntities(mc.player, boundingBox);

        Entity closestEntity = null;
        double closestDistance = ATTACK_RANGE;

        for (Entity entity : entities) {
            if (entity instanceof LivingEntity && entity != mc.player) {
                AABB entityBox = entity.getBoundingBox().inflate(0.3D);
                Optional<Vec3> hitResult = entityBox.clip(eyePos, endVec);

                if (hitResult.isPresent()) {
                    double distance = eyePos.distanceTo(hitResult.get());
                    if (distance < closestDistance) {
                        closestDistance = distance;
                        closestEntity = entity;
                    }
                }
            }
        }

        return closestEntity;
    }

    private void attackEntity(LivingEntity entity) {
        if (mc.player.distanceTo(entity) <= ATTACK_RANGE) {
            mc.player.swing(InteractionHand.MAIN_HAND);
            mc.gameMode.attack(mc.player, entity);
        }
    }
	@Override
	public void setOptions() {
		// TODO 自動生成されたメソッド・スタブ
		
	}
}