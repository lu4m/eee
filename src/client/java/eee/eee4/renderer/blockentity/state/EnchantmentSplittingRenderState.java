package eee.eee4.renderer.blockentity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.core.Direction;


@Environment(EnvType.CLIENT)
public class EnchantmentSplittingRenderState extends BlockEntityRenderState{
    public ItemStackRenderState itemState;
    public float hover;
    public float time;
    public float transX;
    public float transY;
    public float transZ;
    public float yRot;
    public EnchantmentSplittingRenderState() {
        this.itemState = new  ItemStackRenderState();
    }
}
