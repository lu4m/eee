package eee.eee4.menus;

import eee.eee4.EEE;
import eee.eee4.registry.EEEBlocks;
import eee.eee4.registry.EEEMenus;
import net.minecraft.resources.Identifier;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

public class EnchantmentSplittingMenu extends AbstractContainerMenu {

    private final Container inventory;
    private final ContainerLevelAccess context;

    private static final Identifier EMPTY_BOOK_SLOT_TEXTURE = Identifier.fromNamespaceAndPath(EEE.MOD_ID,"container/slot/book");
    private static final Identifier EMPTY_ENCHANTED_BOOK_SLOT_TEXTURE = Identifier.fromNamespaceAndPath(EEE.MOD_ID,"container/slot/enchanted_book");


    public EnchantmentSplittingMenu(int syncId, Inventory playerInventory) {
        this(syncId, playerInventory, ContainerLevelAccess.NULL);
    }

    public EnchantmentSplittingMenu(int syncId, Inventory playerInventory, ContainerLevelAccess context) {
        super(EEEMenus.ENCHANTMENT_SPLITTING_MENU, syncId);
        this.context = context;

        this.inventory = new SimpleContainer(3) {
            @Override
            public void setChanged() {
                super.setChanged();
                EnchantmentSplittingMenu.this.slotsChanged(this);
            }
        };

        this.addSlot(new Slot(this.inventory, 0, 12, 28) {
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
        this.addSlot(new Slot(this.inventory, 1, 76, 87) {
            public boolean mayPlace(@NotNull ItemStack stack) {
                return stack.is(Items.BOOK);
            }

            public Identifier getNoItemIcon() {
                return EMPTY_BOOK_SLOT_TEXTURE;
            }
        });
        this.addSlot(new Slot(this.inventory, 2, 134, 87) {
            public boolean mayPlace(@NotNull ItemStack stack) {
                return false;
            }
        });

        this.addStandardInventorySlots(playerInventory, 8, 124);


        broadcastChanges();

    }

    @Override
    public @NonNull ItemStack quickMoveStack(@NonNull Player player, int i) {
       return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(@NonNull Player player) {
        return stillValid(context, player, EEEBlocks.ENCHANTMENT_SPLITTING_TABLE);
    }
}
