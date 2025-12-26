package eee.eee4.blockEntitie;

import eee.eee4.registry.EEEBlockEntities;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

public class EnchantmentCopyingTableEntity extends BlockEntity implements Nameable {

    private static final Component DEFAULT_NAME = Component.translatable("gui.eee4.enchantment_copying.title");
    public int time;
    public float flip;
    public float oFlip;
    public float flipT;
    public float flipA;
    public float open;
    public float oOpen;
    public float rot;
    public float oRot;
    public float tRot;
    private static final RandomSource RANDOM = RandomSource.create();
    private @Nullable Component name;

    private int lastPage;

    public EnchantmentCopyingTableEntity(BlockPos blockPos, BlockState blockState) {
        super(EEEBlockEntities.ENCHANTMENT_COPYING_TABLE_ENTITY, blockPos, blockState);
    }

    public static void bookAnimationTick(Level level, BlockPos blockPos, BlockState blockState, EnchantmentCopyingTableEntity tableEntity) {
        tableEntity.oOpen = tableEntity.open;
        tableEntity.oRot = tableEntity.rot;
        Player player = level.getNearestPlayer((double)blockPos.getX() + (double)0.5F, (double)blockPos.getY() + (double)0.5F, (double)blockPos.getZ() + (double)0.5F, (double)3.0F, false);
        if (player != null) {
            double d = player.getX() - ((double)blockPos.getX() + (double)0.5F);
            double e = player.getZ() - ((double)blockPos.getZ() + (double)0.5F);
            tableEntity.tRot = (float) Mth.atan2(e, d);
            tableEntity.open += 0.1F;
            if (tableEntity.open < 0.5F || RANDOM.nextInt(40) == 0) {
                float f = tableEntity.flipT;

                do {
                    tableEntity.flipT += (float)(RANDOM.nextInt(4) - RANDOM.nextInt(4));
                } while(f == tableEntity.flipT);
            }
        } else {
            tableEntity.tRot += 0.02F;
            tableEntity.open -= 0.1F;
        }

        while(tableEntity.rot >= (float)Math.PI) {
            tableEntity.rot -= ((float)Math.PI * 2F);
        }

        while(tableEntity.rot < -(float)Math.PI) {
            tableEntity.rot += ((float)Math.PI * 2F);
        }

        while(tableEntity.tRot >= (float)Math.PI) {
            tableEntity.tRot -= ((float)Math.PI * 2F);
        }

        while(tableEntity.tRot < -(float)Math.PI) {
            tableEntity.tRot += ((float)Math.PI * 2F);
        }

        float g;
        for(g = tableEntity.tRot - tableEntity.rot; g >= (float)Math.PI; g -= ((float)Math.PI * 2F));

        while(g < -(float)Math.PI) {
            g += ((float)Math.PI * 2F);
        }

        tableEntity.rot += g * 0.4F;
        tableEntity.open = Mth.clamp(tableEntity.open, 0.0F, 1.0F);
        ++tableEntity.time;
        tableEntity.oFlip = tableEntity.flip;
        float h = (tableEntity.flipT - tableEntity.flip) * 0.4F;
        float i = 0.2F;
        h = Mth.clamp(h, -0.2F, 0.2F);
        tableEntity.flipA += (h - tableEntity.flipA) * 0.9F;
        tableEntity.flip += tableEntity.flipA;
    }

    public static boolean canAccessBookshelves(Level level, BlockPos blockPos, BlockPos offset){
        return level.getBlockState(blockPos.offset(offset.getX() / 2, offset.getY(), offset.getZ() / 2)).is(BlockTags.ENCHANTMENT_POWER_TRANSMITTER);
    }

    @Override
    public @NotNull Component getName() {
        return this.name != null ? this.name : DEFAULT_NAME;
    }

    public void setCustomName(@Nullable Component component) {
        this.name = component;
    }
    @Override
    public @Nullable Component getCustomName() {
        return this.name;
    }

    @Override
    protected void applyImplicitComponents(@NotNull DataComponentGetter dataComponentGetter) {
        super.applyImplicitComponents(dataComponentGetter);
        this.name = dataComponentGetter.get(DataComponents.CUSTOM_NAME);
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.@NotNull Builder builder) {
        super.collectImplicitComponents(builder);
        builder.set(DataComponents.CUSTOM_NAME, this.name);
    }

    public int getLastPage() {
        return lastPage;
    }

    public void setLastPage(int lastPage) {
        this.lastPage = lastPage;
        setChanged();
    }

}
