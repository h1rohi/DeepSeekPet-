package com.example.deepseekpet

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.os.Handler
import android.os.Looper
import android.view.View
import kotlin.math.min
import kotlin.math.sin

class PetView(context: Context) : View(context) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var lastTime = System.currentTimeMillis()
    private var blink = false
    private var nextBlink = System.currentTimeMillis() + 3000
    private var bubbleText: String? = null

    private val handler = Handler(Looper.getMainLooper())
    private val bubbleRunnable = Runnable {
        bubbleText = null
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val size = (300 * resources.displayMetrics.density).toInt()
        setMeasuredDimension(size, size)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val now = System.currentTimeMillis()
        val t = (now - lastTime) / 1000f
        val breath = 1f + 0.03f * sin(t * 2f)

        if (now > nextBlink) {
            blink = true
            if (now > nextBlink + 150) {
                blink = false
                nextBlink = now + 2000 + (Math.random() * 3000).toLong()
            }
        }

        val w = width.toFloat()
        val h = height.toFloat()
        val cx = w / 2
        val cy = h * 0.58f
        val base = min(w, h) / 2 * 0.68f * breath

        // 鲸鱼尾巴
        paint.color = Color.parseColor("#1E3A8A")
        val tailPath = Path().apply {
            moveTo(cx + base * 0.3f, cy + base * 0.6f)
            quadTo(cx + base * 1.2f, cy + base * 0.2f, cx + base * 0.9f, cy - base * 0.3f)
            quadTo(cx + base * 0.7f, cy + base * 0.1f, cx + base * 0.2f, cy + base * 0.4f)
            close()
        }
        canvas.drawPath(tailPath, paint)

        // 后层头发
        paint.color = Color.parseColor("#3B82F6")
        canvas.drawCircle(cx, cy - base * 0.1f, base * 0.75f, paint)

        // 脸
        paint.color = Color.parseColor("#FFE0D0")
        canvas.drawCircle(cx, cy, base * 0.55f, paint)

        // 刘海
        paint.color = Color.parseColor("#60A5FA")
        val bangs = Path().apply {
            moveTo(cx - base * 0.6f, cy - base * 0.2f)
            quadTo(cx, cy - base * 1.0f, cx + base * 0.6f, cy - base * 0.2f)
            quadTo(cx + base * 0.3f, cy - base * 0.4f, cx, cy - base * 0.3f)
            quadTo(cx - base * 0.3f, cy - base * 0.4f, cx - base * 0.6f, cy - base * 0.2f)
            close()
        }
        canvas.drawPath(bangs, paint)

        // 眼睛
        val eyeY = cy + base * 0.05f
        val eyeR = base * 0.12f
        if (blink) {
            paint.color = Color.parseColor("#1E40AF")
            paint.strokeWidth = base * 0.05f
            paint.style = Paint.Style.STROKE
            canvas.drawLine(cx - base * 0.3f, eyeY, cx - base * 0.1f, eyeY, paint)
            canvas.drawLine(cx + base * 0.1f, eyeY, cx + base * 0.3f, eyeY, paint)
            paint.style = Paint.Style.FILL
        } else {
            paint.color = Color.parseColor("#1E40AF")
            canvas.drawCircle(cx - base * 0.2f, eyeY, eyeR, paint)
            canvas.drawCircle(cx + base * 0.2f, eyeY, eyeR, paint)

            paint.color = Color.WHITE
            canvas.drawCircle(cx - base * 0.23f, eyeY - base * 0.04f, eyeR * 0.35f, paint)
            canvas.drawCircle(cx + base * 0.17f, eyeY - base * 0.04f, eyeR * 0.35f, paint)
        }

        // 腮红
        paint.color = Color.parseColor("#FFB6C1")
        paint.alpha = 180
        canvas.drawCircle(cx - base * 0.35f, cy + base * 0.25f, base * 0.1f, paint)
        canvas.drawCircle(cx + base * 0.35f, cy + base * 0.25f, base * 0.1f, paint)
        paint.alpha = 255

        // 嘴
        paint.color = Color.parseColor("#E11D48")
        paint.strokeWidth = base * 0.03f
        paint.style = Paint.Style.STROKE
        val mouth = Path().apply {
            moveTo(cx - base * 0.08f, cy + base * 0.3f)
            quadTo(cx, cy + base * 0.4f, cx + base * 0.08f, cy + base * 0.3f)
        }
        canvas.drawPath(mouth, paint)
        paint.style = Paint.Style.FILL

        // 衣服
        paint.color = Color.parseColor("#1E3A8A")
        val cloth = Path().apply {
            moveTo(cx - base * 0.6f, cy + base * 0.7f)
            quadTo(cx, cy + base * 0.5f, cx + base * 0.6f, cy + base * 0.7f)
            lineTo(cx + base * 0.6f, cy + base * 1.0f)
            lineTo(cx - base * 0.6f, cy + base * 1.0f)
            close()
        }
        canvas.drawPath(cloth, paint)

        // 鲸鱼尾巴装饰
        paint.color = Color.parseColor("#A78BFA")
        canvas.drawCircle(cx + base * 0.5f, cy + base * 0.8f, base * 0.15f, paint)

        // 气泡
        bubbleText?.let { text ->
            paint.color = Color.argb(230, 255, 255, 255)
            paint.style = Paint.Style.FILL
            val rect = RectF(
                cx - base * 1.2f,
                cy - base * 1.5f,
                cx + base * 1.2f,
                cy - base * 0.8f
            )
            canvas.drawRoundRect(rect, 20f, 20f, paint)

            paint.color = Color.parseColor("#1E3A8A")
            paint.textSize = base * 0.25f
            paint.textAlign = Paint.Align.CENTER
            canvas.drawText(text, cx, rect.centerY() + paint.textSize / 3, paint)
        }

        postInvalidateOnAnimation()
    }

    fun triggerBubble(text: String = "你好呀，我是DeepSeek娘~") {
        bubbleText = text
        handler.removeCallbacks(bubbleRunnable)
        handler.postDelayed(bubbleRunnable, 3000)
        invalidate()
    }
}
