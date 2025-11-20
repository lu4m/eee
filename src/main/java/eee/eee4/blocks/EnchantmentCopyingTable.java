package eee.eee4.blocks;

import com.mojang.serialization.MapCodec;
import eee.eee4.blockEntities.EnchantmentCopyingTableEntity;
import eee.eee4.screenHandler.EnchantmentCopyingScreenHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class EnchantmentCopyingTable extends BlockWithEntity {

    public EnchantmentCopyingTable(Settings settings) {
        super(settings);

    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return createCodec(EnchantmentCopyingTable::new);
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new EnchantmentCopyingTableEntity(pos, state);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof EnchantmentCopyingTableEntity) {
            player.openHandledScreen(state.createScreenHandlerFactory(world, pos));
        }

        return ActionResult.SUCCESS;
    }

    @Override
    public NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof EnchantmentCopyingTableEntity) {

            return new SimpleNamedScreenHandlerFactory((syncId, inventory, player)
                    -> new EnchantmentCopyingScreenHandler(syncId, inventory, ScreenHandlerContext.create(world, pos)
                    )
                    , Text.literal("Enchantment Copying"));
        } else {
            return null;
        }
    }

}
