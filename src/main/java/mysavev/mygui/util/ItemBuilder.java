package mysavev.mygui.util;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.level.ItemLike;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import java.util.UUID;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ItemBuilder {
    private final ItemStack stack;

    public ItemBuilder(ItemLike item) {
        this.stack = new ItemStack(item);
    }

    public ItemBuilder(ItemStack stack) {
        this.stack = stack.copy();
    }

    public ItemBuilder(String itemId) {
        this.stack = new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.parse(itemId)));
    }

    public ItemBuilder setName(Component name) {
        this.stack.set(DataComponents.CUSTOM_NAME, name);
        return this;
    }

    public ItemBuilder setName(String name) {
        return this.setName(Component.literal(name.replace("&", "§"))); // Simple legacy color support
    }

    public ItemBuilder setLore(List<Component> lore) {
        this.stack.set(DataComponents.LORE, new ItemLore(lore));
        return this;
    }

    public ItemBuilder setLoreStrings(List<String> lore) {
        return this.setLore(lore.stream()
                .map(line -> Component.literal(line.replace("&", "§")))
                .collect(Collectors.toList()));
    }

    public ItemBuilder addLore(Component... lines) {
        ItemLore currentLore = this.stack.get(DataComponents.LORE);
        List<Component> newLines = currentLore != null ? new ArrayList<>(currentLore.lines()) : new ArrayList<>();
        newLines.addAll(Arrays.asList(lines));
        return this.setLore(newLines);
    }
    
    public ItemBuilder addLore(String... lines) {
         return this.addLore(Arrays.stream(lines)
                 .map(line -> Component.literal(line.replace("&", "§")))
                 .toArray(Component[]::new));
    }

    public ItemBuilder setAmount(int amount) {
        this.stack.setCount(amount);
        return this;
    }

    public ItemBuilder setCustomModelData(Integer data) {
        if (data == null) {
            this.stack.remove(DataComponents.CUSTOM_MODEL_DATA);
        } else {
            this.stack.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(data));
        }
        return this;
    }

    public ItemBuilder setGlow(boolean glow) {
        this.stack.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, glow);
        return this;
    }

    public ItemBuilder setHeadTexture(String textureBase64) {
        if (textureBase64 == null || textureBase64.isEmpty()) return this;
        
        GameProfile profile = new GameProfile(UUID.randomUUID(), "");
        profile.getProperties().put("textures", new Property("textures", textureBase64));
        this.stack.set(DataComponents.PROFILE, new ResolvableProfile(profile));
        return this;
    }

    public ItemStack build() {
        return this.stack;
    }
}
