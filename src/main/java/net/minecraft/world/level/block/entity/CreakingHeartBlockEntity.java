package net.minecraft.world.level.block.entity;

import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.TargetColorParticleOption;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.util.SpawnUtil;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.creaking.Creaking;
import net.minecraft.world.entity.monster.creaking.CreakingTransient;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CreakingHeartBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

public class CreakingHeartBlockEntity extends BlockEntity {
    private static final int PLAYER_DETECTION_RANGE = 32;
    public static final int CREAKING_ROAMING_RADIUS = 32;
    private static final int DISTANCE_CREAKING_TOO_FAR = 34;
    private static final int SPAWN_RANGE_XZ = 16;
    private static final int SPAWN_RANGE_Y = 8;
    private static final int ATTEMPTS_PER_SPAWN = 5;
    private static final int UPDATE_TICKS = 20;
    private static final int HURT_CALL_TOTAL_TICKS = 100;
    private static final int NUMBER_OF_HURT_CALLS = 10;
    private static final int HURT_CALL_INTERVAL = 10;
    private static final int HURT_CALL_PARTICLE_TICKS = 50;
    @Nullable
    private CreakingTransient creaking;
    private int ticker;
    private int emitter;
    @Nullable
    private Vec3 emitterTarget;
    private int outputSignal;

    public CreakingHeartBlockEntity(BlockPos p_369235_, BlockState p_367834_) {
        super(BlockEntityType.CREAKING_HEART, p_369235_, p_367834_);
    }

