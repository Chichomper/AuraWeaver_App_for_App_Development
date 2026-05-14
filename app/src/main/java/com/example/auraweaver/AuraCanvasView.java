package com.example.auraweaver;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AuraCanvasView extends View {
    private final Paint paint;
    private List<Particle> particles;
    private final Random random;
    private float time = 0;
    private int vibeType = 0;
    private int colorIntensity = 70;
    private int particleCount = 50;
    private float speed = 0.5f;
    private boolean isRunning = true;

    private class Particle {
        float x, y, vx, vy, size, life;
        int color;

        Particle(float w, float h) { 
            reset(w, h); 
        }

        void reset(float w, float h) {
            x = random.nextFloat() * w;
            y = random.nextFloat() * h;
            vx = (random.nextFloat() - 0.5f) * 2;
            vy = (random.nextFloat() - 0.5f) * 2;
            size = random.nextFloat() * 20 + 5;
            life = random.nextFloat();
            updateColor();
        }

        void updateColor() {
            int r, g, b;
            switch(vibeType) {
                case 1: 
                    r = (int)(colorIntensity * 0.3); 
                    g = (int)(colorIntensity * 0.2); 
                    b = colorIntensity; 
                    break;
                case 2: 
                    r = colorIntensity; 
                    g = (int)(colorIntensity * 0.7); 
                    b = (int)(colorIntensity * 0.3); 
                    break;
                case 3: 
                    r = (int)(colorIntensity * 0.9); 
                    g = (int)(colorIntensity * 0.2); 
                    b = colorIntensity; 
                    break;
                case 4: 
                    r = (int)(colorIntensity * 0.3); 
                    g = colorIntensity; 
                    b = (int)(colorIntensity * 0.4); 
                    break;
                case 5: 
                    r = colorIntensity; 
                    g = (int)(colorIntensity * 0.5); 
                    b = (int)(colorIntensity * 0.6); 
                    break;
                default: 
                    r = g = b = colorIntensity;
            }
            color = 0xFF000000 | (r << 16) | (g << 8) | b;
        }
    }

    public AuraCanvasView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        random = new Random();
        particles = new ArrayList<>();
    }

    private void initParticles() {
        particles.clear();
        for (int i = 0; i < particleCount; i++) {
            particles.add(new Particle(getWidth(), getHeight()));
        }
    }

    public void setVibe(int type) {
        vibeType = type;
        for (Particle p : particles) {
            p.updateColor();
        }
        invalidate();
    }

    public void setPreferences(int intensity, int count, float spd) {
        colorIntensity = intensity;
        particleCount = count;
        speed = spd;
        initParticles();
    }

    @Override 
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        initParticles();
    }

    @Override 
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(0xFF0A0A0F);

        paint.setStrokeWidth(1);
        for (int i = 0; i < particles.size(); i++) {
            Particle p1 = particles.get(i);
            for (int j = i + 1; j < particles.size(); j++) {
                Particle p2 = particles.get(j);
                float dist = (float)Math.hypot(p1.x - p2.x, p1.y - p2.y);
                if (dist < 150) {
                    int alpha = (int)(255 * (1 - dist / 150) * 0.3);
                    paint.setColor((alpha << 24) | (p1.color & 0x00FFFFFF));
                    canvas.drawLine(p1.x, p1.y, p2.x, p2.y, paint);
                }
            }
        }

        for (Particle p : particles) {
            paint.setColor(p.color);
            paint.setAlpha((int)(255 * p.life));
            canvas.drawCircle(p.x, p.y, p.size * p.life, paint);
            p.x += p.vx * speed; 
            p.y += p.vy * speed;
            p.life -= 0.005f;
            if (p.x < 0) p.x = getWidth(); 
            if (p.x > getWidth()) p.x = 0;
            if (p.y < 0) p.y = getHeight(); 
            if (p.y > getHeight()) p.y = 0;
            if (p.life <= 0) p.reset(getWidth(), getHeight());
        }

        float cx = getWidth() / 2f;
        float cy = getHeight() / 2f;
        for (int i = 3; i > 0; i--) {
            paint.setColor(particles.isEmpty() ? 0xFF666666 : particles.get(0).color);
            paint.setAlpha(30 / i);
            canvas.drawCircle(cx, cy, 50 * i + (float)Math.sin(time) * 20, paint);
        }
        time += 0.05f;
        if (isRunning) invalidate();
    }
}
