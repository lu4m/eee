package eee.eee4.registry;

import eee.eee4.EEE;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class EEETags {

    public static final TagKey<Item> RITUALISTIC_REPAIR =
            TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(EEE.MOD_ID,"ritualistic_repair"));
}

