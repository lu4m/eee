package eee.eee4.renderer.blockentity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;

@Environment(EnvType.CLIENT)
public class EnchantmentCopyingRenderState extends BlockEntityRenderState {
    public float time;
    public float yRot;
    public float flip;
    public float open;
}
