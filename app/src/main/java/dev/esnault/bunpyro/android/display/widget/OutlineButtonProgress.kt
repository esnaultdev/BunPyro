package dev.esnault.bunpyro.android.display.widget

import android.content.Context
import android.content.res.TypedArray
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.google.android.material.card.MaterialCardView
import dev.esnault.bunpyro.R
import dev.esnault.bunpyro.common.dpToPx
import dev.esnault.bunpyro.common.getThemeColor
import dev.esnault.bunpyro.databinding.WidgetOutlineButtonProgressBinding


class OutlineButtonProgress(context: Context, attrs: AttributeSet?, defStyleAttr: Int)
    : MaterialCardView(context, attrs, defStyleAttr) {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    private val binding: WidgetOutlineButtonProgressBinding

    var text: CharSequence
        get() = binding.button.text
        set(value) {
            binding.button.text = value
        }

    var icon: Drawable?
        get() = binding.button.icon
        set(value) {
            binding.button.icon = value
        }

    var progress: Boolean
        get() = binding.progress.isVisible
        set(value) {
            binding.button.isInvisible = value
            binding.progress.isVisible = value
        }

    init {
        initDefaultAttrs()

        val inflater = LayoutInflater.from(context)
        binding = WidgetOutlineButtonProgressBinding.inflate(inflater, this)

        val attributes = context.obtainStyledAttributes(attrs, R.styleable.OutlineButtonProgress)
        loadFromAttributes(attributes)
        attributes.recycle()
    }

    private fun initDefaultAttrs() {
        cardElevation = 0f
        strokeWidth = 1f.dpToPx(context.resources.displayMetrics)
        strokeColor = context.getThemeColor(R.attr.colorPrimary)
    }

    private fun loadFromAttributes(attrs: TypedArray) {
        text = attrs.getText(R.styleable.OutlineButtonProgress_android_text)
        icon = attrs.getDrawable(R.styleable.OutlineButtonProgress_icon)
        progress = attrs.getBoolean(R.styleable.OutlineButtonProgress_progress, false)
    }

    fun setText(resId: Int) {
        binding.button.setText(resId)
    }

    fun setIconResource(iconResourceId: Int) {
        binding.button.setIconResource(iconResourceId)
    }

    override fun setOnClickListener(l: OnClickListener?) {
        super.setOnClickListener(l)
        binding.button.setOnClickListener(l)
    }
}
