package eee.eee4.networking.s2c;

import eee.eee4.EEE;
import eee.eee4.networking.BookSlotData;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;


import java.util.ArrayList;
import java.util.List;

public record BookSlotPayload(List<BookSlotData> books)
        implements CustomPacketPayload {

    public static final Type<@NotNull BookSlotPayload> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath(EEE.MOD_ID,"book_slot_payload"));

    public static final StreamCodec<@NotNull RegistryFriendlyByteBuf, @NotNull BookSlotPayload> STREAM_CODEC =
            CustomPacketPayload.codec(BookSlotPayload::write, BookSlotPayload::new);

    private BookSlotPayload(RegistryFriendlyByteBuf buf) {
        this(read(buf));
    }


    private static List<BookSlotData> read(RegistryFriendlyByteBuf buf) {
        int size = buf.readVarInt();
        List<BookSlotData> result = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            int lineCount = buf.readVarInt();
            List<Component> tooltip = new ArrayList<>(lineCount);

            for (int j = 0; j < lineCount; j++) {
                tooltip.add(ComponentSerialization.STREAM_CODEC.decode(buf));
            }

            int xpCost = buf.readVarInt();

            result.add(new BookSlotData(tooltip, xpCost));
        }

        return result;
    }

    private void write(RegistryFriendlyByteBuf buf) {
        buf.writeVarInt(books.size());

        for (BookSlotData data : books) {
            buf.writeVarInt(data.tooltip().size());
            for (Component t : data.tooltip()) {
                ComponentSerialization.STREAM_CODEC.encode(buf, t);
            }
            buf.writeVarInt(data.xpCost());
        }
    }


    @Override
    public @NotNull Type<? extends @NotNull CustomPacketPayload> type() {
        return TYPE;
    }
}

