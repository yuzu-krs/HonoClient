package me.yuzu.hono.module.combat;

import me.yuzu.hono.module.Category;
import me.yuzu.hono.module.Module;
import me.yuzu.hono.ui.clickgui.options.CheckBox;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
public class KillAura extends Module {

    private CheckBox targetPlayers;
    private CheckBox targetEntities;
    private CheckBox faceTarget;

    public KillAura() {
        super("KillAura", "Automatically attacks nearby entities", Category.COMBAT, 0);

        // 🔹 ここで初期化しておく（null防止）
        targetPlayers = new CheckBox("Target Players", true, 0, 0);
        targetEntities = new CheckBox("Target Entities", false, 0, 0);
        faceTarget = new CheckBox("Face Target", false, 0, 0);
    }

    @Override
    public void onUpdate() {
        if (!this.isToggled() || mc.player == null) return;
        if (targetPlayers == null || targetEntities == null || faceTarget == null) return;

        Entity closestEntity = getClosestTarget();
        if (closestEntity != null && closestEntity.isAlive()) {
            if (faceTarget.isChecked()) {
                faceTarget(closestEntity);
            }
            attackEntity(closestEntity);
        }
    }

    private Entity getClosestTarget() {
        if (targetPlayers == null || targetEntities == null) return null;

        Entity closest = null;
        double closestDistance = 5.0;

        for (Entity entity : mc.level.entitiesForRendering()) {
            if (entity instanceof LivingEntity && entity != mc.player) {
                boolean targetPlayer = targetPlayers.isChecked() && entity instanceof Player;
                boolean targetMob = targetEntities.isChecked() && !(entity instanceof Player);

                if (targetPlayer || targetMob) {
                    double distance = mc.player.distanceTo(entity);
                    if (distance < closestDistance) {
                        closest = entity;
                        closestDistance = distance;
                    }
                }
            }
        }
        return closest;
    }
    private void faceTarget(Entity target) {
        if (mc.player == null || target == null) return;

        // プレイヤーとターゲットの座標差を計算
        double dx = target.getX() - mc.player.getX();
        double dz = target.getZ() - mc.player.getZ();

        // ターゲットの目線の高さを考慮
        double targetEyeY = target.getY() + target.getEyeHeight();
        double playerEyeY = mc.player.getY() + mc.player.getEyeHeight();
        double dy = targetEyeY - playerEyeY;

        // 向き計算
        double distanceXZ = Math.sqrt(dx * dx + dz * dz);
        float yaw = (float) (Math.toDegrees(Math.atan2(dz, dx)) - 90.0);
        float pitch = (float) (-Math.toDegrees(Math.atan2(dy, distanceXZ)));

        // プレイヤーの向きを変更
        mc.player.setYRot(yaw);
        mc.player.setXRot(pitch);
    }


    private void attackEntity(Entity target) {
        if (mc.player.distanceTo(target) <= 5.0f) {
            mc.player.swing(InteractionHand.MAIN_HAND);
            mc.gameMode.attack(mc.player, target);
        }
    }


    @Override
    public void renderOptions(GuiGraphics graphics, int x, int y) {
        // 🔹 ここにも安全ガードを入れる
        if (targetPlayers == null || targetEntities == null || faceTarget == null) return;

        targetPlayers.setPosition(x, y);
        targetPlayers.render(graphics);

        targetEntities.setPosition(x, y + 12);
        targetEntities.render(graphics);

        faceTarget.setPosition(x, y + 24);
        faceTarget.render(graphics);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (targetPlayers != null && targetPlayers.mouseClicked(mouseX, mouseY, button)) return true;
        if (targetEntities != null && targetEntities.mouseClicked(mouseX, mouseY, button)) return true;
        if (faceTarget != null && faceTarget.mouseClicked(mouseX, mouseY, button)) return true;
        return false;
    }

	@Override
	public void setOptions() {
		// TODO 自動生成されたメソッド・スタブ
        // 🔹 GUI用にも登録
        addOption(targetPlayers);
        addOption(targetEntities);
        addOption(faceTarget);
		
	}
}