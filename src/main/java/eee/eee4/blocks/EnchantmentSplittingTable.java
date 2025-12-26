package eee.eee4.blocks;

import com.mojang.serialization.MapCodec;
import eee.eee4.blockEntitie.EnchantmentCopyingTableEntity;
import eee.eee4.blockEntitie.EnchantmentSplittingTableEntity;
import eee.eee4.registry.EEEBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
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
    public @Nullable <T extends BlockEntity> BlockEntityTicker<@NotNull T> getTicker(
            Level level,
            @NotNull BlockState blockState,
            @NotNull BlockEntityType<@NotNull T> blockEntityType
    ) {
        return level.isClientSide() ? createTickerHelper(
                    blockEntityType,
                    EEEBlockEntities.ENCHANTMENT_SPLITTING_TABLE_ENTITY,
                    EnchantmentSplittingTableEntity::itemAnimationTick
                )
                : null;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
        return new EnchantmentSplittingTableEntity(blockPos, blockState);
    }
}
