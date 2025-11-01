package me.yuzu.hono.module.movement;
import org.lwjgl.glfw.GLFW;

import me.yuzu.hono.module.Category;
import me.yuzu.hono.module.Module;
public class Flight extends Module{


    public Flight() {
        // 親クラス (Module) のコンストラクタを呼ぶ（これが必ず最初）
        super("Flight", "Enables creative flight in survival", Category.MOVEMENT, GLFW.GLFW_KEY_F);
    }

    @Override
    public void onEnable() {
        // プレイヤーが存在するか確認してから能力を変更する
        if (mc.player != null) {
            mc.player.getAbilities().mayfly = true;   // 飛行を許可
            mc.player.getAbilities().flying = true;   // 飛行状態にする
        }
        super.onEnable();
    }

    @Override
    public void onDisable() {
        if (mc.player != null) {
            mc.player.getAbilities().flying = false;  // 飛行状態を解除
            mc.player.getAbilities().mayfly = false;  // 飛行許可を解除
        }
        super.onDisable();
    }

    @Override
    public void onUpdate() {
        // ON の間は常に mayfly を維持（サーバーや他処理で消されても復帰できるように）
        if (this.isToggled() && mc.player != null) {
            mc.player.getAbilities().mayfly = true;
            mc.player.getAbilities().flying = true; 
            
        }
    }


}

