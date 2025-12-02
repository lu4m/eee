package eee.eee4.screenHandler;

import eee.eee4.registry.EEEBlocks;
import eee.eee4.registry.EEEScreenHandlers;
import net.minecraft.block.EnchantingTableBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChiseledBookshelfBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.screen.*;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;


public class EnchantmentCopyingScreenHandler extends ScreenHandler {
    private final Inventory inventory;
    private final ScreenHandlerContext context;

    private List<ChiseledBookshelfBlockEntity> bookshelves;

    private final PropertyDelegate properties;
    private static final int BOOKSHELF_IN_VIEW_PROP = 0;
    private static final int ACTIVE_MASK_PROP = 1;
    private static final int PRESENT_MASK_PROP = 2;

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
        });
        this.addSlot(new Slot(this.inventory, 1, 13, 45) {
            public boolean canInsert(ItemStack stack) {
                return stack.isOf(Items.BLACK_DYE);
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

        this.sendContentUpdates();

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

    private void updateBookshelfEncoding() {
        int index = getBookshelfInViewProp();

        if (index < 0 || index >= bookshelves.size()) {
            properties.set(ACTIVE_MASK_PROP, 0);
            properties.set(PRESENT_MASK_PROP, 0);
            return;
        }

        ChiseledBookshelfBlockEntity shelf = bookshelves.get(index);

        int activeMask = 0;
        int presentMask = 0;

        for (int i = 0; i < 6; i++) {
            ItemStack stack = shelf.getStack(i);

            if (!stack.isEmpty()) {
                presentMask |= (1 << i);


                if (stack.isOf(Items.ENCHANTED_BOOK)) {
                    activeMask |= (1 << i);
                }
            }
        }

        properties.set(ACTIVE_MASK_PROP, activeMask);
        properties.set(PRESENT_MASK_PROP, presentMask);
        sendContentUpdates();
    }

    public int getBookshelfInViewProp(){
        return this.properties.get(BOOKSHELF_IN_VIEW_PROP);
    }

    private void setBookshelfInViewProp(int newIndex){
        this.properties.set(BOOKSHELF_IN_VIEW_PROP,newIndex);
        updateBookshelfEncoding();
    }

    public int getActiveMaskProp(){
        return this.properties.get(ACTIVE_MASK_PROP);
    }

    public int getPresentMaskProp(){
        return this.properties.get(PRESENT_MASK_PROP);
    }

    @Override
    public boolean onButtonClick(PlayerEntity player, int id) {

        //pg up
        if (id == 7) {
            int n = bookshelves.size();
            int next = Math.floorMod(getBookshelfInViewProp() + 1, bookshelves.size());
            setBookshelfInViewProp(next);
            sendContentUpdates();
            return true;
        }
        //pg down
        else if (id == 6){
            int n = bookshelves.size();
            int next = Math.floorMod(getBookshelfInViewProp() - 1, bookshelves.size());
            setBookshelfInViewProp(next);
            sendContentUpdates();
            return true;
        }
        //book
        else if (id > 0 && id < 6){

            // TODO

        }
        else{
            throw new IllegalArgumentException("an invalid button id was called");
        }

        return super.onButtonClick(player, id);
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

        // TODO

        return ItemStack.EMPTY;
    }

}
