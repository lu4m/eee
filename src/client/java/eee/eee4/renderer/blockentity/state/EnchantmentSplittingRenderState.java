package eee.eee4.renderer.blockentity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;


@Environment(EnvType.CLIENT)
public class EnchantmentSplittingRenderState extends BlockEntityRenderState{
    public ItemStackRenderState itemState;
    public float hover;
    public float time;
    public float transX;
    public float transY;
    public float transZ;
    public EnchantmentSplittingRenderState() {
        this.itemState = new  ItemStackRenderState();
    }
}
