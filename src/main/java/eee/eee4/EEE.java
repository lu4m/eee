package eee.eee4;

import eee.eee4.itemgroup.ItemGroupsManager;
import eee.eee4.registry.RegistryManager;
import net.fabricmc.api.ModInitializer;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EEE implements ModInitializer {
	public static final String MOD_ID = "eee4";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {

        RegistryManager.initialize();
        ItemGroupsManager.initialize();

	}
}