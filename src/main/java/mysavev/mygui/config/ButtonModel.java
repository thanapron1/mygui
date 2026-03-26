package mysavev.mygui.config;

import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

public class ButtonModel {
    private String material;
    private String name;
    private List<String> lore = new ArrayList<>();
    private int amount = 1;

	// Economy settings (optional)
	private boolean allowBuy = false;
	private boolean allowSell = false;
  /**
   * Item prices (string-backed for JSON compatibility and exact math).
   */
  private String buyPrice = "0";
  private String sellPrice = "0";

    private Integer customModelData;
    private boolean glow;
    private boolean closeOnClick = false;
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

    public boolean isCloseOnClick() {
        return closeOnClick;
    }

    public void setCloseOnClick(boolean closeOnClick) {
        this.closeOnClick = closeOnClick;
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

  public boolean isAllowBuy() {
    return allowBuy;
  }

  public void setAllowBuy(boolean allowBuy) {
    this.allowBuy = allowBuy;
  }

  public boolean isAllowSell() {
    return allowSell;
  }

  public void setAllowSell(boolean allowSell) {
    this.allowSell = allowSell;
  }

  public BigDecimal getBuyPrice() {
    try {
      return new BigDecimal(buyPrice == null ? "0" : buyPrice);
    } catch (Exception e) {
      return BigDecimal.ZERO;
    }
  }

  public void setBuyPrice(BigDecimal buyPrice) {
    this.buyPrice = (buyPrice == null ? BigDecimal.ZERO : buyPrice).toPlainString();
  }

  public void setBuyPriceString(String buyPrice) {
    this.buyPrice = buyPrice;
  }

  public String getBuyPriceString() {
    return this.buyPrice;
  }

  public BigDecimal getSellPrice() {
    try {
      return new BigDecimal(sellPrice == null ? "0" : sellPrice);
    } catch (Exception e) {
      return BigDecimal.ZERO;
    }
  }

  public void setSellPrice(BigDecimal sellPrice) {
    this.sellPrice = (sellPrice == null ? BigDecimal.ZERO : sellPrice).toPlainString();
  }

  public void setSellPriceString(String sellPrice) {
    this.sellPrice = sellPrice;
  }

  public String getSellPriceString() {
    return this.sellPrice;
  }
}
