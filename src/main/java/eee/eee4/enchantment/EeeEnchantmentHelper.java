package eee.eee4.enchantment;

import net.minecraft.ChatFormatting;
import net.minecraft.util.Mth;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.Items;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.List;

import java.util.ArrayList;

public class EeeEnchantmentHelper {

    public static List<Component> buildEnchantmentCopyingTooltip(ItemStack stack) {
        List<Component> tooltip = new ArrayList<>();

        if (stack.isEmpty()) {
            return tooltip;
        }

        if (stack.is(Items.ENCHANTED_BOOK)) {
            tooltip.add(stack.getHoverName().copy().setStyle(Style.EMPTY.withColor(ChatFormatting.AQUA)));
            var enchants = EnchantmentHelper.getEnchantmentsForCrafting(stack).entrySet();

            if (enchants.isEmpty()) {
                tooltip.add(Component.literal("No Enchantments").setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));
            } else {
                for (var entry : enchants) {
                    int level = entry.getIntValue();

                    Component line = Enchantment.getFullname(entry.getKey(),level).copy().setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY));

                    tooltip.add(line);
                }
            }
        }
        else{
            tooltip.add(stack.getHoverName().copy().setStyle(Style.EMPTY.withColor(ChatFormatting.WHITE)));
        }
        return tooltip;
    }

    public static int xpCost(ItemStack book){

        int total = 7;

        var enchants = EnchantmentHelper.getEnchantmentsForCrafting(book);

        for (var entry : enchants.entrySet()) {
            int level = entry.getIntValue();
            total+= level*2;
        }

        return total;
    }

    public static int xpCostSplitting(int level){
        int total = 1;
        total += total + Mth.floor(level * 1.5);
        return total;
    }

    public static int xpPointsDecrease(ItemStack book){
        int total = 100;

        var enchants = EnchantmentHelper.getEnchantmentsForCrafting(book);

        for (var entry : enchants.entrySet()) {
            int level = entry.getIntValue();
            total += 25 * level;
        }

        return total;
    }

}
