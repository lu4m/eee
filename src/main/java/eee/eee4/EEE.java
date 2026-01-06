package eee.eee4;

import eee.eee4.itemgroup.ItemGroupsManager;
import eee.eee4.registry.EEEItems;
import eee.eee4.registry.RegistryManager;
import net.fabricmc.api.ModInitializer;


import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.fabricmc.fabric.api.loot.v3.LootTableSource;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EEE implements ModInitializer {
	public static final String MOD_ID = "eee4";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
        RegistryManager.initialize();
        ItemGroupsManager.initialize();
		modifyLootTables();
	}

	private void modifyLootTables(){
		LootTableEvents.MODIFY.register((key,tableBuilder, source, registries) -> {

			if (source != LootTableSource.VANILLA) return;

			if (key.identifier().equals(
					Identifier.withDefaultNamespace("archaeology/trail_ruins_rare"))
			) {

				tableBuilder.modifyPools(builder -> {

					builder.add(
							LootItem.lootTableItem(EEEItems.STONE_TABLET).setWeight(2)
					);

				});


			}
		});
	}
}