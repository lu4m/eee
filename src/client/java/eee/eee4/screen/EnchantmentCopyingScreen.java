package eee.eee4.screen;

import eee.eee4.EEE;
import eee.eee4.screenHandler.EnchantmentCopyingScreenHandler;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.EnchantingPhrases;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.EnchantmentScreenHandler;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EnchantmentCopyingScreen extends HandledScreen<EnchantmentCopyingScreenHandler> {

    private static final Identifier[] LEVEL_TEXTURES = new Identifier[]{Identifier.ofVanilla("container/enchanting_table/level_1"), Identifier.ofVanilla("container/enchanting_table/level_2"), Identifier.ofVanilla("container/enchanting_table/level_3")};
    private static final Identifier[] LEVEL_DISABLED_TEXTURES = new Identifier[]{Identifier.ofVanilla("container/enchanting_table/level_1_disabled"), Identifier.ofVanilla("container/enchanting_table/level_2_disabled"), Identifier.ofVanilla("container/enchanting_table/level_3_disabled")};
    private static final Identifier ENCHANTMENT_SLOT_DISABLED_TEXTURE = Identifier.ofVanilla("container/enchanting_table/enchantment_slot_disabled");
    private static final Identifier ENCHANTMENT_SLOT_HIGHLIGHTED_TEXTURE = Identifier.ofVanilla("container/enchanting_table/enchantment_slot_highlighted");
    private static final Identifier ENCHANTMENT_SLOT_TEXTURE = Identifier.ofVanilla("container/enchanting_table/enchantment_slot");

    private static final int LIST_X = 42;
    private static final int LIST_Y = 18;
    private static final int ENTRY_WIDTH = 108;
    private static final int ENTRY_HEIGHT = 19;

    private static final int SCROLLBAR_X = 156;
    private static final int SCROLLBAR_Y = 18;
    private static final int SCROLLBAR_HEIGHT = 57;
    private static final int SCROLL_BUTTON_HEIGHT = 15;
    private static final int SCROLL_BUTTON_WIDTH = 12;

    private int entries_amount;

    private int scrollIndex;                    // sempre entre 0 e entries_amount
    private float scrollIndexOffset;            // SCROLLBAR_HEIGHT/entries_amount
    private float fullLengthScrollDistance;     // the real distance needed to scroll from top to bottom
    private float scrollDistanceThreshold;      // fullLengthScrollDistance/entries_amount

    private float scrollDistance;

    private final int[] inView = new  int[]{-1,-1,-1};

    private static final Logger LOGGER = LogManager.getLogger();

    private static final Identifier TEXTURE =
            Identifier.of(EEE.MOD_ID,"textures/gui/enchantment_copying.png");

    public EnchantmentCopyingScreen(
            EnchantmentCopyingScreenHandler handler,
            PlayerInventory playerInventory,
            Text title
    ) {

        super(handler, playerInventory, title);
        this.backgroundWidth = 176;
        this.backgroundHeight = 174;

        this.titleX = 10;
        this.titleY = 6;

        this.playerInventoryTitleX = 8;
        this.playerInventoryTitleY = this.backgroundHeight - 96;

        this.entries_amount = handler.getTotalEntries();

        this.scrollIndex = 0;
        this.scrollIndexOffset = (float) SCROLLBAR_HEIGHT / entries_amount;

        this.fullLengthScrollDistance = 40.0f;

        this.scrollDistanceThreshold = fullLengthScrollDistance / entries_amount;

        updateInView();

    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {

        context.drawTexture(
                RenderPipelines.GUI_TEXTURED,
                TEXTURE,
                this.x, this.y, 0.0F,0.0F , this.backgroundWidth, this.backgroundHeight, 256, 256
        );

        drawEntriesList(context, mouseX, mouseY);
        drawScrollBar(context);

    }

    private void drawEntriesList(DrawContext ctx, int mouseX, int mouseY) {
        for (int i = 0 ; i<3 ; i++){
            drawEntry(i, ctx, mouseX, mouseY);
        }
    }

    private void drawEntry(int displayIndex, DrawContext context, int mouseX, int mouseY){

        int listYRelative = this.y + LIST_Y;
        int listXRelative = this.x + LIST_X;

        /*
            TODO: cool runic text"
        */

        // StringVisitable stringVisitable = EnchantingPhrases.getInstance().generatePhrase(this.textRenderer, textLength);
        // look into how to make enchantment names display nicely (font size ?)
        String placeholder = handler.getEntry(inView[displayIndex]);
        int MaxTextLength = ENTRY_WIDTH - 30;

        int mouseXRelativeToEntry = mouseX - (listXRelative);
        int mouseYRelativeToEntry = mouseY - (listYRelative + ENTRY_HEIGHT * displayIndex);


        int runicTextColor = 0xFF685E4A;
        if (mouseXRelativeToEntry >= 0 && mouseYRelativeToEntry >= 0 && mouseXRelativeToEntry < ENTRY_WIDTH && mouseYRelativeToEntry < ENTRY_HEIGHT) {
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, ENCHANTMENT_SLOT_HIGHLIGHTED_TEXTURE, listXRelative, listYRelative + ENTRY_HEIGHT * displayIndex, ENTRY_WIDTH, ENTRY_HEIGHT);
            runicTextColor = 0xFF80FF20;
        } else {
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, ENCHANTMENT_SLOT_TEXTURE, listXRelative, listYRelative + ENTRY_HEIGHT * displayIndex, ENTRY_WIDTH, ENTRY_HEIGHT);
        }

        context.drawWrappedText(this.textRenderer, Text.literal(placeholder), listXRelative + 19, listYRelative + (ENTRY_HEIGHT * displayIndex) + 4, MaxTextLength, runicTextColor, false);

        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, LEVEL_TEXTURES[displayIndex], listXRelative + 1, listYRelative + ENTRY_HEIGHT * displayIndex, 16, 16);


    }

    private void drawScrollBar(DrawContext ctx) {

        int top = this.y+SCROLLBAR_Y+Math.round(scrollIndexOffset*scrollIndex);
        ctx.fill(
                this.x+SCROLLBAR_X,
                top,
                this.x+SCROLLBAR_X+SCROLL_BUTTON_WIDTH,
                top+SCROLL_BUTTON_HEIGHT,
                0xFFFF0000   // red
        );
    }

    private void updateInView(){
        for(int i = 0 ; i < 3; i++){
            if(i + scrollIndex >= entries_amount)
                inView[i] = -1;
            else
                inView[i] = i + scrollIndex;
        }

    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        return false;
    }


}
