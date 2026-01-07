package eee.eee4.misc;

import eee.eee4.EEE;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SmithingTemplateItem;

import java.util.List;

public class CurseTemplate extends SmithingTemplateItem {

    private static final ChatFormatting TITLE_FORMAT = ChatFormatting.GRAY;
    private static final ChatFormatting DESCRIPTION_FORMAT = ChatFormatting.BLUE;

    private static final Component CURSE_APPLIES_TO = Component.translatable(Util.makeDescriptionId("item", Identifier.fromNamespaceAndPath(EEE.MOD_ID,"smithing_template.curse.applies_to"))).withStyle(DESCRIPTION_FORMAT);
    private static final Component CURSE_INGREDIENTS = Component.translatable(Util.makeDescriptionId("item", Identifier.fromNamespaceAndPath(EEE.MOD_ID,"smithing_template.curse.ingredients"))).withStyle(DESCRIPTION_FORMAT);
    private static final Component CURSE_BASE_SLOT_DESCRIPTION = Component.translatable(Util.makeDescriptionId("item", Identifier.fromNamespaceAndPath(EEE.MOD_ID,"smithing_template.curse.base_slot_description")));
    private static final Component CURSE_ADDITIONS_SLOT_DESCRIPTION = Component.translatable(Util.makeDescriptionId("item", Identifier.fromNamespaceAndPath(EEE.MOD_ID,"smithing_template.curse.additions_slot_description")));

    private static final Identifier EMPTY_SLOT_SWORD = Identifier.withDefaultNamespace("container/slot/sword");
    private static final Identifier EMPTY_SLOT_ECHO_SHARD = Identifier.fromNamespaceAndPath(EEE.MOD_ID,"container/slot/echo_shard");

    public CurseTemplate(
            Component component,
            Component component2,
            Component component3,
            Component component4,
            List<Identifier> list,
            List<Identifier> list2,
            Properties properties
    ) {
        super(component, component2, component3, component4, list, list2, properties);
    }

    public static SmithingTemplateItem createCurseTemplate(Item.Properties properties) {
        return new CurseTemplate(CURSE_APPLIES_TO, CURSE_INGREDIENTS, CURSE_BASE_SLOT_DESCRIPTION, CURSE_ADDITIONS_SLOT_DESCRIPTION, CurseTemplate.createCurseBaseIconList(), CurseTemplate.createCurseMaterialIconList(), properties);
    }

    private static List<Identifier> createCurseBaseIconList(){
        return List.of(EMPTY_SLOT_SWORD);
    }

    private static List<Identifier> createCurseMaterialIconList(){
        return List.of(EMPTY_SLOT_ECHO_SHARD);
    }
}
