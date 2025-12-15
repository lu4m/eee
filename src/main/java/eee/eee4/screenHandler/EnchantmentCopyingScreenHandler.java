package eee.eee4.screenHandler;

import eee.eee4.EEE;
import eee.eee4.enchantment.EeeEnchantmentHelper;
import eee.eee4.networking.BookSlotData;
import eee.eee4.networking.s2c.BookSlotPayload;
import eee.eee4.registry.EEEBlocks;
import eee.eee4.registry.EEEScreenHandlers;
import net.minecraft.block.EnchantingTableBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChiseledBookshelfBlockEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.common.CustomPayloadS2CPacket;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.screen.*;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;


public class EnchantmentCopyingScreenHandler extends ScreenHandler {
    private final Inventory inventory;
    private final ScreenHandlerContext context;

    private List<ChiseledBookshelfBlockEntity> bookshelves;
    private int[] currentBooksXpCosts = {-1,-1,-1,-1,-1,-1};

    private final PropertyDelegate properties;
    private static final int BOOKSHELF_IN_VIEW_PROP = 0;
    private static final int ACTIVE_MASK_PROP = 1;
    private static final int PRESENT_MASK_PROP = 2;
    static final Identifier EMPTY_LAPIS_LAZULI_SLOT_TEXTURE = Identifier.ofVanilla("container/slot/lapis_lazuli");
    static final Identifier EMPTY_BOOK_SLOT_TEXTURE = Identifier.of(EEE.MOD_ID,"container/slot/book");

    private static final int BOOK_SLOT = 0;
    private static final int LAPIS_SLOT = 1;
    private static final int PLAYER_INV_START = 2;
    private static final int PLAYER_INV_END = 38;


