package net.minecraft.world.item.equipment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

public record Equippable(
    EquipmentSlot slot,
    Holder<SoundEvent> equipSound,
    Optional<ResourceLocation> model,
    Optional<ResourceLocation> cameraOverlay,
    Optional<HolderSet<EntityType<?>>> allowedEntities,
    boolean dispensable,
    boolean swappable,
    boolean damageOnHurt
) {
    public static final Codec<Equippable> CODEC = RecordCodecBuilder.create(
        p_362866_ -> p_362866_.group(
                    EquipmentSlot.CODEC.fieldOf("slot").forGetter(Equippable::slot),
                    SoundEvent.CODEC.optionalFieldOf("equip_sound", SoundEvents.ARMOR_EQUIP_GENERIC).forGetter(Equippable::equipSound),
                    ResourceLocation.CODEC.optionalFieldOf("model").forGetter(Equippable::model),
                    ResourceLocation.CODEC.optionalFieldOf("camera_overlay").forGetter(Equippable::cameraOverlay),
                    RegistryCodecs.homogeneousList(Registries.ENTITY_TYPE).optionalFieldOf("allowed_entities").forGetter(Equippable::allowedEntities),
                    Codec.BOOL.optionalFieldOf("dispensable", Boolean.valueOf(true)).forGetter(Equippable::dispensable),
                    Codec.BOOL.optionalFieldOf("swappable", Boolean.valueOf(true)).forGetter(Equippable::swappable),
                    Codec.BOOL.optionalFieldOf("damage_on_hurt", Boolean.valueOf(true)).forGetter(Equippable::damageOnHurt)
                )
                .apply(p_362866_, Equippable::new)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, Equippable> STREAM_CODEC = StreamCodec.composite(
        EquipmentSlot.STREAM_CODEC,
        Equippable::slot,
        SoundEvent.STREAM_CODEC,
        Equippable::equipSound,
        ResourceLocation.STREAM_CODEC.apply(ByteBufCodecs::optional),
        Equippable::model,
        ResourceLocation.STREAM_CODEC.apply(ByteBufCodecs::optional),
        Equippable::cameraOverlay,
        ByteBufCodecs.holderSet(Registries.ENTITY_TYPE).apply(ByteBufCodecs::optional),
        Equippable::allowedEntities,
        ByteBufCodecs.BOOL,
        Equippable::dispensable,
        ByteBufCodecs.BOOL,
        Equippable::swappable,
        ByteBufCodecs.BOOL,
        Equippable::damageOnHurt,
        Equippable::new
    );

    public static Equippable llamaSwag(DyeColor p_369724_) {
        return builder(EquipmentSlot.BODY)
            .setEquipSound(SoundEvents.LLAMA_SWAG)
            .setModel(EquipmentModels.CARPETS.get(p_369724_))
            .setAllowedEntities(EntityType.LLAMA, EntityType.TRADER_LLAMA)
            .build();
    }

    public static Equippable.Builder builder(EquipmentSlot p_362012_) {
        return new Equippable.Builder(p_362012_);
    }

    public InteractionResult swapWithEquipmentSlot(ItemStack p_362062_, Player p_365204_) {
        if (!p_365204_.canUseSlot(this.slot)) {
            return InteractionResult.PASS;
        } else {
            ItemStack itemstack = p_365204_.getItemBySlot(this.slot);
            if ((!EnchantmentHelper.has(itemstack, EnchantmentEffectComponents.PREVENT_ARMOR_CHANGE) || p_365204_.isCreative())
                && !ItemStack.isSameItemSameComponents(p_362062_, itemstack)) {
                if (!p_365204_.level().isClientSide()) {
                    p_365204_.awardStat(Stats.ITEM_USED.get(p_362062_.getItem()));
                }

                if (p_362062_.getCount() <= 1) {
                    ItemStack itemstack3 = itemstack.isEmpty() ? p_362062_ : itemstack.copyAndClear();
                    ItemStack itemstack4 = p_365204_.isCreative() ? p_362062_.copy() : p_362062_.copyAndClear();
                    p_365204_.setItemSlot(this.slot, itemstack4);
                    return InteractionResult.SUCCESS.heldItemTransformedTo(itemstack3);
                } else {
                    ItemStack itemstack1 = itemstack.copyAndClear();
                    ItemStack itemstack2 = p_362062_.consumeAndReturn(1, p_365204_);
                    p_365204_.setItemSlot(this.slot, itemstack2);
                    if (!p_365204_.getInventory().add(itemstack1)) {
                        p_365204_.drop(itemstack1, false);
                    }

                    return InteractionResult.SUCCESS.heldItemTransformedTo(p_362062_);
                }
            } else {
                return InteractionResult.FAIL;
            }
        }
    }

    public boolean canBeEquippedBy(EntityType<?> p_365620_) {
        return this.allowedEntities.isEmpty() || this.allowedEntities.get().contains(p_365620_.builtInRegistryHolder());
    }

    public static class Builder {
        private final EquipmentSlot slot;
        private Holder<SoundEvent> equipSound = SoundEvents.ARMOR_EQUIP_GENERIC;
        private Optional<ResourceLocation> model = Optional.empty();
        private Optional<ResourceLocation> cameraOverlay = Optional.empty();
        private Optional<HolderSet<EntityType<?>>> allowedEntities = Optional.empty();
        private boolean dispensable = true;
        private boolean swappable = true;
        private boolean damageOnHurt = true;

        Builder(EquipmentSlot p_363455_) {
            this.slot = p_363455_;
        }

        public Equippable.Builder setEquipSound(Holder<SoundEvent> p_368836_) {
            this.equipSound = p_368836_;
            return this;
        }

        public Equippable.Builder setModel(ResourceLocation p_369988_) {
            this.model = Optional.of(p_369988_);
            return this;
        }

        public Equippable.Builder setCameraOverlay(ResourceLocation p_360906_) {
            this.cameraOverlay = Optional.of(p_360906_);
            return this;
        }

        public Equippable.Builder setAllowedEntities(EntityType<?>... p_370045_) {
            return this.setAllowedEntities(HolderSet.direct(EntityType::builtInRegistryHolder, p_370045_));
        }

        public Equippable.Builder setAllowedEntities(HolderSet<EntityType<?>> p_363901_) {
            this.allowedEntities = Optional.of(p_363901_);
            return this;
        }

        public Equippable.Builder setDispensable(boolean p_370164_) {
            this.dispensable = p_370164_;
            return this;
        }

        public Equippable.Builder setSwappable(boolean p_367437_) {
            this.swappable = p_367437_;
            return this;
        }

        public Equippable.Builder setDamageOnHurt(boolean p_363080_) {
            this.damageOnHurt = p_363080_;
            return this;
        }

        public Equippable build() {
            return new Equippable(
                this.slot, this.equipSound, this.model, this.cameraOverlay, this.allowedEntities, this.dispensable, this.swappable, this.damageOnHurt
            );
        }
    }
}