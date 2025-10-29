package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexMultiConsumer;
import com.mojang.math.MatrixUtil;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.ItemModelShaper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ARGB;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BundleItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ItemRenderer implements ResourceManagerReloadListener {
    public static final ResourceLocation ENCHANTED_GLINT_ENTITY = ResourceLocation.withDefaultNamespace("textures/misc/enchanted_glint_entity.png");
    public static final ResourceLocation ENCHANTED_GLINT_ITEM = ResourceLocation.withDefaultNamespace("textures/misc/enchanted_glint_item.png");
    public static final int GUI_SLOT_CENTER_X = 8;
    public static final int GUI_SLOT_CENTER_Y = 8;
    public static final int ITEM_DECORATION_BLIT_OFFSET = 200;
    public static final float COMPASS_FOIL_UI_SCALE = 0.5F;
    public static final float COMPASS_FOIL_FIRST_PERSON_SCALE = 0.75F;
    public static final float COMPASS_FOIL_TEXTURE_SCALE = 0.0078125F;
    public static final ModelResourceLocation TRIDENT_MODEL = ModelResourceLocation.inventory(ResourceLocation.withDefaultNamespace("trident"));
    public static final ModelResourceLocation SPYGLASS_MODEL = ModelResourceLocation.inventory(ResourceLocation.withDefaultNamespace("spyglass"));
    private final ModelManager modelManager;
    private final ItemModelShaper itemModelShaper;
    private final ItemColors itemColors;
    private final BlockEntityWithoutLevelRenderer blockEntityRenderer;

    public ItemRenderer(ModelManager p_266850_, ItemColors p_267016_, BlockEntityWithoutLevelRenderer p_267049_) {
        this.modelManager = p_266850_;
        this.itemModelShaper = new ItemModelShaper(p_266850_);
        this.blockEntityRenderer = p_267049_;
        this.itemColors = p_267016_;
    }

    private void renderModelLists(BakedModel p_115190_, ItemStack p_115191_, int p_115192_, int p_115193_, PoseStack p_115194_, VertexConsumer p_115195_) {
        RandomSource randomsource = RandomSource.create();
        long i = 42L;

        for (Direction direction : Direction.values()) {
            randomsource.setSeed(42L);
            this.renderQuadList(p_115194_, p_115195_, p_115190_.getQuads(null, direction, randomsource), p_115191_, p_115192_, p_115193_);
        }

        randomsource.setSeed(42L);
        this.renderQuadList(p_115194_, p_115195_, p_115190_.getQuads(null, null, randomsource), p_115191_, p_115192_, p_115193_);
    }

    public void render(
        ItemStack p_115144_,
        ItemDisplayContext p_270188_,
        boolean p_115146_,
        PoseStack p_115147_,
        MultiBufferSource p_115148_,
        int p_115149_,
        int p_115150_,
        BakedModel p_115151_
    ) {
        if (!p_115144_.isEmpty()) {
            this.renderSimpleItemModel(p_115144_, p_270188_, p_115146_, p_115147_, p_115148_, p_115149_, p_115150_, p_115151_, shouldRenderItemFlat(p_270188_));
        }
    }

    public void renderBundleItem(
        ItemStack p_366843_,
        ItemDisplayContext p_364164_,
        boolean p_366015_,
        PoseStack p_363983_,
        MultiBufferSource p_366400_,
        int p_364183_,
        int p_366202_,
        BakedModel p_364448_,
        @Nullable Level p_364558_,
        @Nullable LivingEntity p_370019_,
        int p_365291_
    ) {
        if (p_366843_.getItem() instanceof BundleItem bundleitem) {
            if (BundleItem.hasSelectedItem(p_366843_)) {
                boolean flag = shouldRenderItemFlat(p_364164_);
                BakedModel bakedmodel = this.resolveModelOverride(this.itemModelShaper.getItemModel(bundleitem.openBackModel()), p_366843_, p_364558_, p_370019_, p_365291_);
                this.renderItemModelRaw(p_366843_, p_364164_, p_366015_, p_363983_, p_366400_, p_364183_, p_366202_, bakedmodel, flag, -1.5F);
                ItemStack itemstack = BundleItem.getSelectedItemStack(p_366843_);
                BakedModel bakedmodel1 = this.getModel(itemstack, p_364558_, p_370019_, p_365291_);
                this.renderSimpleItemModel(itemstack, p_364164_, p_366015_, p_363983_, p_366400_, p_364183_, p_366202_, bakedmodel1, flag);
                BakedModel bakedmodel2 = this.resolveModelOverride(this.itemModelShaper.getItemModel(bundleitem.openFrontModel()), p_366843_, p_364558_, p_370019_, p_365291_);
                this.renderItemModelRaw(p_366843_, p_364164_, p_366015_, p_363983_, p_366400_, p_364183_, p_366202_, bakedmodel2, flag, 0.5F);
            } else {
                this.render(p_366843_, p_364164_, p_366015_, p_363983_, p_366400_, p_364183_, p_366202_, p_364448_);
            }
        }
    }

    private void renderSimpleItemModel(
        ItemStack p_365537_,
        ItemDisplayContext p_362112_,
        boolean p_366241_,
        PoseStack p_362829_,
        MultiBufferSource p_361571_,
        int p_360705_,
        int p_366554_,
        BakedModel p_363511_,
        boolean p_370176_
    ) {
        if (p_370176_) {
            if (p_365537_.is(Items.TRIDENT)) {
                p_363511_ = this.modelManager.getModel(TRIDENT_MODEL);
            } else if (p_365537_.is(Items.SPYGLASS)) {
                p_363511_ = this.modelManager.getModel(SPYGLASS_MODEL);
            }
        }

        this.renderItemModelRaw(p_365537_, p_362112_, p_366241_, p_362829_, p_361571_, p_360705_, p_366554_, p_363511_, p_370176_, -0.5F);
    }

    private void renderItemModelRaw(
        ItemStack p_368265_,
        ItemDisplayContext p_369628_,
        boolean p_365876_,
        PoseStack p_366810_,
        MultiBufferSource p_363829_,
        int p_363235_,
        int p_368132_,
        BakedModel p_369691_,
        boolean p_366607_,
        float p_368655_
    ) {
        p_366810_.pushPose();
        p_369691_.getTransforms().getTransform(p_369628_).apply(p_365876_, p_366810_);
        p_366810_.translate(-0.5F, -0.5F, p_368655_);
        this.renderItem(p_368265_, p_369628_, p_366810_, p_363829_, p_363235_, p_368132_, p_369691_, p_366607_);
        p_366810_.popPose();
    }

    private void renderItem(
        ItemStack p_364096_,
        ItemDisplayContext p_362035_,
        PoseStack p_370127_,
        MultiBufferSource p_365365_,
        int p_363416_,
        int p_367651_,
        BakedModel p_367824_,
        boolean p_366488_
    ) {
        if (!p_367824_.isCustomRenderer() && (!p_364096_.is(Items.TRIDENT) || p_366488_)) {
            RenderType rendertype = ItemBlockRenderTypes.getRenderType(p_364096_);
            VertexConsumer vertexconsumer;
            if (hasAnimatedTexture(p_364096_) && p_364096_.hasFoil()) {
                PoseStack.Pose posestack$pose = p_370127_.last().copy();
                if (p_362035_ == ItemDisplayContext.GUI) {
                    MatrixUtil.mulComponentWise(posestack$pose.pose(), 0.5F);
                } else if (p_362035_.firstPerson()) {
                    MatrixUtil.mulComponentWise(posestack$pose.pose(), 0.75F);
                }

                vertexconsumer = getCompassFoilBuffer(p_365365_, rendertype, posestack$pose);
            } else {
                vertexconsumer = getFoilBuffer(p_365365_, rendertype, true, p_364096_.hasFoil());
            }

            this.renderModelLists(p_367824_, p_364096_, p_363416_, p_367651_, p_370127_, vertexconsumer);
        } else {
            this.blockEntityRenderer.renderByItem(p_364096_, p_362035_, p_370127_, p_365365_, p_363416_, p_367651_);
        }
    }

    private static boolean shouldRenderItemFlat(ItemDisplayContext p_368418_) {
        return p_368418_ == ItemDisplayContext.GUI || p_368418_ == ItemDisplayContext.GROUND || p_368418_ == ItemDisplayContext.FIXED;
    }

    private static boolean hasAnimatedTexture(ItemStack p_286353_) {
        return p_286353_.is(ItemTags.COMPASSES) || p_286353_.is(Items.CLOCK);
    }

    public static VertexConsumer getArmorFoilBuffer(MultiBufferSource p_115185_, RenderType p_115186_, boolean p_115187_) {
        return p_115187_ ? VertexMultiConsumer.create(p_115185_.getBuffer(RenderType.armorEntityGlint()), p_115185_.getBuffer(p_115186_)) : p_115185_.getBuffer(p_115186_);
    }

    public static VertexConsumer getCompassFoilBuffer(MultiBufferSource p_115181_, RenderType p_115182_, PoseStack.Pose p_115183_) {
        return VertexMultiConsumer.create(
            new SheetedDecalTextureGenerator(p_115181_.getBuffer(RenderType.glint()), p_115183_, 0.0078125F), p_115181_.getBuffer(p_115182_)
        );
    }

    public static VertexConsumer getFoilBuffer(MultiBufferSource p_115212_, RenderType p_115213_, boolean p_115214_, boolean p_115215_) {
        if (p_115215_) {
            return Minecraft.useShaderTransparency() && p_115213_ == Sheets.translucentItemSheet()
                ? VertexMultiConsumer.create(p_115212_.getBuffer(RenderType.glintTranslucent()), p_115212_.getBuffer(p_115213_))
                : VertexMultiConsumer.create(p_115212_.getBuffer(p_115214_ ? RenderType.glint() : RenderType.entityGlint()), p_115212_.getBuffer(p_115213_));
        } else {
            return p_115212_.getBuffer(p_115213_);
        }
    }

    private void renderQuadList(PoseStack p_115163_, VertexConsumer p_115164_, List<BakedQuad> p_115165_, ItemStack p_115166_, int p_115167_, int p_115168_) {
        boolean flag = !p_115166_.isEmpty();
        PoseStack.Pose posestack$pose = p_115163_.last();

        for (BakedQuad bakedquad : p_115165_) {
            int i = -1;
            if (flag && bakedquad.isTinted()) {
                i = this.itemColors.getColor(p_115166_, bakedquad.getTintIndex());
            }

            float f = (float)ARGB.alpha(i) / 255.0F;
            float f1 = (float)ARGB.red(i) / 255.0F;
            float f2 = (float)ARGB.green(i) / 255.0F;
            float f3 = (float)ARGB.blue(i) / 255.0F;
            p_115164_.putBulkData(posestack$pose, bakedquad, f1, f2, f3, f, p_115167_, p_115168_);
        }
    }

    public BakedModel getModel(ItemStack p_174265_, @Nullable Level p_174266_, @Nullable LivingEntity p_174267_, int p_174268_) {
        BakedModel bakedmodel = this.itemModelShaper.getItemModel(p_174265_);
        return this.resolveModelOverride(bakedmodel, p_174265_, p_174266_, p_174267_, p_174268_);
    }

    public void renderStatic(
        ItemStack p_270761_,
        ItemDisplayContext p_270648_,
        int p_270410_,
        int p_270894_,
        PoseStack p_270430_,
        MultiBufferSource p_270457_,
        @Nullable Level p_270149_,
        int p_270509_
    ) {
        this.renderStatic(null, p_270761_, p_270648_, false, p_270430_, p_270457_, p_270149_, p_270410_, p_270894_, p_270509_);
    }

    public void renderStatic(
        @Nullable LivingEntity p_270101_,
        ItemStack p_270637_,
        ItemDisplayContext p_270437_,
        boolean p_270434_,
        PoseStack p_270230_,
        MultiBufferSource p_270411_,
        @Nullable Level p_270641_,
        int p_270595_,
        int p_270927_,
        int p_270845_
    ) {
        if (!p_270637_.isEmpty()) {
            BakedModel bakedmodel = this.getModel(p_270637_, p_270641_, p_270101_, p_270845_);
            this.render(p_270637_, p_270437_, p_270434_, p_270230_, p_270411_, p_270595_, p_270927_, bakedmodel);
        }
    }

    @Override
    public void onResourceManagerReload(ResourceManager p_115105_) {
        this.itemModelShaper.invalidateCache();
    }

    @Nullable
    public BakedModel resolveItemModel(ItemStack p_364028_, LivingEntity p_361089_, ItemDisplayContext p_363628_) {
        return p_364028_.isEmpty() ? null : this.getModel(p_364028_, p_361089_.level(), p_361089_, p_361089_.getId() + p_363628_.ordinal());
    }

    private BakedModel resolveModelOverride(BakedModel p_366074_, ItemStack p_366170_, @Nullable Level p_366692_, @Nullable LivingEntity p_368475_, int p_365150_) {
        ClientLevel clientlevel = p_366692_ instanceof ClientLevel ? (ClientLevel)p_366692_ : null;
        BakedModel bakedmodel = p_366074_.overrides().findOverride(p_366170_, clientlevel, p_368475_, p_365150_);
        return bakedmodel == null ? p_366074_ : bakedmodel;
    }
}