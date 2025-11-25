package eee.eee4.screen;

import eee.eee4.EEE;
import eee.eee4.screenHandler.EnchantmentCopyingScreenHandler;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EnchantmentCopyingScreen extends HandledScreen<EnchantmentCopyingScreenHandler> {

    //private static final Identifier[] LEVEL_TEXTURES = new Identifier[]{Identifier.ofVanilla("container/enchanting_table/level_1"), Identifier.ofVanilla("container/enchanting_table/level_2"), Identifier.ofVanilla("container/enchanting_table/level_3")};
    //private static final Identifier[] LEVEL_DISABLED_TEXTURES = new Identifier[]{Identifier.ofVanilla("container/enchanting_table/level_1_disabled"), Identifier.ofVanilla("container/enchanting_table/level_2_disabled"), Identifier.ofVanilla("container/enchanting_table/level_3_disabled")};

    private static final Identifier BG_TEXTURE = Identifier.of(EEE.MOD_ID, "textures/gui/container/enchantment_copying.png");
    private static final Identifier BOOKSHELF_TEXTURE = Identifier.of(EEE.MOD_ID, "textures/gui/sprites/container/enchantment_copying/bookshelf_bg.png");
    private static final Identifier BOOKS_SPRITE = Identifier.of(EEE.MOD_ID, "textures/gui/sprites/container/enchantment_copying/books_sprite.png");
    private static final Identifier BOOKS_HIGHLIGHT = Identifier.of(EEE.MOD_ID,"textures/gui/sprites/container/enchantment_copying/books_highlight.png");
    private static final Identifier BOOKS_SPRITE_DISABLED = Identifier.of(EEE.MOD_ID,"textures/gui/sprites/container/enchantment_copying/books_sprite_disabled.png");

    private static final int BOOKSHELF_X = 36;
    private static final int BOOKSHELF_Y = 18;
    private static final int BOOK_WIDTH = 16;
    private static final int BOOK_HEIGHT = 27;

    private int bookshelfInView = -1;
    private int bookshelfAmount = -1;

    //this logic will change further down the line
    private boolean[] temporaryEncoding =  new boolean[]{true,true,true,true,true,true};

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

        // for testing

        this.bookshelfInView = 0;
        this.bookshelfAmount = 3;

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

        drawBookshelf(context, mouseX, mouseY);

    }

    private void drawBookshelf(DrawContext context, int mouseX, int mouseY) {

        if (bookshelfInView < 0) {
            return;
        }

        context.drawTexture(RenderPipelines.GUI_TEXTURED,BOOKSHELF_TEXTURE,
                this.x + BOOKSHELF_X,this.y+BOOKSHELF_Y,
                0.0F,0.0F,114,47,114,47
            );

        context.drawText(this.textRenderer,"Bookshelf "+(bookshelfInView+1),
                this.x+BOOKSHELF_X + 4,this.y+BOOKSHELF_Y + 4,0xFFDEDEDE,true
        );

        for(int i = 0; i < 6; i++){
            if(temporaryEncoding[i]){
                if(i%2==0){
                    drawBook(context,mouseX,mouseY,i,false);
                }
                else{
                    drawBook(context,mouseX,mouseY,i,true);
                }
            }
        }

    }

    private boolean isMouseOverBook( int mouseX, int mouseY,int bookIndex){

        int startingPointXTexture = bookIndex * (BOOK_WIDTH+2);
        int startingPointXScreen = this.x + BOOKSHELF_X + 4 + startingPointXTexture;
        int startingPointY = this.y+BOOKSHELF_Y+16;

        return mouseX >= startingPointXScreen && mouseX <= startingPointXScreen + BOOK_WIDTH
                && mouseY >= startingPointY && mouseY <= startingPointY + BOOK_HEIGHT;
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


}
