package eee.eee4.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import eee.eee4.EEE;
import eee.eee4.blockEntitie.EnchantmentSplittingTableEntity;
import eee.eee4.renderer.blockentity.state.EnchantmentSplittingRenderState;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class EnchantmentSplittingRenderer implements BlockEntityRenderer<@NotNull EnchantmentSplittingTableEntity, @NotNull EnchantmentSplittingRenderState> {

    private final ItemModelResolver itemModelResolver;
    private static final ItemStack DIAMOND_SWORD_STACK = new ItemStack(Items.DIAMOND_SWORD);


    public EnchantmentSplittingRenderer(BlockEntityRendererProvider.Context context) {
        itemModelResolver = context.itemModelResolver();

    }

    @Override
    public void extractRenderState(
            @NotNull EnchantmentSplittingTableEntity blockEntity,
            @NotNull EnchantmentSplittingRenderState renderState,
            float f,
            @NotNull Vec3 vec3,
            ModelFeatureRenderer.@Nullable CrumblingOverlay crumblingOverlay
    ) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, renderState, f, vec3, crumblingOverlay);

        renderState.time = (float) blockEntity.time + f;

        itemModelResolver.updateForTopItem(renderState.itemState, DIAMOND_SWORD_STACK, ItemDisplayContext.FIXED,blockEntity.getLevel(),null,0);
    }

    @Override
    public EnchantmentSplittingRenderState createRenderState() {
        return new EnchantmentSplittingRenderState();
    }

    @Override
    public void submit(
            EnchantmentSplittingRenderState renderState,
            @NotNull PoseStack poseStack,
            @NotNull SubmitNodeCollector collector,
            @NotNull CameraRenderState camera
    ) {

        poseStack.pushPose();

        poseStack.translate(0.5F, 1.4F, 0.5F);

        float bob = Mth.sin(renderState.time * 0.15F) * 0.1F;

        poseStack.translate(0.0D, bob, 0.0D);

        poseStack.mulPose(Axis.ZP.rotationDegrees(135.0F));

        poseStack.scale(0.8F, 0.8F, 0.8F);

        renderState.itemState.submit(
                poseStack,
                collector,
                renderState.lightCoords,
                OverlayTexture.NO_OVERLAY,
                0
        );

        poseStack.popPose();

    }

}
