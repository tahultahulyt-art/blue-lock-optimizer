package com.rahultheog.bluelockoptimizer;

import net.minecraft.client.gui.DrawContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FootballOpenAnimation {

    private static final float DURATION_TICKS = 9f;
    private float tick = 0f;
    private boolean running = false;

    private static class FootballParticle {
        float x, y, vx, vy, size;
        String symbol;
        FootballParticle(float vx, float vy, float size, String sym) {
            this.vx=vx; this.vy=vy; this.size=size; this.symbol=sym;
        }
    }

    private static class SparkLine {
        float angle, maxLen;
        int color;
        SparkLine(float angle, float maxLen, int color) {
            this.angle=angle; this.maxLen=maxLen; this.color=color;
        }
    }

    private final List<FootballParticle> particles = new ArrayList<>();
    private final List<SparkLine> sparks = new ArrayList<>();
    private final Random rng = new Random();

    public void start() {
        tick=0f; running=true;
        particles.clear(); sparks.clear();

        for (int i = 0; i < 12; i++) {
            float angle = (float)(2*Math.PI*i/12) + rng.nextFloat()*0.4f;
            float speed = 2.5f + rng.nextFloat()*2f;
            particles.add(new FootballParticle(
                (float)Math.cos(angle)*speed,
                (float)Math.sin(angle)*speed,
                0.6f + rng.nextFloat()*0.4f, "O"
            ));
        }

        int[] colors = {0xFF00CFFF, 0xFF0077CC, 0xFF00AAFF, 0xFF33DDFF};
        for (int i = 0; i < 8; i++) {
            sparks.add(new SparkLine(
                (float)(2*Math.PI*i/8),
                40f + rng.nextFloat()*30f,
                colors[i % colors.length]
            ));
        }
    }

    public void tick(float delta) {
        if (!running) return;
        tick += delta;
        if (tick >= DURATION_TICKS) running = false;
    }

    public float getProgress() {
        return Math.min(1f, tick / DURATION_TICKS);
    }

    public void renderParticles(DrawContext ctx, int cx, int cy, float p) {
        float t = p * DURATION_TICKS;

        for (SparkLine s : sparks) {
            float len = s.maxLen * easeOut(Math.min(1f, p*3f));
            int col = withAlpha(s.color, (int)((1f-p)*255));
            drawLine(ctx, cx, cy,
                cx+(int)(Math.cos(s.angle)*len),
                cy+(int)(Math.sin(s.angle)*len), col);
        }

        float bigAlpha = p < 0.5f ? p*2f : 1f-(p-0.5f)*2f;
        float bigScale = 3f - 2f*easeOut(p);
        if (bigAlpha > 0.05f) {
            ctx.getMatrices().push();
            ctx.getMatrices().translate(cx, cy, 0);
            ctx.getMatrices().scale(bigScale, bigScale, 1f);
            ctx.drawTextWithShadow(
                net.minecraft.client.MinecraftClient.getInstance().textRenderer,
                net.minecraft.text.Text.literal("O"),
                -4, -4, withAlpha(0xFFFFFFFF, (int)(bigAlpha*255))
            );
            ctx.getMatrices().pop();
        }

        for (FootballParticle fp : particles) {
            fp.x = fp.vx * t;
            fp.y = fp.vy * t;
            int a = (int)((1f-p)*255);
            if (a <= 0) continue;
            ctx.getMatrices().push();
            ctx.getMatrices().translate(cx+fp.x, cy+fp.y, 0);
            ctx.getMatrices().scale(fp.size, fp.size, 1f);
            ctx.drawTextWithShadow(
                net.minecraft.client.MinecraftClient.getInstance().textRenderer,
                net.minecraft.text.Text.literal(fp.symbol),
                -4, -4, withAlpha(0xFFFFFFFF, a)
            );
            ctx.getMatrices().pop();
        }

        drawCircleOutline(ctx, cx, cy,
            (int)(60f*easeOut(p)),
            withAlpha(0xFF00CFFF, (int)((1f-p)*180)));
    }

    private static int withAlpha(int color, int alpha) {
        return (color & 0x00FFFFFF) | (Math.max(0, Math.min(255, alpha)) << 24);
    }

    private static float easeOut(float t) {
        t = Math.min(1f, Math.max(0f, t));
        return 1f-(1f-t)*(1f-t);
    }

    private static void drawLine(DrawContext ctx, int x1, int y1, int x2, int y2, int color) {
        int dx=x2-x1, dy=y2-y1;
        int steps = Math.max(Math.abs(dx), Math.abs(dy));
        if (steps==0) return;
        float sx=dx/(float)steps, sy=dy/(float)steps;
        for (int i=0; i<=steps; i+=2) {
            ctx.fill((int)(x1+sx*i),(int)(y1+sy*i),
                     (int)(x1+sx*i)+2,(int)(y1+sy*i)+2, color);
        }
    }

    private static void drawCircleOutline(DrawContext ctx, int cx, int cy, int r, int color) {
        if (r<=0) return;
        for (int i=0; i<48; i++) {
            double a1=2*Math.PI*i/48, a2=2*Math.PI*(i+1)/48;
            ctx.fill(
                (int)(cx+Math.cos(a1)*r),(int)(cy+Math.sin(a1)*r),
                (int)(cx+Math.cos(a2)*r)+2,(int)(cy+Math.sin(a2)*r)+2,
                color
            );
        }
    }
}
