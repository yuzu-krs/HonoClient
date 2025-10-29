package net.minecraft.world.entity.ai.behavior;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;

public class UpdateActivityFromSchedule {
    public static BehaviorControl<LivingEntity> create() {
        return BehaviorBuilder.create(p_259429_ -> p_259429_.point((p_359062_, p_359063_, p_359064_) -> {
                p_359063_.getBrain().updateActivityFromSchedule(p_359062_.getDayTime(), p_359062_.getGameTime());
                return true;
            }));
    }
}