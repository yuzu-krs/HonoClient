package net.minecraft.client.model;

import java.util.Set;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.MeshTransformer;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.RabbitRenderState;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RabbitModel extends EntityModel<RabbitRenderState> {
    private static final float REAR_JUMP_ANGLE = 50.0F;
    private static final float FRONT_JUMP_ANGLE = -40.0F;
    private static final float NEW_SCALE = 0.6F;
    private static final MeshTransformer ADULT_TRANSFORMER = MeshTransformer.scaling(0.6F);
    private static final MeshTransformer BABY_TRANSFORMER = new BabyModelTransform(
        true, 22.0F, 2.0F, 2.65F, 2.5F, 36.0F, Set.of("head", "left_ear", "right_ear", "nose")
    );
    private static final String LEFT_HAUNCH = "left_haunch";
    private static final String RIGHT_HAUNCH = "right_haunch";
    private final ModelPart leftRearFoot;
    private final ModelPart rightRearFoot;
    private final ModelPart leftHaunch;
    private final ModelPart rightHaunch;
    private final ModelPart leftFrontLeg;
    private final ModelPart rightFrontLeg;
    private final ModelPart head;
    private final ModelPart rightEar;
    private final ModelPart leftEar;
    private final ModelPart nose;

    public RabbitModel(ModelPart p_170881_) {
        super(p_170881_);
        this.leftRearFoot = p_170881_.getChild("left_hind_foot");
        this.rightRearFoot = p_170881_.getChild("right_hind_foot");
        this.leftHaunch = p_170881_.getChild("left_haunch");
        this.rightHaunch = p_170881_.getChild("right_haunch");
        this.leftFrontLeg = p_170881_.getChild("left_front_leg");
        this.rightFrontLeg = p_170881_.getChild("right_front_leg");
        this.head = p_170881_.getChild("head");
        this.rightEar = p_170881_.getChild("right_ear");
        this.leftEar = p_170881_.getChild("left_ear");
        this.nose = p_170881_.getChild("nose");
    }

    public static LayerDefinition createBodyLayer(boolean p_369409_) {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        partdefinition.addOrReplaceChild(
            "left_hind_foot",
            CubeListBuilder.create().texOffs(26, 24).addBox(-1.0F, 5.5F, -3.7F, 2.0F, 1.0F, 7.0F),
            PartPose.offset(3.0F, 17.5F, 3.7F)
        );
        partdefinition.addOrReplaceChild(
            "right_hind_foot",
            CubeListBuilder.create().texOffs(8, 24).addBox(-1.0F, 5.5F, -3.7F, 2.0F, 1.0F, 7.0F),
            PartPose.offset(-3.0F, 17.5F, 3.7F)
        );
        partdefinition.addOrReplaceChild(
            "left_haunch",
            CubeListBuilder.create().texOffs(30, 15).addBox(-1.0F, 0.0F, 0.0F, 2.0F, 4.0F, 5.0F),
            PartPose.offsetAndRotation(3.0F, 17.5F, 3.7F, (float) (-Math.PI / 9), 0.0F, 0.0F)
        );
        partdefinition.addOrReplaceChild(
            "right_haunch",
            CubeListBuilder.create().texOffs(16, 15).addBox(-1.0F, 0.0F, 0.0F, 2.0F, 4.0F, 5.0F),
            PartPose.offsetAndRotation(-3.0F, 17.5F, 3.7F, (float) (-Math.PI / 9), 0.0F, 0.0F)
        );
        partdefinition.addOrReplaceChild(
            "body",
            CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -2.0F, -10.0F, 6.0F, 5.0F, 10.0F),
            PartPose.offsetAndRotation(0.0F, 19.0F, 8.0F, (float) (-Math.PI / 9), 0.0F, 0.0F)
        );
        partdefinition.addOrReplaceChild(
            "left_front_leg",
            CubeListBuilder.create().texOffs(8, 15).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 7.0F, 2.0F),
            PartPose.offsetAndRotation(3.0F, 17.0F, -1.0F, (float) (-Math.PI / 18), 0.0F, 0.0F)
        );
        partdefinition.addOrReplaceChild(
            "right_front_leg",
            CubeListBuilder.create().texOffs(0, 15).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 7.0F, 2.0F),
            PartPose.offsetAndRotation(-3.0F, 17.0F, -1.0F, (float) (-Math.PI / 18), 0.0F, 0.0F)
        );
        partdefinition.addOrReplaceChild(
            "head", CubeListBuilder.create().texOffs(32, 0).addBox(-2.5F, -4.0F, -5.0F, 5.0F, 4.0F, 5.0F), PartPose.offset(0.0F, 16.0F, -1.0F)
        );
        partdefinition.addOrReplaceChild(
            "right_ear",
            CubeListBuilder.create().texOffs(52, 0).addBox(-2.5F, -9.0F, -1.0F, 2.0F, 5.0F, 1.0F),
            PartPose.offsetAndRotation(0.0F, 16.0F, -1.0F, 0.0F, (float) (-Math.PI / 12), 0.0F)
        );
        partdefinition.addOrReplaceChild(
            "left_ear",
            CubeListBuilder.create().texOffs(58, 0).addBox(0.5F, -9.0F, -1.0F, 2.0F, 5.0F, 1.0F),
            PartPose.offsetAndRotation(0.0F, 16.0F, -1.0F, 0.0F, (float) (Math.PI / 12), 0.0F)
        );
        partdefinition.addOrReplaceChild(
            "tail",
            CubeListBuilder.create().texOffs(52, 6).addBox(-1.5F, -1.5F, 0.0F, 3.0F, 3.0F, 2.0F),
            PartPose.offsetAndRotation(0.0F, 20.0F, 7.0F, -0.3490659F, 0.0F, 0.0F)
        );
        partdefinition.addOrReplaceChild(
            "nose", CubeListBuilder.create().texOffs(32, 9).addBox(-0.5F, -2.5F, -5.5F, 1.0F, 1.0F, 1.0F), PartPose.offset(0.0F, 16.0F, -1.0F)
        );
        return LayerDefinition.create(meshdefinition, 64, 32).apply(p_369409_ ? BABY_TRANSFORMER : ADULT_TRANSFORMER);
    }

    public void setupAnim(RabbitRenderState p_366536_) {
        super.setupAnim(p_366536_);
        this.nose.xRot = p_366536_.xRot * (float) (Math.PI / 180.0);
        this.head.xRot = p_366536_.xRot * (float) (Math.PI / 180.0);
        this.rightEar.xRot = p_366536_.xRot * (float) (Math.PI / 180.0);
        this.leftEar.xRot = p_366536_.xRot * (float) (Math.PI / 180.0);
        this.nose.yRot = p_366536_.yRot * (float) (Math.PI / 180.0);
        this.head.yRot = p_366536_.yRot * (float) (Math.PI / 180.0);
        this.rightEar.yRot = this.nose.yRot - (float) (Math.PI / 12);
        this.leftEar.yRot = this.nose.yRot + (float) (Math.PI / 12);
        float f = Mth.sin(p_366536_.jumpCompletion * (float) Math.PI);
        this.leftHaunch.xRot = (f * 50.0F - 21.0F) * (float) (Math.PI / 180.0);
        this.rightHaunch.xRot = (f * 50.0F - 21.0F) * (float) (Math.PI / 180.0);
        this.leftRearFoot.xRot = f * 50.0F * (float) (Math.PI / 180.0);
        this.rightRearFoot.xRot = f * 50.0F * (float) (Math.PI / 180.0);
        this.leftFrontLeg.xRot = (f * -40.0F - 11.0F) * (float) (Math.PI / 180.0);
        this.rightFrontLeg.xRot = (f * -40.0F - 11.0F) * (float) (Math.PI / 180.0);
    }
}