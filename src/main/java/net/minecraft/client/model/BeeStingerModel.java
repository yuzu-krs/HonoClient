package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BeeStingerModel extends Model {
    public BeeStingerModel(ModelPart p_365461_) {
        super(p_365461_, RenderType::entityCutout);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        CubeListBuilder cubelistbuilder = CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -0.5F, 0.0F, 2.0F, 1.0F, 0.0F);
        partdefinition.addOrReplaceChild("cross_1", cubelistbuilder, PartPose.rotation((float) (Math.PI / 4), 0.0F, 0.0F));
        partdefinition.addOrReplaceChild("cross_2", cubelistbuilder, PartPose.rotation((float) (Math.PI * 3.0 / 4.0), 0.0F, 0.0F));
        return LayerDefinition.create(meshdefinition, 16, 16);
    }
}