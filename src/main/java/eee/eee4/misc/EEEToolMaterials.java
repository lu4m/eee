package eee.eee4.misc;

import eee.eee4.EEE;
import eee.eee4.registry.EEEItems;
import eee.eee4.registry.EEETags;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.crafting.Ingredient;

public class EEEToolMaterials {
    public static final ToolMaterial RITUALISTIC = new ToolMaterial(
            BlockTags.INCORRECT_FOR_STONE_TOOL,
            150,
            4.0f,
            1.5F,
            22,
            EEETags.RITUALISTIC_REPAIR
    );
}
