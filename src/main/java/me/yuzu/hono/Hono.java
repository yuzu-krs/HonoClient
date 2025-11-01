package me.yuzu.hono;

import me.yuzu.hono.module.KeyManager;
import me.yuzu.hono.module.ModuleManager;
import net.minecraft.client.gui.GuiGraphics;

public class Hono {
	public static String name ="Hono";
	public static String creator ="Yuzu";
		
	public static Hono instance = new Hono();

	  // モジュールやキー入力のマネージャー
    public static ModuleManager modManager;
    public static KeyManager keyManager;

    // クライアント起動時に呼ばれる初期化メソッド
    public static void startClient() {
        // 各マネージャーを初期化
        modManager = new ModuleManager();
        keyManager = new KeyManager();
    }

    // 毎フレーム（tickごと）に呼ばれる処理
    public static void onTick() {
        // モジュールの更新とキー入力チェックを行う
        if (modManager != null && keyManager != null) {
            modManager.onTick(keyManager);
        }
    }

    // 画面描画時に呼ばれるメソッド
    public void onRender(GuiGraphics guiGraphics) {


    }
	
		
}
