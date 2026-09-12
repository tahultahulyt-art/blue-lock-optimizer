package com.rahultheog.bluelockoptimizer;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class BlueLockOptimizerClient implements ClientModInitializer {

    public static class Config {
        public boolean fpsHudEnabled       = true;
        public boolean pvpHudEnabled       = true;
        public boolean armorHudEnabled     = false;
        public boolean fullBrightEnabled   = false;
        public boolean coordinatesEnabled  = false;
        public boolean cpsCounterEnabled   = false;
        public boolean potionCounterEnabled= false;
        public boolean keystrokesEnabled   = false;
        public boolean zoomEnabled         = false;
        public boolean toggleSprintEnabled = false;
        public boolean performanceMode     = false;
    }

    public static final Config CONFIG = new Config();

    private static int fps = 0;
    private static int cps = 0;
    private static int cpsClickCount = 0;
    private static long cpsWindowStart = System.currentTimeMillis();
    private static boolean wasRightDown = false;
    private static boolean wasF3Down = false;

    public static class FpsHud {
        public void render(DrawContext ctx, float tickDelta) {
            if (!CONFIG.fpsHudEnabled) return;
            MinecraftClient mc = MinecraftClient.getInstance();
            fps = mc.getCurrentFps();
            int color = fps >= 60 ? 0xFF00FF99 : fps >= 30 ? 0xFFFFFF00 : 0xFFFF4444;
            ctx.drawTextWithShadow(mc.textRenderer, Text.literal("FPS: " + fps), 4, 4, color);
        }
    }

    public static class PvpHud {
        public void render(DrawContext ctx, float tickDelta) {
            if (!CONFIG.pvpHudEnabled) return;
            MinecraftClient mc = MinecraftClient.getInstance();
            int ping = 0;
            if (mc.player != null && mc.getNetworkHandler() != null &&
                mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid()) != null) {
                ping = mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid()).getLatency();
            }
            ctx.drawTextWithShadow(mc.textRenderer,
                Text.literal("CPS: " + cps + "  PING: " + ping + "ms"), 4, 14, 0xFF00CFFF);
        }
    }

    public static class PerformanceManager {
        public void tick(MinecraftClient mc) {
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

    private final FpsHud fpsHud = new FpsHud();
    private final PvpHud pvpHud = new PvpHud();
    private final PerformanceManager perfMgr = new PerformanceManager();

    @Override
    public void onInitializeClient() {

        HudRenderCallback.EVENT.register((ctx, tickDelta) -> {
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc.currentScreen != null) return;
            fpsHud.render(ctx, tickDelta);
            pvpHud.render(ctx, tickDelta);
            renderCoordinates(ctx, mc);
        });

        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            if (mc.player == null) return;

            perfMgr.tick(mc);
            updateCps(mc);

            boolean rightNow = InputUtil.isKeyPressed(
                mc.getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_RIGHT);

            boolean menuContext = (mc.crosshairTarget == null ||
                mc.crosshairTarget.getType().name().equals("MISS"));

            if (rightNow && !wasRightDown && menuContext && mc.currentScreen == null) {
                mc.setScreen(new ModMenuScreen());
            }
            wasRightDown = rightNow;

            boolean f3Now = InputUtil.isKeyPressed(
                mc.getWindow().getHandle(), GLFW.GLFW_KEY_F3);
            if (f3Now && !wasF3Down) {
                CONFIG.performanceMode = !CONFIG.performanceMode;
                mc.player.sendMessage(
                    Text.literal("§b[BlueLock] §fPerformance Mode: " +
                    (CONFIG.performanceMode ? "§aON" : "§cOFF")), true);
            }
            wasF3Down = f3Now;
        });
    }

    private void updateCps(MinecraftClient mc) {
        long now = System.currentTimeMillis();
        if (now - cpsWindowStart >= 1000) {
            cps = cpsClickCount;
            cpsClickCount = 0;
            cpsWindowStart = now;
        }
    }

    private void renderCoordinates(DrawContext ctx, MinecraftClient mc) {
        if (!CONFIG.coordinatesEnabled || mc.player == null) return;
        double x = mc.player.getX();
        double y = mc.player.getY();
        double z = mc.player.getZ();
        ctx.drawTextWithShadow(mc.textRenderer,
            Text.literal(String.format("XYZ: %.1f / %.1f / %.1f", x, y, z)),
            4, 24, 0xFF00CFFF);
    }
}
