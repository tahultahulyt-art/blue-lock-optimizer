package com.rahultheog.bluelockoptimizer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.GraphicsMode;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.option.ParticlesMode;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class BlueLockOptimizerClient implements ClientModInitializer {

    private static KeyBinding toggleFpsHudKey;
    private static KeyBinding togglePvpHudKey;
    private static KeyBinding togglePerfModeKey;

    @Override
    public void onInitializeClient() {
        Config.load();

        toggleFpsHudKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.bluelockoptimizer.toggle_fps_hud",
                InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "key.category.bluelockoptimizer"));
        togglePvpHudKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.bluelockoptimizer.toggle_pvp_hud",
                InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "key.category.bluelockoptimizer"));
        togglePerfModeKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.bluelockoptimizer.toggle_performance_mode",
                InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "key.category.bluelockoptimizer"));

        HudRenderCallback.EVENT.register((context, tickDelta) -> {
            FpsHud.onFrame();
            MinecraftClient client = MinecraftClient.getInstance();
            FpsHud.render(context, client);
            PvpHud.render(context, client);
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleFpsHudKey.wasPressed()) {
                Config.get().showFpsHud = !Config.get().showFpsHud;
                Config.save();
            }
            while (togglePvpHudKey.wasPressed()) {
                Config.get().showPvpHud = !Config.get().showPvpHud;
                Config.save();
            }
            while (togglePerfModeKey.wasPressed()) {
                PerformanceManager.toggle();
            }
            PvpHud.onTick(client);
            PerformanceManager.onTick(client);
        });

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            if (client.player != null) {
                client.player.sendMessage(
                        Text.literal("Blue Lock Optimizer loaded - by rahul_the_og"), false);
            }
        });
    }

    static class Config {
        private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
        private static final Path PATH =
                FabricLoader.getInstance().getConfigDir().resolve("blue_lock_optimizer.json");
        private static Config instance;

        boolean showFpsHud = true;
        boolean showPvpHud = true;
        boolean dynamicFpsEnabled = true;
        int unfocusedFpsCap = 15;

        static Config get() {
            if (instance == null) load();
            return instance;
        }

        static void load() {
            try {
                if (Files.exists(PATH)) {
                    instance = GSON.fromJson(Files.readString(PATH), Config.class);
                }
            } catch (IOException | RuntimeException ignored) {
            }
            if (instance == null) instance = new Config();
            save();
        }

        static void save() {
            try {
                Files.createDirectories(PATH.getParent());
                Files.writeString(PATH, GSON.toJson(instance));
            } catch (IOException ignored) {
            }
        }
    }

    static class FpsHud {
        private static int frameCount = 0;
        private static int fps = 0;
        private static long lastSampleTime = System.currentTimeMillis();

        static void onFrame() {
            frameCount++;
            long now = System.currentTimeMillis();
            if (now - lastSampleTime >= 1000) {
                fps = frameCount;
                frameCount = 0;
                lastSampleTime = now;
            }
        }

        static void render(DrawContext context, MinecraftClient client) {
            if (!Config.get().showFpsHud) return;
            int color = fps >= 60 ? 0x55FF55 : fps >= 30 ? 0xFFFF55 : 0xFF5555;
            context.drawTextWithShadow(client.textRenderer, "FPS: " + fps, 4, 4, color);
        }
    }

    static class PvpHud {
        private static int leftClicksThisSecond = 0;
        private static int rightClicksThisSecond = 0;
        private static int leftCps = 0;
        private static int rightCps = 0;
        private static long lastCpsSampleTime = System.currentTimeMillis();
        private static boolean prevAttackDown = false;
        private static boolean prevUseDown = false;

        static void onTick(MinecraftClient client) {
            GameOptions options = client.options;
            boolean attackDown = options.attackKey.isPressed();
            boolean useDown = options.useKey.isPressed();

            if (attackDown && !prevAttackDown) leftClicksThisSecond++;
            if (useDown && !prevUseDown) rightClicksThisSecond++;
            prevAttackDown = attackDown;
            prevUseDown = useDown;

            long now = System.currentTimeMillis();
            if (now - lastCpsSampleTime >= 1000) {
                leftCps = leftClicksThisSecond;
                rightCps = rightClicksThisSecond;
                leftClicksThisSecond = 0;
                rightClicksThisSecond = 0;
                lastCpsSampleTime = now;
            }
        }

        static void render(DrawContext context, MinecraftClient client) {
            if (!Config.get().showPvpHud) return;

            int y = 16;
            context.drawTextWithShadow(client.textRenderer,
                    "CPS: L" + leftCps + " / R" + rightCps, 4, y, 0x55FF55);
            y += 10;

            if (client.getNetworkHandler() != null && client.player != null) {
                PlayerListEntry entry =
                        client.getNetworkHandler().getPlayerListEntry(client.player.getUuid());
                int ping = entry != null ? entry.getLatency() : -1;
                context.drawTextWithShadow(client.textRenderer,
                        "Ping: " + (ping >= 0 ? ping + "ms" : "--"), 4, y, 0x55AAFF);
                y += 10;
            }

            if (client.player != null) {
                float cooldown = client.player.getAttackCooldownProgress(0.0f);
                int barWidth = 40;
                int filled = (int) (barWidth * cooldown);
                context.fill(4, y, 4 + barWidth, y + 3, 0x55000000);
                context.fill(4, y, 4 + filled, y + 3, cooldown >= 1.0f ? 0xFF55FF55 : 0xFFFFAA00);
            }
        }
    }

    static class PerformanceManager {
        private static boolean enabled = false;
        private static ParticlesMode prevParticles;
        private static GraphicsMode prevGraphics;
        private static int prevViewDistance;
        private static double prevEntityDistance;

        static void toggle() {
            MinecraftClient client = MinecraftClient.getInstance();
            GameOptions options = client.options;
            enabled = !enabled;

            if (enabled) {
                prevParticles = options.getParticles().getValue();
                prevGraphics = options.getGraphicsMode().getValue();
                prevViewDistance = options.getViewDistance().getValue();
                prevEntityDistance = options.getEntityDistanceScaling().getValue();

                options.getParticles().setValue(ParticlesMode.MINIMAL);
                options.getGraphicsMode().setValue(GraphicsMode.FAST);
                options.getViewDistance().setValue(Math.min(prevViewDistance, 8));
                options.getEntityDistanceScaling().setValue(Math.min(prevEntityDistance, 1.0));
            } else {
                options.getParticles().setValue(prevParticles);
                options.getGraphicsMode().setValue(prevGraphics);
                options.getViewDistance().setValue(prevViewDistance);
                options.getEntityDistanceScaling().setValue(prevEntityDistance);
            }

            if (client.world != null) client.worldRenderer.reload();
            if (client.player != null) {
                client.player.sendMessage(
                        Text.literal("Performance Mode: " + (enabled ? "ON" : "OFF")), true);
            }
        }

        private static boolean wasFocused = true;
        private static int savedMaxFps = -1;

        static void onTick(MinecraftClient client) {
            if (!Config.get().dynamicFpsEnabled) return;

            boolean focused = client.isWindowFocused();
            if (focused != wasFocused) {
                GameOptions options = client.options;
                if (!focused) {
                    savedMaxFps = options.getMaxFps().getValue();
                    options.getMaxFps().setValue(Config.get().unfocusedFpsCap);
                } else if (savedMaxFps != -1) {
                    options.getMaxFps().setValue(savedMaxFps);
                    savedMaxFps = -1;
                }
                wasFocused = focused;
            }
        }
    }
}
