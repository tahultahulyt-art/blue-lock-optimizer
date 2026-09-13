package com.rahultheog.bluelockoptimizer;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class ModMenuScreen extends Screen {
private long menuOpenTime;
private float openAnimation = 0.0f;

private static final int ANIMATION_DURATION = 220;    

    private int panelX;
    private int panelY;
    private int panelW;
    private int panelH;

    private int selectedTab = 0;

    private final List<ModuleButton> modules = new ArrayList<>();

    public ModMenuScreen() {
        super(Text.literal("BlueLock Optimizer"));
menuOpenTime = System.currentTimeMillis();
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

    @Override
    protected void init() {

        panelW = Math.min(1100, width - 50);
        panelH = Math.min(650, height - 40);

        panelX = (width - panelW) / 2;
        panelY = (height - panelH) / 2;
    }

    @Override
    public void render(
            DrawContext context,
            int mouseX,
            int mouseY,
            float delta
    ) {
long elapsed = System.currentTimeMillis() - menuOpenTime;

openAnimation = Math.min(
        1.0f,
        elapsed / (float) ANIMATION_DURATION
);

openAnimation = 1.0f - (float) Math.pow(1.0f - openAnimation, 3);
        // =====================================================
        // BACKGROUND
        // =====================================================
int alpha = (int) (184 * openAnimation);
        context.fill(
                0,
                0,
                width,
                height,
                0xB8050B16
        );

        // Blue ambient glow
        context.fill(
                panelX - 5,
                panelY - 5,
                panelX + panelW + 5,
                panelY + panelH + 5,
                0x22008CFF
        );

        // =====================================================
        // MAIN PANEL
        // =====================================================

        drawPanel(
                context,
                panelX,
                panelY,
                panelW,
                panelH,
                0xF2071425,
                0xFF087FFF
        );

        // =====================================================
        // HEADER
        // =====================================================

        drawHeader(context);

        // =====================================================
        // SIDEBAR
        // =====================================================

        drawSidebar(context, mouseX, mouseY);

        // =====================================================
        // CONTENT
        // =====================================================

        if (selectedTab == 0) {
            drawModules(context, mouseX, mouseY);
        }

        if (selectedTab == 1) {
            drawHUD(context);
        }

        if (selectedTab == 2) {
            drawSettings(context);
        }

        super.render(context, mouseX, mouseY, delta);
    }

    // =========================================================
    // HEADER
    // =========================================================

    private void drawHeader(DrawContext context) {

        int headerH = 75;

        // Header line
        context.fill(
                panelX,
                panelY + headerH,
                panelX + panelW,
                panelY + headerH + 1,
                0xFF087FFF
        );

        // Logo
        drawLogo(
                context,
                panelX + 25,
                panelY + 18
        );

        // Title
        context.drawText(
                textRenderer,
                Text.literal("BLUECORE"),
                panelX + 75,
                panelY + 27,
                0xFF38B9FF,
                true
        );

        // Search icon
        drawSearchIcon(
                context,
                panelX + panelW - 120,
                panelY + 37
        );

        // Separator
        context.fill(
                panelX + panelW - 85,
                panelY + 20,
                panelX + panelW - 84,
                panelY + 55,
                0xFF21486B
        );

        // Gear
        context.drawText(
                textRenderer,
                Text.literal("⚙"),
                panelX + panelW - 60,
                panelY + 27,
                0xFF38B9FF,
                true
        );
    }

    // =========================================================
    // SIDEBAR
    // =========================================================

    private void drawSidebar(
            DrawContext context,
            int mouseX,
            int mouseY
    ) {

        int x = panelX;
        int y = panelY + 76;

        int sidebarW = 205;

        context.fill(
                x,
                y,
                x + sidebarW,
                panelY + panelH,
                0xD9071221
        );

        String[] names = {
                "Modules",
                "HUD",
                "Settings"
        };

        String[] icons = {
                "▦",
                "▣",
                "⚙"
        };

        for (int i = 0; i < 3; i++) {

            int buttonY = y + 25 + i * 70;

            boolean selected = selectedTab == i;

            if (selected) {

                context.fill(
                        x + 7,
                        buttonY - 8,
                        x + sidebarW - 7,
                        buttonY + 45,
                        0xFF086ED8
                );

                // Bright left line
                context.fill(
                        x + 7,
                        buttonY - 8,
                        x + 11,
                        buttonY + 45,
                        0xFF37C6FF
                );
            }

            context.drawText(
                    textRenderer,
                    Text.literal(icons[i]),
                    x + 32,
                    buttonY + 5,
                    selected
                            ? 0xFFFFFFFF
                            : 0xFF65AEE8,
                    false
            );

            context.drawText(
                    textRenderer,
                    Text.literal(names[i]),
                    x + 75,
                    buttonY + 5,
                    selected
                            ? 0xFFFFFFFF
                            : 0xFF8EB9DF,
                    false
            );
        }
    }

    // =========================================================
    // MODULES
    // =========================================================

    private void drawModules(
            DrawContext context,
            int mouseX,
            int mouseY
    ) {

        int contentX = panelX + 225;
        int contentY = panelY + 100;

        int cardW = 265;
        int cardH = 155;

        int gapX = 18;
        int gapY = 18;

        for (int i = 0; i < modules.size(); i++) {

            ModuleButton module = modules.get(i);

            int column = i % 3;
            int row = i / 3;

            int x =
                    contentX +
                    column * (cardW + gapX);

            int y =
                    contentY +
                    row * (cardH + gapY);

            drawModule(
                    context,
                    module,
                    x,
                    y,
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

    private void drawModule(
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

        int background =
                hover
                        ? 0xFF0D2743
                        : 0xFF091B2F;

        drawPanel(
                context,
                x,
                y,
                w,
                h,
                background,
                0xFF086ED8
        );

        // =====================================================
        // ICON BOX
        // =====================================================

        drawPanel(
                context,
                x + 17,
                y + 17,
                55,
                55,
                0xFF071B32,
                0xFF087FFF
        );

        drawIcon(
                context,
                module.name,
                x + 44,
                y + 35
        );

        // =====================================================
        // NAME
        // =====================================================

        context.drawText(
                textRenderer,
                Text.literal(module.name),
                x + 88,
                y + 20,
                0xFFFFFFFF,
                true
        );

        // =====================================================
        // DESCRIPTION
        // =====================================================

        drawDescription(
                context,
                module.description,
                x + 88,
                y + 44
        );

        // =====================================================
        // THREE DOTS
        // =====================================================

        context.drawText(
                textRenderer,
                Text.literal("•••"),
                x + w - 32,
                y + 17,
                0xFF4EAFFF,
                false
        );

        // =====================================================
        // TOGGLE
        // =====================================================

        drawToggle(
                context,
                module,
                x + 17,
                y + h - 45
        );
    }

    // =========================================================
    // TOGGLE
    // =========================================================

    private void drawToggle(
            DrawContext context,
            ModuleButton module,
            int x,
            int y
    ) {

        boolean enabled = module.isEnabled();

        int toggleColor =
                enabled
                        ? 0xFF087FFF
                        : 0xFF162D48;

        drawPanel(
                context,
                x,
                y,
                68,
                30,
                toggleColor,
                enabled
                        ? 0xFF2AB8FF
                        : 0xFF385777
        );

        // Circle
        int circleX =
                enabled
                        ? x + 52
                        : x + 16;

        context.fill(
                circleX - 8,
                y + 7,
                circleX + 8,
                y + 23,
                enabled
                        ? 0xFFE6F8FF
                        : 0xFF7E9BB9
        );

        context.drawText(
                textRenderer,
                Text.literal(
                        enabled
                                ? "Enabled"
                                : "Disabled"
                ),
                x + 80,
                y + 8,
                enabled
                        ? 0xFF38B9FF
                        : 0xFF7895B4,
                true
        );
    }

    // =========================================================
    // HUD TAB
    // =========================================================

    private void drawHUD(DrawContext context) {

        int x = panelX + 235;
        int y = panelY + 110;

        context.drawText(
                textRenderer,
                Text.literal("HUD"),
                x,
                y,
                0xFF38B9FF,
                true
        );

        context.drawText(
                textRenderer,
                Text.literal(
                        "Configure your on-screen information."
                ),
                x,
                y + 25,
                0xFF80A8CC,
                false
        );

        drawSetting(
                context,
                x,
                y + 65,
                "FPS Counter",
                "Show current FPS.",
                BlueLockOptimizerClient.CONFIG.fpsHudEnabled
        );

        drawSetting(
                context,
                x,
                y + 135,
                "Coordinates",
                "Show XYZ coordinates.",
                BlueLockOptimizerClient.CONFIG.coordinatesEnabled
        );

        drawSetting(
                context,
                x,
                y + 205,
                "CPS Counter",
                "Show clicks per second.",
                BlueLockOptimizerClient.CONFIG.cpsCounterEnabled
        );
    }

    // =========================================================
    // SETTINGS TAB
    // =========================================================

    private void drawSettings(DrawContext context) {

        int x = panelX + 235;
        int y = panelY + 110;

        context.drawText(
                textRenderer,
                Text.literal("Settings"),
                x,
                y,
                0xFF38B9FF,
                true
        );

        context.drawText(
                textRenderer,
                Text.literal(
                        "Customize BlueCore."
                ),
                x,
                y + 25,
                0xFF80A8CC,
                false
        );

        drawSetting(
                context,
                x,
                y + 65,
                "Performance Mode",
                "Reduce visual effects.",
                BlueLockOptimizerClient.CONFIG.performanceMode
        );

        drawSetting(
                context,
                x,
                y + 135,
                "Full Bright",
                "Increase world brightness.",
                BlueLockOptimizerClient.CONFIG.fullBrightEnabled
        );

        drawSetting(
                context,
                x,
                y + 205,
                "Toggle Sprint",
                "Automatically sprint.",
                BlueLockOptimizerClient.CONFIG.toggleSprintEnabled
        );
    }

    // =========================================================
    // SETTING BOX
    // =========================================================

    private void drawSetting(
            DrawContext context,
            int x,
            int y,
            String title,
            String description,
            boolean enabled
    ) {

        drawPanel(
                context,
                x,
                y,
                560,
                58,
                0xFF091B2F,
                0xFF086ED8
        );

        context.drawText(
                textRenderer,
                Text.literal(title),
                x + 18,
                y + 10,
                0xFFFFFFFF,
                true
        );

        context.drawText(
                textRenderer,
                Text.literal(description),
                x + 18,
                y + 31,
                0xFF719BC0,
                false
        );

        int toggleX = x + 450;

        drawSimpleToggle(
                context,
                toggleX,
                y + 14,
                enabled
        );
    }

    // =========================================================
    // SIMPLE TOGGLE
    // =========================================================

    private void drawSimpleToggle(
            DrawContext context,
            int x,
            int y,
            boolean enabled
    ) {

        context.fill(
                x,
                y,
                x + 65,
                y + 30,
                enabled
                        ? 0xFF087FFF
                        : 0xFF162D48
        );

        int circleX =
                enabled
                        ? x + 50
                        : x + 15;

        context.fill(
                circleX - 8,
                y + 7,
                circleX + 8,
                y + 23,
                enabled
                        ? 0xFFE6F8FF
                        : 0xFF7E9BB9
        );
    }

    // =========================================================
    // MOUSE CLICK
    // =========================================================

    @Override
    public boolean mouseClicked(
            double mouseX,
            double mouseY,
            int button
    ) {

        // -----------------------------------------------------
        // Sidebar
        // -----------------------------------------------------

        int sidebarX = panelX;
        int sidebarY = panelY + 76;

        for (int i = 0; i < 3; i++) {

            int y = sidebarY + 17 + i * 70;

            if (
                    mouseX >= sidebarX &&
                    mouseX <= sidebarX + 205 &&
                    mouseY >= y &&
                    mouseY <= y + 55
            ) {

                selectedTab = i;
                return true;
            }
        }

        // -----------------------------------------------------
        // Module toggles
        // -----------------------------------------------------

        if (selectedTab == 0) {

            int contentX = panelX + 225;
            int contentY = panelY + 100;

            int cardW = 265;
            int cardH = 155;

            int gapX = 18;
            int gapY = 18;

            for (int i = 0; i < modules.size(); i++) {

                ModuleButton module = modules.get(i);

                int column = i % 3;
                int row = i / 3;

                int x =
                        contentX +
                        column * (cardW + gapX);

                int y =
                        contentY +
                        row * (cardH + gapY);

                int toggleY = y + cardH - 45;

                if (
                        mouseX >= x &&
                        mouseX <= x + 180 &&
                        mouseY >= toggleY &&
                        mouseY <= toggleY + 35
                ) {

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
    public boolean shouldPause() {
        return false;
    }

    // =========================================================
    // PANEL
    // =========================================================

    private void drawPanel(
            DrawContext context,
            int x,
            int y,
            int w,
            int h,
            int fill,
            int border
    ) {

        // Main body
        context.fill(
                x + 5,
                y,
                x + w - 5,
                y + h,
                fill
        );

        context.fill(
                x,
                y + 5,
                x + w,
                y + h - 5,
                fill
        );

        // Top border
        context.fill(
                x + 5,
                y,
                x + w - 5,
                y + 1,
                border
        );

        // Bottom border
        context.fill(
                x + 5,
                y + h - 1,
                x + w - 5,
                y + h,
                border
        );

        // Left border
        context.fill(
                x,
                y + 5,
                x + 1,
                y + h - 5,
                border
        );

        // Right border
        context.fill(
                x + w - 1,
                y + 5,
                x + w,
                y + h - 5,
                border
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

        context.fill(
                x + 8,
                y,
                x + 25,
                y + 38,
                0xFF087FFF
        );

        context.fill(
                x,
                y + 8,
                x + 33,
                y + 30,
                0xFF087FFF
        );

        context.fill(
                x + 8,
                y + 8,
                x + 25,
                y + 30,
                0xFF07182B
        );

        context.fill(
                x + 13,
                y + 4,
                x + 20,
                y + 34,
                0xFF38C6FF
        );
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
                x - 10,
                y - 10,
                x + 9,
                y - 8,
                0xFF38B9FF
        );

        context.fill(
                x - 10,
                y - 8,
                x - 8,
                y + 9,
                0xFF38B9FF
        );

        context.fill(
                x + 7,
                y - 8,
                x + 9,
                y + 8,
                0xFF38B9FF
        );

        context.fill(
                x - 8,
                y + 7,
                x + 5,
                y + 9,
                0xFF38B9FF
        );

        context.fill(
                x + 5,
                y + 6,
                x + 13,
                y + 8,
                0xFF38B9FF
        );
    }

    // =========================================================
    // ICONS
    // =========================================================

    private void drawIcon(
            DrawContext context,
            String name,
            int x,
            int y
    ) {

        int color = 0xFF38B9FF;

        if (name.equals("FPS")) {

            context.drawText(
                    textRenderer,
                    Text.literal("FPS"),
                    x - 17,
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

        } else if (name.equals("Coordinates")) {

            context.drawText(
                    textRenderer,
                    Text.literal("◆"),
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
    // DESCRIPTION
    // =========================================================

    private void drawDescription(
            DrawContext context,
            String text,
            int x,
            int y
    ) {

        String[] words = text.split(" ");

        String line = "";
        int lineY = y;

        for (String word : words) {

            String test =
                    line.isEmpty()
                            ? word
                            : line + " " + word;

            if (textRenderer.getWidth(test) > 145) {

                context.drawText(
                        textRenderer,
                        Text.literal(line),
                        x,
                        lineY,
                        0xFF6F9CC5,
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
                    0xFF6F9CC5,
                    false
            );
        }
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
