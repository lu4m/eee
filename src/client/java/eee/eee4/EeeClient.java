package eee.eee4;

import eee.eee4.networking.s2c.BookSlotPayload;
import eee.eee4.registry.EEEScreenHandlers;
import eee.eee4.screen.EnchantmentCopyingScreen;
import eee.eee4.screenHandler.EnchantmentCopyingScreenHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

@Environment(EnvType.CLIENT)
public class EeeClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
        HandledScreens.register(EEEScreenHandlers.ENCHANTMENT_COPYING_SCREEN_HANDLER, EnchantmentCopyingScreen::new);

        ClientPlayNetworking.registerGlobalReceiver(
                BookSlotPayload.ID,
                (payload, context) -> {
                    context.client().execute(() -> {
                        EnchantmentCopyingScreen.CLIENT_BOOKS_SLOTS_DATA.clear();
                        EnchantmentCopyingScreen.CLIENT_BOOKS_SLOTS_DATA.addAll(payload.books());
                    });
                }
        );

	}
}