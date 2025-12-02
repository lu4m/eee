package eee.eee4.screenHandler;

import eee.eee4.EEE;
import eee.eee4.networking.s2c.BookTooltipPayload;
import eee.eee4.registry.EEEBlocks;
import eee.eee4.registry.EEEScreenHandlers;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.EnchantingTableBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChiseledBookshelfBlockEntity;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.c2s.common.CustomPayloadC2SPacket;
import net.minecraft.network.packet.s2c.common.CustomPayloadS2CPacket;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.screen.*;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.StyleSpriteSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

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

        // initial state update
        context.run((world, pos) -> {
            if (playerInventory.player instanceof ServerPlayerEntity serverPlayer){
                updateBookshelfEncoding();
                sendTooltipPacket(serverPlayer);
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

    private void sendTooltipPacket(ServerPlayerEntity player) {
        List<List<Text>> all = new ArrayList<>();

        for (int i = 0; i < 6; i++) {
            all.add(buildTooltipFromServer(i));
        }

        BookTooltipPayload payload = new BookTooltipPayload(all);

        player.networkHandler.sendPacket(new CustomPayloadS2CPacket(payload));
    }

    private List<Text> buildTooltipFromServer(int slot) {
        List<Text> tooltip = new ArrayList<>();

        ItemStack stack = bookshelves.get(getBookshelfInViewProp()).getStack(slot);

        if (stack.isEmpty()) {
            return tooltip;
        }

        if (stack.isOf(Items.ENCHANTED_BOOK)) {
            tooltip.add(stack.getName().copy().formatted(Formatting.AQUA));
            var enchants = EnchantmentHelper.getEnchantments(stack);

            if (enchants.isEmpty()) {
                tooltip.add(Text.literal("No Enchantments").formatted(Formatting.DARK_GRAY));
            } else {
                for (var entry : enchants.getEnchantmentEntries()) {
                    int level = entry.getIntValue();

                    Text line = Enchantment.getName(entry.getKey(),level).copy().formatted(Formatting.GRAY);

                    tooltip.add(line);
                }
            }
        }
        else{
            tooltip.add(stack.getName().copy().formatted(Formatting.WHITE));
        }
        return tooltip;
    }

    @Override
    public boolean onButtonClick(PlayerEntity player, int id) {

        //pg up
        if (id == 7) {
            int n = bookshelves.size();
            int next = Math.floorMod(getBookshelfInViewProp() + 1, bookshelves.size());
            setBookshelfInViewProp(next);
            updateBookshelfEncoding();
            sendTooltipPacket((ServerPlayerEntity) player);
            return true;
        }
        //pg down
        else if (id == 6){
            int n = bookshelves.size();
            int next = Math.floorMod(getBookshelfInViewProp() - 1, bookshelves.size());
            setBookshelfInViewProp(next);
            updateBookshelfEncoding();
            sendTooltipPacket((ServerPlayerEntity) player);
            return true;
        }
        //book
        else if (id >= 0 && id < 6){

            handleBookClick(id);
        }
        else{
            throw new IllegalArgumentException("an invalid button id was called");
        }

        return super.onButtonClick(player, id);
    }

    private void handleBookClick(int index){



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
