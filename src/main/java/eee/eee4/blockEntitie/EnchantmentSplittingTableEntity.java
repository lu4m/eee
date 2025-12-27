package eee.eee4.blockEntitie;

import eee.eee4.registry.EEEBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Random;

public class EnchantmentSplittingTableEntity extends BlockEntity {

    public int time;
    public float hover;
    public float oHover;
    public float transX;
    public float oTransX;
    public float transY;
    public float oTransY;
    public float transZ;
    public float oTransZ;

    private float transXTarget = 0;
    private float transYTarget = 0;
    private float transZTarget = 0;

    private int wanderCooldown = 0;

    public static final Random RANDOM_SOURCE = new Random();

    public EnchantmentSplittingTableEntity(BlockPos blockPos, BlockState blockState) {
        super(EEEBlockEntities.ENCHANTMENT_SPLITTING_TABLE_ENTITY, blockPos, blockState);
    }

    public static void itemAnimationTick(Level level, BlockPos pos, BlockState blockState, EnchantmentSplittingTableEntity e){

        e.oHover  = e.hover;

        e.oTransX = e.transX;
        e.oTransY = e.transY;
        e.oTransZ = e.transZ;

        ++e.time;

        Player player = level.getNearestPlayer(
                pos.getX() + 0.5,
                pos.getY() + 0.5,
                pos.getZ() + 0.5,
                3.0,
                false
        );

        boolean active = player != null;

        if (active && e.hover > 0.9F) {
            if (--e.wanderCooldown <= 0) {
                pickNewWanderTarget(e);
                e.wanderCooldown = 50 + RANDOM_SOURCE.nextInt(70);
            }
        } else {
            e.transXTarget = 0F;
            e.transYTarget = 0F;
            e.transZTarget = 0F;
        }

        if (active)
            e.hover += 0.1F;
        else if (Mth.abs(e.transXTarget - e.transX) < 0.05F
                        && Mth.abs(e.transZTarget - e.transZ) < 0.05F) {
            e.hover -= 0.1F;
        }

        e.hover  = Mth.clamp(e.hover,  0F, 1F);

        e.transX += (e.transXTarget - e.transX) * 0.08F;
        e.transY += (e.transYTarget - e.transY) * 0.08F;
        e.transZ += (e.transZTarget - e.transZ) * 0.08F;
    }


    private static void pickNewWanderTarget(EnchantmentSplittingTableEntity e) {
        e.transXTarget = (EnchantmentSplittingTableEntity.RANDOM_SOURCE.nextFloat() - 0.5F) * 0.6F;
        e.transZTarget = (EnchantmentSplittingTableEntity.RANDOM_SOURCE.nextFloat() - 0.5F) * 0.6F;
        e.transYTarget = EnchantmentSplittingTableEntity.RANDOM_SOURCE.nextFloat() * 0.4F;
    }

}
