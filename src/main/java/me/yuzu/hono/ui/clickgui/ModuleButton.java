package me.yuzu.hono.ui.clickgui;

import me.yuzu.hono.module.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

public class ModuleButton {

    private final Module module;
    private int x, y;

    public ModuleButton(Module module, int x, int y) {
        this.module = module;
        this.x = x;
        this.y = y;
    }

    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        Minecraft mc = Minecraft.getInstance();

        // 背景（トグル状態で色を変更）
        graphics.fill(x, y, x + 100, y + 20,
                module.isToggled() ? 0xFF00FF00 : 0xFFFF0000);

        // テキスト描画（中央寄せは不要なら固定座標でOK）
        graphics.drawString(mc.font, module.getName(), x + 5, y + 6, 0xFFFFFFFF, false);
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // ボタン範囲チェック
        if (mouseX >= x && mouseX <= x + 100 && mouseY >= y && mouseY <= y + 20) {
            module.toggle();
            return true;
        }
        return false;
    }
}