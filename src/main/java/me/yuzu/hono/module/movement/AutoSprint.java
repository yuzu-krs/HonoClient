package me.yuzu.hono.module.movement;

import me.yuzu.hono.module.Category;
import me.yuzu.hono.module.Module;

public class AutoSprint extends Module {

    public AutoSprint() {
        super("AutoSprint", "Makes the player always sprint", Category.MOVEMENT, 0);
    }

    @Override
    public void onUpdate() {
        if (this.isToggled() && mc.player != null) {
            // 前進中かつスプリントしていなければスプリント開始
            if (mc.player.input.forwardImpulse > 0 && !mc.player.isSprinting()) {
                mc.player.setSprinting(true);
            }
        }
    }

	@Override
	public void setOptions() {
		// TODO 自動生成されたメソッド・スタブ
		
	}

}