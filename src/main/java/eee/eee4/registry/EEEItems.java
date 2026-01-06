package eee.eee4.registry;

import eee.eee4.EEE;
import eee.eee4.misc.EEEToolMaterials;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.BookContent;

import java.util.function.Function;

public class EEEItems {

    public static final Item RITUALISTIC_SWORD;
    public static final Item SEMANTIC_SPLITTING_SWORD;
    public static final Item STONE_TABLET;
    public static final Item ARCHAEOLOGICAL_ANNOTATIONS;
    static {
        RITUALISTIC_SWORD = register("ritualistic_sword", Item::new, new Item.Properties().sword(EEEToolMaterials.RITUALISTIC,3.0F,-2.8F));
        SEMANTIC_SPLITTING_SWORD = register("semantic_splitting_sword", Item::new, new Item.Properties().sword(EEEToolMaterials.RITUALISTIC,3.5F,-3.0F));
        STONE_TABLET = register("stone_tablet", Item::new ,new Item.Properties());
        ARCHAEOLOGICAL_ANNOTATIONS = register("archaeological_annotations", Item::new, new Item.Properties());
    }

    public static <GenericItem extends Item> GenericItem register(String name, Function<Item.Properties, GenericItem> itemFactory, Item.Properties settings) {

        ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(EEE.MOD_ID, name));
        GenericItem item = itemFactory.apply(settings.setId(itemKey));
        Registry.register(BuiltInRegistries.ITEM, itemKey, item);

        return item;
    }

    public static void initialize(){}

}
