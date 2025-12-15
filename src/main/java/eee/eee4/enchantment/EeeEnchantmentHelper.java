package eee.eee4.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import java.util.List;

import java.util.ArrayList;

public class EeeEnchantmentHelper {

    public static List<Text> buildEnchantmentCopyingTooltip(ItemStack stack) {
        List<Text> tooltip = new ArrayList<>();

        if (stack.isEmpty()) {
            return tooltip;
        }

        if (stack.isOf(Items.ENCHANTED_BOOK)) {
            tooltip.add(stack.getName().copy().formatted(Formatting.AQUA));
            var enchants = EnchantmentHelper.getEnchantments(stack);

            if (enchants.isEmpty()) {
                tooltip.add(Text.literal("No Enchantments").formatted(Formatting.DARK_GRAY));
            } else {
                for (var entry : enchants.getEnchantmentEntries()) {
                    int level = entry.getIntValue();

                    Text line = Enchantment.getName(entry.getKey(),level).copy().formatted(Formatting.GRAY);

                    tooltip.add(line);
                }
            }
        }
        else{
            tooltip.add(stack.getName().copy().formatted(Formatting.WHITE));
        }
        return tooltip;
    }

    public static int xpCost(ItemStack book){

        int total = 7;

        var enchants = EnchantmentHelper.getEnchantments(book);

        for (var entry : enchants.getEnchantmentEntries()) {
            int level = entry.getIntValue();
            total+= level*2;
        }

        return total;
    }
}
