package eee.eee4.networking.s2c;

import eee.eee4.EEE;
import eee.eee4.networking.BookSlotData;
import eee.eee4.networking.EnchantmentData;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public record EnchantedBookPayload(List<EnchantmentData> enchantments ) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<@NotNull EnchantedBookPayload> TYPE =
            new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(EEE.MOD_ID,"book_slot_payload"));

    public static final StreamCodec<@NotNull RegistryFriendlyByteBuf, @NotNull EnchantedBookPayload> STREAM_CODEC =
            CustomPacketPayload.codec(EnchantedBookPayload::write, EnchantedBookPayload::new);

    private EnchantedBookPayload(RegistryFriendlyByteBuf buf) {
        this(read(buf));
    }


    private static List<EnchantmentData> read(RegistryFriendlyByteBuf buf) {
        int size = buf.readInt();

        List<EnchantmentData> enchantments = new ArrayList<>(size);

        for (int i = 0 ; i < size ; i++) {
            Component name = ComponentSerialization.STREAM_CODEC.decode(buf);
            int xpCost = buf.readInt();
            EnchantmentData enchantmentData = new EnchantmentData(name, xpCost);
            enchantments.add(enchantmentData);

        }

        return enchantments;
    }

    private void write(RegistryFriendlyByteBuf buf) {
        buf.writeVarInt(enchantments.size());

        for (EnchantmentData data : enchantments) {
            ComponentSerialization.STREAM_CODEC.encode(buf, data.name());
            buf.writeVarInt(data.xpCost());
        }
    }


    @Override
    public @NotNull CustomPacketPayload.Type<? extends @NotNull CustomPacketPayload> type() {
        return TYPE;
    }
}
