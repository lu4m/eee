package eee.eee4.screen;

import eee.eee4.EEE;
import eee.eee4.EeeClient;
import eee.eee4.networking.BookSlotData;
import eee.eee4.menus.EnchantmentCopyingMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EnchantmentCopyingScreen extends AbstractContainerScreen<@NotNull EnchantmentCopyingMenu> {

    private static final Identifier BG_TEXTURE = Identifier.fromNamespaceAndPath(EEE.MOD_ID, "textures/gui/container/enchantment_copying.png");
    private static final Identifier BOOKSHELF_TEXTURE = Identifier.fromNamespaceAndPath(EEE.MOD_ID, "textures/gui/sprites/container/enchantment_copying/bookshelf_bg.png");
    private static final Identifier BOOKS_SPRITE = Identifier.fromNamespaceAndPath(EEE.MOD_ID, "textures/gui/sprites/container/enchantment_copying/books_sprite.png");
    private static final Identifier BOOKS_HIGHLIGHT = Identifier.fromNamespaceAndPath(EEE.MOD_ID,"textures/gui/sprites/container/enchantment_copying/books_highlight.png");
    private static final Identifier BOOKS_SPRITE_DISABLED = Identifier.fromNamespaceAndPath(EEE.MOD_ID,"textures/gui/sprites/container/enchantment_copying/books_sprite_disabled.png");
    private static final Identifier PGUP_TEXTURE = Identifier.fromNamespaceAndPath(EEE.MOD_ID,"textures/gui/sprites/container/enchantment_copying/pgup.png");
    private static final Identifier PGDOWN_TEXTURE = Identifier.fromNamespaceAndPath(EEE.MOD_ID,"textures/gui/sprites/container/enchantment_copying/pgdown.png");

    private static final int BOOKSHELF_X = 36;
    private static final int BOOKSHELF_Y = 18;
    private static final int BOOK_WIDTH = 16;
    private static final int BOOK_HEIGHT = 27;

    private static final int PG_X = 155;
    private static final int PGUP_Y = 25;
    private static final int PGDOWN_Y = 44;

    public EnchantmentCopyingScreen(
            EnchantmentCopyingMenu handler,
            Inventory playerInventory,
            Component title
    ) {

        super(handler, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 162;

        this.titleLabelX = 10;
        this.titleLabelY = 6;

        this.inventoryLabelX = 9;
        this.inventoryLabelY = this.imageHeight - 94;

    }

    private boolean isMouseOverPgUp( int mouseX, int mouseY){
        return mouseX >= PG_X+this.leftPos && mouseX <= PG_X+this.leftPos+14
                && mouseY >= PGUP_Y+this.topPos && mouseY <= PGUP_Y+this.topPos+14;
    }

    private boolean isMouseOverPgDown( int mouseX, int mouseY){
        return mouseX >= PG_X+this.leftPos && mouseX <= PG_X+this.leftPos+14
                && mouseY >= PGDOWN_Y+this.topPos && mouseY <= PGDOWN_Y+this.topPos+14;
    }

    private boolean isMouseOverBook( int mouseX, int mouseY,int bookIndex){

        int startingPointXTexture = bookIndex * (BOOK_WIDTH+2);
        int startingPointXScreen = this.leftPos + BOOKSHELF_X + 4 + startingPointXTexture;
        int startingPointY = this.topPos+BOOKSHELF_Y+16;

        return mouseX >= startingPointXScreen && mouseX <= startingPointXScreen + BOOK_WIDTH
                && mouseY >= startingPointY && mouseY <= startingPointY + BOOK_HEIGHT;
    }

    private boolean isSlotPresent(int id){
        int presentMask = this.menu.getPresentMaskProp();
        return (presentMask & (1 << id)) != 0;
    }

    private boolean isSlotActive(int id){
        int activeMask = this.menu.getActiveMaskProp();
        return (activeMask & (1 << id)) != 0;
    }

    private void playClickSound() {
        Minecraft.getInstance().getSoundManager().play(
                SimpleSoundInstance.forUI(
                        SoundEvents.UI_BUTTON_CLICK,
                        1.0f
                )
        );
    }


    @Override
    public boolean mouseClicked(MouseButtonEvent click, boolean doubled){
        int click_y = (int) Math.round(click.y());
        int click_x = (int) Math.round(click.x());

        for(int i = 0; i<6; i++){
            if (isMouseOverBook(click_x,click_y,i)){
                assert this.minecraft.gameMode != null;
                this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, i);
                return true;
            }
        }

        if (isMouseOverPgDown(click_x,click_y)){
            assert this.minecraft.gameMode != null;
            this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, 6);
            playClickSound();
            return true;
        }

        if (isMouseOverPgUp(click_x,click_y)){
            assert this.minecraft.gameMode != null;
            this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, 7);
            playClickSound();
            return true;
        }

        return super.mouseClicked(click, doubled);
    }

    @Override
    protected void init() {
        super.init();
    }



    @Override
    protected void renderBg(GuiGraphics context, float delta, int mouseX, int mouseY) {

        context.blit(
                RenderPipelines.GUI_TEXTURED,BG_TEXTURE,this.leftPos,this.topPos,0.0f,0.0f,this.imageWidth,this.imageHeight,256,256
        );

        if(this.menu.getBookshelfInViewProp() >= 0){
            drawBookshelf(context, mouseX, mouseY);
        }
            drawPgUp(context, mouseX, mouseY);
        drawPgDown(context, mouseX, mouseY);

    }

    private void drawBookshelf(GuiGraphics context, int mouseX, int mouseY) {

        context.blit(RenderPipelines.GUI_TEXTURED,BOOKSHELF_TEXTURE,
                this.leftPos + BOOKSHELF_X,this.topPos+BOOKSHELF_Y,
                0.0F,0.0F,114,47,114,47
            );

        Component shelfTitle = Component.translatable("gui.eee4.enchantment_copying.copy.bookshelf.title",
                this.menu.getBookshelfInViewProp() + 1);

        context.drawString(
                this.font,shelfTitle,
                this.leftPos+BOOKSHELF_X + 4,this.topPos+BOOKSHELF_Y + 4,0xFFDEDEDE,true
        );



        for(int i = 0; i < 6; i++){
            if(isSlotPresent(i)) {
                drawBook(context, mouseX, mouseY, i, isSlotActive(i));
            }
        }

    }

    private void drawPgDown(GuiGraphics context,int mouseX, int mouseY){
        context.blit(RenderPipelines.GUI_TEXTURED,PGDOWN_TEXTURE,PG_X+this.leftPos,PGDOWN_Y+this.topPos,
                0.0F,0.0F,14,14,14,14);
        if (isMouseOverPgDown(mouseX,mouseY)){
            context.renderOutline(PG_X+this.leftPos,PGDOWN_Y+this.topPos,14,14,0x80FFFFFF);
        }
    }

    private void drawPgUp(GuiGraphics context,int mouseX, int mouseY){
        context.blit(RenderPipelines.GUI_TEXTURED,PGUP_TEXTURE,PG_X+this.leftPos,PGUP_Y+this.topPos,
                0.0F,0.0F,14,14,14,14);
        if (isMouseOverPgUp(mouseX,mouseY)){
            context.renderOutline(PG_X+this.leftPos,PGUP_Y+this.topPos,14,14,0x80FFFFFF);
        }
    }


    private void drawBook(GuiGraphics context, int mouseX, int mouseY,int bookIndex,boolean active){

        int startingPointXTexture = bookIndex * (BOOK_WIDTH+2);
        int startingPointXScreen = this.leftPos + BOOKSHELF_X + 4 + startingPointXTexture;
        int startingPointY = this.topPos+BOOKSHELF_Y+16;

        Identifier texture = BOOKS_SPRITE_DISABLED;
        if (active) {
            texture = BOOKS_SPRITE;
        }

        context.blit(RenderPipelines.GUI_TEXTURED,texture,
                startingPointXScreen,
                startingPointY,
                (float) startingPointXTexture,
                0.0F,
                BOOK_WIDTH,BOOK_HEIGHT,
                106,BOOK_HEIGHT
        );

        if (isMouseOverBook(mouseX,mouseY,bookIndex)) {

            context.blit(RenderPipelines.GUI_TEXTURED,BOOKS_HIGHLIGHT,
                    startingPointXScreen - 1,
                    startingPointY,
                    (float) (startingPointXTexture),
                    0.0F,
                    BOOK_WIDTH + 2,BOOK_HEIGHT,
                    108,BOOK_HEIGHT
            );
        }
    }

    @Override
    public void render(@NotNull GuiGraphics context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);
        this.renderTooltip(context, mouseX, mouseY);
        if (this.menu.getBookshelfInViewProp() >= 0) {
            drawBookToolTips(context, mouseX, mouseY);
        }
    }

    private void drawBookToolTips(GuiGraphics context, int mouseX, int mouseY) {

        for (int i = 0; i < 6; i++) {
            if (isMouseOverBook(mouseX, mouseY, i) && isSlotPresent(i)) {

                if (i < this.menu.bookSlotData.size()) {

                    List<Component> tooltip =
                            new ArrayList<>(this.menu.bookSlotData.get(i).tooltip());


                    int xpCost = this.menu.bookSlotData.get(i).xpCost();

                    if (xpCost > 0) {
                        ChatFormatting color =
                                isSlotActive(i) ? ChatFormatting.GREEN : ChatFormatting.DARK_GRAY;

                        Component xpText = Component.literal("XP: " + xpCost)
                                .withStyle(color);

                        tooltip.add(xpText);
                    }

                    if (!tooltip.isEmpty()) {
                        context.setTooltipForNextFrame(this.font, tooltip, Optional.empty(), mouseX, mouseY);
                        return;
                    }
                }
            }
        }
    }


}
