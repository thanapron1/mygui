package mysavev.mygui.editor;

import eu.pb4.sgui.api.gui.SimpleGui;
import mysavev.mygui.util.ItemBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class MaterialSelector {
    private final ServerPlayer player;
    private final Consumer<String> onSelect;
    private final Runnable onBack;
    
    // Cache the item list lazily to ensure registry is fully populated
    private static List<Item> sortedItems;

    private static List<Item> getItems() {
        if (sortedItems == null) {
            sortedItems = BuiltInRegistries.ITEM.stream()
                .filter(item -> item != Items.AIR)
                .sorted(Comparator.comparing(item -> BuiltInRegistries.ITEM.getKey(item).toString()))
                .collect(Collectors.toList());
        }
        return sortedItems;
    }

    public MaterialSelector(ServerPlayer player, Consumer<String> onSelect, Runnable onBack) {
        this.player = player;
        this.onSelect = onSelect;
        this.onBack = onBack;
    }

    public void open(int page) {
        SimpleGui gui = new SimpleGui(MenuType.GENERIC_9x6, player, false);
        gui.setTitle(Component.literal("Select Material (Page " + (page + 1) + ")"));
        
        List<Item> items = getItems();

        int itemsPerPage = 45;
        int totalPages = (int) Math.ceil((double) items.size() / itemsPerPage);
        int startIndex = page * itemsPerPage;
        int endIndex = Math.min(startIndex + itemsPerPage, items.size());

        // Fill item slots
        for (int i = startIndex; i < endIndex; i++) {
            Item item = items.get(i);
            ResourceLocation id = BuiltInRegistries.ITEM.getKey(item);
            int slotIndex = i - startIndex;
            
            gui.setSlot(slotIndex, new ItemStack(item), (index, type, action) -> {
                onSelect.accept(id.toString());
            });
        }

        // Navigation Bar (Row 6)
        // Previous Page
        if (page > 0) {
            gui.setSlot(45, new ItemBuilder(Items.ARROW).setName("§cPrevious Page").build(), (index, type, action) -> {
                open(page - 1);
            });
        }

        // Back / Cancel
        gui.setSlot(49, new ItemBuilder(Items.BARRIER).setName("§cCancel").build(), (index, type, action) -> {
            onBack.run();
        });

        // Next Page
        if (page < totalPages - 1) {
            gui.setSlot(53, new ItemBuilder(Items.ARROW).setName("§aNext Page").build(), (index, type, action) -> {
                open(page + 1);
            });
        }
        
        gui.open();
    }
}
