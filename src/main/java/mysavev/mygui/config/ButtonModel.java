package mysavev.mygui.config;

import java.util.ArrayList;
import java.util.List;

public class ButtonModel {
    private String material;
    private String name;
    private List<String> lore = new ArrayList<>();
    private List<Integer> slots = new ArrayList<>();
    private List<String> actions = new ArrayList<>();

    public ButtonModel() {}

    public ButtonModel(String material, String name, List<String> lore, List<Integer> slots, List<String> actions) {
        this.material = material;
        this.name = name;
        this.lore = lore;
        this.slots = slots;
        this.actions = actions;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getLore() {
        return lore;
    }

    public void setLore(List<String> lore) {
        this.lore = lore;
    }

    public List<Integer> getSlots() {
        return slots;
    }

    public void setSlots(List<Integer> slots) {
        this.slots = slots;
    }

    public List<String> getActions() {
        return actions;
    }

    public void setActions(List<String> actions) {
        this.actions = actions;
    }
}

