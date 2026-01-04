package eee.eee4.screen;

import eee.eee4.EEE;
import eee.eee4.EeeClient;
import eee.eee4.menus.EnchantmentSplittingMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

import java.util.List;

public class EnchantmentSplittingScreen extends AbstractContainerScreen<EnchantmentSplittingMenu> {

    private static final Identifier BG_TEXTURE = Identifier.fromNamespaceAndPath(EEE.MOD_ID, "textures/gui/container/enchantment_splitting.png");
    private static final Identifier ENCHANTMENT_SLOT_HIGHLIGHTED= Identifier.fromNamespaceAndPath(EEE.MOD_ID,"container/enchantment_splitting/bigger_slot_highlighted");
    private static final Identifier ENCHANTMENT_SLOT = Identifier.fromNamespaceAndPath(EEE.MOD_ID,"container/enchantment_splitting/bigger_slot");
    private static final Identifier ENCHANTMENT_SLOT_DISABLED = Identifier.fromNamespaceAndPath(EEE.MOD_ID,"container/enchantment_splitting/bigger_slot_disabled");

    public EnchantmentSplittingScreen(EnchantmentSplittingMenu handler, Inventory inventory, Component title) {
        super(handler, inventory, title);

        this.imageWidth = 176;
        this.imageHeight = 215;

        this.titleLabelX = 9;
        this.titleLabelY = 6;

        this.inventoryLabelX = 9;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float delta, int mouseX, int mouseY) {
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED,BG_TEXTURE,this.leftPos,this.topPos,0.0f,0.0f,this.imageWidth,this.imageHeight,256,256);

        renderEnchantments(guiGraphics);
    }

    private void renderEnchantments(GuiGraphics guiGraphics){
        int start = this.menu.getTopDisplayIndex();
        int last = this.menu.enchantmentsDataList.size();
        for (int i = 0; i<3 && start+i < last; i++){

            var texture = ENCHANTMENT_SLOT_DISABLED;
            int color = 0xFF685E4A;
            boolean hover = false;
            if(hover){
                color = 0xFFFFFF80;
                texture = ENCHANTMENT_SLOT_HIGHLIGHTED;
            }
            else if (menu.selected[i]) {
                color = 0xFF342F25;
                texture = ENCHANTMENT_SLOT;
            }
            guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, texture, this.leftPos + 40, this.topPos + 18 + (22 * i), 108, 22);
            Component text = this.menu.enchantmentsDataList.get(start + i).name();

            List<FormattedCharSequence> lines =
                    this.font.split(text, 90);

            for (int line = 0; line < lines.size(); line++) {
                guiGraphics.drawString(
                        this.font,
                        lines.get(line),
                        this.leftPos + 42,
                        this.topPos + 20 + (22 * i) + (line * 10),
                        color,
                        false
                );
            }

        }
    }

    private boolean isMouseOverSlot(int slot, int mouseX, int mouseY){
        return false;
    }
}
