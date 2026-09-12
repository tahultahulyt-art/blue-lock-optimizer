package com.rahultheog.bluelockoptimizer;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;

import static com.rahultheog.bluelockoptimizer.BlueLockOptimizerClient.CONFIG;

public class ModMenuScreen extends Screen {

    private static final int C_BG           = 0xB8050A14;
    private static final int C_GLOW         = 0x22108CFF;
    private static final int C_PANEL        = 0xF2071222;
    private static final int C_BORDER       = 0xFF087BFF;
    private static final int C_HEADER_LINE  = 0xFF0D72E8;
    private static final int C_SIDEBAR      = 0xB8071325;
    private static final int C_TAB_SEL      = 0xFF0868D9;
    private static final int C_TAB_ACCENT   = 0xFF28C2FF;
    private static final int C_ACCENT       = 0xFF38B8FF;
    private static final int C_WHITE        = 0xFFFFFFFF;
    private static final int C_TEXT_DIM     = 0xFF8BAFD1;
    private static final int C_TEXT_BLUE    = 0xFF72B8F5;
    private static final int C_CARD_BG      = 0xFF091A2E;
    private static final int C_CARD_HOV     = 0xFF0B2039;
    private static final int C_CARD_BORDER  = 0xFF075EBB;
    private static final int C_ICON_BG      = 0xFF071A32;
    private static final int C_TOGGLE_ON    = 0xFF087FF0;
    private static final int C_TOGGLE_OFF   = 0xFF152C49;
    private static final int C_TOGGLE_BDR_ON= 0xFF1AA8FF;
    private static final int C_TOGGLE_BDR_OF= 0xFF31516F;
    private static final int C_KNOB_ON      = 0xFFE5F7FF;
    private static final int C_KNOB_OFF     = 0xFF8BA7C5;
    private static final int C_ENABLED_TXT  = 0xFF38B8FF;
    private static final int C_DISABLED_TXT = 0xFF7895B5;
    private static final int C_DOTS         = 0xFF4CAEFF;
    private static final int C_DESC         = 0xFF71A6D4;
    private static final int C_SEP          = 0xFF15517F;

    private static class Module {
        String name, desc;
        boolean enabled;
        Runnable toggle;
        Module(String name, String desc, boolean enabled, Runnable toggle) {
            this.name = name; this.desc = desc;
            this.enabled = enabled; this.toggle = toggle;
        }
    }

    private final List<Module> modules = new ArrayList<>();
    private int selectedTab = 0;
    private float animation = 0f;

    private int pX, pY, pW, pH;
    private static final int SIDEBAR_W = 210;
    private static final int HEADER_H  = 82;

    public ModMenuScreen() {
        super(Text.literal("BLUECORE"));
        buildModules();
    }

    private void buildModules() {
        modules.clear();
        modules.add(new Module("Armor HUD",     "Shows your armor status on screen.",          CONFIG.armorHudEnabled,      () -> CONFIG.armorHudEnabled      = !CONFIG.armorHudEnabled));
        modules.add(new Module("Full Bright",   "Removes darkness and increases visibility.",  CONFIG.fullBrightEnabled,    () -> CONFIG.fullBrightEnabled    = !CONFIG.fullBrightEnabled));
        modules.add(new Module("Coordinates",   "Displays your current coordinates.",          CONFIG.coordinatesEnabled,   () -> CONFIG.coordinatesEnabled   = !CONFIG.coordinatesEnabled));
        modules.add(new Module("CPS Counter",   "Shows your clicks per second.",               CONFIG.cpsCounterEnabled,    () -> CONFIG.cpsCounterEnabled    = !CONFIG.cpsCounterEnabled));
        modules.add(new Module("Potion Counter","Shows active potion effects and time.",        CONFIG.potionCounterEnabled, () -> CONFIG.potionCounterEnabled = !CONFIG.potionCounterEnabled));
        modules.add(new Module("Keystrokes",    "Shows your key presses in real time.",        CONFIG.keystrokesEnabled,    () -> CONFIG.keystrokesEnabled    = !CONFIG.keystrokesEnabled));
        modules.add(new Module("FPS",           "Displays current frames per second.",         CONFIG.fpsHudEnabled,        () -> CONFIG.fpsHudEnabled        = !CONFIG.fpsHudEnabled));
        modules.add(new Module("Zoom",          "Allows you to zoom in and out.",              CONFIG.zoomEnabled,          () -> CONFIG.zoomEnabled          = !CONFIG.zoomEnabled));
        modules.add(new Module("Toggle Sprint", "Automatically toggles sprint when moving.",   CONFIG.toggleSprintEnabled,  () -> CONFIG.toggleSprintEnabled  = !CONFIG.toggleSprintEnabled));
    }

