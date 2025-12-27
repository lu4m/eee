package eee.eee4.misc;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ToolMaterial;

public class EEEToolMaterials {
    public static final ToolMaterial RITUALISTIC = new ToolMaterial(
            BlockTags.INCORRECT_FOR_STONE_TOOL,
            150,
            4.0f,
            1.5F,
            22,
            ItemTags.COALS  // TODO
    );
}
