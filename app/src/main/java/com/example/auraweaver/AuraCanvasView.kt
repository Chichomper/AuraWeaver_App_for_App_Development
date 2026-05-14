package com.example.auraweaver

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import kotlin.math.hypot
import kotlin.math.sin
import kotlin.random.Random

class AuraCanvasView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val random = Random.Default
    private var particles = mutableListOf<Particle>()
    private var time = 0f
    private var vibeType = 0
    private var colorIntensity = 70
    private var particleCount = 50
    private var speed = 0.5f
    private var isRunning = true

    private inner class Particle(w: Float, h: Float) {
        var x = random.nextFloat() * w
        var y = random.nextFloat() * h
        var vx = (random.nextFloat() - 0.5f) * 2
        var vy = (random.nextFloat() - 0.5f) * 2
        var size = random.nextFloat() * 20 + 5
        var life = random.nextFloat()
        var color = 0

        init { updateColor() }

        fun reset(w: Float, h: Float) {
            x = random.nextFloat() * w
            y = random.nextFloat() * h
            vx = (random.nextFloat() - 0.5f) * 2
            vy = (random.nextFloat() - 0.5f) * 2
            size = random.nextFloat() * 20 + 5
            life = random.nextFloat()
            updateColor()
        }

        fun updateColor() {
            val (r, g, b) = when (vibeType) {
                1 -> Triple((colorIntensity * 0.3).toInt(), (colorIntensity * 0.2).toInt(), colorIntensity)
                2 -> Triple(colorIntensity, (colorIntensity * 0.7).toInt(), (colorIntensity * 0.3).toInt())
                3 -> Triple((colorIntensity * 0.9).toInt(), (colorIntensity * 0.2).toInt(), colorIntensity)
                4 -> Triple((colorIntensity * 0.3).toInt(), colorIntensity, (colorIntensity * 0.4).toInt())
                5 -> Triple(colorIntensity, (colorIntensity * 0.5).toInt(), (colorIntensity * 0.6).toInt())
                else -> Triple(colorIntensity, colorIntensity, colorIntensity)
            }
            color = 0xFF000000.toInt() or (r shl 16) or (g shl 8) or b
        }
    }

    private fun initParticles() {
        particles.clear()
        val w = width.toFloat()
        val h = height.toFloat()
        repeat(particleCount) { particles.add(Particle(w, h)) }
    }

    fun setVibe(type: Int) {
        vibeType = type
        particles.forEach { it.updateColor() }
        invalidate()
    }

    fun setPreferences(intensity: Int, count: Int, spd: Float) {
        colorIntensity = intensity
        particleCount = count
        speed = spd
        initParticles()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        initParticles()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(0xFF0A0A0F.toInt())

        paint.strokeWidth = 1f
        for (i in particles.indices) {
            val p1 = particles[i]
            for (j in i + 1 until particles.size) {
                val p2 = particles[j]
                val dist = hypot(p1.x - p2.x, p1.y - p2.y)
                if (dist < 150) {
                    val alpha = (255 * (1 - dist / 150) * 0.3).toInt()
                    paint.color = (alpha shl 24) or (p1.color and 0x00FFFFFF)
                    canvas.drawLine(p1.x, p1.y, p2.x, p2.y, paint)
                }
            }
        }

        for (p in particles) {
            paint.color = p.color
            paint.alpha = (255 * p.life).toInt()
            canvas.drawCircle(p.x, p.y, p.size * p.life, paint)

            p.x += p.vx * speed
            p.y += p.vy * speed
            p.life -= 0.005f

            if (p.x < 0) p.x = width.toFloat()
            if (p.x > width) p.x = 0f
            if (p.y < 0) p.y = height.toFloat()
            if (p.y > height) p.y = 0f
            if (p.life <= 0) p.reset(width.toFloat(), height.toFloat())
        }

        val cx = width / 2f
        val cy = height / 2f
        for (i in 3 downTo 1) {
            paint.color = if (particles.isEmpty()) 0xFF666666.toInt() else particles[0].color
            paint.alpha = 30 / i
            canvas.drawCircle(cx, cy, 50 * i + sin(time) * 20, paint)
        }

        time += 0.05f
        if (isRunning) invalidate()
    }
}
