package com.rahultheog.bluelockoptimizer;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class ModMenuScreen extends Screen {

    // =========================================================
    // COLORS
    // =========================================================

    private static final int BG = 0xFF050A12;
    private static final int PANEL = 0xF20A1422;
    private static final int PANEL_2 = 0xFF0D1B2B;

    private static final int BLUE = 0xFF087FFF;
    private static final int LIGHT_BLUE = 0xFF38C6FF;

    private static final int WHITE = 0xFFEAF6FF;
    private static final int TEXT = 0xFFB9D4EA;
    private static final int MUTED = 0xFF6F91AC;

    // =========================================================
    // PANEL
    // =========================================================

    private int panelX;
    private int panelY;
    private int panelW;
    private int panelH;

    private int selectedTab = 0;

    // =========================================================
    // ANIMATION
    // =========================================================

    private long menuOpenTime;

    private float openAnimation = 0.0f;
    private float logoPulse = 0.0f;

    private static final int ANIMATION_DURATION = 220;

    // =========================================================
    // MODULES
    // =========================================================

    private final List<ModuleButton> modules = new ArrayList<>();

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public ModMenuScreen() {
        super(Text.literal("BlueLock Optimizer"));

        menuOpenTime = System.currentTimeMillis();

        modules.add(new ModuleButton(
                "Armor HUD",
                "Display your armor status.",
                0
        ));

        modules.add(new ModuleButton(
                "Full Bright",
                "Increase world brightness.",
                1
        ));

        modules.add(new ModuleButton(
                "Coordinates",
                "Show XYZ coordinates.",
                2
        ));

        modules.add(new ModuleButton(
                "CPS Counter",
                "Show clicks per second.",
                3
        ));

        modules.add(new ModuleButton(
                "Potion Counter",
                "Show active potion effects.",
                4
        ));

        modules.add(new ModuleButton(
                "Keystrokes",
                "Display movement keys.",
                5
        ));

        modules.add(new ModuleButton(
                "FPS",
                "Display current FPS.",
                6
        ));

        modules.add(new ModuleButton(
                "Zoom",
                "Zoom your Minecraft view.",
                7
        ));

        modules.add(new ModuleButton(
                "Toggle Sprint",
                "Automatically sprint.",
                8
        ));
    }

    // =========================================================
    // INIT
    // =========================================================

    @Override
    protected void init() {

        panelW = Math.min(1100, width - 50);
        panelH = Math.min(650, height - 40);

        panelX = (width - panelW) / 2;
        panelY = (height - panelH) / 2;
    }

    // =========================================================
    // RENDER
    // =========================================================

    @Override
    public void render(
            DrawContext context,
            int mouseX,
            int mouseY,
            float delta
    ) {

        long elapsed =
                System.currentTimeMillis() - menuOpenTime;

        float animation =
                Math.min(
                        1.0f,
                        elapsed / (float) ANIMATION_DURATION
                );

        openAnimation =
                1.0f -
                        (float) Math.pow(
                                1.0f - animation,
                                3
                        );

        logoPulse += delta * 0.08f;

        // Background
        context.fill(
                0,
                0,
                width,
                height,
                BG
        );

        // Animated panel position
        int animatedY =
                panelY +
                        (int)
                                ((1.0f - openAnimation) * 22);

        // Ambient glow
        context.fill(
                panelX - 6,
                animatedY - 6,
                panelX + panelW + 6,
                animatedY + panelH + 6,
                0x44008CFF
        );

        drawPanel(
                context,
                panelX,
                animatedY,
                panelW,
                panelH
        );

        drawHeader(
                context,
                panelX,
                animatedY
        );

        drawSidebar(
                context,
                panelX,
                animatedY,
                mouseX,
                mouseY
        );

        if (selectedTab == 0) {

            drawModules(
                    context,
                    panelX,
                    animatedY,
                    mouseX,
                    mouseY
            );

        } else if (selectedTab == 1) {

            drawHUD(
                    context,
                    panelX,
                    animatedY
            );

        } else {

            drawSettings(
                    context,
                    panelX,
                    animatedY
            );
        }

        super.render(
                context,
                mouseX,
                mouseY,
                delta
        );
    }

    // =========================================================
    // PANEL
    // =========================================================

    private void drawPanel(
            DrawContext context,
            int x,
            int y,
            int w,
            int h
    ) {

        context.fill(
                x,
                y,
                x + w,
                y + h,
                PANEL
        );

        context.fill(
                x,
                y,
                x + w,
                y + 1,
                BLUE
        );

        context.fill(
                x,
                y + h - 1,
                x + w,
                y + h,
                0xFF12304A
        );

        context.fill(
                x,
                y,
                x + 1,
                y + h,
                0xFF12304A
        );

        context.fill(
                x + w - 1,
                y,
                x + w,
                y + h,
                0xFF12304A
        );
    }

    // =========================================================
    // HEADER
    // =========================================================

    private void drawHeader(
            DrawContext context,
            int x,
            int y
    ) {

        drawLogo(
                context,
                x + 25,
                y + 18
        );

        context.drawText(
                textRenderer,
                Text.literal("BLUECORE"),
                x + 72,
                y + 25,
                WHITE,
                true
        );

        context.drawText(
                textRenderer,
                Text.literal("OPTIMIZER"),
                x + 72,
                y + 40,
                BLUE,
                false
        );

        context.fill(
                x + 20,
                y + 70,
                x + panelW - 20,
                y + 71,
                0xFF12304A
        );

        drawSearchIcon(
                context,
                x + panelW - 80,
                y + 27
        );

        context.drawText(
                textRenderer,
                Text.literal("⚙"),
                x + panelW - 45,
                y + 23,
                LIGHT_BLUE,
                true
        );
    }

    // =========================================================
    // LOGO
    // =========================================================

    private void drawLogo(
            DrawContext context,
            int x,
            int y
    ) {

        float pulse =
                (float) Math.sin(logoPulse);

        int glowAlpha =
                45 +
                        (int)
                                ((pulse + 1.0f) * 20);

        int glow =
                (glowAlpha << 24) |
                        0x087FFF;

        // Glow
        context.fill(
                x - 5,
                y - 5,
                x + 38,
                y + 43,
                glow
        );

        // Main logo
        context.fill(
                x + 8,
                y,
                x + 25,
                y + 38,
                BLUE
        );

        context.fill(
                x,
                y + 8,
                x + 33,
                y + 30,
                BLUE
        );

        // Center cut
        context.fill(
                x + 8,
                y + 8,
                x + 25,
                y + 30,
                0xFF07182B
        );

        // Bright center
        context.fill(
                x + 13,
                y + 4,
                x + 20,
                y + 34,
                LIGHT_BLUE
        );
    }

    // =========================================================
    // SIDEBAR
    // =========================================================

    private void drawSidebar(
            DrawContext context,
            int x,
            int y,
            int mouseX,
            int mouseY
    ) {

        int sidebarX = x + 20;
        int sidebarY = y + 95;

        drawTab(
                context,
                sidebarX,
                sidebarY,
                "Modules",
                0,
                mouseX,
                mouseY
        );

        drawTab(
                context,
                sidebarX,
                sidebarY + 55,
                "HUD",
                1,
                mouseX,
                mouseY
        );

        drawTab(
                context,
                sidebarX,
                sidebarY + 110,
                "Settings",
                2,
                mouseX,
                mouseY
        );
    }

    private void drawTab(
            DrawContext context,
            int x,
            int y,
            String name,
            int id,
            int mouseX,
            int mouseY
    ) {

        boolean selected =
                selectedTab == id;

        boolean hover =
                mouseX >= x &&
                        mouseX <= x + 150 &&
                        mouseY >= y &&
                        mouseY <= y + 40;

        if (selected) {

            context.fill(
                    x,
                    y,
                    x + 150,
                    y + 40,
                    0xFF102C45
            );

            context.fill(
                    x,
                    y,
                    x + 3,
                    y + 40,
                    BLUE
            );

        } else if (hover) {

            context.fill(
                    x,
                    y,
                    x + 150,
                    y + 40,
                    0xFF0C2032
            );
        }

        context.drawText(
                textRenderer,
                Text.literal(name),
                x + 15,
                y + 14,
                selected ? LIGHT_BLUE : TEXT,
                selected
        );
    }

    // =========================================================
    // MODULES
    // =========================================================

    private void drawModules(
            DrawContext context,
            int x,
            int y,
            int mouseX,
            int mouseY
    ) {

        int startX = x + 200;
        int startY = y + 100;

        int cardW = 245;
        int cardH = 135;

        for (int i = 0; i < modules.size(); i++) {

            ModuleButton module =
                    modules.get(i);

            int column = i % 3;
            int row = i / 3;

            int cardX =
                    startX +
                            column * (cardW + 15);

            int cardY =
                    startY +
                            row * (cardH + 15);

            drawModuleCard(
                    context,
                    module,
                    cardX,
                    cardY,
                    cardW,
                    cardH,
                    mouseX,
                    mouseY
            );
        }
    }

    private void drawModuleCard(
            DrawContext context,
            ModuleButton module,
            int x,
            int y,
            int w,
            int h,
            int mouseX,
            int mouseY
    ) {

        boolean hover =
                mouseX >= x &&
                        mouseX <= x + w &&
                        mouseY >= y &&
                        mouseY <= y + h;

        boolean enabled =
                module.isEnabled();

        int background =
                hover
                        ? 0xFF102A40
                        : PANEL_2;

        context.fill(
                x,
                y,
                x + w,
                y + h,
                background
        );

        context.fill(
                x,
                y,
                x + w,
                y + 1,
                enabled
                        ? BLUE
                        : 0xFF18334A
        );

        // Icon
        context.fill(
                x + 15,
                y + 15,
                x + 50,
                y + 50,
                0xFF102E49
        );

        drawIcon(
                context,
                module.name,
                x + 32,
                y + 32,
                enabled
                        ? LIGHT_BLUE
                        : MUTED
        );

        // Name
        context.drawText(
                textRenderer,
                Text.literal(module.name),
                x + 65,
                y + 19,
                WHITE,
                true
        );

        // Description
        drawDescription(
                context,
                module.description,
                x + 15,
                y + 67
        );

        // Toggle
        int toggleX = x + w - 58;
        int toggleY = y + h - 45;

        context.fill(
                toggleX,
                toggleY,
                toggleX + 40,
                toggleY + 22,
                enabled
                        ? BLUE
                        : 0xFF20394D
        );

        if (enabled) {

            context.fill(
                    toggleX + 22,
                    toggleY + 3,
                    toggleX + 37,
                    toggleY + 19,
                    LIGHT_BLUE
            );

        } else {

            context.fill(
                    toggleX + 3,
                    toggleY + 3,
                    toggleX + 18,
                    toggleY + 19,
                    0xFF607589
            );
        }
    }

    // =========================================================
    // HUD
    // =========================================================

    private void drawHUD(
            DrawContext context,
            int x,
            int y
    ) {

        int startX = x + 205;
        int startY = y + 105;

        drawSimpleSetting(
                context,
                startX,
                startY,
                "FPS Counter",
                "Show current FPS.",
                BlueLockOptimizerClient.CONFIG.fpsHudEnabled
        );

        drawSimpleSetting(
                context,
                startX,
                startY + 75,
                "Coordinates",
                "Show XYZ coordinates.",
                BlueLockOptimizerClient.CONFIG.coordinatesEnabled
        );

        drawSimpleSetting(
                context,
                startX,
                startY + 150,
                "CPS Counter",
                "Show clicks per second.",
                BlueLockOptimizerClient.CONFIG.cpsCounterEnabled
        );

        drawSimpleSetting(
                context,
                startX,
                startY + 225,
                "Armor HUD",
                "Display armor status.",
                BlueLockOptimizerClient.CONFIG.armorHudEnabled
        );
    }

    // =========================================================
    // SETTINGS
    // =========================================================

    private void drawSettings(
            DrawContext context,
            int x,
            int y
    ) {

        int startX = x + 205;
        int startY = y + 105;

        drawSimpleSetting(
                context,
                startX,
                startY,
                "Performance Mode",
                "Reduce visual effects.",
                BlueLockOptimizerClient.CONFIG.performanceMode
        );

        drawSimpleSetting(
                context,
                startX,
                startY + 75,
                "Full Bright",
                "Increase world brightness.",
                BlueLockOptimizerClient.CONFIG.fullBrightEnabled
        );

        drawSimpleSetting(
                context,
                startX,
                startY + 150,
                "Toggle Sprint",
                "Automatically sprint.",
                BlueLockOptimizerClient.CONFIG.toggleSprintEnabled
        );

        drawSimpleSetting(
                context,
                startX,
                startY + 225,
                "Zoom",
                "Zoom your Minecraft view.",
                BlueLockOptimizerClient.CONFIG.zoomEnabled
        );
    }

    // =========================================================
    // SIMPLE SETTING
    // =========================================================

    private void drawSimpleSetting(
            DrawContext context,
            int x,
            int y,
            String name,
            String description,
            boolean enabled
    ) {

        context.fill(
                x,
                y,
                x + 520,
                y + 60,
                PANEL_2
        );

        context.fill(
                x,
                y,
                x + 3,
                y + 60,
                enabled
                        ? BLUE
                        : 0xFF18334A
        );

        context.drawText(
                textRenderer,
                Text.literal(name),
                x + 18,
                y + 14,
                WHITE,
                true
        );

        context.drawText(
                textRenderer,
                Text.literal(description),
                x + 18,
                y + 33,
                MUTED,
                false
        );

        context.fill(
                x + 465,
                y + 19,
                x + 505,
                y + 41,
                enabled
                        ? BLUE
                        : 0xFF20394D
        );

        if (enabled) {

            context.fill(
                    x + 487,
                    y + 22,
                    x + 502,
                    y + 38,
                    LIGHT_BLUE
            );

        } else {

            context.fill(
                    x + 468,
                    y + 22,
                    x + 483,
                    y + 38,
                    0xFF607589
            );
        }
    }

    // =========================================================
    // ICON
    // =========================================================

    private void drawIcon(
            DrawContext context,
            String name,
            int x,
            int y,
            int color
    ) {

        if (name.equals("FPS")) {

            context.drawText(
                    textRenderer,
                    Text.literal("FPS"),
                    x - 13,
                    y - 5,
                    color,
                    true
            );

        } else if (name.equals("Zoom")) {

            context.drawText(
                    textRenderer,
                    Text.literal("+"),
                    x - 5,
                    y - 7,
                    color,
                    true
            );

        } else if (name.equals("CPS Counter")) {

            context.drawText(
                    textRenderer,
                    Text.literal("CPS"),
                    x - 12,
                    y - 5,
                    color,
                    true
            );

        } else {

            context.drawText(
                    textRenderer,
                    Text.literal("◆"),
                    x - 5,
                    y - 7,
                    color,
                    true
            );
        }
    }

    // =========================================================
    // SEARCH ICON
    // =========================================================

    private void drawSearchIcon(
            DrawContext context,
            int x,
            int y
    ) {

        context.fill(
                x,
                y,
                x + 14,
                y + 3,
                LIGHT_BLUE
        );

        context.fill(
                x - 2,
                y + 3,
                x + 16,
                y + 14,
                LIGHT_BLUE
        );

        context.fill(
                x + 2,
                y + 5,
                x + 12,
                y + 12,
                BG
        );

        context.fill(
                x + 13,
                y + 13,
                x + 19,
                y + 19,
                LIGHT_BLUE
        );
    }

    // =========================================================
    // DESCRIPTION
    // =========================================================

    private void drawDescription(
            DrawContext context,
            String text,
            int x,
            int y
    ) {

        String[] words =
                text.split(" ");

        String line = "";

        int lineY = y;

        for (String word : words) {

            String test =
                    line.isEmpty()
                            ? word
                            : line + " " + word;

            if (textRenderer.getWidth(test) > 205) {

                context.drawText(
                        textRenderer,
                        Text.literal(line),
                        x,
                        lineY,
                        MUTED,
                        false
                );

                line = word;
                lineY += 12;

            } else {

                line = test;
            }
        }

        if (!line.isEmpty()) {

            context.drawText(
                    textRenderer,
                    Text.literal(line),
                    x,
                    lineY,
                    MUTED,
                    false
            );
        }
    }

    // =========================================================
    // MOUSE
    // =========================================================

    @Override
    public boolean mouseClicked(
            double mouseX,
            double mouseY,
            int button
    ) {

        if (button != 0) {
            return super.mouseClicked(
                    mouseX,
                    mouseY,
                    button
            );
        }

        int actualY = panelY;

        // Sidebar
        int sidebarX = panelX + 20;
        int sidebarY = actualY + 95;

        if (mouseX >= sidebarX &&
                mouseX <= sidebarX + 150) {

            if (mouseY >= sidebarY &&
                    mouseY <= sidebarY + 40) {

                selectedTab = 0;
                return true;
            }

            if (mouseY >= sidebarY + 55 &&
                    mouseY <= sidebarY + 95) {

                selectedTab = 1;
                return true;
            }

            if (mouseY >= sidebarY + 110 &&
                    mouseY <= sidebarY + 150) {

                selectedTab = 2;
                return true;
            }
        }

        // Modules
        if (selectedTab == 0) {

            int startX = panelX + 200;
            int startY = actualY + 100;

            int cardW = 245;
            int cardH = 135;

            for (int i = 0; i < modules.size(); i++) {

                ModuleButton module =
                        modules.get(i);

                int column = i % 3;
                int row = i / 3;

                int cardX =
                        startX +
                                column * (cardW + 15);

                int cardY =
                        startY +
                                row * (cardH + 15);

                int toggleX =
                        cardX + cardW - 58;

                int toggleY =
                        cardY + cardH - 45;

                if (mouseX >= toggleX &&
                        mouseX <= toggleX + 40 &&
                        mouseY >= toggleY &&
                        mouseY <= toggleY + 22) {

                    module.toggle();

                    return true;
                }
            }
        }

        return super.mouseClicked(
                mouseX,
                mouseY,
                button
        );
    }

    // =========================================================
    // KEYBOARD
    // =========================================================

    @Override
    public boolean keyPressed(
            int keyCode,
            int scanCode,
            int modifiers
    ) {

        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {

            close();

            return true;
        }

        return super.keyPressed(
                keyCode,
                scanCode,
                modifiers
        );
    }

    // =========================================================
    // SCREEN BEHAVIOR
    // =========================================================

    @Override
    public boolean shouldPause() {
        return false;
    }

    // =========================================================
    // MODULE DATA
    // =========================================================

    private static class ModuleButton {

        private final String name;
        private final String description;
        private final int configId;

        public ModuleButton(
                String name,
                String description,
                int configId
        ) {

            this.name = name;
            this.description = description;
            this.configId = configId;
        }

        public boolean isEnabled() {

            switch (configId) {

                case 0:
                    return BlueLockOptimizerClient.CONFIG.armorHudEnabled;

                case 1:
                    return BlueLockOptimizerClient.CONFIG.fullBrightEnabled;

                case 2:
                    return BlueLockOptimizerClient.CONFIG.coordinatesEnabled;

                case 3:
                    return BlueLockOptimizerClient.CONFIG.cpsCounterEnabled;

                case 4:
                    return BlueLockOptimizerClient.CONFIG.potionCounterEnabled;

                case 5:
                    return BlueLockOptimizerClient.CONFIG.keystrokesEnabled;

                case 6:
                    return BlueLockOptimizerClient.CONFIG.fpsHudEnabled;

                case 7:
                    return BlueLockOptimizerClient.CONFIG.zoomEnabled;

                case 8:
                    return BlueLockOptimizerClient.CONFIG.toggleSprintEnabled;

                default:
                    return false;
            }
        }

        public void toggle() {

            switch (configId) {

                case 0:
                    BlueLockOptimizerClient.CONFIG.armorHudEnabled =
                            !BlueLockOptimizerClient.CONFIG.armorHudEnabled;
                    break;

                case 1:
                    BlueLockOptimizerClient.CONFIG.fullBrightEnabled =
                            !BlueLockOptimizerClient.CONFIG.fullBrightEnabled;
                    break;

                case 2:
                    BlueLockOptimizerClient.CONFIG.coordinatesEnabled =
                            !BlueLockOptimizerClient.CONFIG.coordinatesEnabled;
                    break;

                case 3:
                    BlueLockOptimizerClient.CONFIG.cpsCounterEnabled =
                            !BlueLockOptimizerClient.CONFIG.cpsCounterEnabled;
                    break;

                case 4:
                    BlueLockOptimizerClient.CONFIG.potionCounterEnabled =
                            !BlueLockOptimizerClient.CONFIG.potionCounterEnabled;
                    break;

                case 5:
                    BlueLockOptimizerClient.CONFIG.keystrokesEnabled =
                            !BlueLockOptimizerClient.CONFIG.keystrokesEnabled;
                    break;

                case 6:
                    BlueLockOptimizerClient.CONFIG.fpsHudEnabled =
                            !BlueLockOptimizerClient.CONFIG.fpsHudEnabled;
                    break;

                case 7:
                    BlueLockOptimizerClient.CONFIG.zoomEnabled =
                            !BlueLockOptimizerClient.CONFIG.zoomEnabled;
                    break;

                case 8:
                    BlueLockOptimizerClient.CONFIG.toggleSprintEnabled =
                            !BlueLockOptimizerClient.CONFIG.toggleSprintEnabled;
                    break;
            }
        }
    }
}
