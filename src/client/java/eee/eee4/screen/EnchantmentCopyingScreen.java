package eee.eee4.screen;

import eee.eee4.EEE;
import eee.eee4.networking.BookSlotData;
import eee.eee4.screenHandler.EnchantmentCopyingScreenHandler;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class EnchantmentCopyingScreen extends HandledScreen<EnchantmentCopyingScreenHandler> {

    private static final Identifier BG_TEXTURE = Identifier.of(EEE.MOD_ID, "textures/gui/container/enchantment_copying.png");
    private static final Identifier BOOKSHELF_TEXTURE = Identifier.of(EEE.MOD_ID, "textures/gui/sprites/container/enchantment_copying/bookshelf_bg.png");
    private static final Identifier BOOKS_SPRITE = Identifier.of(EEE.MOD_ID, "textures/gui/sprites/container/enchantment_copying/books_sprite.png");
    private static final Identifier BOOKS_HIGHLIGHT = Identifier.of(EEE.MOD_ID,"textures/gui/sprites/container/enchantment_copying/books_highlight.png");
    private static final Identifier BOOKS_SPRITE_DISABLED = Identifier.of(EEE.MOD_ID,"textures/gui/sprites/container/enchantment_copying/books_sprite_disabled.png");
    private static final Identifier PGUP_TEXTURE = Identifier.of(EEE.MOD_ID,"textures/gui/sprites/container/enchantment_copying/pgup.png");
    private static final Identifier PGDOWN_TEXTURE = Identifier.of(EEE.MOD_ID,"textures/gui/sprites/container/enchantment_copying/pgdown.png");

    private static final int BOOKSHELF_X = 36;
    private static final int BOOKSHELF_Y = 18;
    private static final int BOOK_WIDTH = 16;
    private static final int BOOK_HEIGHT = 27;

    private static final int PG_X = 155;
    private static final int PGUP_Y = 25;
    private static final int PGDOWN_Y = 44;


    public static final List<BookSlotData> CLIENT_BOOKS_SLOTS_DATA = new ArrayList<>();

    public EnchantmentCopyingScreen(
            EnchantmentCopyingScreenHandler handler,
            PlayerInventory playerInventory,
            Text title
    ) {

        super(handler, playerInventory, title);
        this.backgroundWidth = 176;
        this.backgroundHeight = 162;

        this.titleX = 10;
        this.titleY = 6;

        this.playerInventoryTitleX = 9;
        this.playerInventoryTitleY = this.backgroundHeight - 94;

    }

    private boolean isMouseOverPgUp( int mouseX, int mouseY){
        return mouseX >= PG_X+this.x && mouseX <= PG_X+this.x+14
                && mouseY >= PGUP_Y+this.y && mouseY <= PGUP_Y+this.y+14;
    }

    private boolean isMouseOverPgDown( int mouseX, int mouseY){
        return mouseX >= PG_X+this.x && mouseX <= PG_X+this.x+14
                && mouseY >= PGDOWN_Y+this.y && mouseY <= PGDOWN_Y+this.y+14;
    }

    private boolean isMouseOverBook( int mouseX, int mouseY,int bookIndex){

        int startingPointXTexture = bookIndex * (BOOK_WIDTH+2);
        int startingPointXScreen = this.x + BOOKSHELF_X + 4 + startingPointXTexture;
        int startingPointY = this.y+BOOKSHELF_Y+16;

        return mouseX >= startingPointXScreen && mouseX <= startingPointXScreen + BOOK_WIDTH
                && mouseY >= startingPointY && mouseY <= startingPointY + BOOK_HEIGHT;
    }

    private boolean isSlotPresent(int id){
        int presentMask = this.handler.getPresentMaskProp();
        return (presentMask & (1 << id)) != 0;
    }

    private boolean isSlotActive(int id){
        int activeMask = this.handler.getActiveMaskProp();
        return (activeMask & (1 << id)) != 0;
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled){
        int click_y = (int) Math.round(click.y());
        int click_x = (int) Math.round(click.x());

        for (int i = 0; i<6; i++){
            if (isMouseOverBook(click_x,click_y,i)){
                this.client.interactionManager.clickButton(this.handler.syncId, i);
            }
        }

        if (isMouseOverPgDown(click_x,click_y)){
            this.client.interactionManager.clickButton(this.handler.syncId, 6);
        }

        if (isMouseOverPgUp(click_x,click_y)){
            this.client.interactionManager.clickButton(this.handler.syncId, 7);
        }

        return super.mouseClicked(click, doubled);
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {

        context.drawTexture(
                RenderPipelines.GUI_TEXTURED,
                BG_TEXTURE,
                this.x, this.y, 0.0F,0.0F , this.backgroundWidth, this.backgroundHeight, 256, 256
        );

        if(this.handler.getBookshelfInViewProp() >= 0){
            drawBookshelf(context, mouseX, mouseY);
        }
        drawPgUp(context, mouseX, mouseY);
        drawPgDown(context, mouseX, mouseY);

    }

    private void drawBookshelf(DrawContext context, int mouseX, int mouseY) {

        context.drawTexture(RenderPipelines.GUI_TEXTURED,BOOKSHELF_TEXTURE,
                this.x + BOOKSHELF_X,this.y+BOOKSHELF_Y,
                0.0F,0.0F,114,47,114,47
            );

        context.drawText(this.textRenderer,"Bookshelf"+" "+( this.handler.getBookshelfInViewProp() +1),
                this.x+BOOKSHELF_X + 4,this.y+BOOKSHELF_Y + 4,0xFFDEDEDE,true
        );

        for(int i = 0; i < 6; i++){
            if(isSlotPresent(i)) {
                drawBook(context, mouseX, mouseY, i, isSlotActive(i));
            }
        }

    }

    private void drawPgDown(DrawContext context,int mouseX, int mouseY){
        context.drawTexture(RenderPipelines.GUI_TEXTURED,PGDOWN_TEXTURE,PG_X+this.x,PGDOWN_Y+this.y,
                0.0F,0.0F,14,14,14,14);
        if (isMouseOverPgDown(mouseX,mouseY)){
            context.drawStrokedRectangle(PG_X+this.x,PGDOWN_Y+this.y,14,14,0x80FFFFFF);
        }
    }

    private void drawPgUp(DrawContext context,int mouseX, int mouseY){
        context.drawTexture(RenderPipelines.GUI_TEXTURED,PGUP_TEXTURE,PG_X+this.x,PGUP_Y+this.y,
                0.0F,0.0F,14,14,14,14);
        if (isMouseOverPgUp(mouseX,mouseY)){
            context.drawStrokedRectangle(PG_X+this.x,PGUP_Y+this.y,14,14,0x80FFFFFF);
        }
    }


    private void drawBook(DrawContext context, int mouseX, int mouseY,int bookIndex,boolean active){

        int startingPointXTexture = bookIndex * (BOOK_WIDTH+2);
        int startingPointXScreen = this.x + BOOKSHELF_X + 4 + startingPointXTexture;
        int startingPointY = this.y+BOOKSHELF_Y+16;

        Identifier texture = BOOKS_SPRITE_DISABLED;
        if (active) {
            texture = BOOKS_SPRITE;
        }

        context.drawTexture(RenderPipelines.GUI_TEXTURED,texture,
                startingPointXScreen,
                startingPointY,
                (float) startingPointXTexture,
                0.0F,
                BOOK_WIDTH,BOOK_HEIGHT,
                106,BOOK_HEIGHT
        );

        if (isMouseOverBook(mouseX,mouseY,bookIndex)) {

            context.drawTexture(RenderPipelines.GUI_TEXTURED,BOOKS_HIGHLIGHT,
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
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);
        this.drawMouseoverTooltip(context, mouseX, mouseY);
        if (this.handler.getBookshelfInViewProp() >= 0) {
            drawBookToolTips(context, mouseX, mouseY);
        }
    }

    private void drawBookToolTips(DrawContext context, int mouseX, int mouseY) {

        for (int i = 0; i < 6; i++) {

            if (isMouseOverBook(mouseX, mouseY, i) && isSlotPresent(i)) {

                if (i < CLIENT_BOOKS_SLOTS_DATA.size()) {
                    // localCopy
                    List<Text> tooltip = new ArrayList<>(CLIENT_BOOKS_SLOTS_DATA.get(i).tooltip());

                    int xpCost = CLIENT_BOOKS_SLOTS_DATA.get(i).xpCost();

                    if (xpCost > 0) {
                        Formatting color = isSlotActive(i) ? Formatting.GREEN : Formatting.DARK_GRAY;
                        Text xpText = Text.literal(
                                        "XP " + ": " + CLIENT_BOOKS_SLOTS_DATA.get(i).xpCost()
                                )
                                .formatted(color);

                        tooltip.add(xpText);
                    }
                    if (!tooltip.isEmpty()) {
                        context.drawTooltip(this.textRenderer, tooltip, mouseX, mouseY);
                        return;
                    }
                }

            }
        }
    }

}
