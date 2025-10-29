package net.minecraft.client.renderer;

import com.mojang.blaze3d.buffers.BufferUsage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Vector3f;

@OnlyIn(Dist.CLIENT)
public class SkyRenderer implements AutoCloseable {
    private static final ResourceLocation SUN_LOCATION = ResourceLocation.withDefaultNamespace("textures/environment/sun.png");
    private static final ResourceLocation MOON_LOCATION = ResourceLocation.withDefaultNamespace("textures/environment/moon_phases.png");
    private static final ResourceLocation END_SKY_LOCATION = ResourceLocation.withDefaultNamespace("textures/environment/end_sky.png");
    private static final float SKY_DISC_RADIUS = 512.0F;
    private final VertexBuffer starBuffer = this.createStarBuffer();
    private final VertexBuffer topSkyBuffer = this.createTopSkyBuffer();
    private final VertexBuffer bottomSkyBuffer = this.createBottomSkyBuffer();

    private VertexBuffer createStarBuffer() {
        VertexBuffer vertexbuffer = new VertexBuffer(BufferUsage.STATIC_WRITE);
        vertexbuffer.bind();
        vertexbuffer.upload(this.drawStars(Tesselator.getInstance()));
        VertexBuffer.unbind();
        return vertexbuffer;
    }

    private VertexBuffer createTopSkyBuffer() {
        VertexBuffer vertexbuffer = new VertexBuffer(BufferUsage.STATIC_WRITE);
        vertexbuffer.bind();
        vertexbuffer.upload(this.buildSkyDisc(Tesselator.getInstance(), 16.0F));
        VertexBuffer.unbind();
        return vertexbuffer;
    }

    private VertexBuffer createBottomSkyBuffer() {
        VertexBuffer vertexbuffer = new VertexBuffer(BufferUsage.STATIC_WRITE);
        vertexbuffer.bind();
        vertexbuffer.upload(this.buildSkyDisc(Tesselator.getInstance(), -16.0F));
        VertexBuffer.unbind();
        return vertexbuffer;
    }

    private MeshData drawStars(Tesselator p_367143_) {
        RandomSource randomsource = RandomSource.create(10842L);
        int i = 1500;
        float f = 100.0F;
        BufferBuilder bufferbuilder = p_367143_.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);

        for (int j = 0; j < 1500; j++) {
            float f1 = randomsource.nextFloat() * 2.0F - 1.0F;
            float f2 = randomsource.nextFloat() * 2.0F - 1.0F;
            float f3 = randomsource.nextFloat() * 2.0F - 1.0F;
            float f4 = 0.15F + randomsource.nextFloat() * 0.1F;
            float f5 = Mth.lengthSquared(f1, f2, f3);
            if (!(f5 <= 0.010000001F) && !(f5 >= 1.0F)) {
                Vector3f vector3f = new Vector3f(f1, f2, f3).normalize(100.0F);
                float f6 = (float)(randomsource.nextDouble() * (float) Math.PI * 2.0);
                Matrix3f matrix3f = new Matrix3f().rotateTowards(new Vector3f(vector3f).negate(), new Vector3f(0.0F, 1.0F, 0.0F)).rotateZ(-f6);
                bufferbuilder.addVertex(new Vector3f(f4, -f4, 0.0F).mul(matrix3f).add(vector3f));
                bufferbuilder.addVertex(new Vector3f(f4, f4, 0.0F).mul(matrix3f).add(vector3f));
                bufferbuilder.addVertex(new Vector3f(-f4, f4, 0.0F).mul(matrix3f).add(vector3f));
                bufferbuilder.addVertex(new Vector3f(-f4, -f4, 0.0F).mul(matrix3f).add(vector3f));
            }
        }

