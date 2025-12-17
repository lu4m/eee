package eee.eee4.blocks;

import eee.eee4.blockEntities.EnchantmentCopyingTableEntity;
import eee.eee4.menus.EnchantmentCopyingMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

public class EnchantmentCopyingTable extends Block implements EntityBlock {

    private static final VoxelShape SHAPE =
            Block.box(0.0, 0.0, 0.0, 16.0, 12.0, 16.0);

    public EnchantmentCopyingTable(Properties properties) {
        super(properties);
    }

    @Override
    protected @NotNull VoxelShape getShape(
            @NotNull BlockState blockState,
            @NotNull BlockGetter blockGetter,
            @NotNull BlockPos blockPos,
            @NotNull CollisionContext collisionContext) {
        return SHAPE;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new EnchantmentCopyingTableEntity(blockPos,blockState);
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(
            @NotNull BlockState blockState,
            Level level,
            @NotNull BlockPos blockPos,
            @NotNull Player player,
            @NotNull BlockHitResult blockHitResult
    ) {
        if (!level.isClientSide()) {
            MenuProvider provider = getMenuProvider(blockState, level, blockPos);
            if (provider != null) {
                player.openMenu(provider);
            }
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    protected @Nullable MenuProvider getMenuProvider(
            @NotNull BlockState blockState,
            @NotNull Level level,
            @NotNull BlockPos blockPos
    ) {
        BlockEntity blockEntity = level.getBlockEntity(blockPos);

        if (blockEntity instanceof EnchantmentCopyingTableEntity) {
            return new SimpleMenuProvider(
                    (syncId, inventory, player) ->
                            new EnchantmentCopyingMenu(
                                    syncId,
                                    inventory,
                                    ContainerLevelAccess.create(level, blockPos)
                            ),
                    Component.translatable("gui.eee4.enchantment_copying.title")
            );
        }

        return null;
    }
}

