package eee.eee4.registry;

import eee.eee4.EEE;
import eee.eee4.blockEntitie.EnchantmentCopyingTableEntity;
import eee.eee4.blockEntitie.EnchantmentSplittingTableEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.NotNull;


public final class EEEBlockEntities {

    public static final BlockEntityType<@NotNull EnchantmentCopyingTableEntity> ENCHANTMENT_COPYING_TABLE_ENTITY;
    public static final BlockEntityType<@NotNull EnchantmentSplittingTableEntity> ENCHANTMENT_SPLITTING_TABLE_ENTITY;
    static {
        ENCHANTMENT_COPYING_TABLE_ENTITY = register("enchantment_copying_table",EnchantmentCopyingTableEntity::new, EEEBlocks.ENCHANTMENT_COPYING_TABLE);
        ENCHANTMENT_SPLITTING_TABLE_ENTITY = register("enchantment_splitting_table",EnchantmentSplittingTableEntity::new,EEEBlocks.ENCHANTMENT_SPLITTING_TABLE);
    }
    private static <T extends BlockEntity> BlockEntityType<@NotNull T> register(
            String path,
            FabricBlockEntityTypeBuilder.Factory<? extends @NotNull T> entityFactory,
            Block... blocks
    ) {
        Identifier id = Identifier.fromNamespaceAndPath(EEE.MOD_ID, path);
        return Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id, FabricBlockEntityTypeBuilder.<T>create(entityFactory, blocks).build());
    }

    public static void initialize() {}
}

