package com.rahultheog.bluelockoptimizer;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * BlueLock Optimizer - Premium Mod Menu Screen
 * Recreated from reference image with full animation support.
 */
public class ModMenuScreen extends Screen {

    // ─── Timing ───────────────────────────────────────────────
    private long openTime = -1;
    private static final float ANIM_DURATION = 600f; // ms

    // ─── Layout (computed in init) ────────────────────────────
    private int panelX, panelY, panelW, panelH;
    private int sidebarW;
    private int headerH;
    private int contentX, contentY, contentW, contentH;
    private int bottomBarH = 22;

    // ─── Navigation ───────────────────────────────────────────
    private int selectedTab = 0; // 0=Modules, 1=HUD, 2=Settings
    private static final String[] TAB_NAMES  = {"Modules", "HUD", "Settings"};

    // ─── Search ───────────────────────────────────────────────
    private String searchText = "";
    private boolean searchFocused = false;
    private int searchBoxX, searchBoxY, searchBoxW, searchBoxH;

    // ─── Module Data ──────────────────────────────────────────
    private static class ModuleEntry {
        String name, description, category, iconTag;
        boolean enabled;
        // per-card hover animation [0..1]
        float hoverAnim = 0f;
        // entry stagger animation [0..1]
        float entryAnim = 0f;
        // toggle slide animation [0..1]  (0=off, 1=on)
        float toggleAnim = 0f;

        ModuleEntry(String name, String description, String category, String iconTag, boolean enabled) {
            this.name = name;
            this.description = description;
            this.category = category;
            this.iconTag = iconTag;
            this.enabled = enabled;
            this.toggleAnim = enabled ? 1f : 0f;
        }
    }

    private final List<ModuleEntry> modules = new ArrayList<>();
    private List<ModuleEntry> filteredModules = new ArrayList<>();

    // ─── Card layout ──────────────────────────────────────────
    private static final int COLS = 3;
    private static final int CARD_GAP = 8;
    private int cardW, cardH;

    // ─── Background particles ─────────────────────────────────
    private static class Particle {
        float x, y, vx, vy, life, maxLife, size;
    }
    private final List<Particle> particles = new ArrayList<>();
    private long lastParticleTime = 0;

    // ─── Logo pulse ───────────────────────────────────────────
    private float logoPulse = 0f;

    // ─── Toggle click tracking ────────────────────────────────
    // stored as index into filteredModules
    private int hoveredCard = -1;

    // ─── Scroll ───────────────────────────────────────────────
    private int scrollOffset = 0;
    private int maxScroll = 0;

    // ─── Constructor ──────────────────────────────────────────
    public ModMenuScreen() {
        super(Text.literal("BlueLock Optimizer"));
        initModules();
    }

    private void initModules() {
        modules.add(new ModuleEntry("Armor HUD",      "Shows your armor status on screen.",         "FPS FRIENDLY", "SHIELD",   true));
        modules.add(new ModuleEntry("Full Bright",    "Removes darkness and improves visibility.",  "PERFORMANCE",  "SUN",      true));
        modules.add(new ModuleEntry("Coordinates",    "Displays your current coordinates.",         "UTILITY",      "PIN",      false));
        modules.add(new ModuleEntry("CPS Counter",    "Shows your clicks per second.",              "PVP",          "CPS",      true));
        modules.add(new ModuleEntry("Potion Counter", "Shows active potion effects and time.",      "UTILITY",      "POTION",   false));
        modules.add(new ModuleEntry("Keystrokes",     "Shows your key presses in real time.",       "PVP",          "KBD",      true));
        modules.add(new ModuleEntry("FPS",            "Displays your current frames per second.",  "PERFORMANCE",  "FPS",      true));
        modules.add(new ModuleEntry("Zoom",           "Allows you to zoom your Minecraft view.",   "PVP",          "ZOOM",     false));
        modules.add(new ModuleEntry("Toggle Sprint",  "Automatically toggles sprint.",              "MOVEMENT",     "SPRINT",   true));
        filteredModules.addAll(modules);
    }

    // ─── Screen init ──────────────────────────────────────────
    @Override
    protected void init() {
        super.init();
        openTime = System.currentTimeMillis();

        // Panel dimensions – 88% width, 82% height, centered
        panelW = (int)(width  * 0.88f);
        panelH = (int)(height * 0.82f);
        panelX = (width  - panelW) / 2;
        panelY = (height - panelH) / 2;

        headerH   = (int)(panelH * 0.165f);
        sidebarW  = (int)(panelW * 0.215f);

        contentX = panelX + sidebarW;
        contentY = panelY + headerH;
        contentW = panelW - sidebarW - 10;
        contentH = panelH - headerH - bottomBarH;

        // Search box
        int searchBarY = contentY + 10;
        searchBoxX = contentX + 10;
        searchBoxY = searchBarY;
        searchBoxW = (int)(contentW * 0.52f);
        searchBoxH = 20;

        // Card size
        int gridW = contentW - 20;
        cardW = (gridW - (COLS - 1) * CARD_GAP) / COLS;
        cardH = (int)(cardW * 0.55f);

        // Reset particle list and stagger animations
        particles.clear();
        for (int i = 0; i < modules.size(); i++) {
            modules.get(i).entryAnim = 0f;
        }
        refilter();
    }

