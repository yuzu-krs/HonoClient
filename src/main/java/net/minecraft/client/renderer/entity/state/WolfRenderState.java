package net.minecraft.client.renderer.entity.state;

import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WolfRenderState extends LivingEntityRenderState {
    private static final ResourceLocation DEFAULT_TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/wolf/wolf.png");
    public boolean isAngry;
    public boolean isSitting;
    public float tailAngle = (float) (Math.PI / 5);
    public float headRollAngle;
    public float shakeAnim;
    public float wetShade = 1.0F;
    public ResourceLocation texture = DEFAULT_TEXTURE;
    @Nullable
    public DyeColor collarColor;
    public ItemStack bodyArmorItem = ItemStack.EMPTY;

    public float getBodyRollAngle(float p_362171_) {
        float f = (this.shakeAnim + p_362171_) / 1.8F;
        if (f < 0.0F) {
            f = 0.0F;
        } else if (f > 1.0F) {
            f = 1.0F;
        }

        return Mth.sin(f * (float) Math.PI) * Mth.sin(f * (float) Math.PI * 11.0F) * 0.15F * (float) Math.PI;
    }
}