package mysavev.mygui.config;

import java.util.ArrayList;
import java.util.List;

public class ButtonModel {
    private String material;
    private String name;
    private List<String> lore = new ArrayList<>();
    private int amount = 1;
    private Integer customModelData;
    private boolean glow;
    private String headTexture;
    private List<Integer> slots = new ArrayList<>();
    private List<String> actions = new ArrayList<>();

    public ButtonModel() {}

    public ButtonModel(String material, String name, List<String> lore, List<Integer> slots, List<String> actions) {
        this.material = material;
        this.name = name;
        this.lore = lore;
        this.slots = slots;
        this.actions = actions;
        this.amount = 1;
        this.glow = false;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public Integer getCustomModelData() {
        return customModelData;
    }

    public void setCustomModelData(Integer customModelData) {
        this.customModelData = customModelData;
    }
    
    public boolean isGlow() {
        return glow;
    }

    public void setGlow(boolean glow) {
        this.glow = glow;
    }

    public String getHeadTexture() {
        return headTexture;
    }

    public void setHeadTexture(String headTexture) {
        this.headTexture = headTexture;
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
