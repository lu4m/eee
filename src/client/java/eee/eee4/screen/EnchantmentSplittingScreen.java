package eee.eee4.screen;

import eee.eee4.EEE;
import eee.eee4.menus.EnchantmentSplittingMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class EnchantmentSplittingScreen extends AbstractContainerScreen<EnchantmentSplittingMenu> {

    private float scrollOffs = 0.0F;
    private boolean scrolling = false;

    private static final int SCROLLBAR_X = 151;
    private static final int SCROLLBAR_Y = 18;
    private static final int SCROLLBAR_HEIGHT = 66;
    private static final int SCROLLBAR_WIDTH = 12;

    private static final int THUMB_HEIGHT = 15;


    private static final Identifier SCROLL_THUMB_TEXTURE = Identifier.withDefaultNamespace("container/creative_inventory/scroller");
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

        renderEnchantments(guiGraphics, mouseX, mouseY);
        renderScrollbar(guiGraphics);
    }

    private void renderEnchantments(GuiGraphics guiGraphics, int mouseX, int mouseY){
        int start = getScrollIndex();
        int last = this.menu.enchantmentsDataList.size();
        for (int i = 0; i<3 && start+i < last; i++){

            var texture = ENCHANTMENT_SLOT_DISABLED;
            int color = 0xFF342F25;
            if(isMouseOverSlot(i, mouseX, mouseY)){
                color = 0xFFFFFF80;
                texture = ENCHANTMENT_SLOT_HIGHLIGHTED;
            }
            else if (menu.selected[i+start]) {
                color = 0xFF685E4A;
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

    private void renderScrollbar(GuiGraphics gfx) {
        int x = this.leftPos + SCROLLBAR_X;
        int y = this.topPos + SCROLLBAR_Y;

        int thumbY = y + (int)((SCROLLBAR_HEIGHT - THUMB_HEIGHT) * this.scrollOffs);

        gfx.blitSprite(RenderPipelines.GUI_TEXTURED, SCROLL_THUMB_TEXTURE, x, thumbY, SCROLLBAR_WIDTH, THUMB_HEIGHT);
    }


    @Override
    public void render(@NotNull GuiGraphics context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);
        this.renderTooltip(context, mouseX, mouseY);
    }


    @Override
    public boolean mouseClicked(MouseButtonEvent click, boolean doubled){
        int click_y = (int) Math.round(click.y());
        int click_x = (int) Math.round(click.x());

        for (int i = 0 ; i < 3 ; i++){
            if (isMouseOverSlot(i,click_x,click_y) && i + getScrollIndex() < menu.selected.length) {
                assert this.minecraft.gameMode != null;
                this.minecraft.gameMode.handleInventoryButtonClick(menu.containerId, i + getScrollIndex());
                playClickSound();
                return true;
            }
        }

        if (isMouseOverScrollbar(click_x, click_y) && canScroll()) {
            this.scrolling = true;
            return true;
        }

        return super.mouseClicked(click,doubled);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent mouseButtonEvent, double dx, double dy) {

        int y0 = (int) Math.round(mouseButtonEvent.y());

        if (this.scrolling) {
            int y = this.topPos + SCROLLBAR_Y;
            float pos = ((float)y0 - y - THUMB_HEIGHT / 2F)
                    / (SCROLLBAR_HEIGHT - THUMB_HEIGHT);

            this.scrollOffs = Mth.clamp(pos, 0.0F, 1.0F);
            return true;
        }

        return super.mouseDragged(mouseButtonEvent, dx, dy);

    }

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pHorizontalScroll, double pVerticalScroll) {
        if (!canScroll()) return false;

        int maxScroll = getMaxScroll();
        if (maxScroll <= 0) return false;

        this.scrollOffs = Mth.clamp(
                this.scrollOffs - (float)(pVerticalScroll / maxScroll),
                0.0F,
                1.0F
        );
        return true;
    }

    private int getScrollIndex() {

        return (int)(this.scrollOffs * getMaxScroll() + 0.5);
    }


    private void playClickSound() {
        Minecraft.getInstance().getSoundManager().play(
                SimpleSoundInstance.forUI(
                        SoundEvents.UI_BUTTON_CLICK,
                        1.0f
                )
        );
    }

    private boolean canScroll() {
        return menu.enchantmentsDataList.size() > 3;
    }

    private int getMaxScroll() {
        return Math.max(0, menu.enchantmentsDataList.size() - 3);
    }

    private boolean isMouseOverSlot(int slot, int mouseX, int mouseY){
        return mouseX >= this.leftPos + 40 && mouseX <= this.leftPos + 148 &&
                mouseY >= this.topPos + 18 + (22 * slot) && mouseY <= this.topPos + 40 + (22 * slot);
    }

    private boolean isMouseOverScrollbar(double mouseX, double mouseY) {
        int x = this.leftPos + SCROLLBAR_X;
        int y = this.topPos + SCROLLBAR_Y;

        return mouseX >= x && mouseX < x + SCROLLBAR_WIDTH
                && mouseY >= y && mouseY < y + SCROLLBAR_HEIGHT;
    }



}
