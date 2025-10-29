package net.minecraft.client.gui.font.glyphs;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.font.GlyphRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Style;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix4f;

@OnlyIn(Dist.CLIENT)
public class BakedGlyph {
    private final GlyphRenderTypes renderTypes;
    private final float u0;
    private final float u1;
    private final float v0;
    private final float v1;
    private final float left;
    private final float right;
    private final float up;
    private final float down;

    public BakedGlyph(
        GlyphRenderTypes p_285527_,
        float p_285271_,
        float p_284970_,
        float p_285098_,
        float p_285023_,
        float p_285242_,
        float p_285043_,
        float p_285100_,
        float p_284948_
    ) {
        this.renderTypes = p_285527_;
        this.u0 = p_285271_;
        this.u1 = p_284970_;
        this.v0 = p_285098_;
        this.v1 = p_285023_;
        this.left = p_285242_;
        this.right = p_285043_;
        this.up = p_285100_;
        this.down = p_284948_;
    }

    public void renderChar(BakedGlyph.GlyphInstance p_368554_, Matrix4f p_365625_, VertexConsumer p_370130_, int p_369456_) {
        Style style = p_368554_.style();
        boolean flag = style.isItalic();
        float f = p_368554_.x();
        float f1 = p_368554_.y();
        int i = p_368554_.color();
        this.render(flag, f, f1, p_365625_, p_370130_, i, p_369456_);
        if (style.isBold()) {
            this.render(flag, f + p_368554_.boldOffset(), f1, p_365625_, p_370130_, i, p_369456_);
        }
    }

    private void render(boolean p_95227_, float p_95228_, float p_95229_, Matrix4f p_253706_, VertexConsumer p_95231_, int p_95236_, int p_365126_) {
        float f = p_95228_ + this.left;
        float f1 = p_95228_ + this.right;
        float f2 = p_95229_ + this.up;
        float f3 = p_95229_ + this.down;
        float f4 = p_95227_ ? 1.0F - 0.25F * this.up : 0.0F;
        float f5 = p_95227_ ? 1.0F - 0.25F * this.down : 0.0F;
        p_95231_.addVertex(p_253706_, f + f4, f2, 0.0F).setColor(p_95236_).setUv(this.u0, this.v0).setLight(p_365126_);
        p_95231_.addVertex(p_253706_, f + f5, f3, 0.0F).setColor(p_95236_).setUv(this.u0, this.v1).setLight(p_365126_);
        p_95231_.addVertex(p_253706_, f1 + f5, f3, 0.0F).setColor(p_95236_).setUv(this.u1, this.v1).setLight(p_365126_);
        p_95231_.addVertex(p_253706_, f1 + f4, f2, 0.0F).setColor(p_95236_).setUv(this.u1, this.v0).setLight(p_365126_);
    }

    public void renderEffect(BakedGlyph.Effect p_95221_, Matrix4f p_254370_, VertexConsumer p_95223_, int p_95224_) {
        p_95223_.addVertex(p_254370_, p_95221_.x0, p_95221_.y0, p_95221_.depth)
            .setColor(p_95221_.color)
            .setUv(this.u0, this.v0)
            .setLight(p_95224_);
        p_95223_.addVertex(p_254370_, p_95221_.x1, p_95221_.y0, p_95221_.depth)
            .setColor(p_95221_.color)
            .setUv(this.u0, this.v1)
            .setLight(p_95224_);
        p_95223_.addVertex(p_254370_, p_95221_.x1, p_95221_.y1, p_95221_.depth)
            .setColor(p_95221_.color)
            .setUv(this.u1, this.v1)
            .setLight(p_95224_);
        p_95223_.addVertex(p_254370_, p_95221_.x0, p_95221_.y1, p_95221_.depth)
            .setColor(p_95221_.color)
            .setUv(this.u1, this.v0)
            .setLight(p_95224_);
    }

    public RenderType renderType(Font.DisplayMode p_181388_) {
        return this.renderTypes.select(p_181388_);
    }

    @OnlyIn(Dist.CLIENT)
    public static record Effect(float x0, float y0, float x1, float y1, float depth, int color) {
    }

    @OnlyIn(Dist.CLIENT)
    public static record GlyphInstance(float x, float y, int color, BakedGlyph glyph, Style style, float boldOffset) {
    }
}