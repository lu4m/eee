package eee.eee4.menus;

import eee.eee4.EEE;
import eee.eee4.blockEntitie.EnchantmentSplittingTableEntity;
import eee.eee4.enchantment.EeeEnchantmentHelper;
import eee.eee4.networking.EnchantmentData;
import eee.eee4.networking.s2c.BooleanArrayPayload;
import eee.eee4.networking.s2c.EnchantedBookPayload;
import eee.eee4.registry.EEEBlocks;
import eee.eee4.registry.EEEMenus;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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
    private static final int FULL_XP_COST_PROP = 0;
    private static final int XP_MESSAGE_STATE_PROP = 1;
    private static final int X_ICON_PROP = 2;

    public EnchantmentSplittingMenu(int syncId, Inventory playerInventory) {
        this(syncId, playerInventory, ContainerLevelAccess.NULL);
    }

    public EnchantmentSplittingMenu(int syncId, Inventory playerInventory, ContainerLevelAccess context) {
        super(EEEMenus.ENCHANTMENT_SPLITTING_MENU, syncId);
        this.context = context;
        this.player = playerInventory.player;

        this.properties = new SimpleContainerData(3);
        this.addDataSlots(this.properties);

        setXpMessageState(-1);

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

            @Override
            public int getMaxStackSize() {
                return 1;
            }
        });

        this.outputSlot = new ResultContainer() {
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

            @Override
            public boolean mayPickup(@NonNull Player player) {
                return EnchantmentSplittingMenu.this.splittingAllowed();
            }

            @Override
            public void onTake(@NonNull Player player, @NonNull ItemStack result) {
                EnchantmentSplittingMenu.this.onResultTaken(player, result);
                super.onTake(player, result);
            }

            @Override
            public int getMaxStackSize() {
                return 1;
            }
        });

        this.addStandardInventorySlots(playerInventory, 8, 133);

        unselectAll();

        broadcastChanges();

    }

    private void setFullXpCost(int i){
        this.properties.set(FULL_XP_COST_PROP,i);
    }

    public int getFullXpCost(){
        return this.properties.get(FULL_XP_COST_PROP);
    }

    // -1 = no message ; 0 = not enough ; 1 = enough
    private void setXpMessageState(int i){
        this.properties.set(XP_MESSAGE_STATE_PROP,i);
    }

    // -1 = no message ; 0 = not enough ; 1 = enough
    public int getMessageState(){
        return this.properties.get(XP_MESSAGE_STATE_PROP);
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

    @Override
    public boolean stillValid(@NonNull Player player) {
        return stillValid(context, player, EEEBlocks.ENCHANTMENT_SPLITTING_TABLE);
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

    private int selectedXpRepairCost(){
        int fullCost = 0;
        int i = 0;
        for (var entry : enchantmentsList) {
            if (selected[i]) {
                fullCost += entry.getIntValue();
            }
            i++;
        }
        return fullCost;
    }

    private int unselectedXpRepairCost(){
        int fullCost = 0;
        int i = 0;
        for (var entry : enchantmentsList) {
            if (!selected[i]) {
                fullCost += entry.getIntValue();
            }
            i++;
        }
        return fullCost;
    }

    private boolean shouldXIconAppear(){
        boolean eBookPresent = this.enchantedBookSlot.getSlot(0).get().is(Items.ENCHANTED_BOOK);
        boolean bookPresent = this.bookSlot.getSlot(0).get().is(Items.BOOK);
        return (eBookPresent && anySelected() && !bookPresent) ;

    }

    private boolean shouldDisplayOutput(){
        boolean eBookPresent = this.enchantedBookSlot.getSlot(0).get().is(Items.ENCHANTED_BOOK);
        boolean bookPresent = this.bookSlot.getSlot(0).get().is(Items.BOOK);

        return eBookPresent && bookPresent && anySelected();

    }

    private boolean splittingAllowed(){
        boolean xpCondition = player.experienceLevel >= getFullXpCost() || player.hasInfiniteMaterials();
        return shouldDisplayOutput() && xpCondition;
    }

    private int currentXpMessageState(){

        if (splittingAllowed()){
            return 1;   //
        } else if (shouldDisplayOutput()) {
            return 0;
        }else{
            return -1;
        }
    }

    private boolean anySelected(){
        boolean anySelected = false;
        for (boolean b : selected) {
            anySelected = anySelected || b;
        }
        return anySelected;
    }

    private boolean allSelected(){
        boolean allSelected = true;
        for (boolean b : selected) {
            allSelected = allSelected && b;
        }
        return allSelected;
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
        itemStack.set(DataComponents.REPAIR_COST,selectedXpRepairCost());
        return itemStack;
    }

    private ItemStack getUnselected(){
        if (allSelected()){
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
        itemStack.set(DataComponents.REPAIR_COST,unselectedXpRepairCost());
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
        setXIcon(shouldXIconAppear());
        setXpMessageState(currentXpMessageState());
        if (shouldDisplayOutput()){
            outputSlot.setItem(0,getSelected());
        }else if(!outputSlot.isEmpty()){
            outputSlot.setItem(0,ItemStack.EMPTY);
        }
        sendSelected();

    }
    private void enchantmentsUpdate(ItemStack enchantedBook){
        computeEnchantments(enchantedBook);
        computeEnchantmentData();
        unselectAll();
        selectUpdate();
        sendEnchantments();
    }

    private void sendEnchantments(){
        if (player instanceof ServerPlayer serverPlayer)
            serverPlayer.connection.send(new ClientboundCustomPayloadPacket(new EnchantedBookPayload(this.containerId,List.copyOf(enchantmentsDataList))));
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
    public void removed(@NonNull Player player) {
        super.removed(player);
        this.context.execute((level,pos) ->
        {
              if (level.getBlockEntity(pos) instanceof EnchantmentSplittingTableEntity entity){
                  this.clearContainer(player,bookSlot);
                  this.clearContainer(player,enchantedBookSlot);
              }
        });
    }

    @Override
    public @NonNull ItemStack quickMoveStack(@NonNull Player player, int slotIndex) {
        ItemStack empty = ItemStack.EMPTY;
        Slot slot = this.getSlot(slotIndex);

        if (!slot.hasItem()) {
            return empty;
        }

        ItemStack stackInSlot = slot.getItem();
        ItemStack copy = stackInSlot.copy();


        if (slotIndex == 2) {

            if (!this.moveItemStackTo(stackInSlot, 3, this.slots.size(), true)) {
                return ItemStack.EMPTY;
            }

            slot.onQuickCraft(stackInSlot, copy);
        }

        else if (slotIndex >= 3) {

            if (stackInSlot.is(Items.ENCHANTED_BOOK)) {
                ItemStack single = stackInSlot.split(1);

                if (!this.moveItemStackTo(single, 0, 1, false)) {
                    stackInSlot.grow(1); // rollback if failed
                    return ItemStack.EMPTY;
                }

                slot.setChanged();
                return single;
            }


            else if (stackInSlot.is(Items.BOOK)) {
                ItemStack single = stackInSlot.split(1);

                if (!this.moveItemStackTo(single, 1, 2, false)) {
                    stackInSlot.grow(1);
                    return ItemStack.EMPTY;
                }

                slot.setChanged();
                return single;
            }

            else {
                int invStart = 3;
                int invEnd = 3 + 27;
                int hotbarStart = invEnd;
                int hotbarEnd = this.slots.size();

                if (slotIndex < hotbarStart) {
                    if (!this.moveItemStackTo(stackInSlot, hotbarStart, hotbarEnd, false)) {
                        return ItemStack.EMPTY;
                    }
                } else {
                    if (!this.moveItemStackTo(stackInSlot, invStart, hotbarStart, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            }
        }

        else {
            if (!this.moveItemStackTo(stackInSlot, 3, this.slots.size(), false)) {
                return ItemStack.EMPTY;
            }
        }

        if (stackInSlot.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }

        if (stackInSlot.getCount() == copy.getCount()) {
            return ItemStack.EMPTY;
        }

        slot.onTake(player, stackInSlot);
        return copy;
    }

    @Override
    public void slotsChanged(@NonNull Container container) {
        if(container == this.enchantedBookSlot){
            ItemStack book = container.getItem(0);
            enchantmentsUpdate(book);
        }
        if (container == this.bookSlot){
            selectUpdate();
        }

    }

    @Override
    public boolean clickMenuButton(@NonNull Player player, int i) {
        if (i >= 0 && i < selected.length){
            flipSelectedIndex(i);
            selectUpdate();;
        }

        return super.clickMenuButton(player,i);

    }

    private void onResultTaken(Player player, ItemStack result) {

        context.execute((level,pos) -> {
            level.playSound(null,pos,SoundEvents.ZOMBIE_BREAK_WOODEN_DOOR,SoundSource.BLOCKS,0.2F,1.5F);
        });

        if (!player.hasInfiniteMaterials()) {
            player.giveExperienceLevels(-3);
        }

        this.bookSlot.removeItem(0, 1);

        ItemStack remainder = getUnselected();
        this.enchantedBookSlot.setItem(0, remainder);
        this.outputSlot.setItem(0, ItemStack.EMPTY);

        enchantmentsUpdate(remainder);
        broadcastChanges();
    }
}
