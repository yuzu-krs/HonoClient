package net.minecraft.client.model;

import java.util.Set;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.MeshTransformer;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.FelineRenderState;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FelineModel<T extends FelineRenderState> extends EntityModel<T> {
    public static final MeshTransformer BABY_TRANSFORMER = new BabyModelTransform(true, 10.0F, 4.0F, Set.of("head"));
    public static final MeshTransformer CAT_TRANSFORMER = MeshTransformer.scaling(0.8F);
    private static final float XO = 0.0F;
    private static final float YO = 16.0F;
    private static final float ZO = -9.0F;
    protected static final float BACK_LEG_Y = 18.0F;
    protected static final float BACK_LEG_Z = 5.0F;
    protected static final float FRONT_LEG_Y = 14.1F;
    private static final float FRONT_LEG_Z = -5.0F;
    private static final String TAIL_1 = "tail1";
    private static final String TAIL_2 = "tail2";
    protected final ModelPart leftHindLeg;
    protected final ModelPart rightHindLeg;
    protected final ModelPart leftFrontLeg;
    protected final ModelPart rightFrontLeg;
    protected final ModelPart tail1;
    protected final ModelPart tail2;
    protected final ModelPart head;
    protected final ModelPart body;

    public FelineModel(ModelPart p_365812_) {
        super(p_365812_);
        this.head = p_365812_.getChild("head");
        this.body = p_365812_.getChild("body");
        this.tail1 = p_365812_.getChild("tail1");
        this.tail2 = p_365812_.getChild("tail2");
        this.leftHindLeg = p_365812_.getChild("left_hind_leg");
        this.rightHindLeg = p_365812_.getChild("right_hind_leg");
        this.leftFrontLeg = p_365812_.getChild("left_front_leg");
        this.rightFrontLeg = p_365812_.getChild("right_front_leg");
    }

    public static MeshDefinition createBodyMesh(CubeDeformation p_369358_) {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        CubeDeformation cubedeformation = new CubeDeformation(-0.02F);
        partdefinition.addOrReplaceChild(
            "head",
            CubeListBuilder.create()
                .addBox("main", -2.5F, -2.0F, -3.0F, 5.0F, 4.0F, 5.0F, p_369358_)
                .addBox("nose", -1.5F, -0.001F, -4.0F, 3, 2, 2, p_369358_, 0, 24)
                .addBox("ear1", -2.0F, -3.0F, 0.0F, 1, 1, 2, p_369358_, 0, 10)
                .addBox("ear2", 1.0F, -3.0F, 0.0F, 1, 1, 2, p_369358_, 6, 10),
            PartPose.offset(0.0F, 15.0F, -9.0F)
        );
        partdefinition.addOrReplaceChild(
            "body",
            CubeListBuilder.create().texOffs(20, 0).addBox(-2.0F, 3.0F, -8.0F, 4.0F, 16.0F, 6.0F, p_369358_),
            PartPose.offsetAndRotation(0.0F, 12.0F, -10.0F, (float) (Math.PI / 2), 0.0F, 0.0F)
        );
        partdefinition.addOrReplaceChild(
            "tail1",
            CubeListBuilder.create().texOffs(0, 15).addBox(-0.5F, 0.0F, 0.0F, 1.0F, 8.0F, 1.0F, p_369358_),
            PartPose.offsetAndRotation(0.0F, 15.0F, 8.0F, 0.9F, 0.0F, 0.0F)
        );
        partdefinition.addOrReplaceChild(
            "tail2",
            CubeListBuilder.create().texOffs(4, 15).addBox(-0.5F, 0.0F, 0.0F, 1.0F, 8.0F, 1.0F, cubedeformation),
            PartPose.offset(0.0F, 20.0F, 14.0F)
        );
        CubeListBuilder cubelistbuilder = CubeListBuilder.create().texOffs(8, 13).addBox(-1.0F, 0.0F, 1.0F, 2.0F, 6.0F, 2.0F, p_369358_);
        partdefinition.addOrReplaceChild("left_hind_leg", cubelistbuilder, PartPose.offset(1.1F, 18.0F, 5.0F));
        partdefinition.addOrReplaceChild("right_hind_leg", cubelistbuilder, PartPose.offset(-1.1F, 18.0F, 5.0F));
        CubeListBuilder cubelistbuilder1 = CubeListBuilder.create().texOffs(40, 0).addBox(-1.0F, 0.0F, 0.0F, 2.0F, 10.0F, 2.0F, p_369358_);
        partdefinition.addOrReplaceChild("left_front_leg", cubelistbuilder1, PartPose.offset(1.2F, 14.1F, -5.0F));
        partdefinition.addOrReplaceChild("right_front_leg", cubelistbuilder1, PartPose.offset(-1.2F, 14.1F, -5.0F));
        return meshdefinition;
    }

    public void setupAnim(T p_363812_) {
        super.setupAnim(p_363812_);
        if (p_363812_.isCrouching) {
            this.body.y++;
            this.head.y += 2.0F;
            this.tail1.y++;
            this.tail2.y += -4.0F;
            this.tail2.z += 2.0F;
            this.tail1.xRot = (float) (Math.PI / 2);
            this.tail2.xRot = (float) (Math.PI / 2);
        } else if (p_363812_.isSprinting) {
            this.tail2.y = this.tail1.y;
            this.tail2.z += 2.0F;
            this.tail1.xRot = (float) (Math.PI / 2);
            this.tail2.xRot = (float) (Math.PI / 2);
        }

        this.head.xRot = p_363812_.xRot * (float) (Math.PI / 180.0);
        this.head.yRot = p_363812_.yRot * (float) (Math.PI / 180.0);
        if (!p_363812_.isSitting) {
            this.body.xRot = (float) (Math.PI / 2);
            float f = p_363812_.walkAnimationSpeed;
            float f1 = p_363812_.walkAnimationPos;
            if (p_363812_.isSprinting) {
                this.leftHindLeg.xRot = Mth.cos(f1 * 0.6662F) * f;
                this.rightHindLeg.xRot = Mth.cos(f1 * 0.6662F + 0.3F) * f;
                this.leftFrontLeg.xRot = Mth.cos(f1 * 0.6662F + (float) Math.PI + 0.3F) * f;
                this.rightFrontLeg.xRot = Mth.cos(f1 * 0.6662F + (float) Math.PI) * f;
                this.tail2.xRot = 1.7278761F + (float) (Math.PI / 10) * Mth.cos(f1) * f;
            } else {
                this.leftHindLeg.xRot = Mth.cos(f1 * 0.6662F) * f;
                this.rightHindLeg.xRot = Mth.cos(f1 * 0.6662F + (float) Math.PI) * f;
                this.leftFrontLeg.xRot = Mth.cos(f1 * 0.6662F + (float) Math.PI) * f;
                this.rightFrontLeg.xRot = Mth.cos(f1 * 0.6662F) * f;
                if (!p_363812_.isCrouching) {
                    this.tail2.xRot = 1.7278761F + (float) (Math.PI / 4) * Mth.cos(f1) * f;
                } else {
                    this.tail2.xRot = 1.7278761F + 0.47123894F * Mth.cos(f1) * f;
                }
            }
        }

        float f2 = p_363812_.ageScale;
        if (p_363812_.isSitting) {
            this.body.xRot = (float) (Math.PI / 4);
            this.body.y += -4.0F * f2;
            this.body.z += 5.0F * f2;
            this.head.y += -3.3F * f2;
            this.head.z += 1.0F * f2;
            this.tail1.y += 8.0F * f2;
            this.tail1.z += -2.0F * f2;
            this.tail2.y += 2.0F * f2;
            this.tail2.z += -0.8F * f2;
            this.tail1.xRot = 1.7278761F;
            this.tail2.xRot = 2.670354F;
            this.leftFrontLeg.xRot = (float) (-Math.PI / 20);
            this.leftFrontLeg.y += 2.0F * f2;
            this.leftFrontLeg.z -= 2.0F * f2;
            this.rightFrontLeg.xRot = (float) (-Math.PI / 20);
            this.rightFrontLeg.y += 2.0F * f2;
            this.rightFrontLeg.z -= 2.0F * f2;
            this.leftHindLeg.xRot = (float) (-Math.PI / 2);
            this.leftHindLeg.y += 3.0F * f2;
            this.leftHindLeg.z -= 4.0F * f2;
            this.rightHindLeg.xRot = (float) (-Math.PI / 2);
            this.rightHindLeg.y += 3.0F * f2;
            this.rightHindLeg.z -= 4.0F * f2;
        }

        if (p_363812_.lieDownAmount > 0.0F) {
            this.head.zRot = Mth.rotLerp(p_363812_.lieDownAmount, this.head.zRot, -1.2707963F);
            this.head.yRot = Mth.rotLerp(p_363812_.lieDownAmount, this.head.yRot, 1.2707963F);
            this.leftFrontLeg.xRot = -1.2707963F;
            this.rightFrontLeg.xRot = -0.47079635F;
            this.rightFrontLeg.zRot = -0.2F;
            this.rightFrontLeg.x += f2;
            this.leftHindLeg.xRot = -0.4F;
            this.rightHindLeg.xRot = 0.5F;
            this.rightHindLeg.zRot = -0.5F;
            this.rightHindLeg.x += 0.8F * f2;
            this.rightHindLeg.y += 2.0F * f2;
            this.tail1.xRot = Mth.rotLerp(p_363812_.lieDownAmountTail, this.tail1.xRot, 0.8F);
            this.tail2.xRot = Mth.rotLerp(p_363812_.lieDownAmountTail, this.tail2.xRot, -0.4F);
        }

        if (p_363812_.relaxStateOneAmount > 0.0F) {
            this.head.xRot = Mth.rotLerp(p_363812_.relaxStateOneAmount, this.head.xRot, -0.58177644F);
        }
    }
}