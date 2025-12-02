package eee.eee4.networking.s2c;

import eee.eee4.EEE;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public record BookTooltipPayload(List<List<Text>> tooltips)
        implements CustomPayload {

    public static final CustomPayload.Id<BookTooltipPayload> ID =
            new CustomPayload.Id<>(Identifier.of(EEE.MOD_ID, "book_tooltip"));

    public static final PacketCodec<RegistryByteBuf, BookTooltipPayload> CODEC =
            PacketCodec.of(BookTooltipPayload::write, BookTooltipPayload::new);

    private BookTooltipPayload(RegistryByteBuf buf) {
        this(readTooltips(buf));
    }

    private static List<List<Text>> readTooltips(RegistryByteBuf buf) {
        int outer = buf.readVarInt();
        List<List<Text>> result = new ArrayList<>();

        for (int i = 0; i < outer; i++) {
            int inner = buf.readVarInt();
            List<Text> list = new ArrayList<>();

            for (int j = 0; j < inner; j++) {
                list.add(TextCodecs.PACKET_CODEC.decode(buf));
            }

            result.add(list);
        }

        return result;
    }

    private void write(RegistryByteBuf buf) {
        buf.writeVarInt(tooltips.size());

        for (List<Text> list : tooltips) {
            buf.writeVarInt(list.size());
            for (Text t : list) {
                TextCodecs.PACKET_CODEC.encode(buf,t);
            }
        }
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}

