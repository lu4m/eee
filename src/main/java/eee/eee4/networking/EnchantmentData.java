package eee.eee4.networking;

import net.minecraft.network.chat.Component;

public record EnchantmentData(
    Component name,
    int xpCost
) {}