    public EnchantmentCopyingScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, ScreenHandlerContext.EMPTY);
    }

    public EnchantmentCopyingScreenHandler(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(EEEScreenHandlers.ENCHANTMENT_COPYING_SCREEN_HANDLER, syncId);
        this.context = context;

        this.inventory = new SimpleInventory(2) {
            @Override
            public void markDirty() {
                super.markDirty();
                EnchantmentCopyingScreenHandler.this.onContentChanged(this);
            }
        };

        this.addSlot(new Slot(this.inventory, 0, 13, 22) {
            public int getMaxItemCount() {
                return 1;
            }

            public boolean canInsert(ItemStack stack) {
                return stack.isOf(Items.BOOK);
            }

            public Identifier getBackgroundSprite() {
                return EMPTY_BOOK_SLOT_TEXTURE;
            }
        });
        this.addSlot(new Slot(this.inventory, 1, 13, 45) {
            public boolean canInsert(ItemStack stack) {
                return stack.isOf(Items.LAPIS_LAZULI);
            }

            public Identifier getBackgroundSprite() {
                return EMPTY_LAPIS_LAZULI_SLOT_TEXTURE;
            }
        });

        this.addPlayerSlots(playerInventory, 8, 80);


        this.properties = new ArrayPropertyDelegate(3);
        this.addProperties(this.properties);

        scanBookshelves();

        if (!this.bookshelves.isEmpty()) {
            this.setBookshelfInViewProp(0);
        }
        else{
            this.setBookshelfInViewProp(-1);
        }
        // initial state update
        context.run((world, pos) -> {
            if (playerInventory.player instanceof ServerPlayerEntity serverPlayer){
                updateState(serverPlayer);
            }
        });

    }

    public int getBookshelfInViewProp(){
        return this.properties.get(BOOKSHELF_IN_VIEW_PROP);
    }

    private void setBookshelfInViewProp(int newIndex ){
        this.properties.set(BOOKSHELF_IN_VIEW_PROP,newIndex);
    }

    public int getActiveMaskProp(){
        return this.properties.get(ACTIVE_MASK_PROP);
    }

    public int getPresentMaskProp(){
        return this.properties.get(PRESENT_MASK_PROP);
    }

    private void scanBookshelves() {
        this.bookshelves = new ArrayList<ChiseledBookshelfBlockEntity>();

        this.context.run((world, blockpos) -> {
            for (BlockPos offset : EnchantingTableBlock.POWER_PROVIDER_OFFSETS) {
                BlockEntity be = world.getBlockEntity(blockpos.add(offset));
                if (be instanceof ChiseledBookshelfBlockEntity && EnchantmentCopyingScreenHandler.canAccessBookshelves(world, blockpos, offset)) {
                    bookshelves.add((ChiseledBookshelfBlockEntity) be);
                }
            }
        });

    }

    private void updateXpCosts(){
        for (int i = 0; i<6; i++){
            ItemStack stack = bookshelves.get(getBookshelfInViewProp()).getStack(i);
            currentBooksXpCosts[i] = -1;
            if (stack.isOf(Items.ENCHANTED_BOOK)){
               currentBooksXpCosts[i] = EeeEnchantmentHelper.xpCost(stack);
            }
        }
    }

    private void updateBookshelfEncoding(ServerPlayerEntity player) {
        int index = getBookshelfInViewProp();

        ChiseledBookshelfBlockEntity shelf = bookshelves.get(index);

        int activeMask = 0;
        int presentMask = 0;

        for (int i = 0; i < 6; i++) {
            ItemStack stack = shelf.getStack(i);

            if (!stack.isEmpty()) {
                presentMask |= (1 << i);

                if (
                        stack.isOf(Items.ENCHANTED_BOOK) &&
                                (player.experienceLevel >= currentBooksXpCosts[i] || player.getAbilities().creativeMode)
                ){
                    activeMask |= (1 << i);
                }
            }
        }

        properties.set(ACTIVE_MASK_PROP, activeMask);
        properties.set(PRESENT_MASK_PROP, presentMask);
        sendContentUpdates();

    }

    private void sendTooltipPacket(ServerPlayerEntity player) {
        List<BookSlotData> books = new ArrayList<>();

        for (int i = 0; i < 6; i++) {

            List<Text> toolTip = EeeEnchantmentHelper.buildEnchantmentCopyingTooltip(bookshelves
                    .get(getBookshelfInViewProp()).getStack(i));

            BookSlotData bd = new BookSlotData(toolTip,currentBooksXpCosts[i]);
            books.add(bd);
        }

        BookSlotPayload bookPayload = new BookSlotPayload(books);

        player.networkHandler.sendPacket(new CustomPayloadS2CPacket(bookPayload));
    }

    private void updateState(ServerPlayerEntity player){

        if (bookshelves.isEmpty()){
            return;
        }

        updateXpCosts();
        updateBookshelfEncoding(player);
        sendTooltipPacket(player);
    }

    @Override
    public boolean onButtonClick(PlayerEntity player, int id) {
        if (!(player instanceof ServerPlayerEntity serverPlayer)) return false;


        if (!bookshelves.isEmpty()) {
            // pgUp
            if (id == 7) {
                setBookshelfInViewProp(
                        Math.floorMod(getBookshelfInViewProp() + 1, bookshelves.size())
                );
            }
            // pgDown
            else if (id == 6) {
                setBookshelfInViewProp(
                        Math.floorMod(getBookshelfInViewProp() - 1, bookshelves.size())
                );
            } else {
                handleBookClick(id,player);
            }
            updateState(serverPlayer);
        }

        return super.onButtonClick(player, id);
    }

    private void handleBookClick(int index, PlayerEntity player){

        ItemStack selected = bookshelves.get(getBookshelfInViewProp()).getStack(index);

        if (!selected.isOf(Items.ENCHANTED_BOOK)) return;

        ItemStack book  = inventory.getStack(0);
        ItemStack lapis = inventory.getStack(1);

        boolean freeXp = player.getAbilities().creativeMode;
        boolean xpCondition = freeXp || player.experienceLevel >= currentBooksXpCosts[index];
        boolean bookCondition = book.isOf(Items.BOOK) && book.getCount() >= 1;
        boolean lapisCondition = lapis.isOf(Items.LAPIS_LAZULI)  && lapis.getCount() >= 1;

        if ( !(lapisCondition && bookCondition && xpCondition)) return;

        lapis.decrement(1);
        if (!freeXp){
            player.addExperience(-80);
        }

        ItemStack copy = new ItemStack(Items.ENCHANTED_BOOK);
        EnchantmentHelper.set(
                copy,EnchantmentHelper.getEnchantments(selected)
        );
        copy.setCount(1);

        inventory.setStack(0,copy);

    }

    public static boolean canAccessBookshelves(World world, BlockPos blockPos, BlockPos offset){
        return world.getBlockState(blockPos.add(offset.getX() / 2, offset.getY(), offset.getZ() / 2)).isIn(BlockTags.ENCHANTMENT_POWER_TRANSMITTER);
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return canUse(context, player, EEEBlocks.ENCHANTMENT_COPYING_TABLE);
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        this.context.run((world, pos) -> {
            this.dropInventory(player, this.inventory);
        });
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slotIndex) {
        Slot slot = this.getSlot(slotIndex);

        if (!slot.hasStack()) {
            return ItemStack.EMPTY;
        }

        ItemStack original = slot.getStack();
        ItemStack copy = original.copy();

        boolean moved = false;

        if (slotIndex == BOOK_SLOT || slotIndex == LAPIS_SLOT) {
            moved = this.insertItem(original, PLAYER_INV_START, PLAYER_INV_END, true);
        }
        else if (original.isOf(Items.LAPIS_LAZULI)) {
            moved = this.insertItem(original, LAPIS_SLOT, LAPIS_SLOT + 1, false);
        }
        else if (original.isOf(Items.BOOK)) {
            moved = this.insertItem(original, BOOK_SLOT, BOOK_SLOT + 1, false);
        }

        if (!moved) {
            return ItemStack.EMPTY;
        }

        slot.onQuickTransfer(original, copy);

        return copy;
    }



}
