package eee.eee4.registry;

import eee.eee4.networking.s2c.BookTooltipPayload;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.registry.Registry;

public class EEEPayloads {

    public static void initialize(){
        PayloadTypeRegistry.playS2C().register(
                BookTooltipPayload.ID,
                BookTooltipPayload.CODEC
        );
    }
}
