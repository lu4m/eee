package eee.eee4.registry;

import eee.eee4.networking.s2c.BookSlotPayload;
import eee.eee4.networking.s2c.BooleanArrayPayload;
import eee.eee4.networking.s2c.EnchantedBookPayload;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public final class EEEPayloads {

    public static void initialize(){
        PayloadTypeRegistry.playS2C().register(
                BookSlotPayload.TYPE,
                BookSlotPayload.STREAM_CODEC
        );

        PayloadTypeRegistry.playS2C().register(
                BooleanArrayPayload.TYPE,
                BooleanArrayPayload.STREAM_CODEC
        );

        PayloadTypeRegistry.playS2C().register(
                EnchantedBookPayload.TYPE,
                EnchantedBookPayload.STREAM_CODEC
        );
    }
}
