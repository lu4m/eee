package eee.eee4;

import eee.eee4.menus.EnchantmentCopyingMenu;
import eee.eee4.menus.EnchantmentSplittingMenu;
import eee.eee4.networking.s2c.BookSlotPayload;
import eee.eee4.networking.s2c.BooleanArrayPayload;
import eee.eee4.networking.s2c.EnchantedBookPayload;
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
import net.minecraft.world.entity.player.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(EnvType.CLIENT)
public class EeeClient implements ClientModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger("EEEClient");

	@Override
	public void onInitializeClient() {

        registerRenderers();
        registerMenus();
        registerGlobalReceivers();

	}

    public void registerGlobalReceivers(){

        ClientPlayNetworking.registerGlobalReceiver(
                BookSlotPayload.TYPE,
                (payload, context) ->
                        context.client().execute(() -> {
                            EnchantmentCopyingScreen.CLIENT_BOOKS_SLOTS_DATA.clear();
                            EnchantmentCopyingScreen.CLIENT_BOOKS_SLOTS_DATA.addAll(payload.books());
                        })
        );


        ClientPlayNetworking.registerGlobalReceiver(BooleanArrayPayload.TYPE,
                (payload, context) -> {
                    context.client().execute(() -> {
                        Player player = context.client().player;
                        if (player == null) return;

                        if (player.containerMenu.containerId != payload.syncId()) return;

                        if (player.containerMenu instanceof EnchantmentSplittingMenu menu) {
                            menu.receiveSelected(payload.array());
                        }
                    });
                });

        ClientPlayNetworking.registerGlobalReceiver(EnchantedBookPayload.TYPE,
                (payload, context) -> {
                    context.client().execute(() -> {
                        Player player = context.client().player;
                        if (player == null) return;

                        if (player.containerMenu.containerId != payload.syncId()) return;

                        if (player.containerMenu instanceof EnchantmentSplittingMenu menu) {
                            menu.receiveEnchantmentData(payload.enchantments());
                        }
                    });
                });


    }

    public void registerMenus(){
        MenuScreens.register(
                EEEMenus.ENCHANTMENT_COPYING_MENU,
                EnchantmentCopyingScreen::new
        );

        MenuScreens.register(
                EEEMenus.ENCHANTMENT_SPLITTING_MENU,
                EnchantmentSplittingScreen::new
        );
    }

    public void registerRenderers(){

        BlockEntityRenderers.register(
                EEEBlockEntities.ENCHANTMENT_COPYING_TABLE_ENTITY,
                EnchantmentCopyingRenderer::new
        );

        BlockEntityRenderers.register(
                EEEBlockEntities.ENCHANTMENT_SPLITTING_TABLE_ENTITY,
                EnchantmentSplittingRenderer::new
        );

    }

}