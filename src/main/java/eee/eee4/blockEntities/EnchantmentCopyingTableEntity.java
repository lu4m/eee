package eee.eee4.blockEntities;

import eee.eee4.registry.EEEBlockEntities;
import eee.eee4.screenHandler.EnchantmentCopyingScreenHandler;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class EnchantmentCopyingTableEntity extends BlockEntity {
    public EnchantmentCopyingTableEntity(BlockPos pos, BlockState state) {
        super(EEEBlockEntities.ENCHANTMENT_COPYING_TABLE_ENTITY, pos, state);
    }
}
