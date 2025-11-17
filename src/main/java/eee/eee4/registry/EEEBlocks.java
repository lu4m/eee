package eee.eee4.registry;

import eee.eee4.EEE;
import eee.eee4.blocks.EnchantmentCopyingTable;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public class EEEBlocks {

    public static final Block ENCHANTMENT_COPYING_TABLE;

    static {
        ENCHANTMENT_COPYING_TABLE = register("enchantment_copying_table", EnchantmentCopyingTable::new, AbstractBlock.Settings.create(), true);
    }

    // force loading
    public static void initialize() {}

    private static Block register(String path, Function<AbstractBlock.Settings, Block> blockFactory, AbstractBlock.Settings settings, boolean shouldRegisterItem) {

        RegistryKey<Block> blockKey = keyOfBlock(path);
        Block block = blockFactory.apply(settings.registryKey(blockKey));

        if (shouldRegisterItem) {
            RegistryKey<Item> itemKey = keyOfItem(path);

            BlockItem blockItem = new BlockItem(block, new Item.Settings().registryKey(itemKey).useBlockPrefixedTranslationKey());
            Registry.register(Registries.ITEM, itemKey, blockItem);
        }

        return Registry.register(Registries.BLOCK, blockKey, block);
    }

    private static RegistryKey<Block> keyOfBlock(String name) {
        return RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(EEE.MOD_ID, name));
    }

    private static RegistryKey<Item> keyOfItem(String name) {
        return RegistryKey.of(RegistryKeys.ITEM, Identifier.of(EEE.MOD_ID, name));
    }

}
