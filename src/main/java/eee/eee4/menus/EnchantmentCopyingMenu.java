package eee.eee4.menus;

import eee.eee4.EEE;
import eee.eee4.blockEntitie.EnchantmentCopyingTableEntity;
import eee.eee4.enchantment.EeeEnchantmentHelper;
import eee.eee4.networking.BookSlotData;
import eee.eee4.networking.s2c.BookSlotPayload;
import eee.eee4.registry.EEEBlocks;
import eee.eee4.registry.EEEMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.Container;
import net.minecraft.world.level.block.EnchantingTableBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChiseledBookShelfBlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;


public class EnchantmentCopyingMenu extends AbstractContainerMenu {
    private final Container inventory;
    private final ContainerLevelAccess context;

    private List<ChiseledBookShelfBlockEntity> bookshelves;
    private final int[] currentBooksXpCosts = {-1,-1,-1,-1,-1,-1};

    private final ContainerData properties;
    private static final int BOOKSHELF_IN_VIEW_PROP = 0;
    private static final int ACTIVE_MASK_PROP = 1;
    private static final int PRESENT_MASK_PROP = 2;
    static final Identifier EMPTY_LAPIS_LAZULI_SLOT_TEXTURE = Identifier.withDefaultNamespace("container/slot/lapis_lazuli");
    static final Identifier EMPTY_BOOK_SLOT_TEXTURE = Identifier.fromNamespaceAndPath(EEE.MOD_ID,"container/slot/book");

    private static final int BOOK_SLOT = 0;
    private static final int LAPIS_SLOT = 1;
    private static final int PLAYER_INV_START = 2;
    private static final int PLAYER_INV_END = 38;

    private boolean lapisCondition;
    private boolean bookCondition;

    private int playerXpLevel;

    public EnchantmentCopyingMenu(int syncId, Inventory playerInventory) {
        this(syncId, playerInventory, ContainerLevelAccess.NULL);
    }

    public EnchantmentCopyingMenu(int syncId, Inventory playerInventory, ContainerLevelAccess context) {
        super(EEEMenus.ENCHANTMENT_COPYING_MENU, syncId);
        this.context = context;

        this.inventory = new SimpleContainer(2) {
            @Override
            public void setChanged() {
                super.setChanged();
                EnchantmentCopyingMenu.this.slotsChanged(this);
            }
        };

        this.addSlot(new Slot(this.inventory, 0, 10, 22) {
            public int getMaxStackSize() {
                return 1;
            }

            public boolean mayPlace(@NotNull ItemStack stack) {
                return stack.is(Items.BOOK);
            }

            public Identifier getNoItemIcon() {
                return EMPTY_BOOK_SLOT_TEXTURE;
            }
        });
        this.addSlot(new Slot(this.inventory, 1, 10, 45) {
            public boolean mayPlace(@NotNull ItemStack stack) {
                return stack.is(Items.LAPIS_LAZULI);
            }

            public Identifier getNoItemIcon() {
                return EMPTY_LAPIS_LAZULI_SLOT_TEXTURE;
            }
        });

        this.addStandardInventorySlots(playerInventory, 8, 80);

        this.properties = new SimpleContainerData(3);
        this.addDataSlots(this.properties);

        scanBookshelves();

        context.execute((level, pos) -> {
            int prevBookshelf = -1;;
            if (level.getBlockEntity(pos) instanceof EnchantmentCopyingTableEntity tableEntity)
                prevBookshelf = tableEntity.getLastPage();

            if (prevBookshelf >= 0 && prevBookshelf<bookshelves.size())
                setBookshelfInViewProp(prevBookshelf);
            else if (bookshelves.isEmpty())
                setBookshelfInViewProp(-1);
            else
                setBookshelfInViewProp(0);

            if (playerInventory.player instanceof ServerPlayer player) {
                computeState(player);
                sendToolTips(player);
            }
        });
        broadcastChanges();

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
        this.bookshelves = new ArrayList<>();

        this.context.execute((level, blockpos) -> {
            for (BlockPos offset : EnchantingTableBlock.BOOKSHELF_OFFSETS) {
                BlockEntity be = level.getBlockEntity(blockpos.offset(offset));

                if (be instanceof ChiseledBookShelfBlockEntity && EnchantmentCopyingTableEntity.canAccessBookshelves(level, blockpos, offset)) {
                    bookshelves.add((ChiseledBookShelfBlockEntity) be);
                }
            }
        });

    }

    private void updateXpCosts(){
        for (int i = 0; i<6; i++){
            ItemStack stack = bookshelves.get(getBookshelfInViewProp()).getItem(i);
            currentBooksXpCosts[i] = -1;
            if (stack.is(Items.ENCHANTED_BOOK)){
               currentBooksXpCosts[i] = EeeEnchantmentHelper.xpCost(stack);
            }
        }
    }

    private void updateSlotConditions(){
        ItemStack book  = inventory.getItem(BOOK_SLOT);
        ItemStack lapis = inventory.getItem(LAPIS_SLOT);

        bookCondition = book.is(Items.BOOK) && book.getCount() >= 1;
        lapisCondition = lapis.is(Items.LAPIS_LAZULI)  && lapis.getCount() >= 1;
    }


