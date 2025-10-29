package net.minecraft.world.entity.monster.creaking;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CreakingHeartBlock;
import net.minecraft.world.level.block.entity.CreakingHeartBlockEntity;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.PathfindingContext;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class CreakingTransient extends Creaking {
    public static final int INVULNERABILITY_ANIMATION_DURATION = 8;
    private int invulnerabilityAnimationRemainingTicks;
    @Nullable
    BlockPos homePos;

    public CreakingTransient(EntityType<? extends Creaking> p_369499_, Level p_370053_) {
        super(p_369499_, p_370053_);
    }

    public void bindToCreakingHeart(BlockPos p_368510_) {
        this.homePos = p_368510_;
    }

    @Override
    public boolean hurtServer(ServerLevel p_367073_, DamageSource p_364521_, float p_361228_) {
        if (this.level().isClientSide) {
            return super.hurtServer(p_367073_, p_364521_, p_361228_);
        } else if (p_364521_.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            return super.hurtServer(p_367073_, p_364521_, p_361228_);
        } else if (!this.isInvulnerableTo(p_367073_, p_364521_) && this.invulnerabilityAnimationRemainingTicks <= 0) {
            this.invulnerabilityAnimationRemainingTicks = 8;
            this.level().broadcastEntityEvent(this, (byte)66);
            if (this.level().getBlockEntity(this.homePos) instanceof CreakingHeartBlockEntity creakingheartblockentity && creakingheartblockentity.isProtector(this)
                )
             {
                if (p_364521_.getEntity() instanceof Player) {
                    creakingheartblockentity.creakingHurt();
                }

                this.playHurtSound(p_364521_);
            }

            return true;
        } else {
            return false;
        }
    }

    @Override
    public void aiStep() {
        if (this.invulnerabilityAnimationRemainingTicks > 0) {
            this.invulnerabilityAnimationRemainingTicks--;
        }

        super.aiStep();
    }

    @Override
    public void tick() {
        if (this.level().isClientSide
            || this.homePos != null
                && this.level().getBlockEntity(this.homePos) instanceof CreakingHeartBlockEntity creakingheartblockentity
                && creakingheartblockentity.isProtector(this)) {
            super.tick();
            if (this.level().isClientSide) {
                this.setupAnimationStates();
            }
        } else {
            this.setRemoved(Entity.RemovalReason.DISCARDED);
        }
    }

    @Override
    public void handleEntityEvent(byte p_369608_) {
        if (p_369608_ == 66) {
            this.invulnerabilityAnimationRemainingTicks = 8;
            this.playHurtSound(this.damageSources().generic());
        } else {
            super.handleEntityEvent(p_369608_);
        }
    }

    private void setupAnimationStates() {
        this.invulnerabilityAnimationState.animateWhen(this.invulnerabilityAnimationRemainingTicks > 0, this.tickCount);
    }

    public void tearDown(@Nullable DamageSource p_364992_) {
        if (this.level() instanceof ServerLevel serverlevel) {
            AABB aabb = this.getBoundingBox();
            Vec3 vec3 = aabb.getCenter();
            double d0 = aabb.getXsize() * 0.3;
            double d1 = aabb.getYsize() * 0.3;
            double d2 = aabb.getZsize() * 0.3;
            serverlevel.sendParticles(
                new BlockParticleOption(ParticleTypes.BLOCK_CRUMBLE, Blocks.PALE_OAK_WOOD.defaultBlockState()),
                vec3.x,
                vec3.y,
                vec3.z,
                100,
                d0,
                d1,
                d2,
                0.0
            );
            serverlevel.sendParticles(
                new BlockParticleOption(
                    ParticleTypes.BLOCK_CRUMBLE, Blocks.CREAKING_HEART.defaultBlockState().setValue(CreakingHeartBlock.CREAKING, CreakingHeartBlock.CreakingHeartState.ACTIVE)
                ),
                vec3.x,
                vec3.y,
                vec3.z,
                10,
                d0,
                d1,
                d2,
                0.0
            );
        }

        this.makeSound(this.getDeathSound());
        if (this.deathScore >= 0 && p_364992_ != null && p_364992_.getEntity() instanceof LivingEntity livingentity) {
            livingentity.awardKillScore(this, this.deathScore, p_364992_);
        }

        this.remove(Entity.RemovalReason.DISCARDED);
    }

    @Override
    protected boolean canAddPassenger(Entity p_363651_) {
        return false;
    }

    @Override
    protected boolean couldAcceptPassenger() {
        return false;
    }

    @Override
    protected void addPassenger(Entity p_368493_) {
        throw new IllegalStateException("Should never addPassenger without checking couldAcceptPassenger()");
    }

    @Override
    public boolean canUsePortal(boolean p_366573_) {
        return false;
    }

    @Override
    protected PathNavigation createNavigation(Level p_363371_) {
        return new CreakingTransient.CreakingPathNavigation(this, p_363371_);
    }

    class CreakingPathNavigation extends GroundPathNavigation {
        CreakingPathNavigation(final Creaking p_361170_, final Level p_363485_) {
            super(p_361170_, p_363485_);
        }

        @Override
        public void tick() {
            if (CreakingTransient.this.canMove()) {
                super.tick();
            }
        }

        @Override
        protected PathFinder createPathFinder(int p_367061_) {
            this.nodeEvaluator = CreakingTransient.this.new HomeNodeEvaluator();
            return new PathFinder(this.nodeEvaluator, p_367061_);
        }
    }

    class HomeNodeEvaluator extends WalkNodeEvaluator {
        private static final int MAX_DISTANCE_TO_HOME_SQ = 1024;

        @Override
        public PathType getPathType(PathfindingContext p_368341_, int p_366707_, int p_367110_, int p_364778_) {
            BlockPos blockpos = CreakingTransient.this.homePos;
            if (blockpos == null) {
                return super.getPathType(p_368341_, p_366707_, p_367110_, p_364778_);
            } else {
                double d0 = blockpos.distSqr(new Vec3i(p_366707_, p_367110_, p_364778_));
                return d0 > 1024.0 && d0 >= blockpos.distSqr(p_368341_.mobPosition())
                    ? PathType.BLOCKED
                    : super.getPathType(p_368341_, p_366707_, p_367110_, p_364778_);
            }
        }
    }
}