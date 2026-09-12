
package com.rahultheog.bluelockoptimizer;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import static com.rahultheog.bluelockoptimizer.BlueLockOptimizerClient.CONFIG;

public class ModMenuScreen extends Screen {

    private static final int COL_BG          = 0xFF050D1A;
    private static final int COL_PANEL       = 0xFF0A1628;
    private static final int COL_CARD        = 0xFF0D1F36;
    private static final int COL_CARD_BORDER = 0xFF1A4080;
    private static final int COL_ACCENT      = 0xFF00CFFF;
    private static final int COL_ACCENT2     = 0xFF0077CC;
    private static final int COL_WHITE       = 0xFFFFFFFF;
    private static final int COL_GREY        = 0xFF7090A0;
    private static final int COL_SELECTED_TAB= 0xFF0A2850;
    private static final int COL_TOGGLE_ON   = 0xFF0099DD;
    private static final int COL_TOGGLE_OFF  = 0xFF1A3050;
    private static final int COL_KNOB        = 0xFFFFFFFF;
    private static final int COL_ENABLED_TXT = 0xFF00CFFF;
    private static final int COL_DISABLED_TXT= 0xFF4A6070;

    private final FootballOpenAnimation animation = new FootballOpenAnimation();

    private enum Tab { MODULES, HUD, SETTINGS }
    private Tab currentTab = Tab.MODULES;

    private static class Module {
        String name, desc, icon;
        BooleanSupplier getter;
        BooleanConsumer setter;
        Module(String icon, String name, String desc, BooleanSupplier getter, BooleanConsumer setter) {
            this.icon = icon; this.name = name; this.desc = desc;
            this.getter = getter; this.setter = setter;
        }
    }

    @FunctionalInterface interface BooleanSupplier { boolean get(); }
    @FunctionalInterface interface BooleanConsumer  { void accept(boolean v); }

    private final Module[] modulesTab = {
        new Module("A",  "Armor HUD",      "Shows your armor status on screen.",        () -> CONFIG.armorHudEnabled,     v -> CONFIG.armorHudEnabled     = v),
        new Module("B",  "Full Bright",    "Removes darkness and increases visibility.", () -> CONFIG.fullBrightEnabled,   v -> CONFIG.fullBrightEnabled   = v),
        new Module("C",  "Coordinates",    "Displays your current coordinates.",        () -> CONFIG.coordinatesEnabled,  v -> CONFIG.coordinatesEnabled  = v),
        new Module("D",  "CPS Counter",    "Shows your clicks per second.",             () -> CONFIG.cpsCounterEnabled,   v -> CONFIG.cpsCounterEnabled   = v),
        new Module("E",  "Potion Counter", "Shows active potion effects and time.",     () -> CONFIG.potionCounterEnabled,v -> CONFIG.potionCounterEnabled = v),
        new Module("F",  "Keystrokes",     "Shows your key presses in real time.",      () -> CONFIG.keystrokesEnabled,   v -> CONFIG.keystrokesEnabled   = v),
        new Module("G",  "FPS",            "Displays current frames per second.",       () -> CONFIG.fpsHudEnabled,       v -> CONFIG.fpsHudEnabled       = v),
        new Module("H",  "Zoom",           "Allows you to zoom in and out.",            () -> CONFIG.zoomEnabled,         v -> CONFIG.zoomEnabled         = v),
        new Module("I",  "Toggle Sprint",  "Automatically toggles sprint when moving.", () -> CONFIG.toggleSprintEnabled, v -> CONFIG.toggleSprintEnabled = v),
    };

    private final Module[] hudTab = {
        new Module("G", "FPS HUD",    "Live FPS counter on screen.",  () -> CONFIG.fpsHudEnabled,      v -> CONFIG.fpsHudEnabled      = v),
        new Module("A", "PvP HUD",    "CPS + ping overlay for PvP.",  () -> CONFIG.pvpHudEnabled,      v -> CONFIG.pvpHudEnabled      = v),
        new Module("C", "Coords HUD", "XYZ coordinates display.",     () -> CONFIG.coordinatesEnabled, v -> CONFIG.coordinatesEnabled = v),
    };

    public ModMenuScreen() { super(Text.literal("BlueLock Menu")); }

    @Override protected void init() { animation.start(); }
    @Override public boolean shouldPause() { return false; }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        animation.tick(delta);
        float progress = animation.getProgress();
        if (progress <= 0f) return;

        int menuW = Math.min(700, this.width - 40);
        int menuH = Math.min(480, this.height - 60);
        int menuX = (this.width - menuW) / 2;
        int menuY = (this.height - menuH) / 2;

        ctx.getMatrices().push();
        float scale = 0.6f + 0.4f * easeOutBack(progress);
        ctx.getMatrices().translate(menuX + menuW / 2f, menuY + menuH / 2f, 0);
        ctx.getMatrices().scale(scale, scale, 1f);
        ctx.getMatrices().translate(-(menuX + menuW / 2f), -(menuY + menuH / 2f), 0);

        if (progress < 1f) animation.renderParticles(ctx, menuX + menuW / 2, menuY + menuH / 2, progress);

        drawRoundRect(ctx, menuX, menuY, menuW, menuH, COL_BG);

        int headerH = 44;
        drawRoundRect(ctx, menuX, menuY, menuW, headerH, COL_PANEL);
        ctx.fill(menuX, menuY + 8, menuX + 3, menuY + headerH - 8, COL_ACCENT);
        ctx.drawTextWithShadow(client.textRenderer, Text.literal("BLUECORE"), menuX + 16, menuY + 14, COL_ACCENT);
        ctx.fill(menuX, menuY + headerH, menuX + menuW, menuY + headerH + 1, COL_CARD_BORDER);