    @Override
    protected void init() {
        pW = Math.min(1180, this.width  - 80);
        pH = Math.min(680,  this.height - 80);
        pX = (this.width  - pW) / 2;
        pY = (this.height - pH) / 2;
        animation = 0f;
    }

    @Override
    public boolean shouldPause() { return false; }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        animation = MathHelper.lerp(delta * 0.15f, animation, 1.0f);
        float scale = 0.7f + 0.3f * animation;

        ctx.fill(0, 0, width, height, C_BG);

        ctx.getMatrices().push();
        ctx.getMatrices().translate(pX + pW / 2f, pY + pH / 2f, 0);
        ctx.getMatrices().scale(scale, scale, 1f);
        ctx.getMatrices().translate(-(pX + pW / 2f), -(pY + pH / 2f), 0);

        ctx.fill(pX - 10, pY - 10, pX + pW + 10, pY + pH + 10, C_GLOW);

        drawRoundedPanel(ctx, pX, pY, pW, pH, C_PANEL, C_BORDER);
        drawHeader(ctx);
        drawSidebar(ctx, mouseX, mouseY);

        if (selectedTab == 0) drawModules(ctx, mouseX, mouseY);
        else if (selectedTab == 1) drawHUD(ctx);
        else drawSettings(ctx);

        ctx.getMatrices().pop();
        super.render(ctx, mouseX, mouseY, delta);
    }

    private void drawHeader(DrawContext ctx) {
        ctx.fill(pX, pY + HEADER_H, pX + pW, pY + HEADER_H + 1, C_HEADER_LINE);
        drawDiamond(ctx, pX + 42, pY + 40, 18, C_ACCENT);
        ctx.drawText(textRenderer, Text.literal("BLUE"), pX + 68, pY + 32, C_ACCENT, true);
        ctx.drawText(textRenderer, Text.literal("CORE"), pX + 68 + textRenderer.getWidth("BLUE"), pY + 32, C_WHITE, true);
        ctx.drawText(textRenderer, Text.literal("[ Search ]"), pX + pW - 200, pY + 32, C_TEXT_DIM, false);
        ctx.fill(pX + pW - 100, pY + 20, pX + pW - 99, pY + 62, C_SEP);
        ctx.drawText(textRenderer, Text.literal("[ Settings ]"), pX + pW - 95, pY + 32, C_TEXT_DIM, false);
    }

    private void drawSidebar(DrawContext ctx, int mouseX, int mouseY) {
        int sX = pX, sY = pY + HEADER_H;
        ctx.fill(sX, sY, sX + SIDEBAR_W, pY + pH, C_SIDEBAR);

        String[] tabNames = {"Modules", "HUD", "Settings"};
        String[] tabIcons = {"▦", "▣", "⚙"};

        for (int i = 0; i < 3; i++) {
            int tY = sY + 28 + i * 72;
            boolean sel = selectedTab == i;
            if (sel) {
                ctx.fill(sX + 7, tY - 10, sX + SIDEBAR_W - 8, tY + 49, C_TAB_SEL);
                ctx.fill(sX + 7, tY - 10, sX + 11, tY + 49, C_TAB_ACCENT);
            }
            ctx.drawText(textRenderer, Text.literal(tabIcons[i]), sX + 35, tY + 5, sel ? C_WHITE : C_TEXT_BLUE, false);
            ctx.drawText(textRenderer, Text.literal(tabNames[i]), sX + 60, tY + 5, sel ? C_WHITE : C_TEXT_DIM, false);
        }
    }

    private void drawModules(DrawContext ctx, int mouseX, int mouseY) {
        int cX = pX + SIDEBAR_W + 20;
        int cY = pY + HEADER_H + 20;
        int cardW = 265, cardH = 165, gapX = 20, gapY = 20;

        for (int i = 0; i < modules.size(); i++) {
            int col = i % 3, row = i / 3;
            int x = cX + col * (cardW + gapX);
            int y = cY + row * (cardH + gapY);
            drawModuleCard(ctx, modules.get(i), x, y, cardW, cardH, mouseX, mouseY);
        }
    }

    private void drawModuleCard(DrawContext ctx, Module m, int x, int y, int w, int h, int mouseX, int mouseY) {
        boolean hov = mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + h;
        drawRoundedPanel(ctx, x, y, w, h, hov ? C_CARD_HOV : C_CARD_BG, C_CARD_BORDER);
        drawRoundedPanel(ctx, x + 18, y + 18, 58, 58, C_ICON_BG, C_CARD_BORDER);
        ctx.drawText(textRenderer, Text.literal(m.name.substring(0, 1)), x + 43, y + 43, C_ACCENT, true);
        ctx.drawText(textRenderer, Text.literal(m.name), x + 92, y + 25, C_WHITE, true);
        drawWrappedText(ctx, m.desc, x + 92, y + 45, 155, C_DESC);
        ctx.drawText(textRenderer, Text.literal("•••"), x + w - 34, y + 18, C_DOTS, false);
        drawToggle(ctx, x + 18, y + h - 48, m.enabled);
        ctx.drawText(textRenderer, Text.literal(m.enabled ? "Enabled" : "Disabled"), x + 100, y + h - 40, m.enabled ? C_ENABLED_TXT : C_DISABLED_TXT, true);
    }

    private void drawHUD(DrawContext ctx) {
        int x = pX + SIDEBAR_W + 40, y = pY + HEADER_H + 40;
        ctx.drawText(textRenderer, Text.literal("HUD Settings"), x, y, C_ACCENT, true);
        ctx.drawText(textRenderer, Text.literal("Configure the information displayed on your screen."), x, y + 28, C_TEXT_DIM, false);
        drawSettingBox(ctx, x, y + 75,  "FPS Counter",  "Show FPS on screen.",       CONFIG.fpsHudEnabled);
        drawSettingBox(ctx, x, y + 145, "Coordinates",  "Show XYZ coordinates.",     CONFIG.coordinatesEnabled);
        drawSettingBox(ctx, x, y + 215, "Keystrokes",   "Show keyboard input.",      CONFIG.keystrokesEnabled);
        drawSettingBox(ctx, x, y + 285, "CPS Counter",  "Show clicks per second.",   CONFIG.cpsCounterEnabled);
    }

    private void drawSettings(DrawContext ctx) {
        int x = pX + SIDEBAR_W + 40, y = pY + HEADER_H + 40;
        ctx.drawText(textRenderer, Text.literal("BlueCore Settings"), x, y, C_ACCENT, true);
        ctx.drawText(textRenderer, Text.literal("Customize the appearance and behavior of BlueCore."), x, y + 28, C_TEXT_DIM, false);
        drawSettingBox(ctx, x, y + 75,  "Blue Theme",    "Use the neon blue interface.",           true);
        drawSettingBox(ctx, x, y + 145, "Animations",    "Enable menu animations.",                true);
        drawSettingBox(ctx, x, y + 215, "Full Bright",   "Remove darkness for better visibility.", CONFIG.fullBrightEnabled);
        drawSettingBox(ctx, x, y + 285, "Toggle Sprint", "Auto sprint when moving.",               CONFIG.toggleSprintEnabled);
    }

    private void drawSettingBox(DrawContext ctx, int x, int y, String title, String desc, boolean enabled) {
        drawRoundedPanel(ctx, x, y, 550, 55, C_CARD_BG, C_CARD_BORDER);
        ctx.drawText(textRenderer, Text.literal(title), x + 20, y + 10, C_WHITE, true);
        ctx.drawText(textRenderer, Text.literal(desc),  x + 20, y + 29, C_DESC, false);
        drawToggle(ctx, x + 450, y + 12, enabled);
    }

    private void drawToggle(DrawContext ctx, int x, int y, boolean on) {
        drawRoundedPanel(ctx, x, y, 72, 32, on ? C_TOGGLE_ON : C_TOGGLE_OFF, on ? C_TOGGLE_BDR_ON : C_TOGGLE_BDR_OF);
        int kX = on ? x + 47 : x + 7;
        ctx.fill(kX, y + 7, kX + 18, y + 25, on ? C_KNOB_ON : C_KNOB_OFF);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int sX = pX, sY = pY + HEADER_H;
        for (int i = 0; i < 3; i++) {
            int tY = sY + 18 + i * 72;
            if (mouseX >= sX && mouseX <= sX + SIDEBAR_W && mouseY >= tY && mouseY <= tY + 55) {
                selectedTab = i;
                return true;
            }
        }

        if (selectedTab == 0) {
            int cX = pX + SIDEBAR_W + 20, cY = pY + HEADER_H + 20;
            int cardW = 265, cardH = 165, gapX = 20, gapY = 20;
            for (int i = 0; i < modules.size(); i++) {
                int col = i % 3, row = i / 3;
                int x = cX + col * (cardW + gapX);
                int y = cY + row * (cardH + gapY);
                if (mouseX >= x && mouseX <= x + cardW && mouseY >= y && mouseY <= y + cardH) {
                    modules.get(i).toggle.run();
                    modules.get(i).enabled = !modules.get(i).enabled;
                    return true;
                }
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) { this.close(); return true; }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void drawRoundedPanel(DrawContext ctx, int x, int y, int w, int h, int bg, int border) {
        ctx.fill(x + 2, y,     x + w - 2, y + h,     bg);
        ctx.fill(x,     y + 2, x + w,     y + h - 2, bg);
        ctx.fill(x + 2, y,         x + w - 2, y + 1,     border);
        ctx.fill(x + 2, y + h - 1, x + w - 2, y + h,     border);
        ctx.fill(x,     y + 2,     x + 1,     y + h - 2, border);
        ctx.fill(x + w - 1, y + 2, x + w,     y + h - 2, border);
    }

    private void drawDiamond(DrawContext ctx, int cx, int cy, int r, int color) {
        for (int i = -r; i <= r; i++) {
            int hw = r - Math.abs(i);
            ctx.fill(cx - hw, cy + i, cx + hw, cy + i + 1, color);
        }
    }

    private void drawWrappedText(DrawContext ctx, String text, int x, int y, int maxW, int color) {
        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();
        int lineY = y;
        for (String word : words) {
            String test = line.length() > 0 ? line + " " + word : word;
            if (textRenderer.getWidth(test) > maxW && line.length() > 0) {
                ctx.drawText(textRenderer, Text.literal(line.toString()), x, lineY, color, false);
                line = new StringBuilder(word);
                lineY += 11;
            } else {
                line = new StringBuilder(test);
            }
        }
        if (line.length() > 0) {
            ctx.drawText(textRenderer, Text.literal(line.toString()), x, lineY, color, false);
        }
    }
}