    public static void serverTick(Level p_360952_, BlockPos p_367184_, BlockState p_365574_, CreakingHeartBlockEntity p_366884_) {
        int i = p_366884_.computeAnalogOutputSignal();
        if (p_366884_.outputSignal != i) {
            p_366884_.outputSignal = i;
            p_360952_.updateNeighbourForOutputSignal(p_367184_, Blocks.CREAKING_HEART);
        }

        if (p_366884_.emitter > 0) {
            if (p_366884_.emitter > 50) {
                p_366884_.emitParticles((ServerLevel)p_360952_, 1, true);
                p_366884_.emitParticles((ServerLevel)p_360952_, 1, false);
            }

            if (p_366884_.emitter % 10 == 0 && p_360952_ instanceof ServerLevel serverlevel && p_366884_.emitterTarget != null) {
                if (p_366884_.creaking != null) {
                    p_366884_.emitterTarget = p_366884_.creaking.getBoundingBox().getCenter();
                }

                Vec3 vec3 = Vec3.atCenterOf(p_367184_);
                float f = 0.2F + 0.8F * (float)(100 - p_366884_.emitter) / 100.0F;
                Vec3 vec31 = vec3.subtract(p_366884_.emitterTarget).scale((double)f).add(p_366884_.emitterTarget);
                BlockPos blockpos = BlockPos.containing(vec31);
                float f1 = (float)p_366884_.emitter / 2.0F / 100.0F + 0.5F;
                serverlevel.playSound(null, blockpos, SoundEvents.CREAKING_HEART_HURT, SoundSource.BLOCKS, f1, 1.0F);
            }

            p_366884_.emitter--;
        }

        if (p_366884_.ticker-- < 0) {
            p_366884_.ticker = 20;
            if (p_366884_.creaking != null) {
                if (CreakingHeartBlock.canSummonCreaking(p_360952_) && !(p_366884_.distanceToCreaking() > 34.0)) {
                    if (p_366884_.creaking.isRemoved()) {
                        p_366884_.creaking = null;
                    }

                    if (!CreakingHeartBlock.hasRequiredLogs(p_365574_, p_360952_, p_367184_) && p_366884_.creaking == null) {
                        p_360952_.setBlock(p_367184_, p_365574_.setValue(CreakingHeartBlock.CREAKING, CreakingHeartBlock.CreakingHeartState.DISABLED), 3);
                    }
                } else {
                    p_366884_.removeProtector(null);
                }
            } else if (!CreakingHeartBlock.hasRequiredLogs(p_365574_, p_360952_, p_367184_)) {
                p_360952_.setBlock(p_367184_, p_365574_.setValue(CreakingHeartBlock.CREAKING, CreakingHeartBlock.CreakingHeartState.DISABLED), 3);
            } else {
                if (!CreakingHeartBlock.canSummonCreaking(p_360952_)) {
                    if (p_365574_.getValue(CreakingHeartBlock.CREAKING) == CreakingHeartBlock.CreakingHeartState.ACTIVE) {
                        p_360952_.setBlock(p_367184_, p_365574_.setValue(CreakingHeartBlock.CREAKING, CreakingHeartBlock.CreakingHeartState.DORMANT), 3);
                        return;
                    }
                } else if (p_365574_.getValue(CreakingHeartBlock.CREAKING) == CreakingHeartBlock.CreakingHeartState.DORMANT) {
                    p_360952_.setBlock(p_367184_, p_365574_.setValue(CreakingHeartBlock.CREAKING, CreakingHeartBlock.CreakingHeartState.ACTIVE), 3);
                    return;
                }

                if (p_365574_.getValue(CreakingHeartBlock.CREAKING) == CreakingHeartBlock.CreakingHeartState.ACTIVE) {
                    if (p_360952_.getDifficulty() != Difficulty.PEACEFUL) {
                        if (p_360952_ instanceof ServerLevel serverlevel1 && !serverlevel1.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING)) {
                            return;
                        }

                        Player player = p_360952_.getNearestPlayer(
                            (double)p_367184_.getX(), (double)p_367184_.getY(), (double)p_367184_.getZ(), 32.0, false
                        );
                        if (player != null) {
                            p_366884_.creaking = spawnProtector((ServerLevel)p_360952_, p_366884_);
                            if (p_366884_.creaking != null) {
                                p_366884_.creaking.makeSound(SoundEvents.CREAKING_SPAWN);
                                p_360952_.playSound(null, p_366884_.getBlockPos(), SoundEvents.CREAKING_HEART_SPAWN, SoundSource.BLOCKS, 1.0F, 1.0F);
                            }
                        }
                    }
                }
            }
        }
    }

    private double distanceToCreaking() {
        return this.creaking == null ? 0.0 : Math.sqrt(this.creaking.distanceToSqr(Vec3.atBottomCenterOf(this.getBlockPos())));
    }

    @Nullable
    private static CreakingTransient spawnProtector(ServerLevel p_362442_, CreakingHeartBlockEntity p_369130_) {
        BlockPos blockpos = p_369130_.getBlockPos();
        Optional<CreakingTransient> optional = SpawnUtil.trySpawnMob(
            EntityType.CREAKING_TRANSIENT, EntitySpawnReason.SPAWNER, p_362442_, blockpos, 5, 16, 8, SpawnUtil.Strategy.ON_TOP_OF_COLLIDER_NO_LEAVES
        );
        if (optional.isEmpty()) {
            return null;
        } else {
            CreakingTransient creakingtransient = optional.get();
            p_362442_.gameEvent(creakingtransient, GameEvent.ENTITY_PLACE, creakingtransient.position());
            p_362442_.broadcastEntityEvent(creakingtransient, (byte)60);
            creakingtransient.bindToCreakingHeart(blockpos);
            return creakingtransient;
        }
    }

    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider p_366353_) {
        return this.saveCustomOnly(p_366353_);
    }

    public void creakingHurt() {
        if (this.creaking != null) {
            if (this.level instanceof ServerLevel serverlevel) {
                this.emitParticles(serverlevel, 20, false);
                this.emitter = 100;
                this.emitterTarget = this.creaking.getBoundingBox().getCenter();
            }
        }
    }

    private void emitParticles(ServerLevel p_366930_, int p_366541_, boolean p_366282_) {
        if (this.creaking != null) {
            int i = p_366282_ ? 16545810 : 6250335;
            RandomSource randomsource = p_366930_.random;

            for (double d0 = 0.0; d0 < (double)p_366541_; d0++) {
                Vec3 vec3 = this.creaking
                    .getBoundingBox()
                    .getMinPosition()
                    .add(
                        randomsource.nextDouble() * this.creaking.getBoundingBox().getXsize(),
                        randomsource.nextDouble() * this.creaking.getBoundingBox().getYsize(),
                        randomsource.nextDouble() * this.creaking.getBoundingBox().getZsize()
                    );
                Vec3 vec31 = Vec3.atLowerCornerOf(this.getBlockPos()).add(randomsource.nextDouble(), randomsource.nextDouble(), randomsource.nextDouble());
                if (p_366282_) {
                    Vec3 vec32 = vec3;
                    vec3 = vec31;
                    vec31 = vec32;
                }

                TargetColorParticleOption targetcolorparticleoption = new TargetColorParticleOption(vec31, i);
                p_366930_.sendParticles(targetcolorparticleoption, vec3.x, vec3.y, vec3.z, 1, 0.0, 0.0, 0.0, 0.0);
            }
        }
    }

    public void removeProtector(@Nullable DamageSource p_364053_) {
        if (this.creaking != null) {
            this.creaking.tearDown(p_364053_);
            this.creaking = null;
        }
    }

    public boolean isProtector(Creaking p_367915_) {
        return this.creaking == p_367915_;
    }

    public int getAnalogOutputSignal() {
        return this.outputSignal;
    }

    public int computeAnalogOutputSignal() {
        if (this.creaking == null) {
            return 0;
        } else {
            double d0 = this.distanceToCreaking();
            double d1 = Math.clamp(d0, 0.0, 32.0) / 32.0;
            return 15 - (int)Math.floor(d1 * 15.0);
        }
    }
}