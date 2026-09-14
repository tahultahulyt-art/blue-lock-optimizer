package com.rahultheog.bluelockoptimizer;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class ModMenuScreen extends Screen {

    // =========================================================
    // COLORS — BLUELOCK REFERENCE
    // =========================================================

    private static final int BG = 0xFF020710;

    private static final int PANEL = 0xF2071425;
    private static final int PANEL_DARK = 0xFF07121F;

    private static final int CARD = 0xE60A1726;
    private static final int CARD_HOVER = 0xEE102A40;

    private static final int BLUE = 0xFF087FFF;
    private static final int BLUE_2 = 0xFF149BFF;
    private static final int LIGHT_BLUE = 0xFF38C6FF;

    private static final int WHITE = 0xFFF0F7FF;
    private static final int TEXT = 0xFFB7CDE0;
    private static final int MUTED = 0xFF63819B;

    // =========================================================
    // FIXED REFERENCE PROPORTION
    // =========================================================

    private static final float DESIGN_W = 900.0f;
    private static final float DESIGN_H = 500.0f;

    private float scale = 1.0f;

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
    private float hoverAnimation = 0.0f;
    private float logoPulse = 0.0f;

    private static final int OPEN_TIME = 420;

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
                "Removes darkness and increases visibility.",
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
                "Displays current frames per second.",
                6
        ));

        modules.add(new ModuleButton(
                "Zoom",
                "Allows you to zoom in and out.",
                7
        ));

        modules.add(new ModuleButton(
                "Toggle Sprint",
                "Automatically toggles sprint when moving.",
                8
        ));
    }

    // =========================================================
    // INIT
    // =========================================================

    @Override
    protected void init() {

        scale =
                Math.min(
                        width / DESIGN_W,
                        height / DESIGN_H
                );

        panelW =
                (int) (DESIGN_W * scale);

        panelH =
                (int) (DESIGN_H * scale);

        panelX =
                (width - panelW) / 2;

        panelY =
                (height - panelH) / 2;
    }

    // =========================================================
    // MAIN RENDER
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

        float progress =
                Math.min(
                        1.0f,
                        elapsed /
                                (float) OPEN_TIME
                );

        // Smooth ease-out
        openAnimation =
                1.0f -
                        (float) Math.pow(
                                1.0f - progress,
                                3.0
                        );

        logoPulse += delta * 0.055f;

        // -----------------------------------------------------
        // FULL DARK BACKGROUND
        // -----------------------------------------------------

        context.fill(
                0,
                0,
                width,
                height,
                BG
        );

        // -----------------------------------------------------
        // PANEL ANIMATION
        // -----------------------------------------------------

        int animatedY =
                panelY +
                        (int)
                                ((1.0f -
                                        openAnimation)
                                        * 12.0f);

        // -----------------------------------------------------
        // OUTER BLUE ATMOSPHERE
        // -----------------------------------------------------

        drawRect(
                context,
                panelX - 5,
                animatedY - 5,
                panelW + 10,
                panelH + 10,
                0x18008CFF
        );

        drawRect(
                context,
                panelX - 2,
                animatedY - 2,
                panelW + 4,
                panelH + 4,
                0x30008CFF
        );

        // -----------------------------------------------------
        // MAIN PANEL
        // -----------------------------------------------------

        drawPanel(
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
        // LEFT SIDEBAR
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

        super.render(
                context,
                mouseX,
                mouseY,
                delta
        );
    }

    // =========================================================
    // MAIN PANEL
    // =========================================================

    private void drawPanel(
            DrawContext context,
            int x,
            int y,
            int w,
            int h
    ) {

        // Outer frame
        drawRect(
                context,
                x,
                y,
                w,
                h,
                PANEL
        );

        // Inner frame
        drawRect(
                context,
                x + 3,
                y + 3,
                w - 6,
                h - 6,
                PANEL_DARK
        );

        // Top blue border
        drawRect(
                context,
                x + 5,
                y + 5,
                w - 10,
                2,
                BLUE
        );

        // Left blue border
        drawRect(
                context,
                x + 5,
                y + 7,
                2,
                h - 14,
                0xFF075EA8
        );

        // Right blue border
        drawRect(
                context,
                x + w - 7,
                y + 7,
                2,
                h - 14,
                0xFF075EA8
        );

        // Bottom blue border
        drawRect(
                context,
                x + 5,
                y + h - 7,
                w - 10,
                2,
                BLUE
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

        int headerH = 72;

        // Header background
        drawRect(
                context,
                x + 7,
                y + 7,
                w - 14,
                headerH,
                0xFF081522
        );

        // Header bottom line
        drawRect(
                context,
                x + 16,
                y + headerH + 7,
                w - 32,
                1,
                0xFF12314B
        );

        // =====================================================
        // FOOTBALL LOGO
        // =====================================================

        int logoX = x + 35;
        int logoY = y + 19;

        // Glow
        drawRect(
                context,
                logoX - 7,
                logoY - 7,
                48,
                48,
                0x28008CFF
        );

        // Logo box
        drawRect(
                context,
                logoX,
                logoY,
                34,
                34,
                0xFF0A3152
        );

        drawFootballLogo(
                context,
                logoX + 17,
                logoY + 17
        );

        // =====================================================
        // TITLE
        // =====================================================

        context.drawText(
                textRenderer,
                Text.literal("BLUELOCK"),
                x + 78,
                y + 22,
                WHITE,
                true
        );

        context.drawText(
                textRenderer,
                Text.literal("OPTIMIZER"),
                x + 78,
                y + 37,
                LIGHT_BLUE,
                false
        );

        context.drawText(
                textRenderer,
                Text.literal("PLAY SMART • PLAY FAST • BE THE BEST"),
                x + 78,
                y + 51,
                MUTED,
                false
        );

        // =====================================================
        // SEARCH
        // =====================================================

        drawSearchIcon(
                context,
                x + w - 72,
                y + 31
        );

        // Small separator
        drawRect(
                context,
                x + w - 48,
                y + 19,
                1,
                32,
                0xFF173A55
        );

        // Small blue dot
        drawRect(
                context,
                x + w - 32,
                y + 27,
                6,
                6,
                LIGHT_BLUE
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

        int c = LIGHT_BLUE;

        // Pixel football shape
        drawRect(
                context,
                cx - 7,
                cy - 13,
                14,
                26,
                c
        );

        drawRect(
                context,
                cx - 12,
                cy - 7,
                24,
                14,
                c
        );

        drawRect(
                context,
                cx - 9,
                cy - 10,
                18,
                20,
                c
        );

        // Dark center
        drawRect(
                context,
                cx - 4,
                cy - 4,
                8,
                8,
                0xFF061526
        );

        // seams
        drawRect(
                context,
                cx - 1,
                cy - 10,
                2,
                6,
                BLUE
        );

        drawRect(
                context,
                cx - 1,
                cy + 4,
                2,
                6,
                BLUE
        );

        drawRect(
                context,
                cx - 10,
                cy - 1,
                6,
                2,
                BLUE
        );

        drawRect(
                context,
                cx + 4,
                cy - 1,
                6,
                2,
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

        int sidebarX = x + 18;
        int sidebarY = y + 88;

        // Sidebar separator
        drawRect(
                context,
                x + 174,
                y + 87,
                1,
                panelH - 100,
                0xFF102B40
        );

        drawSidebarItem(
                context,
                sidebarX,
                sidebarY,
                "Modules",
                0,
                mouseX,
                mouseY
        );

        drawSidebarItem(
                context,
                sidebarX,
                sidebarY + 47,
                "HUD",
                1,
                mouseX,
                mouseY
        );

        drawSidebarItem(
                context,
                sidebarX,
                sidebarY + 94,
                "Settings",
                2,
                mouseX,
                mouseY
        );

        // =====================================================
        // BOTTOM SIDEBAR TEXT
        // =====================================================

        context.drawText(
                textRenderer,
                Text.literal("BLUELOCK OPTIMIZER"),
                sidebarX,
                y + panelH - 52,
                0xFF315D7C,
                false
        );

        context.drawText(
                textRenderer,
                Text.literal("v1.0"),
                sidebarX,
                y + panelH - 38,
                0xFF24455E,
                false
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
                        mouseX <= x + 135 &&
                        mouseY >= y &&
                        mouseY <= y + 35;

        if (selected) {

            drawRect(
                    context,
                    x,
                    y,
                    135,
                    35,
                    0xFF12304A
            );

            drawRect(
                    context,
                    x,
                    y,
                    3,
                    35,
                    BLUE
            );

        } else if (hover) {

            drawRect(
                    context,
                    x,
                    y,
                    135,
                    35,
                    0xFF0C2235
            );
        }

        context.drawText(
                textRenderer,
                Text.literal(name),
                x + 16,
                y + 13,
                selected
                        ? LIGHT_BLUE
                        : TEXT,
                selected
        );
    }

    // =========================================================
    // MODULE GRID
    // =========================================================

    private void drawModules(
            DrawContext context,
            int x,
            int y,
            int mouseX,
            int mouseY
    ) {

        int startX = x + 188;
        int startY = y + 88;

        int cardW = 205;
        int cardH = 100;

        int gapX = 10;
        int gapY = 10;

        for (int i = 0;
             i < modules.size();
             i++) {

            ModuleButton module =
                    modules.get(i);

            int col = i % 3;
            int row = i / 3;

            int cardX =
                    startX +
                            col *
                                    (cardW + gapX);

            int cardY =
                    startY +
                            row *
                                    (cardH + gapY);

            float cardAnim =
                    Math.max(
                            0.0f,
                            Math.min(
                                    1.0f,
                                    openAnimation * 1.25f
                                            - i * 0.045f
                            )
                    );

            int animatedY =
                    cardY +
                            (int)
                                    ((1.0f -
                                            cardAnim)
                                            * 7);

            drawModuleCard(
                    context,
                    module,
                    cardX,
                    animatedY,
                    cardW,
                    cardH,
                    mouseX,
                    mouseY
            );
        }
    }

    // =========================================================
    // MODULE CARD
    // =========================================================

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

        int cardColor =
                hover
                        ? CARD_HOVER
                        : CARD;

        // Outer blue glow
        if (hover || enabled) {

            drawRect(
                    context,
                    x - 2,
                    y - 2,
                    w + 4,
                    h + 4,
                    enabled
                            ? 0x28008CFF
                            : 0x14008CFF
            );
        }

        // Card
        drawRect(
                context,
                x,
                y,
                w,
                h,
                cardColor
        );

        // Top line
        drawRect(
                context,
                x,
                y,
                w,
                2,
                enabled
                        ? BLUE
                        : 0xFF12304A
        );

        // Icon box
        drawRect(
                context,
                x + 11,
                y + 13,
                38,
                38,
                enabled
                        ? 0xFF0E304C
                        : 0xFF0C2234
        );

        drawModuleIcon(
                context,
                module.name,
                x + 30,
                y + 32,
                enabled
                        ? LIGHT_BLUE
                        : 0xFF66859D
        );

        // Name
        context.drawText(
                textRenderer,
                Text.literal(module.name),
                x + 61,
                y + 16,
                WHITE,
                true
        );

        // Description
        drawDescription(
                context,
                module.description,
                x + 11,
                y + 58
        );

        // Category
        context.drawText(
                textRenderer,
                Text.literal(
                        getCategory(module.name)
                ),
                x + 11,
                y + h - 17,
                0xFF39647F,
                false
        );

        // Three dots
        context.drawText(
                textRenderer,
                Text.literal("•••"),
                x + w - 28,
                y + 12,
                0xFF4B6D83,
                false
        );

        // Toggle
        drawToggle(
                context,
                x + w - 47,
                y + h - 31,
                enabled,
                hover
        );
    }

    // =========================================================
    // MODULE ICON
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

            drawRect(
                    context,
                    x - 6,
                    y - 6,
                    12,
                    12,
                    color
            );

            drawRect(
                    context,
                    x - 2,
                    y - 2,
                    4,
                    4,
                    0xFF071726
            );
        }
    }

    // =========================================================
    // CATEGORY
    // =========================================================

    private String getCategory(
            String name
    ) {

        if (name.equals("FPS") ||
                name.equals("CPS Counter") ||
                name.equals("Keystrokes")) {

            return "PVP";
        }

        if (name.equals("Full Bright")) {

            return "PERFORMANCE";
        }

        if (name.equals("Coordinates") ||
                name.equals("Armor HUD") ||
                name.equals("Potion Counter")) {

            return "UTILITY";
        }

        if (name.equals("Toggle Sprint")) {

            return "MOVEMENT";
        }

        return "PVP";
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
                                ? 0xFF31546B
                                : 0xFF20394D;

        drawRect(
                context,
                x,
                y,
                40,
                20,
                track
        );

        int knobX =
                enabled
                        ? x + 22
                        : x + 3;

        drawRect(
                context,
                knobX,
                y + 3,
                15,
                14,
                enabled
                        ? WHITE
                        : 0xFF6E879A
        );

        if (enabled) {

            drawRect(
                    context,
                    x + 27,
                    y + 7,
                    4,
                    4,
                    LIGHT_BLUE
            );
        }
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

            if (textRenderer.getWidth(test)
                    > 170) {

                if (!line.isEmpty()) {

                    context.drawText(
                            textRenderer,
                            Text.literal(line),
                            x,
                            lineY,
                            MUTED,
                            false
                    );

                    lineY += 10;
                }

                line = word;

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
    // SEARCH ICON
    // =========================================================

    private void drawSearchIcon(
            DrawContext context,
            int x,
            int y
    ) {

        drawRect(
                context,
                x - 7,
                y - 7,
                15,
                15,
                0xFF0C263B
        );

        // Circle
        drawRect(
                context,
                x - 3,
                y - 4,
                8,
                8,
                LIGHT_BLUE
        );

        drawRect(
                context,
                x - 1,
                y - 2,
                4,
                4,
                0xFF071526
        );

        // Handle
        drawRect(
                context,
                x + 5,
                y + 4,
                6,
                2,
                LIGHT_BLUE
        );
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

        int startX = x + 188;
        int startY = y + 94;

        drawSettingCard(
                context,
                startX,
                startY,
                "FPS Counter",
                "Display current FPS.",
                BlueLockOptimizerClient.CONFIG
                        .fpsHudEnabled,
                mouseX,
                mouseY
        );

        drawSettingCard(
                context,
                startX,
                startY + 62,
                "Coordinates",
                "Show XYZ coordinates.",
                BlueLockOptimizerClient.CONFIG
                        .coordinatesEnabled,
                mouseX,
                mouseY
        );

        drawSettingCard(
                context,
                startX,
                startY + 124,
                "CPS Counter",
                "Display clicks per second.",
                BlueLockOptimizerClient.CONFIG
                        .cpsCounterEnabled,
                mouseX,
                mouseY
        );

        drawSettingCard(
                context,
                startX,
                startY + 186,
                "Armor HUD",
                "Display armor status.",
                BlueLockOptimizerClient.CONFIG
                        .armorHudEnabled,
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

        int startX = x + 188;
        int startY = y + 94;

        drawSettingCard(
                context,
                startX,
                startY,
                "Full Bright",
                "Increase world brightness.",
                BlueLockOptimizerClient.CONFIG
                        .fullBrightEnabled,
                mouseX,
                mouseY
        );

        drawSettingCard(
                context,
                startX,
                startY + 62,
                "Toggle Sprint",
                "Automatically sprint when moving.",
                BlueLockOptimizerClient.CONFIG
                        .toggleSprintEnabled,
                mouseX,
                mouseY
        );

        drawSettingCard(
                context,
                startX,
                startY + 124,
                "Zoom",
                "Zoom your Minecraft view.",
                BlueLockOptimizerClient.CONFIG
                        .zoomEnabled,
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
                        mouseY <= y + 48;

        drawRect(
                context,
                x,
                y,
                500,
                48,
                hover
                        ? CARD_HOVER
                        : CARD
        );

        drawRect(
                context,
                x,
                y,
                2,
                48,
                enabled
                        ? BLUE
                        : 0xFF173149
        );

        context.drawText(
                textRenderer,
                Text.literal(name),
                x + 15,
                y + 10,
                WHITE,
                true
        );

        context.drawText(
                textRenderer,
                Text.literal(description),
                x + 15,
                y + 27,
                MUTED,
                false
        );

        drawToggle(
                context,
                x + 447,
                y + 13,
                enabled,
                hover
        );
    }

    // =========================================================
    // RECTANGLE HELPER
    // =========================================================

    private void drawRect(
            DrawContext context,
            int x,
            int y,
            int w,
            int h,
            int color
    ) {

        context.fill(
                x,
                y,
                x + w,
                y + h,
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

        int sidebarX = panelX + 18;
        int sidebarY = panelY + 88;

        if (mouseX >= sidebarX &&
                mouseX <= sidebarX + 135) {

            if (mouseY >= sidebarY &&
                    mouseY <= sidebarY + 35) {

                selectedTab = 0;
                return true;
            }

            if (mouseY >= sidebarY + 47 &&
                    mouseY <= sidebarY + 82) {

                selectedTab = 1;
                return true;
            }

            if (mouseY >= sidebarY + 94 &&
                    mouseY <= sidebarY + 129) {

                selectedTab = 2;
                return true;
            }
        }

        // -----------------------------------------------------
        // MODULES
        // -----------------------------------------------------

        if (selectedTab == 0) {

            int startX = panelX + 188;
            int startY = panelY + 88;

            int cardW = 205;
            int cardH = 100;

            int gapX = 10;
            int gapY = 10;

            for (int i = 0;
                 i < modules.size();
                 i++) {

                ModuleButton module =
                        modules.get(i);

                int col = i % 3;
                int row = i / 3;

                int cardX =
                        startX +
                                col *
                                        (cardW + gapX);

                int cardY =
                        startY +
                                row *
                                        (cardH + gapY);

                int toggleX =
                        cardX +
                                cardW -
                                47;

                int toggleY =
                        cardY +
                                cardH -
                                31;

                if (mouseX >= toggleX &&
                        mouseX <= toggleX + 40 &&
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
    // DON'T PAUSE GAME
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
