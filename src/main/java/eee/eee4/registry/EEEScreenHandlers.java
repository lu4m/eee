package eee.eee4.registry;

import eee.eee4.EEE;
import eee.eee4.screenHandler.EnchantmentCopyingScreenHandler;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class EEEScreenHandlers {

    public static final ScreenHandlerType<EnchantmentCopyingScreenHandler> ENCHANTMENT_COPYING_SCREEN_HANDLER =
            register("enchantment_copying_screen_handler", EnchantmentCopyingScreenHandler::new,FeatureSet.empty());

    private static <T extends ScreenHandler> ScreenHandlerType<T> register(
            String path,
            ScreenHandlerType.Factory<T> factory,
            FeatureSet featureSet
    ) {
        Identifier id = Identifier.of(EEE.MOD_ID, path);
        return Registry.register(Registries.SCREEN_HANDLER, id,new ScreenHandlerType<>(factory,featureSet) );
    }

    public static void initialize() { }
}

