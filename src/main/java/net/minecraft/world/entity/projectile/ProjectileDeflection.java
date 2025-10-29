package net.minecraft.world.entity.projectile;

import javax.annotation.Nullable;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

@FunctionalInterface
public interface ProjectileDeflection {
    ProjectileDeflection NONE = (p_335766_, p_335741_, p_334113_) -> {
    };
    ProjectileDeflection REVERSE = (p_359354_, p_359355_, p_359356_) -> {
        float f = 170.0F + p_359356_.nextFloat() * 20.0F;
        p_359354_.setDeltaMovement(p_359354_.getDeltaMovement().scale(-0.5));
        p_359354_.setYRot(p_359354_.getYRot() + f);
        p_359354_.yRotO += f;
        p_359354_.hasImpulse = true;
    };
    ProjectileDeflection AIM_DEFLECT = (p_359351_, p_359352_, p_359353_) -> {
        if (p_359352_ != null) {
            Vec3 vec3 = p_359352_.getLookAngle().normalize();
            p_359351_.setDeltaMovement(vec3);
            p_359351_.hasImpulse = true;
        }
    };
    ProjectileDeflection MOMENTUM_DEFLECT = (p_359348_, p_359349_, p_359350_) -> {
        if (p_359349_ != null) {
            Vec3 vec3 = p_359349_.getDeltaMovement().normalize();
            p_359348_.setDeltaMovement(vec3);
            p_359348_.hasImpulse = true;
        }
    };

    void deflect(Projectile p_332034_, @Nullable Entity p_330319_, RandomSource p_333938_);
}