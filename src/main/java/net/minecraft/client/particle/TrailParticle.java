package net.minecraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.TargetColorParticleOption;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TrailParticle extends TextureSheetParticle {
    private final Vec3 target;

    TrailParticle(
        ClientLevel p_368897_,
        double p_365643_,
        double p_367323_,
        double p_365378_,
        double p_369758_,
        double p_361767_,
        double p_368109_,
        Vec3 p_369945_,
        int p_367065_
    ) {
        super(p_368897_, p_365643_, p_367323_, p_365378_, p_369758_, p_361767_, p_368109_);
        p_367065_ = ARGB.scaleRGB(
            p_367065_, 0.875F + this.random.nextFloat() * 0.25F, 0.875F + this.random.nextFloat() * 0.25F, 0.875F + this.random.nextFloat() * 0.25F
        );
        this.rCol = (float)ARGB.red(p_367065_) / 255.0F;
        this.gCol = (float)ARGB.green(p_367065_) / 255.0F;
        this.bCol = (float)ARGB.blue(p_367065_) / 255.0F;
        this.quadSize = 0.26F;
        this.target = p_369945_;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
        } else {
            int i = this.lifetime - this.age;
            double d0 = 1.0 / (double)i;
            this.x = Mth.lerp(d0, this.x, this.target.x());
            this.y = Mth.lerp(d0, this.y, this.target.y());
            this.z = Mth.lerp(d0, this.z, this.target.z());
        }
    }

    @Override
    public int getLightColor(float p_360980_) {
        return 15728880;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<TargetColorParticleOption> {
        private final SpriteSet sprite;

        public Provider(SpriteSet p_363729_) {
            this.sprite = p_363729_;
        }

        public Particle createParticle(
            TargetColorParticleOption p_370032_,
            ClientLevel p_369121_,
            double p_360788_,
            double p_367642_,
            double p_369587_,
            double p_368409_,
            double p_365137_,
            double p_367012_
        ) {
            TrailParticle trailparticle = new TrailParticle(
                p_369121_, p_360788_, p_367642_, p_369587_, p_368409_, p_365137_, p_367012_, p_370032_.target(), p_370032_.color()
            );
            trailparticle.pickSprite(this.sprite);
            trailparticle.setLifetime(p_369121_.random.nextInt(40) + 10);
            return trailparticle;
        }
    }
}