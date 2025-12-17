package eee.eee4.registry;

import eee.eee4.EEE;
import eee.eee4.menus.EnchantmentCopyingMenu;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.NotNull;

public final class EEEMenus {

    public static final MenuType<@NotNull EnchantmentCopyingMenu> ENCHANTMENT_COPYING_MENU;

    static {
        ENCHANTMENT_COPYING_MENU = register("enchantment_copying_screen_handler",
                EnchantmentCopyingMenu::new,FeatureFlagSet.of());
    }


    private static <T extends AbstractContainerMenu> MenuType<@NotNull T> register(
            String path,
            MenuType.MenuSupplier<@NotNull T> supplier,
            FeatureFlagSet featureFlagSet
    ) {
        return Registry.register(
                BuiltInRegistries.MENU,
                Identifier.fromNamespaceAndPath(EEE.MOD_ID,path),
                new MenuType<>(supplier,featureFlagSet)
        );
    }

    public static void initialize() { }
}

