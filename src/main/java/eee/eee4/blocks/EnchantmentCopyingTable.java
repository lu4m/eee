package eee.eee4.blocks;

import com.mojang.serialization.MapCodec;
import eee.eee4.EEE;
import eee.eee4.blockEntities.EnchantmentCopyingTableEntity;
import eee.eee4.menus.EnchantmentCopyingMenu;
import eee.eee4.registry.EEEBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

import static net.minecraft.world.level.block.EnchantingTableBlock.BOOKSHELF_OFFSETS;

public class EnchantmentCopyingTable extends BaseEntityBlock {
    public static final MapCodec<EnchantmentCopyingTable> CODEC = simpleCodec(EnchantmentCopyingTable::new);
    private static final VoxelShape SHAPE =
            Block.box(0.0, 0.0, 0.0, 16.0, 12.0, 16.0);

    public EnchantmentCopyingTable(Properties properties) {
        super(properties);
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected boolean useShapeForLightOcclusion(@NotNull BlockState blockState) {
        return true;
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
    public <T extends BlockEntity> @Nullable BlockEntityTicker<@NotNull T> getTicker(Level level, @NotNull BlockState blockState, @NotNull BlockEntityType<@NotNull T> blockEntityType) {
        EEE.LOGGER.atDebug().log("{}",blockEntityType == EEEBlockEntities.ENCHANTMENT_COPYING_TABLE_ENTITY);
        EEE.LOGGER.atDebug().log("{}",blockEntityType);
        EEE.LOGGER.atDebug().log("{}",EEEBlockEntities.ENCHANTMENT_COPYING_TABLE_ENTITY);

        return level.isClientSide() ? createTickerHelper(blockEntityType, EEEBlockEntities.ENCHANTMENT_COPYING_TABLE_ENTITY, EnchantmentCopyingTableEntity::bookAnimationTick) : null;
    }

    @Override
    public void animateTick(@NotNull BlockState blockState, @NotNull Level level, @NotNull BlockPos blockPos, @NotNull RandomSource randomSource) {
        super.animateTick(blockState, level, blockPos, randomSource);

        for(BlockPos blockPos2 : BOOKSHELF_OFFSETS) {
            if (randomSource.nextInt(16) == 0 && isValidBookChiseledShelf(level, blockPos, blockPos2)) {
                level.addParticle(ParticleTypes.ENCHANT,
                        (double)blockPos.getX() + (double)0.5F,
                        (double)blockPos.getY() + (double)2.0F,
                        (double)blockPos.getZ() + (double)0.5F,
                        (double)((float)blockPos2.getX() + randomSource.nextFloat())
                                - (double)0.5F, ((float)blockPos2.getY() - randomSource.nextFloat() - 1.0F),
                        (double)((float)blockPos2.getZ() + randomSource.nextFloat()) - (double)0.5F);
            }
        }

    }

    public static boolean isValidBookChiseledShelf(Level level, BlockPos blockPos, BlockPos blockPos2) {
        return level.getBlockState(blockPos.offset(blockPos2)).is(Blocks.CHISELED_BOOKSHELF) &&
                level.getBlockState(blockPos.offset(blockPos2.getX() / 2, blockPos2.getY(), blockPos2.getZ() / 2))
                        .is(BlockTags.ENCHANTMENT_POWER_TRANSMITTER);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
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

        if (blockEntity instanceof EnchantmentCopyingTableEntity enchantmentCopyingTableEntity) {
            return new SimpleMenuProvider(
                    (syncId, inventory, player) ->
                            new EnchantmentCopyingMenu(
                                    syncId,
                                    inventory,
                                    ContainerLevelAccess.create(level, blockPos)
                            ),
                    enchantmentCopyingTableEntity.getDisplayName()
            );
        }

        return null;
    }
}

