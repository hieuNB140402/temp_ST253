package com.conchoback.haingon.ui.language

import android.content.Context
import com.conchoback.haingon.R
import com.conchoback.haingon.core.base.BaseAdapter
import com.conchoback.haingon.core.extension.loadImage
import com.conchoback.haingon.core.extension.tap
import com.conchoback.haingon.data.model.LanguageModel
import com.conchoback.haingon.databinding.ItemLanguageBinding
import kotlin.apply

class LanguageAdapter(val context: Context) : BaseAdapter<LanguageModel, ItemLanguageBinding>(ItemLanguageBinding::inflate) {
    var onItemClick: ((String) -> Unit) = {}
    override fun onBind(binding: ItemLanguageBinding, item: LanguageModel, position: Int) {
        binding.apply {
            loadImage(item.flag, imvFlag, false)
            tvLang.text = item.name

            val ratio= if (item.activate) R.drawable.ic_tick_lang else R.drawable.ic_not_tick_lang
            loadImage(ratio, btnRadio, false)

            root.tap { onItemClick.invoke(item.code) }
        }
    }
}