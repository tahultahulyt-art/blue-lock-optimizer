package com.rahultheog.bluelockoptimizer;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class BlueLockOptimizerClient implements ClientModInitializer {

    public static KeyBinding OPEN_MENU;

    public static class Config {
        public boolean fpsHudEnabled        = true;
        public boolean pvpHudEnabled        = true;
        public boolean armorHudEnabled      = true;
        public boolean fullBrightEnabled    = true;
        public boolean coordinatesEnabled   = true;
        public boolean cpsCounterEnabled    = true;
        public boolean potionCounterEnabled = false;
        public boolean keystrokesEnabled    = true;
        public boolean zoomEnabled          = false;
        public boolean toggleSprintEnabled  = true;
        public boolean performanceMode      = false;
    }

    public static final Config CONFIG = new Config();

    private static int fps = 0;
    private static int cps = 0;
    private static long cpsWindowStart = System.currentTimeMillis();

    @Override
    public void onInitializeClient() {

        OPEN_MENU = KeyBindingHelper.registerKeyBinding(
            new KeyBinding(
                "key.bluelockoptimizer.open_menu",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_M,
                "category.bluelockoptimizer"
            )
        );

        HudRenderCallback.EVENT.register((ctx, tickDelta) -> {
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc.currentScreen != null) return;
            renderFps(ctx, mc);
            renderCoordinates(ctx, mc);
            renderCps(ctx, mc);
        });

        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            if (mc.player == null) return;
            updateCps(mc);
            handleFullBright(mc);
            while (OPEN_MENU.wasPressed()) {
                if (mc.currentScreen instanceof ModMenuScreen) {
                    mc.setScreen(null);
                } else {
                    mc.setScreen(new ModMenuScreen());
                }
            }
        });
    }

    private void renderFps(DrawContext ctx, MinecraftClient mc) {
        if (!CONFIG.fpsHudEnabled) return;
        fps = mc.getCurrentFps();
        int color = fps >= 60 ? 0xFF00FF99 : fps >= 30 ? 0xFFFFFF00 : 0xFFFF4444;
        ctx.drawTextWithShadow(mc.textRenderer, Text.literal("FPS: " + fps), 4, 4, color);
    }

    private void renderCoordinates(DrawContext ctx, MinecraftClient mc) {
        if (!CONFIG.coordinatesEnabled || mc.player == null) return;
        String coords = String.format("XYZ: %.1f / %.1f / %.1f",
            mc.player.getX(), mc.player.getY(), mc.player.getZ());
        ctx.drawTextWithShadow(mc.textRenderer, Text.literal(coords), 4, 14, 0xFF00CFFF);
    }

    private void renderCps(DrawContext ctx, MinecraftClient mc) {
        if (!CONFIG.cpsCounterEnabled) return;
        ctx.drawTextWithShadow(mc.textRenderer,
            Text.literal("CPS: " + cps), 4, 24, 0xFF00CFFF);
    }

    private void updateCps(MinecraftClient mc) {
        long now = System.currentTimeMillis();
        if (now - cpsWindowStart >= 1000) {
            cps = 0;
            cpsWindowStart = now;
        }
    }

    private void handleFullBright(MinecraftClient mc) {
        if (mc.options == null) return;
        if (CONFIG.fullBrightEnabled) {
            mc.options.getGamma().setValue(10.0);
        } else {
            if (mc.options.getGamma().getValue() == 10.0) {
                mc.options.getGamma().setValue(1.0);
            }
        }
    }
}
