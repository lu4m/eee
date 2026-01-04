package eee.eee4.menus;

import eee.eee4.EEE;
import eee.eee4.enchantment.EeeEnchantmentHelper;
import eee.eee4.networking.EnchantmentData;
import eee.eee4.networking.s2c.BooleanArrayPayload;
import eee.eee4.networking.s2c.EnchantedBookPayload;
import eee.eee4.registry.EEEBlocks;
import eee.eee4.registry.EEEMenus;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class EnchantmentSplittingMenu extends AbstractContainerMenu {

    private final Container bookSlot;
    private final Container enchantedBookSlot;
    private final Container outputSlot;
    private final ContainerLevelAccess context;

    private final Player player;
    private final List<Object2IntMap.Entry<Holder<Enchantment>>> enchantmentsList = new ArrayList<>();
    public boolean[] selected;
    public final List<EnchantmentData> enchantmentsDataList = new ArrayList<>();
    private final ContainerData properties;

    private static final Identifier EMPTY_BOOK_SLOT_TEXTURE = Identifier.fromNamespaceAndPath(EEE.MOD_ID,"container/slot/book");
    private static final Identifier EMPTY_ENCHANTED_BOOK_SLOT_TEXTURE = Identifier.fromNamespaceAndPath(EEE.MOD_ID,"container/slot/enchanted_book");
    private static final int TOP_DISPLAY_INDEX_PROP = 0;
    private static final int FULL_XP_COST_PROP = 1;
    private static final int X_ICON_PROP = 2;

    private ItemStack lastEnchantedBookStack;
    private ItemStack lastOutputStack;

    public EnchantmentSplittingMenu(int syncId, Inventory playerInventory) {
        this(syncId, playerInventory, ContainerLevelAccess.NULL);
    }

    public EnchantmentSplittingMenu(int syncId, Inventory playerInventory, ContainerLevelAccess context) {
        super(EEEMenus.ENCHANTMENT_SPLITTING_MENU, syncId);
        this.context = context;
        this.player = playerInventory.player;

        this.properties = new SimpleContainerData(3);
        this.addDataSlots(this.properties);

        this.enchantedBookSlot = new SimpleContainer(1) {
            @Override
            public void setChanged() {
                super.setChanged();
                EnchantmentSplittingMenu.this.slotsChanged(this);
            }
        };

        this.addSlot(new Slot(this.enchantedBookSlot, 0, 12, 30) {
            public int getMaxStackSize() {
                return 1;
            }

            public boolean mayPlace(@NotNull ItemStack stack) {
                return stack.is(Items.ENCHANTED_BOOK);
            }

            public Identifier getNoItemIcon() {
                return EMPTY_ENCHANTED_BOOK_SLOT_TEXTURE;
            }

        });

        this.bookSlot = new SimpleContainer(1) {
            @Override
            public void setChanged() {
                super.setChanged();
                EnchantmentSplittingMenu.this.slotsChanged(this);
            }
        };

        this.addSlot(new Slot(this.bookSlot, 0, 76, 96) {
            public boolean mayPlace(@NotNull ItemStack stack) {
                return stack.is(Items.BOOK);
            }

            public Identifier getNoItemIcon() {
                return EMPTY_BOOK_SLOT_TEXTURE;
            }
        });

        this.outputSlot = new SimpleContainer(1) {
            @Override
            public void setChanged() {
                super.setChanged();
                EnchantmentSplittingMenu.this.slotsChanged(this);
            }
        };

        this.addSlot(new Slot(this.outputSlot, 0, 134, 96) {
            public boolean mayPlace(@NotNull ItemStack stack) {
                return false;
            }
        });

        this.addStandardInventorySlots(playerInventory, 8, 133);

        unselectAll();

        broadcastChanges();

    }


    private void setTopDisplayIndex(int i){
        this.properties.set(TOP_DISPLAY_INDEX_PROP,i);
    }

    public int getTopDisplayIndex(){
        return this.properties.get(TOP_DISPLAY_INDEX_PROP);
    }

    private void setFullXpCost(int i){
        this.properties.set(FULL_XP_COST_PROP,i);
    }

    public int getFullXpCost(){
        return this.properties.get(FULL_XP_COST_PROP);
    }

    private void setXIcon(boolean b){
        this.properties.set(X_ICON_PROP,b ? 1 : 0);
    }

    public boolean getXIcon(){
        return this.properties.get(X_ICON_PROP) == 1;
    }

    private void flipSelectedIndex(int i){
        if (i >= 0 && i < selected.length)
            selected[i] = !selected[i];
    }

    private void unselectAll(){
        selected = new boolean[enchantmentsList.size()];
        Arrays.fill(selected, false);
    }

    private void computeEnchantments(ItemStack book){
        enchantmentsList.clear();
        if (book.is(Items.ENCHANTED_BOOK))
            enchantmentsList.addAll(EnchantmentHelper.getEnchantmentsForCrafting(book).entrySet());

    }

    private int fullXpCost(){
        int fullCost = 0;
        int i = 0;
        for (var entry : enchantmentsList) {
            if (selected[i]) {
                fullCost += EeeEnchantmentHelper.xpCostSplitting(entry.getIntValue());
            }
            i++;
        }
        return fullCost;
    }

    private boolean ShouldXIconAppear(){
        boolean eBookPresent = this.enchantedBookSlot.getSlot(0).get().is(Items.ENCHANTED_BOOK);
        boolean bookCondition = this.bookSlot.getSlot(0).get().is(Items.BOOK);
        boolean xpCondition = this.player.experienceLevel >= fullXpCost();

        return eBookPresent && bookCondition && !xpCondition && anySelected();

    }

    private boolean shouldDisplayOutput(){
        boolean eBookPresent = this.enchantedBookSlot.getSlot(0).get().is(Items.ENCHANTED_BOOK);
        boolean bookCondition = this.bookSlot.getSlot(0).get().is(Items.BOOK);
        boolean xpCondition = this.player.experienceLevel >= fullXpCost();

        return eBookPresent && bookCondition && xpCondition && anySelected();

    }

    private boolean anySelected(){
        boolean anySelected = false;
        for (boolean b : selected) {
            anySelected = anySelected || b;
        }
        return anySelected;
    }

    private boolean allSelected(){
        boolean anySelected = true;
        for (boolean b : selected) {
            anySelected = anySelected && b;
        }
        return anySelected;
    }

    private ItemStack getSelected(){
        if (!anySelected()){
            ItemStack plain = new ItemStack(Items.BOOK);
            plain.setCount(1);
            return new ItemStack(Items.BOOK);
        }
        ItemStack itemStack = new ItemStack(Items.ENCHANTED_BOOK);
        itemStack.setCount(1);
        ItemEnchantments.Mutable selectedSet = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);

        for (int i = 0 ; i < enchantmentsList.size() ; i++ ) {
            if (selected[i]) {
                selectedSet.set(enchantmentsList.get(i).getKey(), enchantmentsList.get(i).getIntValue());
            }
        }

        EnchantmentHelper.setEnchantments(itemStack,selectedSet.toImmutable());
        return itemStack;
    }

    private ItemStack getUnselected(){
        if (anySelected()){
            ItemStack plain = new ItemStack(Items.BOOK);
            plain.setCount(1);
            return new ItemStack(Items.BOOK);
        }
        ItemStack itemStack = new ItemStack(Items.ENCHANTED_BOOK);
        itemStack.setCount(1);
        ItemEnchantments.Mutable unselectedSet = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);

        for (int i = 0 ; i < enchantmentsList.size() ; i++ ) {
            if (!selected[i]) {
                unselectedSet.set(enchantmentsList.get(i).getKey(), enchantmentsList.get(i).getIntValue());
            }
        }

        EnchantmentHelper.setEnchantments(itemStack, unselectedSet.toImmutable());
        return itemStack;
    }

    private void computeEnchantmentData(){
        enchantmentsDataList.clear();

        enchantmentsList.forEach( entry -> {
            int level = entry.getIntValue();
            Component name = Enchantment.getFullname(entry.getKey(),level).copy().setStyle(Style.EMPTY);

            EnchantmentData e = new EnchantmentData(name,level);
            enchantmentsDataList.add(e);
        });
    }

    private void selectUpdate(){
        setFullXpCost(fullXpCost());
        setXIcon(ShouldXIconAppear());
        sendSelected();
        broadcastChanges();

    }
    private void enchantmentsUpdate(ItemStack enchantedBook){
        computeEnchantments(enchantedBook);
        computeEnchantmentData();
        unselectAll();
        setFullXpCost(fullXpCost());
        setXIcon(ShouldXIconAppear());
        sendSelected();
        sendEnchantments();
        broadcastChanges();
    }

    private void sendEnchantments(){
        if (player instanceof ServerPlayer serverPlayer)
            serverPlayer.connection.send(new ClientboundCustomPayloadPacket(new EnchantedBookPayload(this.containerId, enchantmentsDataList)));
    }

    public void receiveEnchantmentData(List<EnchantmentData> receivedData){
        this.enchantmentsDataList.clear();
        this.enchantmentsDataList.addAll(receivedData);
    }


    private void sendSelected(){
        if (player instanceof ServerPlayer serverPlayer)
            serverPlayer.connection.send(new ClientboundCustomPayloadPacket(new BooleanArrayPayload(this.containerId,selected)));
    }

    public void receiveSelected(boolean[] receivedSelected){
        selected = receivedSelected;
    }


    @Override
    public @NonNull ItemStack quickMoveStack(@NonNull Player player, int i) {
       return ItemStack.EMPTY;
       // TODO
    }

    @Override
    public boolean stillValid(@NonNull Player player) {
        return stillValid(context, player, EEEBlocks.ENCHANTMENT_SPLITTING_TABLE);
    }

    @Override
    public void slotsChanged(@NonNull Container container) {
        if(container == this.enchantedBookSlot){
            ItemStack book = container.getItem(0);
            enchantmentsUpdate(book);
        }
        // TODO

    }
}