        int sideW = 110;
        int bodyY = menuY + headerH + 1;
        int bodyH = menuH - headerH - 1;
        drawRoundRect(ctx, menuX, bodyY, sideW, bodyH, COL_PANEL);

        String[] tabNames = {"Modules", "HUD", "Settings"};
        Tab[] tabVals = Tab.values();
        int tabH = 36;
        for (int i = 0; i < tabNames.length; i++) {
            int ty = bodyY + i * (tabH + 2) + 8;
            boolean sel = currentTab == tabVals[i];
            if (sel) {
                drawRoundRect(ctx, menuX + 4, ty, sideW - 8, tabH, COL_SELECTED_TAB);
                ctx.fill(menuX + 4, ty, menuX + 6, ty + tabH, COL_ACCENT);
            }
            ctx.drawTextWithShadow(client.textRenderer, Text.literal(tabNames[i]), menuX + 14, ty + 12, sel ? COL_ACCENT : COL_GREY);
        }

        int contentX = menuX + sideW + 8;
        int contentW = menuW - sideW - 16;
        Module[] modules = currentTab == Tab.MODULES ? modulesTab : currentTab == Tab.HUD ? hudTab : new Module[0];
        int cols = 3, cardW = (contentW - (cols-1)*6)/cols, cardH = 90, startY = bodyY + 8;

        for (int i = 0; i < modules.length; i++) {
            drawModuleCard(ctx, modules[i], contentX + (i%cols)*(cardW+6), startY + (i/cols)*(cardH+6), cardW, cardH, mouseX, mouseY);
        }

        if (currentTab == Tab.SETTINGS) {
            ctx.drawTextWithShadow(client.textRenderer, Text.literal("BlueLock Optimizer v2.0.0"), contentX, startY + 10, COL_WHITE);
            ctx.drawTextWithShadow(client.textRenderer, Text.literal("By rahul_the_og"), contentX, startY + 24, COL_GREY);
        }

        ctx.getMatrices().pop();
        super.render(ctx, mouseX, mouseY, delta);
    }

    private void drawModuleCard(DrawContext ctx, Module m, int x, int y, int w, int h, int mouseX, int mouseY) {
        boolean hovered = mouseX >= x && mouseX <= x+w && mouseY >= y && mouseY <= y+h;
        ctx.fill(x, y, x+w, y+h, hovered ? 0xFF0F2540 : COL_CARD);
        drawBorder(ctx, x, y, w, h, hovered ? COL_ACCENT2 : COL_CARD_BORDER);
        ctx.drawTextWithShadow(client.textRenderer, Text.literal(m.icon), x+8, y+8, COL_ACCENT);
        ctx.drawTextWithShadow(client.textRenderer, Text.literal(m.name), x+28, y+8, COL_WHITE);
        ctx.drawTextWithShadow(client.textRenderer, Text.literal(m.desc), x+8, y+22, COL_GREY);
        boolean on = m.getter.get();
        int tX=x+8, tY=y+h-20, tW=30, tH=10;
        ctx.fill(tX, tY, tX+tW, tY+tH, on ? COL_TOGGLE_ON : COL_TOGGLE_OFF);
        int knobX = on ? tX+tW-tH : tX;
        ctx.fill(knobX, tY, knobX+tH, tY+tH, COL_KNOB);
        ctx.drawTextWithShadow(client.textRenderer, Text.literal(on ? "Enabled" : "Disabled"), tX+tW+4, tY+1, on ? COL_ENABLED_TXT : COL_DISABLED_TXT);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int menuW = Math.min(700, this.width-40), menuH = Math.min(480, this.height-60);
        int menuX = (this.width-menuW)/2, menuY = (this.height-menuH)/2;
        int headerH=44, sideW=110, bodyY=menuY+headerH+1;
        Tab[] tabVals = Tab.values();
        for (int i = 0; i < tabVals.length; i++) {
            int ty = bodyY + i*38 + 8;
            if (mouseX>=menuX+4 && mouseX<=menuX+sideW-4 && mouseY>=ty && mouseY<=ty+36) {
                currentTab = tabVals[i]; return true;
            }
        }
        Module[] modules = currentTab==Tab.MODULES ? modulesTab : currentTab==Tab.HUD ? hudTab : new Module[0];
        int contentX=menuX+sideW+8, contentW=menuW-sideW-16;
        int cols=3, cardW=(contentW-(cols-1)*6)/cols, cardH=90, startY=bodyY+8;
        for (int i = 0; i < modules.length; i++) {
            int cx=contentX+(i%cols)*(cardW+6), cy=startY+(i/cols)*(cardH+6);
            if (mouseX>=cx && mouseX<=cx+cardW && mouseY>=cy && mouseY<=cy+cardH) {
                modules[i].setter.accept(!modules[i].getter.get()); return true;
            }
        }
        if (mouseX<menuX || mouseX>menuX+menuW || mouseY<menuY || mouseY>menuY+menuH) {
            this.close(); return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) { this.close(); return true; }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void drawRoundRect(DrawContext ctx, int x, int y, int w, int h, int color) {
        ctx.fill(x+2, y, x+w-2, y+h, color);
        ctx.fill(x, y+2, x+w, y+h-2, color);
    }

    private void drawBorder(DrawContext ctx, int x, int y, int w, int h, int color) {
        ctx.fill(x, y, x+w, y+1, color);
        ctx.fill(x, y+h-1, x+w, y+h, color);
        ctx.fill(x, y, x+1, y+h, color);
        ctx.fill(x+w-1, y, x+w, y+h, color);
    }

    private float easeOutBack(float t) {
        float c1=1.70158f, c3=c1+1f;
        return 1f + c3*(float)Math.pow(t-1,3) + c1*(float)Math.pow(t-1,2);
    }
}
