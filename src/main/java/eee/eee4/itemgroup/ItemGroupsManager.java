package eee.eee4.itemgroup;

import eee.eee4.registry.EEEBlocks;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Items;

public final class ItemGroupsManager {

    public static void initialize(){
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS).register(
                entries ->  entries.addAfter(Items.ENCHANTING_TABLE, EEEBlocks.ENCHANTMENT_COPYING_TABLE)
        );
    }

}
