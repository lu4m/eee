package eee.eee4.registry;

import eee.eee4.EEE;
import eee.eee4.blockEntities.EnchantmentCopyingTableEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class EEEBlockEntities {
    public static final BlockEntityType<EnchantmentCopyingTableEntity> ENCHANTMENT_COPYING_TABLE_ENTITY;

    static {
        ENCHANTMENT_COPYING_TABLE_ENTITY = register("enchantment_copying_table_entity", EnchantmentCopyingTableEntity::new, EEEBlocks.ENCHANTMENT_COPYING_TABLE);
    }

    //force loading
    public static void initialize() {

    }

    private static <T extends BlockEntity> BlockEntityType<T> register(
            String path,
            FabricBlockEntityTypeBuilder.Factory<? extends T> entityFactory,
            Block... blocks
    ) {
        Identifier id = Identifier.of(EEE.MOD_ID, path);
        return Registry.register(Registries.BLOCK_ENTITY_TYPE, id, FabricBlockEntityTypeBuilder.<T>create(entityFactory, blocks).build());
    }
}