        return bufferbuilder.buildOrThrow();
    }

    private MeshData buildSkyDisc(Tesselator p_363522_, float p_363584_) {
        float f = Math.signum(p_363584_) * 512.0F;
        BufferBuilder bufferbuilder = p_363522_.begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION);
        bufferbuilder.addVertex(0.0F, p_363584_, 0.0F);

        for (int i = -180; i <= 180; i += 45) {
            bufferbuilder.addVertex(
                f * Mth.cos((float)i * (float) (Math.PI / 180.0)), p_363584_, 512.0F * Mth.sin((float)i * (float) (Math.PI / 180.0))
            );
        }

        return bufferbuilder.buildOrThrow();
    }

    public void renderSkyDisc(float p_369198_, float p_369913_, float p_362432_) {
        RenderSystem.depthMask(false);
        RenderSystem.setShader(CoreShaders.POSITION);
        RenderSystem.setShaderColor(p_369198_, p_369913_, p_362432_, 1.0F);
        this.topSkyBuffer.bind();
        this.topSkyBuffer.drawWithShader(RenderSystem.getModelViewMatrix(), RenderSystem.getProjectionMatrix(), RenderSystem.getShader());
        VertexBuffer.unbind();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.depthMask(true);
    }

    public void renderDarkDisc(PoseStack p_367581_) {
        RenderSystem.depthMask(false);
        RenderSystem.setShader(CoreShaders.POSITION);
        RenderSystem.setShaderColor(0.0F, 0.0F, 0.0F, 1.0F);
        p_367581_.pushPose();
        p_367581_.translate(0.0F, 12.0F, 0.0F);
        this.bottomSkyBuffer.bind();
        this.bottomSkyBuffer.drawWithShader(RenderSystem.getModelViewMatrix(), RenderSystem.getProjectionMatrix(), RenderSystem.getShader());
        VertexBuffer.unbind();
        p_367581_.popPose();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.depthMask(true);
    }

    public void renderSunMoonAndStars(PoseStack p_362673_, Tesselator p_361800_, float p_369057_, int p_364932_, float p_366540_, float p_368016_, FogParameters p_362209_) {
        p_362673_.pushPose();
        p_362673_.mulPose(Axis.YP.rotationDegrees(-90.0F));
        p_362673_.mulPose(Axis.XP.rotationDegrees(p_369057_ * 360.0F));
        this.renderSun(p_366540_, p_361800_, p_362673_);
        this.renderMoon(p_364932_, p_366540_, p_361800_, p_362673_);
        if (p_368016_ > 0.0F) {
            this.renderStars(p_362209_, p_368016_, p_362673_);
        }

        p_362673_.popPose();
    }

    private void renderSun(float p_363755_, Tesselator p_365676_, PoseStack p_369287_) {
        float f = 30.0F;
        float f1 = 100.0F;
        BufferBuilder bufferbuilder = p_365676_.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        Matrix4f matrix4f = p_369287_.last().pose();
        RenderSystem.depthMask(false);
        RenderSystem.overlayBlendFunc();
        RenderSystem.setShader(CoreShaders.POSITION_TEX);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, p_363755_);
        RenderSystem.setShaderTexture(0, SUN_LOCATION);
        RenderSystem.enableBlend();
        bufferbuilder.addVertex(matrix4f, -30.0F, 100.0F, -30.0F).setUv(0.0F, 0.0F);
        bufferbuilder.addVertex(matrix4f, 30.0F, 100.0F, -30.0F).setUv(1.0F, 0.0F);
        bufferbuilder.addVertex(matrix4f, 30.0F, 100.0F, 30.0F).setUv(1.0F, 1.0F);
        bufferbuilder.addVertex(matrix4f, -30.0F, 100.0F, 30.0F).setUv(0.0F, 1.0F);
        BufferUploader.drawWithShader(bufferbuilder.buildOrThrow());
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.depthMask(true);
    }

    private void renderMoon(int p_367893_, float p_364034_, Tesselator p_369904_, PoseStack p_369177_) {
        float f = 20.0F;
        int i = p_367893_ % 4;
        int j = p_367893_ / 4 % 2;
        float f1 = (float)(i + 0) / 4.0F;
        float f2 = (float)(j + 0) / 2.0F;
        float f3 = (float)(i + 1) / 4.0F;
        float f4 = (float)(j + 1) / 2.0F;
        float f5 = 100.0F;
        BufferBuilder bufferbuilder = p_369904_.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        RenderSystem.depthMask(false);
        RenderSystem.overlayBlendFunc();
        RenderSystem.setShader(CoreShaders.POSITION_TEX);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, p_364034_);
        RenderSystem.setShaderTexture(0, MOON_LOCATION);
        RenderSystem.enableBlend();
        Matrix4f matrix4f = p_369177_.last().pose();
        bufferbuilder.addVertex(matrix4f, -20.0F, -100.0F, 20.0F).setUv(f3, f4);
        bufferbuilder.addVertex(matrix4f, 20.0F, -100.0F, 20.0F).setUv(f1, f4);
        bufferbuilder.addVertex(matrix4f, 20.0F, -100.0F, -20.0F).setUv(f1, f2);
        bufferbuilder.addVertex(matrix4f, -20.0F, -100.0F, -20.0F).setUv(f3, f2);
        BufferUploader.drawWithShader(bufferbuilder.buildOrThrow());
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.depthMask(true);
    }

    private void renderStars(FogParameters p_362284_, float p_361462_, PoseStack p_364130_) {
        Matrix4fStack matrix4fstack = RenderSystem.getModelViewStack();
        matrix4fstack.pushMatrix();
        matrix4fstack.mul(p_364130_.last().pose());
        RenderSystem.depthMask(false);
        RenderSystem.overlayBlendFunc();
        RenderSystem.setShader(CoreShaders.POSITION);
        RenderSystem.setShaderColor(p_361462_, p_361462_, p_361462_, p_361462_);
        RenderSystem.enableBlend();
        RenderSystem.setShaderFog(FogParameters.NO_FOG);
        this.starBuffer.bind();
        this.starBuffer.drawWithShader(matrix4fstack, RenderSystem.getProjectionMatrix(), RenderSystem.getShader());
        VertexBuffer.unbind();
        RenderSystem.setShaderFog(p_362284_);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.depthMask(true);
        matrix4fstack.popMatrix();
    }

    public void renderSunriseAndSunset(PoseStack p_365939_, Tesselator p_369967_, float p_368996_, int p_365467_) {
        RenderSystem.setShader(CoreShaders.POSITION_COLOR);
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        p_365939_.pushPose();
        p_365939_.mulPose(Axis.XP.rotationDegrees(90.0F));
        float f = Mth.sin(p_368996_) < 0.0F ? 180.0F : 0.0F;
        p_365939_.mulPose(Axis.ZP.rotationDegrees(f));
        p_365939_.mulPose(Axis.ZP.rotationDegrees(90.0F));
        Matrix4f matrix4f = p_365939_.last().pose();
        BufferBuilder bufferbuilder = p_369967_.begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR);
        float f1 = ARGB.from8BitChannel(ARGB.alpha(p_365467_));
        bufferbuilder.addVertex(matrix4f, 0.0F, 100.0F, 0.0F).setColor(p_365467_);
        int i = ARGB.transparent(p_365467_);
        int j = 16;

        for (int k = 0; k <= 16; k++) {
            float f2 = (float)k * (float) (Math.PI * 2) / 16.0F;
            float f3 = Mth.sin(f2);
            float f4 = Mth.cos(f2);
            bufferbuilder.addVertex(matrix4f, f3 * 120.0F, f4 * 120.0F, -f4 * 40.0F * f1).setColor(i);
        }

        BufferUploader.drawWithShader(bufferbuilder.buildOrThrow());
        p_365939_.popPose();
        RenderSystem.disableBlend();
        RenderSystem.depthMask(true);
    }

    public void renderEndSky(PoseStack p_367775_) {
        RenderSystem.enableBlend();
        RenderSystem.depthMask(false);
        RenderSystem.setShader(CoreShaders.POSITION_TEX_COLOR);
        RenderSystem.setShaderTexture(0, END_SKY_LOCATION);
        Tesselator tesselator = Tesselator.getInstance();

        for (int i = 0; i < 6; i++) {
            p_367775_.pushPose();
            if (i == 1) {
                p_367775_.mulPose(Axis.XP.rotationDegrees(90.0F));
            }

            if (i == 2) {
                p_367775_.mulPose(Axis.XP.rotationDegrees(-90.0F));
            }

            if (i == 3) {
                p_367775_.mulPose(Axis.XP.rotationDegrees(180.0F));
            }

            if (i == 4) {
                p_367775_.mulPose(Axis.ZP.rotationDegrees(90.0F));
            }

            if (i == 5) {
                p_367775_.mulPose(Axis.ZP.rotationDegrees(-90.0F));
            }

            Matrix4f matrix4f = p_367775_.last().pose();
            BufferBuilder bufferbuilder = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
            bufferbuilder.addVertex(matrix4f, -100.0F, -100.0F, -100.0F).setUv(0.0F, 0.0F).setColor(-14145496);
            bufferbuilder.addVertex(matrix4f, -100.0F, -100.0F, 100.0F).setUv(0.0F, 16.0F).setColor(-14145496);
            bufferbuilder.addVertex(matrix4f, 100.0F, -100.0F, 100.0F).setUv(16.0F, 16.0F).setColor(-14145496);
            bufferbuilder.addVertex(matrix4f, 100.0F, -100.0F, -100.0F).setUv(16.0F, 0.0F).setColor(-14145496);
            BufferUploader.drawWithShader(bufferbuilder.buildOrThrow());
            p_367775_.popPose();
        }

        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
    }

    @Override
    public void close() {
        this.starBuffer.close();
        this.topSkyBuffer.close();
        this.bottomSkyBuffer.close();
    }
}