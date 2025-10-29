package net.minecraft.world.level.levelgen.feature.treedecorators;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.List;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.features.VegetationFeatures;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HangingMossBlock;
import net.minecraft.world.level.block.MossyCarpetBlock;
import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableObject;

public class PaleMossDecorator extends TreeDecorator {
    public static final MapCodec<PaleMossDecorator> CODEC = RecordCodecBuilder.mapCodec(
        p_367624_ -> p_367624_.group(
                    Codec.floatRange(0.0F, 1.0F).fieldOf("leaves_probability").forGetter(p_368139_ -> p_368139_.leavesProbability),
                    Codec.floatRange(0.0F, 1.0F).fieldOf("trunk_probability").forGetter(p_368321_ -> p_368321_.trunkProbability),
                    Codec.floatRange(0.0F, 1.0F).fieldOf("ground_probability").forGetter(p_364066_ -> p_364066_.groundProbability)
                )
                .apply(p_367624_, PaleMossDecorator::new)
    );
    private final float leavesProbability;
    private final float trunkProbability;
    private final float groundProbability;

    @Override
    protected TreeDecoratorType<?> type() {
        return TreeDecoratorType.PALE_MOSS;
    }

    public PaleMossDecorator(float p_362537_, float p_367812_, float p_366658_) {
        this.leavesProbability = p_362537_;
        this.trunkProbability = p_367812_;
        this.groundProbability = p_366658_;
    }

    @Override
    public void place(TreeDecorator.Context p_366717_) {
        RandomSource randomsource = p_366717_.random();
        WorldGenLevel worldgenlevel = (WorldGenLevel)p_366717_.level();
        List<BlockPos> list = Util.shuffledCopy(p_366717_.logs(), randomsource);
        if (!list.isEmpty()) {
            Mutable<BlockPos> mutable = new MutableObject<>(list.getFirst());
            list.forEach(p_360964_ -> {
                if (p_360964_.getY() < mutable.getValue().getY()) {
                    mutable.setValue(p_360964_);
                }
            });
            BlockPos blockpos = mutable.getValue();
            if (randomsource.nextFloat() < this.groundProbability) {
                worldgenlevel.registryAccess()
                    .lookup(Registries.CONFIGURED_FEATURE)
                    .flatMap(p_362216_ -> p_362216_.get(VegetationFeatures.PALE_MOSS_PATCH_BONEMEAL))
                    .ifPresent(
                        p_370079_ -> p_370079_.value()
                                .place(worldgenlevel, worldgenlevel.getLevel().getChunkSource().getGenerator(), randomsource, blockpos.above())
                    );
            }

            p_366717_.logs().forEach(p_364038_ -> {
                if (randomsource.nextFloat() < this.trunkProbability) {
                    BlockPos blockpos1 = p_364038_.below();
                    if (p_366717_.isAir(blockpos1)) {
                        addMossHanger(blockpos1, p_366717_);
                    }
                }

                if (randomsource.nextFloat() < this.trunkProbability) {
                    BlockPos blockpos2 = p_364038_.above();
                    if (p_366717_.isAir(blockpos2)) {
                        MossyCarpetBlock.placeAt((WorldGenLevel)p_366717_.level(), blockpos2, p_366717_.random(), 3);
                    }
                }
            });
            p_366717_.leaves().forEach(p_364665_ -> {
                if (randomsource.nextFloat() < this.leavesProbability) {
                    BlockPos blockpos1 = p_364665_.below();
                    if (p_366717_.isAir(blockpos1)) {
                        addMossHanger(blockpos1, p_366717_);
                    }
                }
            });
        }
    }

    private static void addMossHanger(BlockPos p_368181_, TreeDecorator.Context p_365451_) {
        while (p_365451_.isAir(p_368181_.below()) && !((double)p_365451_.random().nextFloat() < 0.5)) {
            p_365451_.setBlock(p_368181_, Blocks.PALE_HANGING_MOSS.defaultBlockState().setValue(HangingMossBlock.TIP, Boolean.valueOf(false)));
            p_368181_ = p_368181_.below();
        }

        p_365451_.setBlock(p_368181_, Blocks.PALE_HANGING_MOSS.defaultBlockState().setValue(HangingMossBlock.TIP, Boolean.valueOf(true)));
    }
}