package eee.eee4.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import eee.eee4.blockEntitie.EnchantmentSplittingTableEntity;
import eee.eee4.renderer.blockentity.state.EnchantmentSplittingRenderState;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
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
            @NotNull EnchantmentSplittingTableEntity e,
            @NotNull EnchantmentSplittingRenderState s,
            float partialTick,
            @NotNull Vec3 vec3,
            ModelFeatureRenderer.@Nullable CrumblingOverlay crumblingOverlay
    ) {

        BlockEntityRenderer.super.extractRenderState(
                e, s, partialTick, vec3, crumblingOverlay
        );

        s.hover  = Mth.lerp(partialTick, e.oHover,  e.hover);
        s.transX = Mth.lerp(partialTick, e.oTransX, e.transX);
        s.transZ = Mth.lerp(partialTick, e.oTransZ, e.transZ);
        s.transY = Mth.lerp(partialTick, e.oTransY, e.transY);

        s.yRot = switch (e.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING)) {
            case NORTH -> 180.0F;
            case SOUTH -> 0.0F;
            case WEST  -> 90.0F;
            case EAST  -> -90.0F;
            default -> 0.0F;
        };

        s.time = e.time + partialTick;

        itemModelResolver.updateForTopItem(
                s.itemState,
                DIAMOND_SWORD_STACK,
                ItemDisplayContext.FIXED,
                e.getLevel(),
                null,
                0
        );
    }

    @Override
    public EnchantmentSplittingRenderState createRenderState() {
        return new EnchantmentSplittingRenderState();
    }

    @Override
    public void submit(
            EnchantmentSplittingRenderState s,
            @NotNull PoseStack poseStack,
            @NotNull SubmitNodeCollector collector,
            @NotNull CameraRenderState camera
    ) {

        poseStack.pushPose();

        // initial pose
        poseStack.translate(0.5F, 0.85F, 0.5F);

        // purposely early Y rotation
        poseStack.mulPose(Axis.YP.rotationDegrees(s.yRot));

        // hover
        float hoverEase = (float) Mth.smoothstep(s.hover);
        poseStack.translate(0.0F, hoverEase * 0.55F, 0.0F);

        // translate
        poseStack.translate(s.transX,s.transY,s.transZ);

        // bob
        float bob =
                (
                        Mth.sin(s.time * 0.15F) * 0.06F +
                        Mth.sin((s.time * 0.04F)+7.0F)  * 0.02F
                ) * s.hover;

        poseStack.translate(0.0F, bob, 0.0F);

        // downwards orientation
        poseStack.mulPose(Axis.ZP.rotationDegrees(135.0F));

        // tilt
        poseStack.mulPose(Axis.XP.rotationDegrees(
                Mth.sin(s.time * 0.07F) * 3.0F * s.hover
        ));
        poseStack.mulPose(Axis.ZP.rotationDegrees(
                Mth.cos(s.time * 0.09F) * 2.0F * s.hover
        ));

        poseStack.mulPose(Axis.YP.rotationDegrees(
                Mth.cos(s.time * 0.09F) * 3.0F * s.hover
        ));

        // scale down
        poseStack.scale(0.75F, 0.75F, 0.75F);

        // breathe
        float breathe = 1.0F + Mth.sin(s.time * 0.08F) * 0.03F * s.hover;
        poseStack.scale(breathe, breathe, breathe);

        // the item model
        s.itemState.submit(
                poseStack,
                collector,
                s.lightCoords,
                OverlayTexture.NO_OVERLAY,
                0
        );

        poseStack.popPose();

    }

}
