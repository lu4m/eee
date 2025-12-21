package eee.eee4.blocks;

import com.mojang.serialization.MapCodec;
import eee.eee4.blockEntitie.EnchantmentSplittingTableEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

public class EnchantmentSplittingTable extends BaseEntityBlock {

    public static final MapCodec<EnchantmentSplittingTable> CODEC = simpleCodec(EnchantmentSplittingTable::new);
    private static final VoxelShape SHAPE =
            Block.box(0.0, 0.0, 0.0, 16.0, 12.0, 16.0);

    public EnchantmentSplittingTable(Properties properties) {
        super(properties);
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected @NotNull VoxelShape getShape(
            @NotNull BlockState blockState,
            @NotNull BlockGetter blockGetter,
            @NotNull BlockPos blockPos,
            @NotNull CollisionContext collisionContext
    ) {
        return SHAPE;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
        return new EnchantmentSplittingTableEntity(blockPos, blockState);
    }
}
