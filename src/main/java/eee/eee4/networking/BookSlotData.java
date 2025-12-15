package eee.eee4.networking;

import net.minecraft.text.Text;

import java.util.List;

public record BookSlotData (
        List<Text> tooltip,
        int xpCost
) {}
