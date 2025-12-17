package eee.eee4.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import eee.eee4.blockEntities.EnchantmentCopyingTableEntity;
import eee.eee4.renderer.blockentity.state.EnchantmentCopyingRenderState;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.object.book.BookModel;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.MaterialSet;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class EnchantmentCopyingRenderer implements BlockEntityRenderer<@NotNull EnchantmentCopyingTableEntity, @NotNull EnchantmentCopyingRenderState> {
    public static final Material BOOK_TEXTURE;
    private final MaterialSet materials;
    private final BookModel bookModel;

    public EnchantmentCopyingRenderer(BlockEntityRendererProvider.Context context) {
        this.materials = context.materials();
        this.bookModel = new BookModel(context.bakeLayer(ModelLayers.BOOK));
    }
    @Override
    public EnchantmentCopyingRenderState createRenderState() {
        return new EnchantmentCopyingRenderState();
    }

    @Override
    public void extractRenderState(
            EnchantmentCopyingTableEntity tableEntity, EnchantmentCopyingRenderState renderState,
            float f, @NotNull Vec3 vec3, ModelFeatureRenderer.@Nullable CrumblingOverlay crumblingOverlay
    ) {
        BlockEntityRenderer.super.extractRenderState(tableEntity, renderState, f, vec3, crumblingOverlay);
        renderState.flip = Mth.lerp(f, tableEntity.oFlip, tableEntity.flip);
        renderState.open = Mth.lerp(f, tableEntity.oOpen, tableEntity.open);
        renderState.time = (float) tableEntity.time + f;

        float g;
        for(g = tableEntity.rot - tableEntity.oRot; g >= (float)Math.PI; g -= ((float)Math.PI * 2F)) {}

        while(g < -(float)Math.PI) {
            g += ((float)Math.PI * 2F);
        }

        renderState.yRot = tableEntity.oRot + g * f;
    }

    @Override
    public void submit(EnchantmentCopyingRenderState renderState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {
        poseStack.pushPose();
        poseStack.translate(0.5F, 0.75F, 0.5F);
        poseStack.translate(0.0F, 0.1F + Mth.sin((renderState.time * 0.1F)) * 0.01F, 0.0F);
        float f = renderState.yRot;
        poseStack.mulPose(Axis.YP.rotation(-f));
        poseStack.mulPose(Axis.ZP.rotationDegrees(80.0F));
        float g = Mth.frac(renderState.flip + 0.25F) * 1.6F - 0.3F;
        float h = Mth.frac(renderState.flip + 0.75F) * 1.6F - 0.3F;
        BookModel.State state = new BookModel.State(renderState.time, Mth.clamp(g, 0.0F, 1.0F), Mth.clamp(h, 0.0F, 1.0F), renderState.open);
        submitNodeCollector.submitModel(this.bookModel, state, poseStack, BOOK_TEXTURE.renderType(RenderTypes::entitySolid), renderState.lightCoords, OverlayTexture.NO_OVERLAY, -1, this.materials.get(BOOK_TEXTURE), 0, renderState.breakProgress);
        poseStack.popPose();
    }

    static {
        BOOK_TEXTURE = Sheets.BLOCK_ENTITIES_MAPPER.defaultNamespaceApply("enchanting_table_book");
    }
}