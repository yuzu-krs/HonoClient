package me.yuzu.hono.ui.clickgui;

import java.util.ArrayList;
import java.util.List;

import me.yuzu.hono.Hono;
import me.yuzu.hono.module.Category;
import me.yuzu.hono.module.Module;
import me.yuzu.hono.module.ModuleManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;


public class GuiCategory {
    private final Category category;
    private int x, y;
    private final List<ModuleButton> moduleButtons = new ArrayList<>();
    private final int categoryTitleHeight = 20;
    private boolean expanded = false;
    private boolean isDragging = false;
    private int dragOffsetX, dragOffsetY;

    private Module selectedModule = null;
    private boolean showOptions = false;
    

    public GuiCategory(Category category, int x, int y) {
        this.category = category;
        this.x = x;
        this.y = y;

        // モジュールボタンをカテゴリータイトルの下に配置
        int buttonY = y + categoryTitleHeight + 5;
        for (Module module : ModuleManager.getModulesByCategory(category)) {
            moduleButtons.add(new ModuleButton(module, x, buttonY));
            buttonY += 20;
        }
    }

    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        if (isDragging) {
            x = mouseX - dragOffsetX;
           y = mouseY - dragOffsetY;
        }
        
        

        Minecraft mc = Minecraft.getInstance();
        int categoryTextWidth = mc.font.width(category.name());
        int backgroundColor = expanded ? 0xFFFFFFFF : 0xCC000000;

        // カテゴリーヘッダー背景
        graphics.fill(x, y, x + categoryTextWidth + 8, y + 13, backgroundColor);
        
        
        // カテゴリー名
        graphics.drawString(mc.font, category.name(), x + 5, y + 3, 0xFFFF0000);

        if (expanded) {
            int moduleY = y + 17;
            int offsetX = 5;

            for (Module module : Hono.instance.modManager.getModulesByCategory(category)) {
                int moduleTextWidth = mc.font.width(module.getName());

                // 背景
                graphics.fill(x + 5 - offsetX, moduleY, x + moduleTextWidth + 10 + offsetX, moduleY + 15, 0xFF4F4F4F);

                // 枠線
                // 上
                graphics.fill(x + 5 - offsetX, moduleY, x + moduleTextWidth + 10 + offsetX, moduleY + 1, 0xFF000000);
                // 下
                graphics.fill(x + 5 - offsetX, moduleY + 14, x + moduleTextWidth + 10 + offsetX, moduleY + 15, 0xFF000000);
                // 左
                graphics.fill(x + 5 - offsetX, moduleY, x + 6 - offsetX, moduleY + 15, 0xFF000000);
                // 右
                graphics.fill(x + moduleTextWidth + 10 + offsetX, moduleY, x + moduleTextWidth + 11 + offsetX, moduleY + 15, 0xFF000000);

                int textColor = module.isToggled() ? 0xFFFF0000 : 0xFFFFFFFF;
                graphics.drawString(mc.font, module.getName(), x + 8, moduleY + 3, textColor);

                // モジュール説明（マウスオーバー時）
                if (mouseX >= x + 5 - offsetX && mouseX <= x + moduleTextWidth + 10 + offsetX
                        && mouseY >= moduleY && mouseY <= moduleY + 15) {
                    String description = module.getDescription();
                    int descriptionWidth = mc.font.width(description);
                    graphics.fill(mouseX, mouseY, mouseX + descriptionWidth + 8, mouseY + 12, 0xFF000000);
                    graphics.drawString(mc.font, description, mouseX + 4, mouseY + 2, 0xFFFFFFFF);
                }
                
                moduleY+=13;

                // オプション描画
                if (module == selectedModule && showOptions) {
                    module.renderOptions(graphics, x + 20 + offsetX, moduleY - 11);
                }
            }
        }
    }
    
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int categoryTextWidth = Minecraft.getInstance().font.width(category.name());

        // カテゴリタイトルのクリック判定
        if (mouseX >= x && mouseX <= x + categoryTextWidth + 10 && mouseY >= y && mouseY <= y + 15) {
            if (button == 0) { // 左クリック
                isDragging = true;
                dragOffsetX = (int)(mouseX - x);
                dragOffsetY = (int)(mouseY - y);
            } else if (button == 1) { // 右クリック
                toggleExpanded();
            }
            return true;
        }

        // カテゴリ展開時のモジュールクリック判定
        if (expanded) {
            int moduleY = y + 17;
            for (Module module : Hono.instance.modManager.getModulesByCategory(category)) {
                int moduleTextWidth = Minecraft.getInstance().font.width(module.getName());
                if (mouseX >= x && mouseX <= x + moduleTextWidth + 5 && mouseY >= moduleY && mouseY <= moduleY + 15) {
                    if (button == 0) {
                        module.toggle();
                    } else if (button == 1) {
                        selectedModule = module;
                        showOptions = !showOptions;
                    }
                    return true;
                }
                moduleY += 13;
            }
        }

        // 選択モジュールのオプション判定
        if (selectedModule != null && selectedModule.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }

        return false;
    }
    

    public void mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) { // 左クリック
            isDragging = false;
        }
    }
    
    

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Category getCategory() {
        return category;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public void toggleExpanded() {
        this.expanded = !this.expanded; // トグル処理
        ClickGuiMain.expandedStates.put(this.category, this.expanded);
    }
    
    
    
    
}