    private void updateBookshelfEncoding() {
        int index = getBookshelfInViewProp();

        ChiseledBookShelfBlockEntity shelf = bookshelves.get(index);

        int activeMask = 0;
        int presentMask = 0;

        for (int i = 0; i < 6; i++) {
            ItemStack stack = shelf.getItem(i);

            if (!stack.isEmpty()) {
                presentMask |= (1 << i);

                if (bookCondition  && lapisCondition && playerXpLevel >= currentBooksXpCosts[i]){
                    activeMask |= (1 << i);
                }
            }
        }

        properties.set(ACTIVE_MASK_PROP, activeMask);
        properties.set(PRESENT_MASK_PROP, presentMask);

    }

    private void computeStatePlayerless(){
        if (bookshelves.isEmpty()) return;

        updateXpCosts();
        updateSlotConditions();

        updateBookshelfEncoding();
    }

    private void computeState(ServerPlayer player) {
        playerXpLevel = player.experienceLevel;
        computeStatePlayerless();

    }

    private void sendToolTips(ServerPlayer player) {
        List<BookSlotData> books = new ArrayList<>();

        if (bookshelves.isEmpty()) return;

        for (int i = 0; i < 6; i++) {
            books.add(new BookSlotData(
                    EeeEnchantmentHelper.buildEnchantmentCopyingTooltip(
                            bookshelves.get(getBookshelfInViewProp()).getItem(i)
                    ),
                    currentBooksXpCosts[i]
            ));
        }

        player.connection.send(
                new ClientboundCustomPayloadPacket(new BookSlotPayload(books))
        );
    }

    @Override
    public void sendAllDataToRemote() {
        super.sendAllDataToRemote();
    }

    @Override
    public void slotsChanged(@NonNull Container container) {
        computeStatePlayerless();
        broadcastChanges();
    }

    @Override
    public boolean clickMenuButton(@NotNull Player player, int id) {
        if (!(player instanceof ServerPlayer serverPlayer)) return false;

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

            computeState(serverPlayer);
            sendToolTips(serverPlayer);
            broadcastChanges();

            return true;
        }

        return false;
    }

    private void handleBookClick(int index, Player player){

        ItemStack selected = bookshelves.get(getBookshelfInViewProp()).getItem(index);

        if (!selected.is(Items.ENCHANTED_BOOK)) return;

        ItemStack book  = inventory.getItem(BOOK_SLOT);
        ItemStack lapis = inventory.getItem(LAPIS_SLOT);

        boolean freeXp = player.hasInfiniteMaterials();
        boolean xpCondition = freeXp || player.experienceLevel >= currentBooksXpCosts[index];
        boolean bookCondition = book.is(Items.BOOK) && book.getCount() >= 1;
        boolean lapisCondition = lapis.is(Items.LAPIS_LAZULI)  && lapis.getCount() >= 1;

        if ( !(lapisCondition && bookCondition && xpCondition)) return;

        lapis.shrink(1);
        if (!freeXp){
            player.giveExperiencePoints(-EeeEnchantmentHelper.xpPointsDecrease(selected));
        }

        ItemStack copy = new ItemStack(Items.ENCHANTED_BOOK);
        EnchantmentHelper.setEnchantments(
                copy,EnchantmentHelper.getEnchantmentsForCrafting(selected)
        );
        copy.setCount(1);
        copy.set(DataComponents.REPAIR_COST,selected.get(DataComponents.REPAIR_COST));

        inventory.setItem(BOOK_SLOT,copy);

        context.execute(
                (level, blockPos) ->
                level.playSound( null, blockPos, SoundEvents.ENCHANTMENT_TABLE_USE,
                        SoundSource.BLOCKS, 1.0F, level.random.nextFloat() * 0.1F + 0.9F)
        );

    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return stillValid(context, player, EEEBlocks.ENCHANTMENT_COPYING_TABLE);
    }

    @Override
    public void removed(@NotNull Player player) {
        super.removed(player);

        this.context.execute((level, pos) -> {
            if (level.getBlockEntity(pos) instanceof EnchantmentCopyingTableEntity tableEntity) {
                tableEntity.setLastPage(getBookshelfInViewProp());
            }

            this.clearContainer(player, this.inventory);
        });
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int slotIndex) {
        Slot slot = this.getSlot(slotIndex);

        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack original = slot.getItem();
        ItemStack copy = original.copy();

        boolean moved = false;

        if (slotIndex == BOOK_SLOT || slotIndex == LAPIS_SLOT) {
            moved = this.moveItemStackTo(original, PLAYER_INV_START, PLAYER_INV_END, true);
        }
        else if (original.is(Items.LAPIS_LAZULI)) {
            moved = this.moveItemStackTo(original, LAPIS_SLOT, LAPIS_SLOT + 1, false);
        }
        else if (original.is(Items.BOOK)) {
            moved = this.moveItemStackTo(original, BOOK_SLOT, BOOK_SLOT + 1, false);
        }

        if (!moved) {
            return ItemStack.EMPTY;
        }

        slot.setChanged();
        return copy;
    }

}
