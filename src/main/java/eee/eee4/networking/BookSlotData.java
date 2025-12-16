package eee.eee4.networking;

import net.minecraft.network.chat.Component;

import java.util.List;

public record BookSlotData (
        List<Component> tooltip,
        int xpCost
) {}
