package net.minecraft.client.renderer.entity;

import net.minecraft.client.gui.Font;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MapRenderer;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.layers.EquipmentLayerRenderer;
import net.minecraft.client.resources.model.EquipmentModelSet;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@FunctionalInterface
@OnlyIn(Dist.CLIENT)
public interface EntityRendererProvider<T extends Entity> {
    EntityRenderer<T, ?> create(EntityRendererProvider.Context p_174010_);

    @OnlyIn(Dist.CLIENT)
    public static class Context {
        private final EntityRenderDispatcher entityRenderDispatcher;
        private final ItemRenderer itemRenderer;
        private final MapRenderer mapRenderer;
        private final BlockRenderDispatcher blockRenderDispatcher;
        private final ResourceManager resourceManager;
        private final EntityModelSet modelSet;
        private final EquipmentModelSet equipmentModels;
        private final Font font;
        private final EquipmentLayerRenderer equipmentRenderer;

        public Context(
            EntityRenderDispatcher p_234590_,
            ItemRenderer p_234591_,
            MapRenderer p_361143_,
            BlockRenderDispatcher p_234592_,
            ResourceManager p_234594_,
            EntityModelSet p_234595_,
            EquipmentModelSet p_363737_,
            Font p_234596_
        ) {
            this.entityRenderDispatcher = p_234590_;
            this.itemRenderer = p_234591_;
            this.mapRenderer = p_361143_;
            this.blockRenderDispatcher = p_234592_;
            this.resourceManager = p_234594_;
            this.modelSet = p_234595_;
            this.equipmentModels = p_363737_;
            this.font = p_234596_;
            this.equipmentRenderer = new EquipmentLayerRenderer(p_363737_, this.getModelManager().getAtlas(Sheets.ARMOR_TRIMS_SHEET));
        }

        public EntityRenderDispatcher getEntityRenderDispatcher() {
            return this.entityRenderDispatcher;
        }

        public ItemRenderer getItemRenderer() {
            return this.itemRenderer;
        }

        public MapRenderer getMapRenderer() {
            return this.mapRenderer;
        }

        public BlockRenderDispatcher getBlockRenderDispatcher() {
            return this.blockRenderDispatcher;
        }

        public ResourceManager getResourceManager() {
            return this.resourceManager;
        }

        public EntityModelSet getModelSet() {
            return this.modelSet;
        }

        public EquipmentModelSet getEquipmentModels() {
            return this.equipmentModels;
        }

        public EquipmentLayerRenderer getEquipmentRenderer() {
            return this.equipmentRenderer;
        }

        public ModelManager getModelManager() {
            return this.blockRenderDispatcher.getBlockModelShaper().getModelManager();
        }

        public ModelPart bakeLayer(ModelLayerLocation p_174024_) {
            return this.modelSet.bakeLayer(p_174024_);
        }

        public Font getFont() {
            return this.font;
        }
    }
}