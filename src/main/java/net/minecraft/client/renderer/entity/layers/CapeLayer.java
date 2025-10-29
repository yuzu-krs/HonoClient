package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerCapeModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.client.resources.model.EquipmentModelSet;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.EquipmentModel;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CapeLayer extends RenderLayer<PlayerRenderState, PlayerModel> {
    private final HumanoidModel<PlayerRenderState> model;
    private final EquipmentModelSet equipmentModels;

    public CapeLayer(RenderLayerParent<PlayerRenderState, PlayerModel> p_116602_, EntityModelSet p_364158_, EquipmentModelSet p_365349_) {
        super(p_116602_);
        this.model = new PlayerCapeModel<>(p_364158_.bakeLayer(ModelLayers.PLAYER_CAPE));
        this.equipmentModels = p_365349_;
    }

    private boolean hasLayer(ItemStack p_362441_, EquipmentModel.LayerType p_367137_) {
        Equippable equippable = p_362441_.get(DataComponents.EQUIPPABLE);
        if (equippable != null && !equippable.model().isEmpty()) {
            EquipmentModel equipmentmodel = this.equipmentModels.get(equippable.model().get());
            return !equipmentmodel.getLayers(p_367137_).isEmpty();
        } else {
            return false;
        }
    }

    public void render(PoseStack p_116615_, MultiBufferSource p_116616_, int p_116617_, PlayerRenderState p_367257_, float p_116619_, float p_116620_) {
        if (!p_367257_.isInvisible && p_367257_.showCape) {
            PlayerSkin playerskin = p_367257_.skin;
            if (playerskin.capeTexture() != null) {
                if (!this.hasLayer(p_367257_.chestItem, EquipmentModel.LayerType.WINGS)) {
                    p_116615_.pushPose();
                    if (this.hasLayer(p_367257_.chestItem, EquipmentModel.LayerType.HUMANOID)) {
                        p_116615_.translate(0.0F, -0.053125F, 0.06875F);
                    }

                    VertexConsumer vertexconsumer = p_116616_.getBuffer(RenderType.entitySolid(playerskin.capeTexture()));
                    this.getParentModel().copyPropertiesTo(this.model);
                    this.model.setupAnim(p_367257_);
                    this.model.renderToBuffer(p_116615_, vertexconsumer, p_116617_, OverlayTexture.NO_OVERLAY);
                    p_116615_.popPose();
                }
            }
        }
    }
}