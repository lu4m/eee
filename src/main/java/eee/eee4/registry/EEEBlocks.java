package eee.eee4.registry;

import eee.eee4.EEE;
import eee.eee4.blocks.EnchantmentCopyingTable;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.jetbrains.annotations.NotNull;


import java.util.function.Function;

public final class EEEBlocks {

    public static final Block ENCHANTMENT_COPYING_TABLE;

    static {
        ENCHANTMENT_COPYING_TABLE = register("enchantment_copying_table",
                EnchantmentCopyingTable::new,
                BlockBehaviour.Properties.ofFullCopy(Blocks.CALCITE)
                        .strength(1.75f,7f)
                        .lightLevel(s -> 5)
                        .requiresCorrectToolForDrops()
                , true);
    }

    // force loading
    public static void initialize() {}

    private static Block register(String path, Function<BlockBehaviour.Properties,
            Block> blockFactory, BlockBehaviour.Properties settings, boolean shouldRegisterItem) {

        ResourceKey <@NotNull Block> blockKey = keyOfBlock(path);
        Block block = blockFactory.apply(settings.setId(blockKey));

        if (shouldRegisterItem) {
            ResourceKey<@NotNull Item> itemKey = keyOfItem(path);

            BlockItem blockItem = new BlockItem(block, new Item.Properties().setId(itemKey).useBlockDescriptionPrefix());
            Registry.register(BuiltInRegistries.ITEM, itemKey, blockItem);
        }

        return Registry.register(BuiltInRegistries.BLOCK, blockKey, block);
    }

    private static ResourceKey<@NotNull Block> keyOfBlock(String name) {
        return ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(EEE.MOD_ID, name));
    }

    private static ResourceKey<@NotNull Item> keyOfItem(String name) {
        return ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(EEE.MOD_ID, name));
    }

}
