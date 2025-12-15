package eee.eee4.networking.s2c;

import eee.eee4.EEE;
import eee.eee4.networking.BookSlotData;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public record BookSlotPayload(List<BookSlotData> books)
        implements CustomPayload {

    public static final Id<BookSlotPayload> ID =
            new Id<>(Identifier.of(EEE.MOD_ID, "book_tooltip"));

    public static final PacketCodec<RegistryByteBuf, BookSlotPayload> CODEC =
            PacketCodec.of(BookSlotPayload::write, BookSlotPayload::new);

    private BookSlotPayload(RegistryByteBuf buf) {
        this(read(buf));
    }


    private static List<BookSlotData> read(RegistryByteBuf buf) {
        int size = buf.readVarInt();
        List<BookSlotData> result = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            int lineCount = buf.readVarInt();
            List<Text> tooltip = new ArrayList<>(lineCount);

            for (int j = 0; j < lineCount; j++) {
                tooltip.add(TextCodecs.PACKET_CODEC.decode(buf));
            }

            int xpCost = buf.readVarInt();

            result.add(new BookSlotData(tooltip, xpCost));
        }

        return result;
    }

    private void write(RegistryByteBuf buf) {
        buf.writeVarInt(books.size());

        for (BookSlotData data : books) {
            buf.writeVarInt(data.tooltip().size());
            for (Text t : data.tooltip()) {
                TextCodecs.PACKET_CODEC.encode(buf, t);
            }
            buf.writeVarInt(data.xpCost());
        }
    }


    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}