    // ─── Filter ───────────────────────────────────────────────
    private void refilter() {
        filteredModules.clear();
        String q = searchText.toLowerCase();
        for (ModuleEntry m : modules) {
            if (q.isEmpty() || m.name.toLowerCase().contains(q) || m.category.toLowerCase().contains(q)) {
                filteredModules.add(m);
            }
        }
        // recalculate scroll
        int rows = (int)Math.ceil(filteredModules.size() / (double)COLS);
        maxScroll = Math.max(0, rows * (cardH + CARD_GAP) - (contentH - 44));
        scrollOffset = MathHelper.clamp(scrollOffset, 0, maxScroll);
    }

    // ─── Render ───────────────────────────────────────────────
    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        long now = System.currentTimeMillis();
        float t = openTime < 0 ? 1f : Math.min(1f, (now - openTime) / ANIM_DURATION);
        float ease = easeOutCubic(t);

        // Animate logo pulse
        logoPulse = (float)(Math.sin(now * 0.002) * 0.5 + 0.5);

        // ── Background ──────────────────────────────────────
        ctx.fill(0, 0, width, height, color(0x03, 0x08, 0x11, (int)(220 * ease)));
        renderParticles(ctx, now, ease);

        // Panel slide-in: starts 4% below final, scale 96→100%
        float panelOffY = (int)((1f - ease) * panelH * 0.04f);
        float scaleFactor = 0.96f + 0.04f * ease;

        // We can't really matrix-transform in vanilla DrawContext easily,
        // so we approximate with an offset and alpha fade only
        int animPanelY = panelY + (int)panelOffY;

        int alpha = (int)(255 * ease);

        // ── Outer glow ──────────────────────────────────────
        drawGlowRect(ctx, panelX - 4, animPanelY - 4, panelW + 8, panelH + 8,
                color(0x08, 0x7F, 0xFF, (int)(60 * ease)), 6);

        // ── Main panel ──────────────────────────────────────
        drawRoundRect(ctx, panelX, animPanelY, panelW, panelH,
                color(0x06, 0x13, 0x26, (int)(245 * ease)));
        drawRoundRectBorder(ctx, panelX, animPanelY, panelW, panelH,
                color(0x08, 0x7F, 0xFF, (int)(200 * ease)));

        // ── Header ──────────────────────────────────────────
        renderHeader(ctx, animPanelY, now, ease, alpha);

        // ── Sidebar ─────────────────────────────────────────
        renderSidebar(ctx, animPanelY, mouseX, mouseY, ease, alpha);

        // ── Content area ────────────────────────────────────
        int realContentY = animPanelY + headerH;
        renderContent(ctx, realContentY, mouseX, mouseY, now, ease, alpha);

        // ── Bottom bar ──────────────────────────────────────
        renderBottomBar(ctx, animPanelY, ease, alpha);

        // ── Right decoration ────────────────────────────────
        renderRightDecoration(ctx, animPanelY, ease, alpha);

