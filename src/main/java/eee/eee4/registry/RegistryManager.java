package eee.eee4.registry;

public final class RegistryManager {
    public static void initialize(){

        EEEBlocks.initialize();
        EEEBlockEntities.initialize();
        EEEMenus.initialize();
        EEEPayloads.initialize();
        EEEItems.initialize();
    }

}
