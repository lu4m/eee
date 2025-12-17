package eee.eee4.registry;

import eee.eee4.EEE;
import eee.eee4.blockEntities.EnchantmentCopyingTableEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.NotNull;


public class EEEBlockEntities {

    public static final BlockEntityType<@NotNull EnchantmentCopyingTableEntity> ENCHANTMENT_COPYING_TABLE_ENTITY;
    static {
        ENCHANTMENT_COPYING_TABLE_ENTITY = register(EnchantmentCopyingTableEntity::new, EEEBlocks.ENCHANTMENT_COPYING_TABLE);
    }
    private static <T extends BlockEntity> BlockEntityType<@NotNull T> register(
            FabricBlockEntityTypeBuilder.Factory<? extends @NotNull T> entityFactory,
            Block... blocks
    ) {
        Identifier id = Identifier.fromNamespaceAndPath(EEE.MOD_ID, "enchantment_copying_table");
        return Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id, FabricBlockEntityTypeBuilder.<T>create(entityFactory, blocks).build());
    }

    public static void initialize() {}
}

