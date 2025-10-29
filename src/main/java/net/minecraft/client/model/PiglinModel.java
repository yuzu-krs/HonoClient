package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.PiglinRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.monster.piglin.PiglinArmPose;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PiglinModel extends AbstractPiglinModel<PiglinRenderState> {
    public PiglinModel(ModelPart p_170810_) {
        super(p_170810_);
    }

    public void setupAnim(PiglinRenderState p_363261_) {
        super.setupAnim(p_363261_);
        float f = (float) (Math.PI / 6);
        float f1 = p_363261_.attackTime;
        PiglinArmPose piglinarmpose = p_363261_.armPose;
        if (piglinarmpose == PiglinArmPose.DANCING) {
            float f2 = p_363261_.ageInTicks / 60.0F;
            this.rightEar.zRot = (float) (Math.PI / 6) + (float) (Math.PI / 180.0) * Mth.sin(f2 * 30.0F) * 10.0F;
            this.leftEar.zRot = (float) (-Math.PI / 6) - (float) (Math.PI / 180.0) * Mth.cos(f2 * 30.0F) * 10.0F;
            this.head.x = this.head.x + Mth.sin(f2 * 10.0F);
            this.head.y = this.head.y + Mth.sin(f2 * 40.0F) + 0.4F;
            this.rightArm.zRot = (float) (Math.PI / 180.0) * (70.0F + Mth.cos(f2 * 40.0F) * 10.0F);
            this.leftArm.zRot = this.rightArm.zRot * -1.0F;
            this.rightArm.y = this.rightArm.y + (Mth.sin(f2 * 40.0F) * 0.5F - 0.5F);
            this.leftArm.y = this.leftArm.y + Mth.sin(f2 * 40.0F) * 0.5F + 0.5F;
            this.body.y = this.body.y + Mth.sin(f2 * 40.0F) * 0.35F;
        } else if (piglinarmpose == PiglinArmPose.ATTACKING_WITH_MELEE_WEAPON && f1 == 0.0F) {
            this.holdWeaponHigh(p_363261_);
        } else if (piglinarmpose == PiglinArmPose.CROSSBOW_HOLD) {
            AnimationUtils.animateCrossbowHold(this.rightArm, this.leftArm, this.head, p_363261_.mainArm == HumanoidArm.RIGHT);
        } else if (piglinarmpose == PiglinArmPose.CROSSBOW_CHARGE) {
            AnimationUtils.animateCrossbowCharge(this.rightArm, this.leftArm, p_363261_.maxCrossbowChageDuration, p_363261_.ticksUsingItem, p_363261_.mainArm == HumanoidArm.RIGHT);
        } else if (piglinarmpose == PiglinArmPose.ADMIRING_ITEM) {
            this.head.xRot = 0.5F;
            this.head.yRot = 0.0F;
            if (p_363261_.mainArm == HumanoidArm.LEFT) {
                this.rightArm.yRot = -0.5F;
                this.rightArm.xRot = -0.9F;
            } else {
                this.leftArm.yRot = 0.5F;
                this.leftArm.xRot = -0.9F;
            }
        }
    }

    protected void setupAttackAnimation(PiglinRenderState p_368036_, float p_103352_) {
        float f = p_368036_.attackTime;
        if (f > 0.0F && p_368036_.armPose == PiglinArmPose.ATTACKING_WITH_MELEE_WEAPON) {
            AnimationUtils.swingWeaponDown(this.rightArm, this.leftArm, p_368036_.mainArm, f, p_368036_.ageInTicks);
        } else {
            super.setupAttackAnimation(p_368036_, p_103352_);
        }
    }

    private void holdWeaponHigh(PiglinRenderState p_368860_) {
        if (p_368860_.mainArm == HumanoidArm.LEFT) {
            this.leftArm.xRot = -1.8F;
        } else {
            this.rightArm.xRot = -1.8F;
        }
    }

    @Override
    public void setAllVisible(boolean p_369030_) {
        super.setAllVisible(p_369030_);
        this.leftSleeve.visible = p_369030_;
        this.rightSleeve.visible = p_369030_;
        this.leftPants.visible = p_369030_;
        this.rightPants.visible = p_369030_;
        this.jacket.visible = p_369030_;
    }
}