package com.rahultheog.bluelockoptimizer;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class ModMenuScreen extends Screen {

    // =========================================================
    // BLUECORE / BLUELOCK THEME
    // =========================================================

    private static final int BG = 0xFF030811;
    private static final int PANEL = 0xF20A1422;
    private static final int PANEL_INNER = 0xE80C1928;

    private static final int CARD = 0xE60D1C2C;
    private static final int CARD_HOVER = 0xF0183047;

    private static final int BLUE = 0xFF087FFF;
    private static final int LIGHT_BLUE = 0xFF38C6FF;
    private static final int CYAN = 0xFF69D6FF;

    private static final int WHITE = 0xFFF0F8FF;
    private static final int TEXT = 0xFFB7D0E5;
    private static final int MUTED = 0xFF66839B;

    // =========================================================
    // PREMIUM MENU SIZE
    // =========================================================

    private static final int MENU_WIDTH = 900;
    private static final int MENU_HEIGHT = 500;

    // =========================================================
    // PANEL
    // =========================================================

    private int panelX;
    private int panelY;
    private int panelW;
    private int panelH;

    private int selectedTab = 0;

    // =========================================================
    // OPEN ANIMATION
    // =========================================================

    private long menuOpenTime;

    private float openProgress = 0.0f;
    private float logoRotation = 0.0f;

    private static final int OPEN_DURATION = 750;

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

        modules.add(new ModuleButton(
                "Armor HUD",
                "Shows your armor status on screen.",
                0
        ));

        modules.add(new ModuleButton(
                "Full Bright",
                "Removes darkness and improves visibility.",
                1
        ));

        modules.add(new ModuleButton(
                "Coordinates",
                "Displays your current coordinates.",
                2
        ));

        modules.add(new ModuleButton(
                "CPS Counter",
                "Shows your clicks per second.",
                3
        ));

        modules.add(new ModuleButton(
                "Potion Counter",
                "Shows active potion effects and time.",
                4
        ));

        modules.add(new ModuleButton(
                "Keystrokes",
                "Shows your key presses in real time.",
                5
        ));

        modules.add(new ModuleButton(
                "FPS",
                "Displays your current frames per second.",
                6
        ));

        modules.add(new ModuleButton(
                "Zoom",
                "Allows you to zoom your Minecraft view.",
                7
        ));

        modules.add(new ModuleButton(
                "Toggle Sprint",
                "Automatically toggles sprint.",
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
                        MENU_WIDTH,
                        width - 40
                );

        panelH =
                Math.min(
                        MENU_HEIGHT,
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
        // OPEN ANIMATION
        // -----------------------------------------------------

        long elapsed =
                System.currentTimeMillis()
                        - menuOpenTime;

        float raw =
                Math.min(
                        1.0f,
                        elapsed /
                                (float) OPEN_DURATION
                );

        // Smooth cubic ease-out
        openProgress =
                1.0f -
                        (float)
                                Math.pow(
                                        1.0f - raw,
                                        3.0
                                );

        // -----------------------------------------------------
        // LOGO ANIMATION
        // -----------------------------------------------------

        logoRotation +=
                delta * 0.025f;

        // -----------------------------------------------------
        // BACKGROUND
        // -----------------------------------------------------

        context.fill(
                0,
                0,
                width,
                height,
                BG
        );

        // Large subtle blue atmosphere
        drawGlow(
                context,
                panelX - 18,
                panelY - 18,
                panelW + 36,
                panelH + 36,
                0x22008CFF
        );

        // -----------------------------------------------------
        // ANIMATED PANEL POSITION
        // -----------------------------------------------------

        int animatedY =
                panelY +
                        (int)
                                ((1.0f -
                                        openProgress) *
                                        18);

        // -----------------------------------------------------
        // PANEL
        // -----------------------------------------------------

        drawPremiumPanel(
                context,
                panelX,
                animatedY,
                panelW,
                panelH
        );

        // -----------------------------------------------------
        // HEADER
        // -----------------------------------------------------

        drawHeader(
                context,
                panelX,
                animatedY,
                panelW,
                mouseX,
                mouseY
        );

        // -----------------------------------------------------
        // SIDEBAR
        // -----------------------------------------------------

        drawSidebar(
                context,
                panelX,
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
                    panelX,
                    animatedY,
                    mouseX,
                    mouseY
            );

        } else if (selectedTab == 1) {

            drawHUD(
                    context,
                    panelX,
                    animatedY,
                    mouseX,
                    mouseY
            );

        } else {

            drawSettings(
                    context,
                    panelX,
                    animatedY,
                    mouseX,
                    mouseY
            );
        }
    }

    // =========================================================
    // PREMIUM PANEL
    // =========================================================

    private void drawPremiumPanel(
            DrawContext context,
            int x,
            int y,
            int w,
            int h
    ) {

        // Outer glow
        drawRoundedRect(
                context,
                x - 2,
                y - 2,
                w + 4,
                h + 4,
                10,
                0x42007FFF
        );

        // Outer panel
        drawRoundedRect(
                context,
                x,
                y,
                w,
                h,
                8,
                PANEL
        );

        // Inner panel
        drawRoundedRect(
                context,
                x + 1,
                y + 1,
                w - 2,
                h - 2,
                7,
                PANEL_INNER
        );

        // Top neon line
        drawRoundedRect(
                context,
                x + 8,
                y,
                w - 16,
                2,
                1,
                BLUE
        );

        // Bottom subtle line
        context.fill(
                x + 15,
                y + h - 1,
                x + w - 15,
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
            int w,
            int mouseX,
            int mouseY
    ) {

        // Logo background
        drawRoundedRect(
                context,
                x + 18,
                y + 14,
                52,
                52,
                10,
                0xFF0A2035
        );

        // Blue logo glow
        drawRoundedRect(
                context,
                x + 21,
                y + 17,
                46,
                46,
                9,
                0x33008CFF
        );

        // Football logo
        drawFootballLogo(
                context,
                x + 44,
                y + 40
        );

        // Brand
        context.drawText(
                textRenderer,
                Text.literal("BLUELOCK"),
                x + 83,
                y + 24,
                WHITE,
                true
        );

        context.drawText(
                textRenderer,
                Text.literal("OPTIMIZER"),
                x + 83,
                y + 40,
                LIGHT_BLUE,
                false
        );

        // Small status
        drawRoundedRect(
                context,
                x + 83,
                y + 55,
                7,
                7,
                3,
                0xFF2DFFB2
        );

        context.drawText(
                textRenderer,
                Text.literal("ONLINE"),
                x + 96,
                y + 53,
                MUTED,
                false
        );

        // Header separator
        context.fill(
                x + 18,
                y + 78,
                x + w - 18,
                y + 79,
                0xFF102B41
        );

        // Search
        drawSearchButton(
                context,
                x + w - 76,
                y + 25,
                mouseX,
                mouseY
        );

        // Settings
        drawGearButton(
                context,
                x + w - 39,
                y + 25,
                mouseX,
                mouseY
        );
    }

    // =========================================================
    // FOOTBALL LOGO
    // =========================================================

    private void drawFootballLogo(
            DrawContext context,
            int cx,
            int cy
    ) {

        // Outer glow
        drawRoundedRect(
                context,
                cx - 17,
                cy - 17,
                34,
                34,
                17,
                0x30008CFF
        );

        // Pixel-style football silhouette
        context.fill(
                cx - 8,
                cy - 14,
                cx + 8,
                cy + 14,
                LIGHT_BLUE
        );

        context.fill(
                cx - 14,
                cy - 8,
                cx + 14,
                cy + 8,
                LIGHT_BLUE
        );

        context.fill(
                cx - 10,
                cy - 11,
                cx + 10,
                cy + 11,
                LIGHT_BLUE
        );

        // Dark center pattern
        context.fill(
                cx - 4,
                cy - 5,
                cx + 5,
                cy + 5,
                0xFF07182B
        );

        // Football seams
        context.fill(
                cx - 1,
                cy - 11,
                cx + 2,
                cy - 5,
                BLUE
        );

        context.fill(
                cx - 1,
                cy + 5,
                cx + 2,
                cy + 11,
                BLUE
        );

        context.fill(
                cx - 11,
                cy - 1,
                cx - 4,
                cy + 2,
                BLUE
        );

        context.fill(
                cx + 4,
                cy - 1,
                cx + 11,
                cy + 2,
                BLUE
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
                x + 18;

        int sidebarY =
                y + 96;

        drawSidebarItem(
                context,
                sidebarX,
                sidebarY,
                "MODULES",
                0,
                mouseX,
                mouseY
        );

        drawSidebarItem(
                context,
                sidebarX,
                sidebarY + 54,
                "HUD",
                1,
                mouseX,
                mouseY
        );

        drawSidebarItem(
                context,
                sidebarX,
                sidebarY + 108,
                "SETTINGS",
                2,
                mouseX,
                mouseY
        );

        // Sidebar divider
        context.fill(
                x + 177,
                y + 96,
                x + 178,
                y + panelH - 18,
                0xFF102A40
        );
    }

    private void drawSidebarItem(
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
                        mouseX <= x + 145 &&
                        mouseY >= y &&
                        mouseY <= y + 38;

        if (selected) {

            drawRoundedRect(
                    context,
                    x,
                    y,
                    145,
                    38,
                    7,
                    0xFF102C45
            );

            drawRoundedRect(
                    context,
                    x,
                    y + 5,
                    3,
                    28,
                    2,
                    BLUE
            );

        } else if (hover) {

            drawRoundedRect(
                    context,
                    x,
                    y,
                    145,
                    38,
                    7,
                    0xFF0D2236
            );
        }

        context.drawText(
                textRenderer,
                Text.literal(name),
                x + 16,
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
            int mouseX,
            int mouseY
    ) {

        int startX =
                x + 194;

        int startY =
                y + 96;

        int cardW = 215;
        int cardH = 116;

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
                                    (cardH + 10);

            // Staggered entrance
            float cardProgress =
                    Math.max(
                            0.0f,
                            Math.min(
                                    1.0f,
                                    (openProgress * 1.35f)
                                            - i * 0.055f
                            )
                    );

            int animatedCardY =
                    cardY +
                            (int)
                                    ((1.0f -
                                            cardProgress) *
                                            10);

            drawModuleCard(
                    context,
                    module,
                    cardX,
                    animatedCardY,
                    cardW,
                    cardH,
                    mouseX,
                    mouseY,
                    cardProgress
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
            int mouseY,
            float animation
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
                        ? CARD_HOVER
                        : CARD;

        // Card glow
        if (hover || enabled) {

            drawRoundedRect(
                    context,
                    x - 2,
                    y - 2,
                    w + 4,
                    h + 4,
                    9,
                    enabled
                            ? 0x30008CFF
                            : 0x18008CFF
            );
        }

        // Main card
        drawRoundedRect(
                context,
                x,
                y,
                w,
                h,
                8,
                background
        );

        // Top accent
        drawRoundedRect(
                context,
                x + 1,
                y,
                w - 2,
                2,
                1,
                enabled
                        ? BLUE
                        : 0xFF16324A
        );

        // Icon container
        drawRoundedRect(
                context,
                x + 12,
                y + 13,
                38,
                38,
                8,
                enabled
                        ? 0xFF103452
                        : 0xFF10263A
        );

        drawModuleIcon(
                context,
                module.name,
                x + 31,
                y + 32,
                enabled
                        ? LIGHT_BLUE
                        : MUTED
        );

        // Module name
        context.drawText(
                textRenderer,
                Text.literal(module.name),
                x + 61,
                y + 17,
                WHITE,
                true
        );

        // Description
        drawDescription(
                context,
                module.description,
                x + 13,
                y + 64
        );

        // Toggle
        drawToggle(
                context,
                x + w - 54,
                y + h - 31,
                enabled,
                hover
        );
    }

    // =========================================================
    // MODULE ICONS
    // =========================================================

    private void drawModuleIcon(
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

        } else if (name.equals("CPS Counter")) {

            context.drawText(
                    textRenderer,
                    Text.literal("CPS"),
                    x - 12,
                    y - 5,
                    color,
                    true
            );

        } else if (name.equals("Zoom")) {

            context.drawText(
                    textRenderer,
                    Text.literal("+"),
                    x - 4,
                    y - 8,
                    color,
                    true
            );

        } else if (name.equals("Keystrokes")) {

            context.drawText(
                    textRenderer,
                    Text.literal("W"),
                    x - 4,
                    y - 7,
                    color,
                    true
            );

        } else {

            context.fill(
                    x - 5,
                    y - 5,
                    x + 6,
                    y + 6,
                    color
            );

            context.fill(
                    x - 2,
                    y - 2,
                    x + 3,
                    y + 3,
                    0xFF0B1B2B
            );
        }
    }

    // =========================================================
    // TOGGLE
    // =========================================================

    private void drawToggle(
            DrawContext context,
            int x,
            int y,
            boolean enabled,
            boolean hover
    ) {

        int track =
                enabled
                        ? BLUE
                        : hover
                                ? 0xFF294A62
                                : 0xFF20384B;

        drawRoundedRect(
                context,
                x,
                y,
                42,
                22,
                11,
                track
        );

        int knobX =
                enabled
                        ? x + 23
                        : x + 3;

        drawRoundedRect(
                context,
                knobX,
                y + 3,
                16,
                16,
                8,
                enabled
                        ? WHITE
                        : 0xFF71879A
        );

        if (enabled) {

            drawRoundedRect(
                    context,
                    x + 25,
                    y + 5,
                    4,
                    4,
                    2,
                    LIGHT_BLUE
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
            int mouseX,
            int mouseY
    ) {

        int startX =
                x + 194;

        int startY =
                y + 98;

        drawSettingCard(
                context,
                startX,
                startY,
                "FPS Counter",
                "Show current FPS.",
                BlueLockOptimizerClient.CONFIG.fpsHudEnabled,
                mouseX,
                mouseY
        );

        drawSettingCard(
                context,
                startX,
                startY + 72,
                "Coordinates",
                "Show XYZ coordinates.",
                BlueLockOptimizerClient.CONFIG.coordinatesEnabled,
                mouseX,
                mouseY
        );

        drawSettingCard(
                context,
                startX,
                startY + 144,
                "CPS Counter",
                "Show clicks per second.",
                BlueLockOptimizerClient.CONFIG.cpsCounterEnabled,
                mouseX,
                mouseY
        );

        drawSettingCard(
                context,
                startX,
                startY + 216,
                "Armor HUD",
                "Display armor status.",
                BlueLockOptimizerClient.CONFIG.armorHudEnabled,
                mouseX,
                mouseY
        );
    }

    // =========================================================
    // SETTINGS
    // =========================================================

    private void drawSettings(
            DrawContext context,
            int x,
            int y,
            int mouseX,
            int mouseY
    ) {

        int startX =
                x + 194;

        int startY =
                y + 98;

        drawSettingCard(
                context,
                startX,
                startY,
                "Performance Mode",
                "Reduce unnecessary visual effects.",
                BlueLockOptimizerClient.CONFIG.performanceMode,
                mouseX,
                mouseY
        );

        drawSettingCard(
                context,
                startX,
                startY + 72,
                "Full Bright",
                "Increase world brightness.",
                BlueLockOptimizerClient.CONFIG.fullBrightEnabled,
                mouseX,
                mouseY
        );

        drawSettingCard(
                context,
                startX,
                startY + 144,
                "Toggle Sprint",
                "Automatically sprint while moving.",
                BlueLockOptimizerClient.CONFIG.toggleSprintEnabled,
                mouseX,
                mouseY
        );

        drawSettingCard(
                context,
                startX,
                startY + 216,
                "Zoom",
                "Zoom your Minecraft view.",
                BlueLockOptimizerClient.CONFIG.zoomEnabled,
                mouseX,
                mouseY
        );
    }

    // =========================================================
    // SETTING CARD
    // =========================================================

    private void drawSettingCard(
            DrawContext context,
            int x,
            int y,
            String name,
            String description,
            boolean enabled,
            int mouseX,
            int mouseY
    ) {

        boolean hover =
                mouseX >= x &&
                        mouseX <= x + 500 &&
                        mouseY >= y &&
                        mouseY <= y + 55;

        drawRoundedRect(
                context,
                x,
                y,
                500,
                55,
                8,
                hover
                        ? CARD_HOVER
                        : CARD
        );

        drawRoundedRect(
                context,
                x,
                y + 5,
                3,
                45,
                2,
                enabled
                        ? BLUE
                        : 0xFF16324A
        );

        context.drawText(
                textRenderer,
                Text.literal(name),
                x + 18,
                y + 11,
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

        drawToggle(
                context,
                x + 445,
                y + 16,
                enabled,
                hover
        );
    }

    // =========================================================
    // SEARCH BUTTON
    // =========================================================

    private void drawSearchButton(
            DrawContext context,
            int x,
            int y,
            int mouseX,
            int mouseY
    ) {

        boolean hover =
                mouseX >= x - 10 &&
                        mouseX <= x + 25 &&
                        mouseY >= y - 10 &&
                        mouseY <= y + 25;

        drawRoundedRect(
                context,
                x - 8,
                y - 8,
                30,
                30,
                8,
                hover
                        ? 0xFF102C45
                        : 0xFF0B1D2E
        );

        // Magnifying glass
        context.fill(
                x - 1,
                y - 1,
                x + 10,
                y + 2,
                LIGHT_BLUE
        );

        context.fill(
                x - 1,
                y - 1,
                x + 2,
                y + 10,
                LIGHT_BLUE
        );

        context.fill(
                x + 8,
                y - 1,
                x + 11,
                y + 10,
                LIGHT_BLUE
        );

        context.fill(
                x + 1,
                y + 7,
                x + 9,
                y + 10,
                LIGHT_BLUE
        );

        context.fill(
                x + 9,
                y + 9,
                x + 15,
                y + 12,
                LIGHT_BLUE
        );
    }

    // =========================================================
    // GEAR
    // =========================================================

    private void drawGearButton(
            DrawContext context,
            int x,
            int y,
            int mouseX,
            int mouseY
    ) {

        boolean hover =
                mouseX >= x - 10 &&
                        mouseX <= x + 25 &&
                        mouseY >= y - 10 &&
                        mouseY <= y + 25;

        drawRoundedRect(
                context,
                x - 8,
                y - 8,
                30,
                30,
                8,
                hover
                        ? 0xFF102C45
                        : 0xFF0B1D2E
        );

        context.drawText(
                textRenderer,
                Text.literal("⚙"),
                x - 1,
                y - 3,
                CYAN,
                true
        );
    }

    // =========================================================
    // DESCRIPTION WRAPPING
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
                lineY += 11;

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
    // ROUNDED RECTANGLE
    // =========================================================

    private void drawRoundedRect(
            DrawContext context,
            int x,
            int y,
            int w,
            int h,
            int radius,
            int color
    ) {

        if (w <= radius * 2 ||
                h <= radius * 2) {

            context.fill(
                    x,
                    y,
                    x + w,
                    y + h,
                    color
            );

            return;
        }

        // Center
        context.fill(
                x + radius,
                y,
                x + w - radius,
                y + h,
                color
        );

        context.fill(
                x,
                y + radius,
                x + w,
                y + h - radius,
                color
        );

        // Corners
        context.fill(
                x + 2,
                y + 2,
                x + radius,
                y + radius,
                color
        );

        context.fill(
                x + w - radius,
                y + 2,
                x + w - 2,
                y + radius,
                color
        );

        context.fill(
                x + 2,
                y + h - radius,
                x + radius,
                y + h - 2,
                color
        );

        context.fill(
                x + w - radius,
                y + h - radius,
                x + w - 2,
                y + h - 2,
                color
        );
    }

    // =========================================================
    // GLOW
    // =========================================================

    private void drawGlow(
            DrawContext context,
            int x,
            int y,
            int w,
            int h,
            int color
    ) {

        drawRoundedRect(
                context,
                x,
                y,
                w,
                h,
                14,
                color
        );
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
                panelX + 18;

        int sidebarY =
                panelY + 96;

        if (mouseX >= sidebarX &&
                mouseX <= sidebarX + 145) {

            if (mouseY >= sidebarY &&
                    mouseY <= sidebarY + 38) {

                selectedTab = 0;
                return true;
            }

            if (mouseY >= sidebarY + 54 &&
                    mouseY <= sidebarY + 92) {

                selectedTab = 1;
                return true;
            }

            if (mouseY >= sidebarY + 108 &&
                    mouseY <= sidebarY + 146) {

                selectedTab = 2;
                return true;
            }
        }

        // -----------------------------------------------------
        // MODULE TOGGLES
        // -----------------------------------------------------

        if (selectedTab == 0) {

            int startX =
                    panelX + 194;

            int startY =
                    panelY + 96;

            int cardW = 215;
            int cardH = 116;

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
                                        (cardH + 10);

                int toggleX =
                        cardX +
                                cardW -
                                54;

                int toggleY =
                        cardY +
                                cardH -
                                31;

                if (mouseX >= toggleX &&
                        mouseX <= toggleX + 42 &&
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
    // ESC
    // =========================================================

    @Override
    public boolean keyPressed(
            int keyCode,
            int scanCode,
            int modifiers
    ) {

        if (keyCode == 256) {

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
    // NO GAME PAUSE
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
