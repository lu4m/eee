package eee.eee4.networking.s2c;

import eee.eee4.EEE;
import eee.eee4.networking.BookSlotData;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public record BooleanArrayPayload(boolean[] array ) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<@NotNull BooleanArrayPayload> TYPE =
            new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(EEE.MOD_ID,"boolean_array_payload"));


    private BooleanArrayPayload(RegistryFriendlyByteBuf buf) {
        this(read(buf));
    }

    private static boolean[] read(RegistryFriendlyByteBuf buf) {
        int size = buf.readVarInt();
        boolean[] result = new boolean[size];

        for (int i = 0; i < size; i++) {
            result[i] = buf.readBoolean();
        }

        return result;
    }

    private void write(RegistryFriendlyByteBuf buf) {
        buf.writeVarInt(array.length);

        for (boolean b : array) {
            buf.writeBoolean(b);
        }
    }


    @Override
    public @NotNull CustomPacketPayload.Type<? extends @NotNull CustomPacketPayload> type() {
        return TYPE;
    }
}
