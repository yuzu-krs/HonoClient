package me.yuzu.hono.ui.clickgui.options;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

public class CheckBox {
    private String label;
    private boolean checked;
    private int x, y;

    public CheckBox(String label, boolean initialState, int x, int y) {
        this.label = label;
        this.checked = initialState;
        this.x = x;
        this.y = y;
    }

    public void render(GuiGraphics graphics) {
        Minecraft mc = Minecraft.getInstance();
        int boxSize = 10;
        int padding = 2;
        int labelWidth = mc.font.width(label);
        int boxWidth = boxSize + 10 + labelWidth;

        // 背景の枠
        graphics.fill(x - padding, y - padding, x + boxWidth + padding, y + boxSize + padding, 0xFF000000); // 黒い枠

        // チェックボックス本体（灰色背景）
        graphics.fill(x, y, x + boxSize, y + boxSize, 0xFF4F4F4F);

        // チェックが入っている場合は緑、入っていない場合は白
        graphics.fill(x + 1, y + 1, x + boxSize - 1, y + boxSize - 1, checked ? 0xFF00FF00 : 0xFFFFFFFF);

        // ラベル描画
        graphics.drawString(mc.font, label, x + boxSize + 6, y + 1, 0xFFFFFFFF, false);
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int boxSize = 10;
        if (button == 0 && mouseX >= x && mouseX <= x + boxSize && mouseY >= y && mouseY <= y + boxSize) {
            this.checked = !this.checked; // ← ここで状態を反転
            return true;
        }
        return false;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public String getLabel() {
        return label;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
