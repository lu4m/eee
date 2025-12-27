package eee.eee4.blockEntitie;

import eee.eee4.registry.EEEBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class EnchantmentSplittingTableEntity extends BlockEntity {

    public int time;
    public float hover;
    public float oHover;
    public float bobA;
    public float oBobA;
    public float twistF;
    public float oTwistF;

    public EnchantmentSplittingTableEntity(BlockPos blockPos, BlockState blockState) {
        super(EEEBlockEntities.ENCHANTMENT_SPLITTING_TABLE_ENTITY, blockPos, blockState);
    }

    public static void itemAnimationTick(Level level, BlockPos blockPos, BlockState blockState, EnchantmentSplittingTableEntity tableEntity){

        tableEntity.oHover = tableEntity.hover;
        tableEntity.oTwistF = tableEntity.twistF;
        tableEntity.oBobA = tableEntity.bobA;
        ++tableEntity.time;

        Player player = level.getNearestPlayer((double)blockPos.getX() + (double)0.5F, (double)blockPos.getY() + (double)0.5F, (double)blockPos.getZ() + (double)0.5F, (double)3.0F, false);
        if (player != null) {
            tableEntity.hover += 0.1F;
        }else{
            tableEntity.hover -= 0.1F;
        }

        if (tableEntity.hover > 0.5F) {
            tableEntity.bobA += 0.2F;
            tableEntity.twistF += 0.2F;
        }
        else {
            tableEntity.bobA -= 0.15F;
            tableEntity.twistF -= 0.15F;
        }

        tableEntity.hover = Mth.clamp(tableEntity.hover, 0.0F, 1F);
        tableEntity.bobA = Mth.clamp(tableEntity.bobA, 0.0F, 1F);
        tableEntity.twistF = Mth.clamp(tableEntity.twistF, 0.0F, 1F);
    }


}
