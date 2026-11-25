package dev.bombowy.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class BombowyClient implements ClientModInitializer {
    public static final String MOD_ID = "bombowyclient";
    public static final BombowyConfig CONFIG = new BombowyConfig();

    public static KeyBinding openMenuKey;
    public static KeyBinding testKillEffectKey;
    public static KeyBinding triggerColdOwnerKey;

    @Override
    public void onInitializeClient() {
        openMenuKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.bombowyclient.menu",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_GRAVE_ACCENT,
            "category.bombowyclient.main"
        ));

        testKillEffectKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.bombowyclient.test_kill",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_K,
            "category.bombowyclient.main"
        ));

        triggerColdOwnerKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.bombowyclient.coldowner",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_H,
            "category.bombowyclient.main"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(this::onClientTick);
        HudRenderCallback.EVENT.register((ctx, tickCounter) -> {
            float tickDelta = tickCounter.getTickDelta(true);
            BombowyHud.render(ctx, tickDelta);
            KillEffects.renderHudOverlay(ctx, tickDelta);
        });
    }

    private void onClientTick(MinecraftClient client) {
        while (openMenuKey.wasPressed()) {
            if (client.currentScreen instanceof MenuScreen) {
                client.setScreen(null);
            } else {
                client.setScreen(new MenuScreen());
            }
        }

        while (testKillEffectKey.wasPressed()) {
            KillEffects.triggerTest();
        }

        while (triggerColdOwnerKey.wasPressed()) {
            BombowyHud.triggerColdOwner();
        }

        KillEffects.tick();
        BombowyHud.tick();
    }
}
