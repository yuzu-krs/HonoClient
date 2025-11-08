package me.yuzu.hono.module.player;
import me.yuzu.hono.module.Category;
import me.yuzu.hono.module.Module;

public class AntiAFK extends Module {

    private boolean movingForward = true;

    public AntiAFK() {
        super("AntiAFK", "Prevents player from being AFK kicked", Category.PLAYER, 0);
    }

    @Override
    public void onUpdate() {
        if (!this.isToggled() || mc.player == null) return;

        // 前進 or 後退
        if (movingForward) {
            mc.player.input.forwardImpulse = 1.0f;
        } else {
            mc.player.input.forwardImpulse = -1.0f;
        }

        // 40tick (=2秒前後) で方向入れ替え
        if (mc.level.getGameTime() % 40 == 0) {
            movingForward = !movingForward;
        }
    }



	@Override
	public void setOptions() {
		// TODO 自動生成されたメソッド・スタブ
		
	}
}