package eee.eee4.blockEntitie;

import eee.eee4.registry.EEEBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class EnchantmentSplittingTableEntity extends BlockEntity {

    public int time;

    public EnchantmentSplittingTableEntity(BlockPos blockPos, BlockState blockState) {
        super(EEEBlockEntities.ENCHANTMENT_SPLITTING_TABLE_ENTITY, blockPos, blockState);
    }

    public static void itemAnimationTick(Level level, BlockPos blockPos, BlockState blockState, EnchantmentSplittingTableEntity tableEntity){
        ++tableEntity.time;
    }


}
