package net.minecraft.server.level;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.EntityGetter;
import net.minecraft.world.phys.AABB;

public interface ServerEntityGetter extends EntityGetter {
    ServerLevel getLevel();

    @Nullable
    default Player getNearestPlayer(TargetingConditions p_368835_, LivingEntity p_369012_) {
        return this.getNearestEntity(this.players(), p_368835_, p_369012_, p_369012_.getX(), p_369012_.getY(), p_369012_.getZ());
    }

    @Nullable
    default Player getNearestPlayer(TargetingConditions p_367520_, LivingEntity p_363007_, double p_369096_, double p_368606_, double p_362625_) {
        return this.getNearestEntity(this.players(), p_367520_, p_363007_, p_369096_, p_368606_, p_362625_);
    }

    @Nullable
    default Player getNearestPlayer(TargetingConditions p_364861_, double p_366090_, double p_366921_, double p_362082_) {
        return this.getNearestEntity(this.players(), p_364861_, null, p_366090_, p_366921_, p_362082_);
    }

    @Nullable
    default <T extends LivingEntity> T getNearestEntity(
        Class<? extends T> p_364887_,
        TargetingConditions p_361392_,
        @Nullable LivingEntity p_363210_,
        double p_365809_,
        double p_364247_,
        double p_363656_,
        AABB p_369962_
    ) {
        return this.getNearestEntity(this.getEntitiesOfClass(p_364887_, p_369962_, p_369748_ -> true), p_361392_, p_363210_, p_365809_, p_364247_, p_363656_);
    }

    @Nullable
    default <T extends LivingEntity> T getNearestEntity(
        List<? extends T> p_363565_, TargetingConditions p_366923_, @Nullable LivingEntity p_362427_, double p_366460_, double p_364508_, double p_364611_
    ) {
        double d0 = -1.0;
        T t = null;

        for (T t1 : p_363565_) {
            if (p_366923_.test(this.getLevel(), p_362427_, t1)) {
                double d1 = t1.distanceToSqr(p_366460_, p_364508_, p_364611_);
                if (d0 == -1.0 || d1 < d0) {
                    d0 = d1;
                    t = t1;
                }
            }
        }

        return t;
    }

    default List<Player> getNearbyPlayers(TargetingConditions p_362024_, LivingEntity p_363864_, AABB p_363224_) {
        List<Player> list = new ArrayList<>();

        for (Player player : this.players()) {
            if (p_363224_.contains(player.getX(), player.getY(), player.getZ()) && p_362024_.test(this.getLevel(), p_363864_, player)) {
                list.add(player);
            }
        }

        return list;
    }

    default <T extends LivingEntity> List<T> getNearbyEntities(Class<T> p_369219_, TargetingConditions p_364644_, LivingEntity p_368357_, AABB p_361404_) {
        List<T> list = this.getEntitiesOfClass(p_369219_, p_361404_, p_368152_ -> true);
        List<T> list1 = new ArrayList<>();

        for (T t : list) {
            if (p_364644_.test(this.getLevel(), p_368357_, t)) {
                list1.add(t);
            }
        }

        return list1;
    }
}