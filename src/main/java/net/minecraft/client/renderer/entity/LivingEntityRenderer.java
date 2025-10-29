package net.minecraft.client.renderer.entity;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.scores.Team;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class LivingEntityRenderer<T extends LivingEntity, S extends LivingEntityRenderState, M extends EntityModel<? super S>>
    extends EntityRenderer<T, S>
    implements RenderLayerParent<S, M> {
    private static final float EYE_BED_OFFSET = 0.1F;
    protected M model;
    protected final ItemRenderer itemRenderer;
    protected final List<RenderLayer<S, M>> layers = Lists.newArrayList();

    public LivingEntityRenderer(EntityRendererProvider.Context p_174289_, M p_174290_, float p_174291_) {
        super(p_174289_);
        this.itemRenderer = p_174289_.getItemRenderer();
        this.model = p_174290_;
        this.shadowRadius = p_174291_;
    }

    protected final boolean addLayer(RenderLayer<S, M> p_115327_) {
        return this.layers.add(p_115327_);
    }

    @Override
    public M getModel() {
        return this.model;
    }

    protected AABB getBoundingBoxForCulling(T p_361472_) {
        AABB aabb = super.getBoundingBoxForCulling(p_361472_);
        if (p_361472_.getItemBySlot(EquipmentSlot.HEAD).is(Items.DRAGON_HEAD)) {
            float f = 0.5F;
            return aabb.inflate(0.5, 0.5, 0.5);
        } else {
            return aabb;
        }
    }

    public void render(S p_364280_, PoseStack p_115311_, MultiBufferSource p_115312_, int p_115313_) {
        p_115311_.pushPose();
        if (p_364280_.hasPose(Pose.SLEEPING)) {
            Direction direction = p_364280_.bedOrientation;
            if (direction != null) {
                float f = p_364280_.eyeHeight - 0.1F;
                p_115311_.translate((float)(-direction.getStepX()) * f, 0.0F, (float)(-direction.getStepZ()) * f);
            }
        }

        float f1 = p_364280_.scale;
        p_115311_.scale(f1, f1, f1);
        this.setupRotations(p_364280_, p_115311_, p_364280_.bodyRot, f1);
        p_115311_.scale(-1.0F, -1.0F, 1.0F);
        this.scale(p_364280_, p_115311_);
        p_115311_.translate(0.0F, -1.501F, 0.0F);
        this.model.setupAnim(p_364280_);
        boolean flag1 = this.isBodyVisible(p_364280_);
        boolean flag = !flag1 && !p_364280_.isInvisibleToPlayer;
        RenderType rendertype = this.getRenderType(p_364280_, flag1, flag, p_364280_.appearsGlowing);
        if (rendertype != null) {
            VertexConsumer vertexconsumer = p_115312_.getBuffer(rendertype);
            int i = getOverlayCoords(p_364280_, this.getWhiteOverlayProgress(p_364280_));
            int j = flag ? 654311423 : -1;
            int k = ARGB.multiply(j, this.getModelTint(p_364280_));
            this.model.renderToBuffer(p_115311_, vertexconsumer, p_115313_, i, k);
        }

        if (this.shouldRenderLayers(p_364280_)) {
            for (RenderLayer<S, M> renderlayer : this.layers) {
                renderlayer.render(p_115311_, p_115312_, p_115313_, p_364280_, p_364280_.yRot, p_364280_.xRot);
            }
        }

        p_115311_.popPose();
        super.render(p_364280_, p_115311_, p_115312_, p_115313_);
    }

    protected boolean shouldRenderLayers(S p_360804_) {
        return true;
    }

    protected int getModelTint(S p_361319_) {
        return -1;
    }

    public abstract ResourceLocation getTextureLocation(S p_362468_);

    @Nullable
    protected RenderType getRenderType(S p_369777_, boolean p_115323_, boolean p_115324_, boolean p_115325_) {
        ResourceLocation resourcelocation = this.getTextureLocation(p_369777_);
        if (p_115324_) {
            return RenderType.itemEntityTranslucentCull(resourcelocation);
        } else if (p_115323_) {
            return this.model.renderType(resourcelocation);
        } else {
            return p_115325_ ? RenderType.outline(resourcelocation) : null;
        }
    }

    public static int getOverlayCoords(LivingEntityRenderState p_365259_, float p_115340_) {
        return OverlayTexture.pack(OverlayTexture.u(p_115340_), OverlayTexture.v(p_365259_.hasRedOverlay));
    }

    protected boolean isBodyVisible(S p_363166_) {
        return !p_363166_.isInvisible;
    }

    private static float sleepDirectionToRotation(Direction p_115329_) {
        switch (p_115329_) {
            case SOUTH:
                return 90.0F;
            case WEST:
                return 0.0F;
            case NORTH:
                return 270.0F;
            case EAST:
                return 180.0F;
            default:
                return 0.0F;
        }
    }

    protected boolean isShaking(S p_361206_) {
        return p_361206_.isFullyFrozen;
    }

    protected void setupRotations(S p_370120_, PoseStack p_115318_, float p_115319_, float p_115320_) {
        if (this.isShaking(p_370120_)) {
            p_115319_ += (float)(Math.cos((double)((float)Mth.floor(p_370120_.ageInTicks) * 3.25F)) * Math.PI * 0.4F);
        }

        if (!p_370120_.hasPose(Pose.SLEEPING)) {
            p_115318_.mulPose(Axis.YP.rotationDegrees(180.0F - p_115319_));
        }

        if (p_370120_.deathTime > 0.0F) {
            float f = (p_370120_.deathTime - 1.0F) / 20.0F * 1.6F;
            f = Mth.sqrt(f);
            if (f > 1.0F) {
                f = 1.0F;
            }

            p_115318_.mulPose(Axis.ZP.rotationDegrees(f * this.getFlipDegrees()));
        } else if (p_370120_.isAutoSpinAttack) {
            p_115318_.mulPose(Axis.XP.rotationDegrees(-90.0F - p_370120_.xRot));
            p_115318_.mulPose(Axis.YP.rotationDegrees(p_370120_.ageInTicks * -75.0F));
        } else if (p_370120_.hasPose(Pose.SLEEPING)) {
            Direction direction = p_370120_.bedOrientation;
            float f1 = direction != null ? sleepDirectionToRotation(direction) : p_115319_;
            p_115318_.mulPose(Axis.YP.rotationDegrees(f1));
            p_115318_.mulPose(Axis.ZP.rotationDegrees(this.getFlipDegrees()));
            p_115318_.mulPose(Axis.YP.rotationDegrees(270.0F));
        } else if (p_370120_.isUpsideDown) {
            p_115318_.translate(0.0F, (p_370120_.boundingBoxHeight + 0.1F) / p_115320_, 0.0F);
            p_115318_.mulPose(Axis.ZP.rotationDegrees(180.0F));
        }
    }

    protected float getFlipDegrees() {
        return 90.0F;
    }

    protected float getWhiteOverlayProgress(S p_367139_) {
        return 0.0F;
    }

    protected void scale(S p_363445_, PoseStack p_115315_) {
    }

    protected boolean shouldShowName(T p_115333_, double p_365822_) {
        if (p_115333_.isDiscrete()) {
            float f = 32.0F;
            if (p_365822_ >= 1024.0) {
                return false;
            }
        }

        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer localplayer = minecraft.player;
        boolean flag = !p_115333_.isInvisibleTo(localplayer);
        if (p_115333_ != localplayer) {
            Team team = p_115333_.getTeam();
            Team team1 = localplayer.getTeam();
            if (team != null) {
                Team.Visibility team$visibility = team.getNameTagVisibility();
                switch (team$visibility) {
                    case ALWAYS:
                        return flag;
                    case NEVER:
                        return false;
                    case HIDE_FOR_OTHER_TEAMS:
                        return team1 == null ? flag : team.isAlliedTo(team1) && (team.canSeeFriendlyInvisibles() || flag);
                    case HIDE_FOR_OWN_TEAM:
                        return team1 == null ? flag : !team.isAlliedTo(team1) && flag;
                    default:
                        return true;
                }
            }
        }

        return Minecraft.renderNames() && p_115333_ != minecraft.getCameraEntity() && flag && !p_115333_.isVehicle();
    }

    public static boolean isEntityUpsideDown(LivingEntity p_194454_) {
        if (p_194454_ instanceof Player || p_194454_.hasCustomName()) {
            String s = ChatFormatting.stripFormatting(p_194454_.getName().getString());
            if ("Dinnerbone".equals(s) || "Grumm".equals(s)) {
                return !(p_194454_ instanceof Player) || ((Player)p_194454_).isModelPartShown(PlayerModelPart.CAPE);
            }
        }

        return false;
    }

    protected float getShadowRadius(S p_363803_) {
        return super.getShadowRadius(p_363803_) * p_363803_.scale;
    }

    public void extractRenderState(T p_368665_, S p_363057_, float p_364497_) {
        super.extractRenderState(p_368665_, p_363057_, p_364497_);
        float f = Mth.rotLerp(p_364497_, p_368665_.yHeadRotO, p_368665_.yHeadRot);
        p_363057_.bodyRot = solveBodyRot(p_368665_, f, p_364497_);
        p_363057_.yRot = Mth.wrapDegrees(f - p_363057_.bodyRot);
        p_363057_.xRot = p_368665_.getXRot(p_364497_);
        p_363057_.customName = p_368665_.getCustomName();
        p_363057_.isUpsideDown = isEntityUpsideDown(p_368665_);
        if (p_363057_.isUpsideDown) {
            p_363057_.xRot *= -1.0F;
            p_363057_.yRot *= -1.0F;
        }

        if (!p_368665_.isPassenger() && p_368665_.isAlive()) {
            p_363057_.walkAnimationPos = p_368665_.walkAnimation.position(p_364497_);
            p_363057_.walkAnimationSpeed = p_368665_.walkAnimation.speed(p_364497_);
        } else {
            p_363057_.walkAnimationPos = 0.0F;
            p_363057_.walkAnimationSpeed = 0.0F;
        }

        if (p_368665_.getVehicle() instanceof LivingEntity livingentity) {
            p_363057_.wornHeadAnimationPos = livingentity.walkAnimation.position(p_364497_);
        } else {
            p_363057_.wornHeadAnimationPos = p_363057_.walkAnimationPos;
        }

        p_363057_.scale = p_368665_.getScale();
        p_363057_.ageScale = p_368665_.getAgeScale();
        p_363057_.pose = p_368665_.getPose();
        p_363057_.bedOrientation = p_368665_.getBedOrientation();
        if (p_363057_.bedOrientation != null) {
            p_363057_.eyeHeight = p_368665_.getEyeHeight(Pose.STANDING);
        }

        p_363057_.isFullyFrozen = p_368665_.isFullyFrozen();
        p_363057_.isBaby = p_368665_.isBaby();
        p_363057_.isInWater = p_368665_.isInWater();
        p_363057_.isAutoSpinAttack = p_368665_.isAutoSpinAttack();
        p_363057_.hasRedOverlay = p_368665_.hurtTime > 0 || p_368665_.deathTime > 0;
        ItemStack itemstack1 = p_368665_.getItemBySlot(EquipmentSlot.HEAD);
        p_363057_.headItem = itemstack1.copy();
        p_363057_.headItemModel = this.itemRenderer.resolveItemModel(itemstack1, p_368665_, ItemDisplayContext.HEAD);
        p_363057_.mainArm = p_368665_.getMainArm();
        ItemStack itemstack2 = p_368665_.getItemHeldByArm(HumanoidArm.RIGHT);
        ItemStack itemstack = p_368665_.getItemHeldByArm(HumanoidArm.LEFT);
        p_363057_.rightHandItem = itemstack2.copy();
        p_363057_.leftHandItem = itemstack.copy();
        p_363057_.rightHandItemModel = this.itemRenderer.resolveItemModel(itemstack2, p_368665_, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND);
        p_363057_.leftHandItemModel = this.itemRenderer.resolveItemModel(itemstack, p_368665_, ItemDisplayContext.THIRD_PERSON_LEFT_HAND);
        p_363057_.deathTime = p_368665_.deathTime > 0 ? (float)p_368665_.deathTime + p_364497_ : 0.0F;
        Minecraft minecraft = Minecraft.getInstance();
        p_363057_.isInvisibleToPlayer = p_363057_.isInvisible && p_368665_.isInvisibleTo(minecraft.player);
        p_363057_.appearsGlowing = minecraft.shouldEntityAppearGlowing(p_368665_);
    }

    private static float solveBodyRot(LivingEntity p_367822_, float p_362662_, float p_362007_) {
        if (p_367822_.getVehicle() instanceof LivingEntity livingentity) {
            float f2 = Mth.rotLerp(p_362007_, livingentity.yBodyRotO, livingentity.yBodyRot);
            float f = 85.0F;
            float f1 = Mth.clamp(Mth.wrapDegrees(p_362662_ - f2), -85.0F, 85.0F);
            f2 = p_362662_ - f1;
            if (Math.abs(f1) > 50.0F) {
                f2 += f1 * 0.2F;
            }

            return f2;
        } else {
            return Mth.rotLerp(p_362007_, p_367822_.yBodyRotO, p_367822_.yBodyRot);
        }
    }
}