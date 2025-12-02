package eee.eee4.registry;

public class RegistryManager {
    public static void init(){

        EEEBlocks.initialize();
        EEEBlockEntities.initialize();
        EEEScreenHandlers.initialize();
        EEEPayloads.initialize();

    }

}
