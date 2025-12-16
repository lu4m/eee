package eee.eee4.registry;

import eee.eee4.networking.s2c.BookSlotPayload;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public class EEEPayloads {

    public static void initialize(){
        PayloadTypeRegistry.playS2C().register(
                BookSlotPayload.TYPE,
                BookSlotPayload.STREAM_CODEC
        );
    }
}
