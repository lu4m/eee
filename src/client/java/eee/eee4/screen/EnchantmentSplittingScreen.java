package eee.eee4.screen;

import eee.eee4.EEE;
import eee.eee4.menus.EnchantmentSplittingMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class EnchantmentSplittingScreen extends AbstractContainerScreen<EnchantmentSplittingMenu> {

    private static final Identifier BG_TEXTURE = Identifier.fromNamespaceAndPath(EEE.MOD_ID, "textures/gui/container/enchantment_splitting.png");
    private static final Identifier ENCHANTMENT_SLOT_DISABLED = Identifier.withDefaultNamespace("container/enchanting_table/enchantment_slot_disabled");
    private static final Identifier ENCHANTMENT_SLOT_HIGHLIGHTED= Identifier.withDefaultNamespace("container/enchanting_table/enchantment_slot_highlighted");
    private static final Identifier ENCHANTMENT_SLOT = Identifier.withDefaultNamespace("container/enchanting_table/enchantment_slot");

    public EnchantmentSplittingScreen(EnchantmentSplittingMenu handler, Inventory inventory, Component title) {
        super(handler, inventory, title);

        this.imageWidth = 176;
        this.imageHeight = 206;

        this.titleLabelX = 10;
        this.titleLabelY = 6;

        this.inventoryLabelX = 9;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float f, int i, int j) {
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED,BG_TEXTURE,this.leftPos,this.topPos,0.0f,0.0f,this.imageWidth,this.imageHeight,256,256);

        renderEnchantments(guiGraphics);
    }

    private void renderEnchantments(GuiGraphics guiGraphics){
        int start = this.menu.getTopDisplayIndex();
        int last = this.menu.enchantmentsDataList.size();
        for (int i = 0; i<3 && start+i < last; i++){
            guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED,ENCHANTMENT_SLOT_DISABLED,this.leftPos+20,this.topPos+40+(19*i),108,19);
            guiGraphics.drawString(this.font,this.menu.enchantmentsDataList.get(i).name(),this.leftPos+24,this.topPos+42+(19*i),0XFFFFFFFF);
        }
    }
}
