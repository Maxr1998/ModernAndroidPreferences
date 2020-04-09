package de.Maxr1998.modernpreferences.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatSeekBar
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.ViewCompat
import de.Maxr1998.modernpreferences.R
import java.lang.reflect.Method

class ModernSeekBar(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : AppCompatSeekBar(context, attrs, defStyleAttr) {
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.attr.seekBarStyle)
    constructor(context: Context) : this(context, null)

    var hasTickMarks
        get() = getCompatTickMark() != null
        set(value) {
            setCompatTickMark(if (value) {
                context.getDrawable(R.drawable.map_seekbar_tick_mark)
            } else null)
        }

    var default: Int? = null
        set(value) {
            require(value == null || value in 0..max) {
                "Default must be in range 0 to max (is $value)"
            }
            field = value
            if (value != null) {
                if (defaultMarker == null)
                    defaultMarker = context.getDrawable(R.drawable.map_seekbar_default_marker)
            } else defaultMarker = null
        }

    private var defaultMarker: Drawable? = null
        set(value) {
            field?.apply {
                callback = null
            }
            field = value

            if (value != null) {
                value.callback = this
                DrawableCompat.setLayoutDirection(value, ViewCompat.getLayoutDirection(this))
                if (value.isStateful) value.state = drawableState
            }

            invalidate()
        }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (progress != default) drawDefaultMarker(canvas)
    }

    override fun drawableStateChanged() {
        super.drawableStateChanged()
        val defaultMarker = defaultMarker
        if (defaultMarker != null && defaultMarker.isStateful && defaultMarker.setState(drawableState)) {
            invalidateDrawable(defaultMarker)
        }
    }

    override fun jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState()
        defaultMarker?.jumpToCurrentState()
    }

    private fun drawDefaultMarker(canvas: Canvas) {
        val default = default ?: return
        defaultMarker?.let {
            val w: Int = it.intrinsicWidth
            val h: Int = it.intrinsicHeight
            val halfW = if (w >= 0) w / 2 else 1
            val halfH = if (h >= 0) h / 2 else 1
            it.setBounds(-halfW, -halfH, halfW, halfH)
            val saveCount = canvas.save()
            val spacing: Float = default * (width - paddingLeft - paddingRight) / max.toFloat()
            canvas.translate(paddingLeft.toFloat() + spacing, height / 2.toFloat())
            it.draw(canvas)
            canvas.restoreToCount(saveCount)
        }
    }

    private val helper: Any = AppCompatSeekBar::class.java.getDeclaredField("mAppCompatSeekBarHelper")
            .apply { isAccessible = true }.get(this)
    private val getTickMarkMethod: Method = helper::class.java.getDeclaredMethod("getTickMark")
            .apply { isAccessible = true }
    private val setTickMarkMethod: Method = helper::class.java.getDeclaredMethod("setTickMark", Drawable::class.java)
            .apply { isAccessible = true }

    private fun getCompatTickMark(): Drawable? {
        return getTickMarkMethod.invoke(helper) as Drawable?
    }

    private fun setCompatTickMark(tickMark: Drawable?) {
        setTickMarkMethod.invoke(helper, tickMark)
    }
}