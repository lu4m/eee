package eee.eee4;

import eee.eee4.menus.EnchantmentCopyingMenu;
import eee.eee4.networking.s2c.BookSlotPayload;
import eee.eee4.registry.EEEBlockEntities;
import eee.eee4.registry.EEEMenus;
import eee.eee4.renderer.blockentity.EnchantmentCopyingRenderer;
import eee.eee4.screen.EnchantmentCopyingScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.network.chat.Component;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;

@Environment(EnvType.CLIENT)
public class EeeClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {

        MenuScreens.register(
                EEEMenus.ENCHANTMENT_COPYING_MENU,
                EnchantmentCopyingScreen::new
        );

        BlockEntityRenderers.register(
                EEEBlockEntities.ENCHANTMENT_COPYING_TABLE_ENTITY,
                EnchantmentCopyingRenderer::new
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


    public static class ClientModEvents{

    }
}