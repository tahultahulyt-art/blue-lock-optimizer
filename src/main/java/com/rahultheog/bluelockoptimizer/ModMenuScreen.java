package com.rahultheog.bluelockoptimizer;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class ModMenuScreen extends Screen {

    // =========================================================
    // BLUECORE COLORS
    // =========================================================

    private static final int BACKGROUND = 0xFF050A12;
    private static final int PANEL = 0xF20A1422;
    private static final int PANEL_DARK = 0xFF0B1725;
    private static final int CARD = 0xFF0D1B2B;

    private static final int BLUE = 0xFF087FFF;
    private static final int LIGHT_BLUE = 0xFF38C6FF;

    private static final int WHITE = 0xFFEAF6FF;
    private static final int TEXT = 0xFFB9D4EA;
    private static final int MUTED = 0xFF6F91AC;

    // =========================================================
    // BLAZE-STYLE MENU SIZE
    // =========================================================

    private static final int TARGET_WIDTH = 900;
    private static final int TARGET_HEIGHT = 500;

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

    /*
     * Blaze uses a smooth sine based opening animation.
     * We use the same idea here.
     */
    private static final int ANIMATION_DURATION = 750;

    // =========================================================
    // MODULES
    // =========================================================

    private final List<ModuleButton> modules =
            new ArrayList<>();

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public ModMenuScreen() {

        super(Text.literal("BlueLock Optimizer"));

        menuOpenTime =
                System.currentTimeMillis();

        // -----------------------------------------------------
        // EXISTING BLUELOCK MODULES
        // -----------------------------------------------------

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

        panelW =
                Math.min(
                        TARGET_WIDTH,
                        width - 40
                );

        panelH =
                Math.min(
                        TARGET_HEIGHT,
                        height - 40
                );

        panelX =
                (width - panelW) / 2;

        panelY =
                (height - panelH) / 2;
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

        // -----------------------------------------------------
        // ANIMATION PROGRESS
        // -----------------------------------------------------

        long elapsed =
                System.currentTimeMillis()
                        - menuOpenTime;

        float progress =
                Math.min(
                        1.0f,
                        elapsed /
                                (float) ANIMATION_DURATION
                );

        /*
         * Smooth sine easing.
         *
         * 0 -> 1
         *
         * Starts slowly,
         * becomes smooth,
         * finishes softly.
         */
        openAnimation =
                (float)
                        Math.sin(
                                progress *
                                        Math.PI /
                                        2.0
                        );

        // -----------------------------------------------------
        // LOGO ANIMATION
        // -----------------------------------------------------

        logoPulse +=
                delta * 0.08f;

        // -----------------------------------------------------
        // BACKGROUND
        // -----------------------------------------------------

        context.fill(
                0,
                0,
                width,
                height,
                BACKGROUND
        );

        // -----------------------------------------------------
        // ANIMATED SIZE
        // -----------------------------------------------------

        /*
         * The menu starts slightly smaller and grows
         * smoothly to 900x500.
         */
        float scale =
                0.92f +
                        0.08f *
                                openAnimation;

        int animatedW =
                (int)
                        (panelW * scale);

        int animatedH =
                (int)
                        (panelH * scale);

        int animatedX =
                panelX +
                        (panelW - animatedW) / 2;

        int animatedY =
                panelY +
                        (panelH - animatedH) / 2;

        // -----------------------------------------------------
        // SLIGHT UPWARD MOTION
        // -----------------------------------------------------

        animatedY +=
                (int)
                        ((1.0f -
                                openAnimation) *
                                12);

        // -----------------------------------------------------
        // BLUE AMBIENT GLOW
        // -----------------------------------------------------

        context.fill(
                animatedX - 7,
                animatedY - 7,
                animatedX + animatedW + 7,
                animatedY + animatedH + 7,
                0x55008CFF
        );

        // -----------------------------------------------------
        // MAIN PANEL
        // -----------------------------------------------------

        drawPanel(
                context,
                animatedX,
                animatedY,
                animatedW,
                animatedH
        );

        // -----------------------------------------------------
        // HEADER
        // -----------------------------------------------------

        drawHeader(
                context,
                animatedX,
                animatedY,
                animatedW
        );

        // -----------------------------------------------------
        // SIDEBAR
        // -----------------------------------------------------

        drawSidebar(
                context,
                animatedX,
                animatedY,
                mouseX,
                mouseY
        );

        // -----------------------------------------------------
        // CONTENT
        // -----------------------------------------------------

        if (selectedTab == 0) {

            drawModules(
                    context,
                    animatedX,
                    animatedY,
                    animatedW,
                    mouseX,
                    mouseY
            );

        } else if (selectedTab == 1) {

            drawHUD(
                    context,
                    animatedX,
                    animatedY,
                    animatedW
            );

        } else {

            drawSettings(
                    context,
                    animatedX,
                    animatedY,
                    animatedW
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

        // Top blue line
        context.fill(
                x,
                y,
                x + w,
                y + 2,
                BLUE
        );

        // Bottom border
        context.fill(
                x,
                y + h - 1,
                x + w,
                y + h,
                0xFF12304A
        );

        // Left border
        context.fill(
                x,
                y,
                x + 1,
                y + h,
                0xFF12304A
        );

        // Right border
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
            int y,
            int w
    ) {

        drawLogo(
                context,
                x + 25,
                y + 17
        );

        context.drawText(
                textRenderer,
                Text.literal("BLUECORE"),
                x + 70,
                y + 23,
                WHITE,
                true
        );

        context.drawText(
                textRenderer,
                Text.literal("OPTIMIZER"),
                x + 70,
                y + 39,
                BLUE,
                false
        );

        context.fill(
                x + 20,
                y + 70,
                x + w - 20,
                y + 71,
                0xFF12304A
        );

        drawSearchIcon(
                context,
                x + w - 78,
                y + 25
        );

        context.drawText(
                textRenderer,
                Text.literal("⚙"),
                x + w - 45,
                y + 22,
                LIGHT_BLUE,
                true
        );
    }

    // =========================================================
    // ANIMATED LOGO
    // =========================================================

    private void drawLogo(
            DrawContext context,
            int x,
            int y
    ) {

        float pulse =
                (float)
                        Math.sin(
                                logoPulse
                        );

        int alpha =
                40 +
                        (int)
                                ((pulse + 1.0f)
                                        * 18);

        int glow =
                (alpha << 24)
                        | 0x087FFF;

        // Glow
        context.fill(
                x - 6,
                y - 6,
                x + 39,
                y + 44,
                glow
        );

        // Main shape
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

        int sidebarX =
                x + 20;

        int sidebarY =
                y + 95;

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
                selected
                        ? LIGHT_BLUE
                        : TEXT,
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
            int w,
            int mouseX,
            int mouseY
    ) {

        int startX =
                x + 190;

        int startY =
                y + 95;

        int cardW =
                215;

        int cardH =
                125;

        for (int i = 0;
             i < modules.size();
             i++) {

            ModuleButton module =
                    modules.get(i);

            int column =
                    i % 3;

            int row =
                    i / 3;

            int cardX =
                    startX +
                            column *
                                    (cardW + 12);

            int cardY =
                    startY +
                            row *
                                    (cardH + 12);

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

        context.fill(
                x,
                y,
                x + w,
                y + h,
                hover
                        ? 0xFF102A40
                        : CARD
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

        // Icon box
        context.fill(
                x + 13,
                y + 13,
                x + 47,
                y + 47,
                0xFF102E49
        );

        drawIcon(
                context,
                module.name,
                x + 30,
                y + 30,
                enabled
                        ? LIGHT_BLUE
                        : MUTED
        );

        // Name
        context.drawText(
                textRenderer,
                Text.literal(module.name),
                x + 59,
                y + 17,
                WHITE,
                true
        );

        // Description
        drawDescription(
                context,
                module.description,
                x + 13,
                y + 62
        );

        // Toggle
        int toggleX =
                x + w - 53;

        int toggleY =
                y + h - 35;

        context.fill(
                toggleX,
                toggleY,
                toggleX + 38,
                toggleY + 20,
                enabled
                        ? BLUE
                        : 0xFF20394D
        );

        if (enabled) {

            context.fill(
                    toggleX + 21,
                    toggleY + 3,
                    toggleX + 35,
                    toggleY + 17,
                    LIGHT_BLUE
            );

        } else {

            context.fill(
                    toggleX + 3,
                    toggleY + 3,
                    toggleX + 17,
                    toggleY + 17,
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
            int y,
            int w
    ) {

        int startX =
                x + 190;

        int startY =
                y + 100;

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
                startY + 70,
                "Coordinates",
                "Show XYZ coordinates.",
                BlueLockOptimizerClient.CONFIG.coordinatesEnabled
        );

        drawSimpleSetting(
                context,
                startX,
                startY + 140,
                "CPS Counter",
                "Show clicks per second.",
                BlueLockOptimizerClient.CONFIG.cpsCounterEnabled
        );

        drawSimpleSetting(
                context,
                startX,
                startY + 210,
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
            int y,
            int w
    ) {

        int startX =
                x + 190;

        int startY =
                y + 100;

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
                startY + 70,
                "Full Bright",
                "Increase world brightness.",
                BlueLockOptimizerClient.CONFIG.fullBrightEnabled
        );

        drawSimpleSetting(
                context,
                startX,
                startY + 140,
                "Toggle Sprint",
                "Automatically sprint.",
                BlueLockOptimizerClient.CONFIG.toggleSprintEnabled
        );

        drawSimpleSetting(
                context,
                startX,
                startY + 210,
                "Zoom",
                "Zoom your Minecraft view.",
                BlueLockOptimizerClient.CONFIG.zoomEnabled
        );
    }

    // =========================================================
    // SETTING BOX
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
                x + 500,
                y + 55,
                CARD
        );

        context.fill(
                x,
                y,
                x + 3,
                y + 55,
                enabled
                        ? BLUE
                        : 0xFF18334A
        );

        context.drawText(
                textRenderer,
                Text.literal(name),
                x + 18,
                y + 12,
                WHITE,
                true
        );

        context.drawText(
                textRenderer,
                Text.literal(description),
                x + 18,
                y + 31,
                MUTED,
                false
        );

        context.fill(
                x + 450,
                y + 17,
                x + 490,
                y + 39,
                enabled
                        ? BLUE
                        : 0xFF20394D
        );

        if (enabled) {

            context.fill(
                    x + 472,
                    y + 20,
                    x + 487,
                    y + 36,
                    LIGHT_BLUE
            );

        } else {

            context.fill(
                    x + 453,
                    y + 20,
                    x + 468,
                    y + 36,
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
    // SEARCH
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
                BACKGROUND
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

            if (textRenderer.getWidth(test) > 185) {

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

        // -----------------------------------------------------
        // SIDEBAR
        // -----------------------------------------------------

        int sidebarX =
                panelX + 20;

        int sidebarY =
                panelY + 95;

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

        // -----------------------------------------------------
        // MODULE TOGGLES
        // -----------------------------------------------------

        if (selectedTab == 0) {

            int startX =
                    panelX + 190;

            int startY =
                    panelY + 95;

            int cardW =
                    215;

            int cardH =
                    125;

            for (int i = 0;
                 i < modules.size();
                 i++) {

                ModuleButton module =
                        modules.get(i);

                int column =
                        i % 3;

                int row =
                        i / 3;

                int cardX =
                        startX +
                                column *
                                        (cardW + 12);

                int cardY =
                        startY +
                                row *
                                        (cardH + 12);

                int toggleX =
                        cardX +
                                cardW -
                                53;

                int toggleY =
                        cardY +
                                cardH -
                                35;

                if (mouseX >= toggleX &&
                        mouseX <= toggleX + 38 &&
                        mouseY >= toggleY &&
                        mouseY <= toggleY + 20) {

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
    // ESC
    // =========================================================

    @Override
    public boolean keyPressed(
            int keyCode,
            int scanCode,
            int modifiers
    ) {

        if (keyCode ==
                GLFW.GLFW_KEY_ESCAPE) {

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
    // GAME DOES NOT PAUSE
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
                    return BlueLockOptimizerClient
                            .CONFIG
                            .armorHudEnabled;

                case 1:
                    return BlueLockOptimizerClient
                            .CONFIG
                            .fullBrightEnabled;

                case 2:
                    return BlueLockOptimizerClient
                            .CONFIG
                            .coordinatesEnabled;

                case 3:
                    return BlueLockOptimizerClient
                            .CONFIG
                            .cpsCounterEnabled;

                case 4:
                    return BlueLockOptimizerClient
                            .CONFIG
                            .potionCounterEnabled;

                case 5:
                    return BlueLockOptimizerClient
                            .CONFIG
                            .keystrokesEnabled;

                case 6:
                    return BlueLockOptimizerClient
                            .CONFIG
                            .fpsHudEnabled;

                case 7:
                    return BlueLockOptimizerClient
                            .CONFIG
                            .zoomEnabled;

                case 8:
                    return BlueLockOptimizerClient
                            .CONFIG
                            .toggleSprintEnabled;

                default:
                    return false;
            }
        }

        public void toggle() {

            switch (configId) {

                case 0:
                    BlueLockOptimizerClient.CONFIG
                            .armorHudEnabled =
                            !BlueLockOptimizerClient.CONFIG
                                    .armorHudEnabled;
                    break;

                case 1:
                    BlueLockOptimizerClient.CONFIG
                            .fullBrightEnabled =
                            !BlueLockOptimizerClient.CONFIG
                                    .fullBrightEnabled;
                    break;

                case 2:
                    BlueLockOptimizerClient.CONFIG
                            .coordinatesEnabled =
                            !BlueLockOptimizerClient.CONFIG
                                    .coordinatesEnabled;
                    break;

                case 3:
                    BlueLockOptimizerClient.CONFIG
                            .cpsCounterEnabled =
                            !BlueLockOptimizerClient.CONFIG
                                    .cpsCounterEnabled;
                    break;

                case 4:
                    BlueLockOptimizerClient.CONFIG
                            .potionCounterEnabled =
                            !BlueLockOptimizerClient.CONFIG
                                    .potionCounterEnabled;
                    break;

                case 5:
                    BlueLockOptimizerClient.CONFIG
                            .keystrokesEnabled =
                            !BlueLockOptimizerClient.CONFIG
                                    .keystrokesEnabled;
                    break;

                case 6:
                    BlueLockOptimizerClient.CONFIG
                            .fpsHudEnabled =
                            !BlueLockOptimizerClient.CONFIG
                                    .fpsHudEnabled;
                    break;

                case 7:
                    BlueLockOptimizerClient.CONFIG
                            .zoomEnabled =
                            !BlueLockOptimizerClient.CONFIG
                                    .zoomEnabled;
                    break;

                case 8:
                    BlueLockOptimizerClient.CONFIG
                            .toggleSprintEnabled =
                            !BlueLockOptimizerClient.CONFIG
                                    .toggleSprintEnabled;
                    break;
            }
        }
    }
}
