package eee.eee4.blockEntities;

import eee.eee4.registry.EEEBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public class EnchantmentCopyingTableEntity extends BlockEntity {
    public EnchantmentCopyingTableEntity(BlockPos pos, BlockState state) {
        super(EEEBlockEntities.ENCHANTMENT_COPYING_TABLE_ENTITY, pos, state);
    }
}
