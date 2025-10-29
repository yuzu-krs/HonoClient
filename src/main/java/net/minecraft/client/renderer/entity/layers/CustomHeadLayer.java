package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.Map;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.SkullModelBase;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.block.AbstractSkullBlock;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CustomHeadLayer<S extends LivingEntityRenderState, M extends EntityModel<S> & HeadedModel> extends RenderLayer<S, M> {
    private static final float ITEM_SCALE = 0.625F;
    private static final float SKULL_SCALE = 1.1875F;
    private final CustomHeadLayer.Transforms transforms;
    private final Map<SkullBlock.Type, SkullModelBase> skullModels;
    private final ItemRenderer itemRenderer;

    public CustomHeadLayer(RenderLayerParent<S, M> p_234829_, EntityModelSet p_234830_, ItemRenderer p_366480_) {
        this(p_234829_, p_234830_, CustomHeadLayer.Transforms.DEFAULT, p_366480_);
    }

    public CustomHeadLayer(RenderLayerParent<S, M> p_234822_, EntityModelSet p_234823_, CustomHeadLayer.Transforms p_362492_, ItemRenderer p_368013_) {
        super(p_234822_);
        this.transforms = p_362492_;
        this.skullModels = SkullBlockRenderer.createSkullRenderers(p_234823_);
        this.itemRenderer = p_368013_;
    }

    public void render(PoseStack p_116731_, MultiBufferSource p_116732_, int p_116733_, S p_363423_, float p_116735_, float p_116736_) {
        ItemStack itemstack = p_363423_.headItem;
        BakedModel bakedmodel = p_363423_.headItemModel;
        if (!itemstack.isEmpty() && bakedmodel != null) {
            label17: {
                Item item = itemstack.getItem();
                p_116731_.pushPose();
                p_116731_.scale(this.transforms.horizontalScale(), 1.0F, this.transforms.horizontalScale());
                M m = this.getParentModel();
                m.root().translateAndRotate(p_116731_);
                m.getHead().translateAndRotate(p_116731_);
                if (item instanceof BlockItem blockitem && blockitem.getBlock() instanceof AbstractSkullBlock abstractskullblock) {
                    p_116731_.translate(0.0F, this.transforms.skullYOffset(), 0.0F);
                    p_116731_.scale(1.1875F, -1.1875F, -1.1875F);
                    ResolvableProfile resolvableprofile = itemstack.get(DataComponents.PROFILE);
                    p_116731_.translate(-0.5, 0.0, -0.5);
                    SkullBlock.Type skullblock$type = abstractskullblock.getType();
                    SkullModelBase skullmodelbase = this.skullModels.get(skullblock$type);
                    RenderType rendertype = SkullBlockRenderer.getRenderType(skullblock$type, resolvableprofile);
                    SkullBlockRenderer.renderSkull(null, 180.0F, p_363423_.wornHeadAnimationPos, p_116731_, p_116732_, p_116733_, skullmodelbase, rendertype);
                    break label17;
                }

                if (!HumanoidArmorLayer.shouldRender(itemstack, EquipmentSlot.HEAD)) {
                    translateToHead(p_116731_, this.transforms);
                    this.itemRenderer.render(itemstack, ItemDisplayContext.HEAD, false, p_116731_, p_116732_, p_116733_, OverlayTexture.NO_OVERLAY, bakedmodel);
                }
            }

            p_116731_.popPose();
        }
    }

    public static void translateToHead(PoseStack p_174484_, CustomHeadLayer.Transforms p_366424_) {
        p_174484_.translate(0.0F, -0.25F + p_366424_.yOffset(), 0.0F);
        p_174484_.mulPose(Axis.YP.rotationDegrees(180.0F));
        p_174484_.scale(0.625F, -0.625F, -0.625F);
    }

    @OnlyIn(Dist.CLIENT)
    public static record Transforms(float yOffset, float skullYOffset, float horizontalScale) {
        public static final CustomHeadLayer.Transforms DEFAULT = new CustomHeadLayer.Transforms(0.0F, 0.0F, 1.0F);
    }
}