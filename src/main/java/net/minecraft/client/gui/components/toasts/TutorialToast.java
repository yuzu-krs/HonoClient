package net.minecraft.client.gui.components.toasts;

import javax.annotation.Nullable;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TutorialToast implements Toast {
    private static final ResourceLocation BACKGROUND_SPRITE = ResourceLocation.withDefaultNamespace("toast/tutorial");
    public static final int PROGRESS_BAR_WIDTH = 154;
    public static final int PROGRESS_BAR_HEIGHT = 1;
    public static final int PROGRESS_BAR_X = 3;
    public static final int PROGRESS_BAR_Y = 28;
    private final TutorialToast.Icons icon;
    private final Component title;
    @Nullable
    private final Component message;
    private Toast.Visibility visibility = Toast.Visibility.SHOW;
    private long lastSmoothingTime;
    private float smoothedProgress;
    private float progress;
    private final boolean progressable;
    private final int timeToDisplayMs;

    public TutorialToast(TutorialToast.Icons p_361346_, Component p_369759_, @Nullable Component p_363508_, boolean p_369872_, int p_368984_) {
        this.icon = p_361346_;
        this.title = p_369759_;
        this.message = p_363508_;
        this.progressable = p_369872_;
        this.timeToDisplayMs = p_368984_;
    }

    public TutorialToast(TutorialToast.Icons p_94958_, Component p_94959_, @Nullable Component p_94960_, boolean p_94961_) {
        this(p_94958_, p_94959_, p_94960_, p_94961_, 0);
    }

    @Override
    public Toast.Visibility getWantedVisibility() {
        return this.visibility;
    }

    @Override
    public void update(ToastManager p_369846_, long p_364600_) {
        if (this.timeToDisplayMs > 0) {
            this.progress = Math.min((float)p_364600_ / (float)this.timeToDisplayMs, 1.0F);
            this.smoothedProgress = this.progress;
            this.lastSmoothingTime = p_364600_;
            if (p_364600_ > (long)this.timeToDisplayMs) {
                this.hide();
            }
        } else if (this.progressable) {
            this.smoothedProgress = Mth.clampedLerp(this.smoothedProgress, this.progress, (float)(p_364600_ - this.lastSmoothingTime) / 100.0F);
            this.lastSmoothingTime = p_364600_;
        }
    }

    @Override
    public void render(GuiGraphics p_283197_, Font p_365679_, long p_281902_) {
        p_283197_.blitSprite(RenderType::guiTextured, BACKGROUND_SPRITE, 0, 0, this.width(), this.height());
        this.icon.render(p_283197_, 6, 6);
        if (this.message == null) {
            p_283197_.drawString(p_365679_, this.title, 30, 12, -11534256, false);
        } else {
            p_283197_.drawString(p_365679_, this.title, 30, 7, -11534256, false);
            p_283197_.drawString(p_365679_, this.message, 30, 18, -16777216, false);
        }

        if (this.progressable) {
            p_283197_.fill(3, 28, 157, 29, -1);
            int i;
            if (this.progress >= this.smoothedProgress) {
                i = -16755456;
            } else {
                i = -11206656;
            }

            p_283197_.fill(3, 28, (int)(3.0F + 154.0F * this.smoothedProgress), 29, i);
        }
    }

    public void hide() {
        this.visibility = Toast.Visibility.HIDE;
    }

    public void updateProgress(float p_94963_) {
        this.progress = p_94963_;
    }

    @OnlyIn(Dist.CLIENT)
    public static enum Icons {
        MOVEMENT_KEYS(ResourceLocation.withDefaultNamespace("toast/movement_keys")),
        MOUSE(ResourceLocation.withDefaultNamespace("toast/mouse")),
        TREE(ResourceLocation.withDefaultNamespace("toast/tree")),
        RECIPE_BOOK(ResourceLocation.withDefaultNamespace("toast/recipe_book")),
        WOODEN_PLANKS(ResourceLocation.withDefaultNamespace("toast/wooden_planks")),
        SOCIAL_INTERACTIONS(ResourceLocation.withDefaultNamespace("toast/social_interactions")),
        RIGHT_CLICK(ResourceLocation.withDefaultNamespace("toast/right_click"));

        private final ResourceLocation sprite;

        private Icons(final ResourceLocation p_297613_) {
            this.sprite = p_297613_;
        }

        public void render(GuiGraphics p_282818_, int p_283064_, int p_282765_) {
            p_282818_.blitSprite(RenderType::guiTextured, this.sprite, p_283064_, p_282765_, 20, 20);
        }
    }
}