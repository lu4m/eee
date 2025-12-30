package eee.eee4;

import eee.eee4.menus.EnchantmentCopyingMenu;
import eee.eee4.networking.s2c.BookSlotPayload;
import eee.eee4.registry.EEEBlockEntities;
import eee.eee4.registry.EEEMenus;
import eee.eee4.renderer.blockentity.EnchantmentCopyingRenderer;
import eee.eee4.renderer.blockentity.EnchantmentSplittingRenderer;
import eee.eee4.screen.EnchantmentCopyingScreen;
import eee.eee4.screen.EnchantmentSplittingScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;

@Environment(EnvType.CLIENT)
public class EeeClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {

        MenuScreens.register(
                EEEMenus.ENCHANTMENT_COPYING_MENU,
                EnchantmentCopyingScreen::new
        );

        MenuScreens.register(
                EEEMenus.ENCHANTMENT_SPLITTING_MENU,
                EnchantmentSplittingScreen::new
        );

        BlockEntityRenderers.register(
                EEEBlockEntities.ENCHANTMENT_COPYING_TABLE_ENTITY,
                EnchantmentCopyingRenderer::new
        );

        BlockEntityRenderers.register(
                EEEBlockEntities.ENCHANTMENT_SPLITTING_TABLE_ENTITY,
                EnchantmentSplittingRenderer::new
        );

        ClientPlayNetworking.registerGlobalReceiver(
                BookSlotPayload.TYPE,
                (payload, context) ->
                    context.client().execute(() -> {
                        EnchantmentCopyingScreen.CLIENT_BOOKS_SLOTS_DATA.clear();
                        EnchantmentCopyingScreen.CLIENT_BOOKS_SLOTS_DATA.addAll(payload.books());
                    })
        );

	}

}