        super.render(ctx, mouseX, mouseY, delta);
    }

    // ─── Header ───────────────────────────────────────────────
    private void renderHeader(DrawContext ctx, int panelTop, long now, float ease, int alpha) {
        int hx = panelX;
        int hy = panelTop;
        int hw = panelW;
        int hh = headerH;

        // Header background gradient (dark navy)
        ctx.fill(hx, hy, hx + hw, hy + hh, color(0x04, 0x0D, 0x20, alpha));

        // Subtle right-side eye artwork placeholder (dark blue wash)
        int eyeW = (int)(hw * 0.45f);
        drawFadeRect(ctx, hx + hw - eyeW, hy, eyeW, hh,
                color(0x05, 0x18, 0x40, (int)(80*ease)),
                color(0x04, 0x0D, 0x20, 0));

        // Soccer ball icon
        int ballSize = (int)(hh * 0.70f);
        int ballX = hx + 12;
        int ballY = hy + (hh - ballSize) / 2;
        drawSoccerBall(ctx, ballX, ballY, ballSize, logoPulse, alpha);

        // BLUELOCK text
        int textX = ballX + ballSize + 10;
        int textY = hy + hh / 2 - 14;
        drawBoldText(ctx, "BLUELOCK", textX, textY, color(0x18, 0xA8, 0xFF, alpha), 2.0f);
        ctx.drawText(client.textRenderer, "OPTIMIZER",
                textX, textY + 16, color(0x38, 0xC6, 0xFF, alpha), false);
        ctx.drawText(client.textRenderer, "PLAY SMART  \u2022  AIM HIGH  \u2022  BE THE BEST",
                textX, textY + 26, color(0x5F, 0x78, 0x95, alpha), false);

        // Minimize / Close buttons (top-right)
        int btnY = hy + 10;
        int closeX = hx + hw - 18;
        int minX   = closeX - 18;
        ctx.drawText(client.textRenderer, "\u2013", minX,   btnY, color(0x8E, 0xA8, 0xC7, alpha), false);
        ctx.drawText(client.textRenderer, "\u00D7", closeX, btnY, color(0x8E, 0xA8, 0xC7, alpha), false);

        // Japanese vertical text top-right
        int jpX = hx + hw - 90;
        int jpY = hy + 12;
        ctx.drawText(client.textRenderer, "\u30D6\u30EB\u30FC\u30ED\u30C3\u30AF", jpX, jpY,
                color(0x18, 0xA8, 0xFF, (int)(160*ease)), false);

        // Separator line
        int sepY = hy + hh - 1;
        drawGlowLine(ctx, hx, sepY, hx + hw, sepY, color(0x08, 0x7F, 0xFF, alpha));
    }

    // ─── Sidebar ──────────────────────────────────────────────
    private void renderSidebar(DrawContext ctx, int panelTop, int mouseX, int mouseY,
                                float ease, int alpha) {
        int sx = panelX;
        int sy = panelTop + headerH;
        int sh = panelH - headerH - bottomBarH;

        // Sidebar bg
        ctx.fill(sx, sy, sx + sidebarW, sy + sh, color(0x05, 0x10, 0x22, (int)(200*ease)));

        // Sidebar right border
        ctx.fill(sx + sidebarW - 1, sy, sx + sidebarW, sy + sh,
                color(0x08, 0x7F, 0xFF, (int)(80*ease)));

        // Nav items
        String[] icons = {"\u2588\u2588", "\u25A1", "\u2699"};
        for (int i = 0; i < TAB_NAMES.length; i++) {
            int itemY = sy + 14 + i * 44;
            int itemX = sx + 10;
            int itemW = sidebarW - 20;
            int itemH = 34;

            boolean hovered = mouseX >= itemX && mouseX <= itemX + itemW &&
                    mouseY >= itemY && mouseY <= itemY + itemH;
            boolean selected = i == selectedTab;

            if (selected) {
                // Blue highlight
                drawRoundRect(ctx, itemX, itemY, itemW, itemH,
                        color(0x08, 0x7F, 0xFF, (int)(180*ease)));
                // Left accent bar
                ctx.fill(itemX, itemY + 4, itemX + 3, itemY + itemH - 4,
                        color(0x38, 0xC6, 0xFF, alpha));
                ctx.drawText(client.textRenderer, TAB_NAMES[i],
                        itemX + 22, itemY + 12, color(0xF4, 0xF8, 0xFF, alpha), false);
            } else if (hovered) {
                drawRoundRect(ctx, itemX, itemY, itemW, itemH,
                        color(0x08, 0x7F, 0xFF, (int)(60*ease)));
                ctx.drawText(client.textRenderer, TAB_NAMES[i],
                        itemX + 22, itemY + 12, color(0xC0, 0xD8, 0xFF, alpha), false);
            } else {
                ctx.drawText(client.textRenderer, TAB_NAMES[i],
                        itemX + 22, itemY + 12, color(0x8E, 0xA8, 0xC7, alpha), false);
            }

            // Icon placeholder
            ctx.drawText(client.textRenderer, icons[i],
                    itemX + 6, itemY + 12, color(0x38, 0xC6, 0xFF, alpha), false);
        }

        // Quote at bottom
        int quoteY = sy + sh - 90;
        String[] quoteLines = {"\u201CIt's not about", "being the best,",
                "it's about never", "stopping.\u201D", "\u2014 Blue Lock"};
        for (int i = 0; i < quoteLines.length; i++) {
            ctx.drawText(client.textRenderer, quoteLines[i],
                    sx + 12, quoteY + i * 10, color(0x5F, 0x78, 0x95, (int)(160*ease)), false);
        }
    }

    // ─── Content ──────────────────────────────────────────────
    private void renderContent(DrawContext ctx, int realContentY, int mouseX, int mouseY,
                               long now, float ease, int alpha) {
        int cx = contentX;
        int cy = realContentY;
        int cw = contentW;
        int ch = contentH;

        // Content bg
        ctx.fill(cx, cy, cx + cw, cy + ch, color(0x06, 0x13, 0x26, (int)(230*ease)));

        if (selectedTab == 0) {
            renderModulesTab(ctx, cx, cy, cw, ch, mouseX, mouseY, now, ease, alpha);
        } else if (selectedTab == 1) {
            renderPlaceholderTab(ctx, cx, cy, cw, ch, ease, alpha, "HUD Settings");
        } else {
            renderPlaceholderTab(ctx, cx, cy, cw, ch, ease, alpha, "Settings");
        }
    }

    private void renderModulesTab(DrawContext ctx, int cx, int cy, int cw, int ch,
                                   int mouseX, int mouseY, long now, float ease, int alpha) {
        // Search bar
        int sbx = cx + 10;
        int sby = cy + 10;
        int sbw = (int)(cw * 0.52f);
        int sbh = 20;
        searchBoxX = sbx; searchBoxY = sby; searchBoxW = sbw; searchBoxH = sbh;

        boolean sHovered = mouseX >= sbx && mouseX < sbx + sbw &&
                mouseY >= sby && mouseY < sby + sbh;
        int borderCol = searchFocused ? color(0x38, 0xC6, 0xFF, alpha) :
                (sHovered ? color(0x18, 0xA8, 0xFF, alpha) : color(0x08, 0x7F, 0xFF, (int)(160*ease)));
        ctx.fill(sbx, sby, sbx + sbw, sby + sbh, color(0x08, 0x18, 0x2B, alpha));
        drawBorder(ctx, sbx, sby, sbw, sbh, borderCol);
        ctx.drawText(client.textRenderer, "\uD83D\uDD0D", sbx + 4, sby + 6,
                color(0x5F, 0x78, 0x95, alpha), false);
        String displaySearch = searchText.isEmpty() && !searchFocused ? "Search modules..." : searchText;
        int searchTextColor = searchText.isEmpty() ? color(0x5F, 0x78, 0x95, alpha) :
                color(0xF4, 0xF8, 0xFF, alpha);
        ctx.drawText(client.textRenderer, displaySearch, sbx + 14, sby + 6,
                searchTextColor, false);

        // Module count badge
        int badgeX = cx + cw - 130;
        int badgeY = sby;
        String badgeText = filteredModules.size() + " Modules";
        ctx.fill(badgeX, badgeY, badgeX + 70, badgeY + sbh, color(0x0A, 0x1A, 0x30, alpha));
        drawBorder(ctx, badgeX, badgeY, 70, sbh, color(0x08, 0x7F, 0xFF, (int)(120*ease)));
        ctx.drawText(client.textRenderer, badgeText, badgeX + 6, badgeY + 6,
                color(0x8E, 0xA8, 0xC7, alpha), false);

        // Category dropdown
        int dropX = badgeX + 75;
        ctx.fill(dropX, badgeY, dropX + 50, badgeY + sbh, color(0x0A, 0x1A, 0x30, alpha));
        drawBorder(ctx, dropX, badgeY, 50, sbh, color(0x08, 0x7F, 0xFF, (int)(120*ease)));
        ctx.drawText(client.textRenderer, "All \u25BC", dropX + 8, badgeY + 6,
                color(0x8E, 0xA8, 0xC7, alpha), false);

        // Module grid with scissor clipping
        int gridTop = cy + 38;
        int gridH   = ch - 40;
        int gridLeft = cx + 10;

        // Enable scissor to clip cards within content area
        // (Minecraft's enableScissor clips in screen coordinates)
        ctx.enableScissor(cx, gridTop, cx + cw, cy + ch);

        hoveredCard = -1;
        for (int i = 0; i < filteredModules.size(); i++) {
            ModuleEntry m = filteredModules.get(i);
            int col = i % COLS;
            int row = i / COLS;
            int cardX = gridLeft + col * (cardW + CARD_GAP);
            int cardY = gridTop + row * (cardH + CARD_GAP) - scrollOffset;

            // Skip if outside visible area
            if (cardY + cardH < gridTop || cardY > cy + ch) {
                m.hoverAnim = 0f;
                continue;
            }

            // Update stagger entry animation
            float staggerDelay = i * 0.05f;
            float cardT = Math.max(0f, (float)(System.currentTimeMillis() - openTime) / ANIM_DURATION - staggerDelay);
            m.entryAnim = Math.min(1f, easeOutCubic(cardT));

            // Hover detection
            boolean cardHov = mouseX >= cardX && mouseX < cardX + cardW &&
                    mouseY >= cardY && mouseY < cardY + cardH &&
                    mouseY >= gridTop && mouseY <= cy + ch;
            if (cardHov) hoveredCard = i;

            // Animate hover
            float hoverTarget = cardHov ? 1f : 0f;
            m.hoverAnim += (hoverTarget - m.hoverAnim) * 0.18f;

            // Animate toggle
            float toggleTarget = m.enabled ? 1f : 0f;
            m.toggleAnim += (toggleTarget - m.toggleAnim) * 0.14f;

            // Entry offset: card starts 8px lower
            int entryOffY = (int)((1f - m.entryAnim) * 8);
            int drawCardY = cardY + entryOffY;
            int hoverOffY = (int)(m.hoverAnim * 3); // lifts card up on hover

            renderModuleCard(ctx, m, cardX, drawCardY - hoverOffY, cardW, cardH,
                    m.hoverAnim, m.entryAnim, alpha);
        }

        ctx.disableScissor();
    }

    // ─── Module Card ──────────────────────────────────────────
    private void renderModuleCard(DrawContext ctx, ModuleEntry m,
                                   int cx, int cy, int cw, int ch,
                                   float hov, float entryAnim, int alpha) {
        int cardAlpha = (int)(alpha * entryAnim);
        if (cardAlpha <= 0) return;

        // Card background
        int bgR = lerp(0x08, 0x0D, hov);
        int bgG = lerp(0x18, 0x27, hov);
        int bgB = lerp(0x2B, 0x42, hov);
        ctx.fill(cx, cy, cx + cw, cy + ch, color(bgR, bgG, bgB, cardAlpha));

        // Border glow on hover
        int borderAlpha = (int)((120 + 100 * hov) * entryAnim);
        drawBorder(ctx, cx, cy, cw, ch, color(0x08, 0x7F, 0xFF, borderAlpha));

        // Top highlight line
        int highlightAlpha = (int)((60 + 100 * hov) * entryAnim);
        ctx.fill(cx + 2, cy, cx + cw - 2, cy + 1,
                color(0x38, 0xC6, 0xFF, highlightAlpha));

        // Icon box
        int iconSize = (int)(ch * 0.52f);
        int iconX = cx + 10;
        int iconY = cy + (ch - iconSize) / 2;
        ctx.fill(iconX, iconY, iconX + iconSize, iconY + iconSize,
                color(0x04, 0x20, 0x45, cardAlpha));
        drawBorder(ctx, iconX, iconY, iconSize, iconSize,
                color(0x08, 0x7F, 0xFF, (int)(140 * entryAnim)));
        drawModuleIcon(ctx, m.iconTag, iconX, iconY, iconSize, cardAlpha);

        // Module name
        int textX = iconX + iconSize + 10;
        int nameY = cy + 10;
        ctx.drawText(client.textRenderer, m.name, textX, nameY,
                color(0xF4, 0xF8, 0xFF, cardAlpha), false);

        // Description (word-wrapped to 2 lines)
        String[] descLines = wrapText(m.description, cw - iconSize - 80, 2);
        for (int i = 0; i < descLines.length; i++) {
            ctx.drawText(client.textRenderer, descLines[i], textX, nameY + 10 + i * 9,
                    color(0x8E, 0xA8, 0xC7, cardAlpha), false);
        }

        // Category badge
        int badgeY = cy + ch - 16;
        int badgeW = client.textRenderer.getWidth(m.category) + 10;
        ctx.fill(textX, badgeY, textX + badgeW, badgeY + 11,
                color(0x04, 0x20, 0x45, cardAlpha));
        drawBorder(ctx, textX, badgeY, badgeW, 11,
                color(0x08, 0x7F, 0xFF, (int)(120 * entryAnim)));
        ctx.drawText(client.textRenderer, m.category, textX + 5, badgeY + 2,
                color(0x38, 0xC6, 0xFF, cardAlpha), false);

        // Three-dot menu (top-right of card)
        ctx.drawText(client.textRenderer, "\u2022\u2022\u2022",
                cx + cw - 20, cy + 8, color(0x5F, 0x78, 0x95, cardAlpha), false);

        // Toggle switch (bottom-right)
        int toggleW = 28;
        int toggleH = 14;
        int toggleX = cx + cw - toggleW - 10;
        int toggleY = cy + ch - toggleH - 10;
        renderToggle(ctx, toggleX, toggleY, toggleW, toggleH, m.toggleAnim, cardAlpha);
    }

    // ─── Toggle Switch ────────────────────────────────────────
    private void renderToggle(DrawContext ctx, int tx, int ty, int tw, int th,
                               float animT, int alpha) {
        // Track color interpolates off→on
        int r = lerp(0x1A, 0x08, animT);
        int g = lerp(0x2A, 0x7F, animT);
        int b = lerp(0x40, 0xFF, animT);
        ctx.fill(tx, ty, tx + tw, ty + th, color(r, g, b, alpha));
        // Track border
        int borderAlpha = (int)(160 * animT) + 40;
        drawBorder(ctx, tx, ty, tw, th, color(0x38, 0xC6, 0xFF, borderAlpha));

        // Knob slides from left (off) to right (on)
        int knobSize = th - 4;
        int knobTravel = tw - knobSize - 4;
        int knobX = tx + 2 + (int)(knobTravel * animT);
        int knobY = ty + 2;
        int knobR = lerp(0x5F, 0xF4, animT);
        int knobG = lerp(0x78, 0xF8, animT);
        int knobB = lerp(0x95, 0xFF, animT);
        ctx.fill(knobX, knobY, knobX + knobSize, knobY + knobSize,
                color(knobR, knobG, knobB, alpha));

        // Glow on when enabled
        if (animT > 0.5f) {
            int glowA = (int)((animT - 0.5f) * 2f * 60);
            ctx.fill(tx - 1, ty - 1, tx + tw + 1, ty + th + 1,
                    color(0x08, 0x7F, 0xFF, glowA));
        }
    }

    // ─── Icons ────────────────────────────────────────────────
    private void drawModuleIcon(DrawContext ctx, String tag, int ix, int iy, int size, int alpha) {
        int centerX = ix + size / 2 - 3;
        int centerY = iy + size / 2 - 4;
        int iconColor = color(0x38, 0xC6, 0xFF, alpha);

        switch (tag) {
            case "SHIELD"  -> ctx.drawText(client.textRenderer, "\u26E8", centerX, centerY, iconColor, false);
            case "SUN"     -> ctx.drawText(client.textRenderer, "\u2600", centerX, centerY, iconColor, false);
            case "PIN"     -> ctx.drawText(client.textRenderer, "\uD83D\uDCCD", centerX, centerY, iconColor, false);
            case "CPS"     -> ctx.drawText(client.textRenderer, "CPS", centerX - 4, centerY, iconColor, false);
            case "POTION"  -> ctx.drawText(client.textRenderer, "\u2697", centerX, centerY, iconColor, false);
            case "KBD"     -> ctx.drawText(client.textRenderer, "\u2328", centerX, centerY, iconColor, false);
            case "FPS"     -> ctx.drawText(client.textRenderer, "FPS", centerX - 4, centerY, iconColor, false);
            case "ZOOM"    -> ctx.drawText(client.textRenderer, "\uD83D\uDD0D", centerX, centerY, iconColor, false);
            case "SPRINT"  -> ctx.drawText(client.textRenderer, "\u25B6", centerX, centerY, iconColor, false);
            default        -> ctx.drawText(client.textRenderer, "\u25A0", centerX, centerY, iconColor, false);
        }
    }

    // ─── Soccer Ball ──────────────────────────────────────────
    private void drawSoccerBall(DrawContext ctx, int bx, int by, int size, float pulse, int alpha) {
        // Outer glow circle (several concentric translucent rings)
        int glowA = (int)(60 + 30 * pulse);
        for (int r = size / 2 + 5; r > size / 2; r--) {
            drawCircle(ctx, bx + size/2, by + size/2, r, color(0x08, 0x7F, 0xFF, glowA));
        }
        // Main circle
        drawCircle(ctx, bx + size/2, by + size/2, size/2,
                color(0x04, 0x20, 0x45, alpha));
        // Border
        drawCircleBorder(ctx, bx + size/2, by + size/2, size/2,
                color(0x18, 0xA8, 0xFF, alpha));
        // Simple pentagon pattern placeholder
        int cx = bx + size/2;
        int cy2 = by + size/2;
        ctx.fill(cx - 3, cy2 - 3, cx + 3, cy2 + 3,
                color(0xF4, 0xF8, 0xFF, alpha));
        // Highlight
        ctx.fill(cx - size/4, cy2 - size/3, cx - size/5, cy2 - size/4,
                color(0xF4, 0xF8, 0xFF, (int)(140 * (0.5f + 0.5f * pulse))));
    }

    // ─── Bottom Bar ───────────────────────────────────────────
    private void renderBottomBar(DrawContext ctx, int panelTop, float ease, int alpha) {
        int bbY = panelTop + panelH - bottomBarH;
        ctx.fill(panelX, bbY, panelX + panelW, panelTop + panelH,
                color(0x04, 0x0D, 0x20, alpha));
        drawGlowLine(ctx, panelX, bbY, panelX + panelW, bbY,
                color(0x08, 0x7F, 0xFF, alpha));

        String barText = "\u223F  BLUELOCK OPTIMIZER  |  BETTER FPS  \u2022  SMOOTHER GAMEPLAY  \u2022  MORE CONTROL";
        int textW = client.textRenderer.getWidth(barText);
        int barCX = panelX + panelW / 2 - textW / 2;
        ctx.drawText(client.textRenderer, barText, barCX, bbY + 7,
                color(0x5F, 0x78, 0x95, alpha), false);
    }

    // ─── Right Decoration ─────────────────────────────────────
    private void renderRightDecoration(DrawContext ctx, int panelTop, float ease, int alpha) {
        int dx = panelX + panelW + 8;
        int dy = panelTop + 10;

        // Crown icon
        ctx.drawText(client.textRenderer, "\u265B", dx, dy,
                color(0x18, 0xA8, 0xFF, (int)(180 * ease)), false);

        // Vertical Japanese text
        String jp = "\u30D6\u30EB\u30FC\u30ED\u30C3\u30AF";
        for (int i = 0; i < jp.length(); i++) {
            ctx.drawText(client.textRenderer, String.valueOf(jp.charAt(i)),
                    dx, dy + 16 + i * 10,
                    color(0x18, 0xA8, 0xFF, (int)(140 * ease)), false);
        }

        // Glowing vertical line
        int lineX = dx + 6;
        int lineY = dy + 90;
        int lineH = panelH - 120;
        for (int y = lineY; y < lineY + lineH; y += 2) {
            ctx.fill(lineX, y, lineX + 1, y + 1,
                    color(0x08, 0x7F, 0xFF, (int)(100 * ease)));
        }
    }

    // ─── Particles ────────────────────────────────────────────
    private void renderParticles(DrawContext ctx, long now, float ease) {
        // Spawn new particles every 200ms, max 12
        if (now - lastParticleTime > 200 && particles.size() < 12) {
            Particle p = new Particle();
            p.x = panelX + (float)(Math.random() * panelW);
            p.y = panelY + panelH;
            p.vx = (float)(Math.random() - 0.5) * 0.3f;
            p.vy = -(float)(Math.random() * 0.4 + 0.1f);
            p.maxLife = 3000 + (float)(Math.random() * 2000);
            p.life    = p.maxLife;
            p.size    = (float)(Math.random() * 2 + 1);
            particles.add(p);
            lastParticleTime = now;
        }

        particles.removeIf(p -> p.life <= 0);
        for (Particle p : particles) {
            float lifeRatio = p.life / p.maxLife;
            p.x += p.vx;
            p.y += p.vy;
            p.life -= 16;
            int pa = (int)(40 * lifeRatio * ease);
            ctx.fill((int)p.x, (int)p.y, (int)(p.x + p.size), (int)(p.y + p.size),
                    color(0x38, 0xC6, 0xFF, pa));
        }
    }

    // ─── Placeholder tabs ─────────────────────────────────────
    private void renderPlaceholderTab(DrawContext ctx, int cx, int cy, int cw, int ch,
                                       float ease, int alpha, String label) {
        int tw = client.textRenderer.getWidth(label);
        ctx.drawText(client.textRenderer, label,
                cx + cw/2 - tw/2, cy + ch/2,
                color(0x5F, 0x78, 0x95, alpha), false);
    }

    // ═══════════════════════════════════════════════════════════
    // MOUSE / KEYBOARD INPUT
    // ═══════════════════════════════════════════════════════════

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int mx = (int)mouseX, my = (int)mouseY;

        // Sidebar nav
        int sy = panelY + headerH;
        for (int i = 0; i < TAB_NAMES.length; i++) {
            int itemY = sy + 14 + i * 44;
            int itemX = panelX + 10;
            int itemW = sidebarW - 20;
            if (mx >= itemX && mx <= itemX + itemW && my >= itemY && my <= itemY + 34) {
                selectedTab = i;
                scrollOffset = 0;
                return true;
            }
        }

        // Close button
        int closeX = panelX + panelW - 18;
        if (mx >= closeX && mx <= closeX + 10 && my >= panelY + 10 && my <= panelY + 20) {
            this.close();
            return true;
        }

        // Search box
        if (mx >= searchBoxX && mx <= searchBoxX + searchBoxW &&
                my >= searchBoxY && my <= searchBoxY + searchBoxH) {
            searchFocused = true;
            return true;
        } else {
            searchFocused = false;
        }

        // Toggle clicks – check toggle region per card
        if (selectedTab == 0) {
            int gridTop  = panelY + headerH + 38;
            int gridLeft = contentX + 10;

            for (int i = 0; i < filteredModules.size(); i++) {
                int col = i % COLS;
                int row = i / COLS;
                int cx  = gridLeft + col * (cardW + CARD_GAP);
                int cy  = gridTop  + row * (cardH + CARD_GAP) - scrollOffset;

                int toggleW = 28, toggleH = 14;
                int tx = cx + cardW - toggleW - 10;
                int ty = cy + cardH - toggleH - 10;

                if (mx >= tx && mx <= tx + toggleW && my >= ty && my <= ty + toggleH) {
                    ModuleEntry m = filteredModules.get(i);
                    m.enabled = !m.enabled;
                    return true;
                }
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // ESC
        if (keyCode == 256) { this.close(); return true; }
        // Backspace in search
        if (searchFocused && keyCode == 259 && !searchText.isEmpty()) {
            searchText = searchText.substring(0, searchText.length() - 1);
            refilter();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (searchFocused && chr >= 32 && searchText.length() < 32) {
            searchText += chr;
            refilter();
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        scrollOffset = MathHelper.clamp(scrollOffset - (int)(verticalAmount * 12), 0, maxScroll);
        return true;
    }

    @Override
    public boolean shouldPause() { return false; }

    // ═══════════════════════════════════════════════════════════
    // DRAW HELPERS
    // ═══════════════════════════════════════════════════════════

    private void drawRoundRect(DrawContext ctx, int x, int y, int w, int h, int col) {
        ctx.fill(x + 2, y, x + w - 2, y + h, col);
        ctx.fill(x, y + 2, x + 2, y + h - 2, col);
        ctx.fill(x + w - 2, y + 2, x + w, y + h - 2, col);
    }

    private void drawRoundRectBorder(DrawContext ctx, int x, int y, int w, int h, int col) {
        ctx.fill(x + 2, y, x + w - 2, y + 1, col);
        ctx.fill(x + 2, y + h - 1, x + w - 2, y + h, col);
        ctx.fill(x, y + 2, x + 1, y + h - 2, col);
        ctx.fill(x + w - 1, y + 2, x + w, y + h - 2, col);
    }

    private void drawBorder(DrawContext ctx, int x, int y, int w, int h, int col) {
        ctx.fill(x, y, x + w, y + 1, col);
        ctx.fill(x, y + h - 1, x + w, y + h, col);
        ctx.fill(x, y, x + 1, y + h, col);
        ctx.fill(x + w - 1, y, x + w, y + h, col);
    }

    private void drawGlowRect(DrawContext ctx, int x, int y, int w, int h, int col, int layers) {
        for (int i = layers; i >= 0; i--) {
            int a = (col >> 24) & 0xFF;
            int layerA = (int)(a * (i / (float)layers) * 0.4f);
            ctx.fill(x - i, y - i, x + w + i, y + h + i,
                    (col & 0x00FFFFFF) | (layerA << 24));
        }
    }

    private void drawGlowLine(DrawContext ctx, int x1, int y, int x2, int y2, int col) {
        ctx.fill(x1, y, x2, y + 1, col);
        int a = ((col >> 24) & 0xFF) / 3;
        ctx.fill(x1, y - 1, x2, y, (col & 0x00FFFFFF) | (a << 24));
        ctx.fill(x1, y + 1, x2, y + 2, (col & 0x00FFFFFF) | (a << 24));
    }

    private void drawFadeRect(DrawContext ctx, int x, int y, int w, int h, int colLeft, int colRight) {
        // Approximate horizontal gradient with several vertical slices
        for (int i = 0; i < 16; i++) {
            float t = i / 15f;
            int r = lerp((colLeft >> 16) & 0xFF, (colRight >> 16) & 0xFF, t);
            int g = lerp((colLeft >>  8) & 0xFF, (colRight >>  8) & 0xFF, t);
            int b = lerp( colLeft        & 0xFF,  colRight        & 0xFF, t);
            int a = lerp((colLeft >> 24) & 0xFF, (colRight >> 24) & 0xFF, t);
            int sliceX = x + i * w / 16;
            int nextX  = x + (i + 1) * w / 16;
            ctx.fill(sliceX, y, nextX, y + h, (a << 24) | (r << 16) | (g << 8) | b);
        }
    }

    private void drawCircle(DrawContext ctx, int cx, int cy, int r, int col) {
        for (int dy = -r; dy <= r; dy++) {
            int span = (int)Math.sqrt(r * r - dy * dy);
            ctx.fill(cx - span, cy + dy, cx + span, cy + dy + 1, col);
        }
    }

    private void drawCircleBorder(DrawContext ctx, int cx, int cy, int r, int col) {
        for (int angle = 0; angle < 360; angle += 4) {
            double rad = Math.toRadians(angle);
            int bx = cx + (int)(r * Math.cos(rad));
            int by = cy + (int)(r * Math.sin(rad));
            ctx.fill(bx, by, bx + 1, by + 1, col);
        }
    }

    private void drawBoldText(DrawContext ctx, String text, int x, int y, int col, float scale) {
        // Draw text with slight shadow offset for bold look
        ctx.drawText(client.textRenderer, text, x + 1, y + 1,
                color(0, 0, 0, (col >> 24) & 0xFF), false);
        ctx.drawText(client.textRenderer, text, x, y, col, false);
    }

    private String[] wrapText(String text, int maxWidth, int maxLines) {
        List<String> lines = new ArrayList<>();
        String[] words = text.split(" ");
        StringBuilder current = new StringBuilder();
        for (String word : words) {
            String test = current.isEmpty() ? word : current + " " + word;
            if (client.textRenderer.getWidth(test) > maxWidth && !current.isEmpty()) {
                lines.add(current.toString());
                current = new StringBuilder(word);
                if (lines.size() >= maxLines) break;
            } else {
                current = new StringBuilder(test);
            }
        }
        if (!current.isEmpty() && lines.size() < maxLines) lines.add(current.toString());
        return lines.toArray(new String[0]);
    }

    // ═══════════════════════════════════════════════════════════
    // COLOR / MATH HELPERS
    // ═══════════════════════════════════════════════════════════

    private static int color(int r, int g, int b, int a) {
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    private static int lerp(int a, int b, float t) {
        return (int)(a + (b - a) * t);
    }

    private static int lerp(float a, float b, float t) {
        return (int)(a + (b - a) * t);
    }

    private static float easeOutCubic(float t) {
        float t1 = 1f - t;
        return 1f - t1 * t1 * t1;
    }
}
