package com.conchoback.haingon.ui.intro

import android.content.Context
import com.conchoback.haingon.core.base.BaseAdapter
import com.conchoback.haingon.core.extension.loadImage
import com.conchoback.haingon.core.extension.select
import com.conchoback.haingon.core.extension.strings
import com.conchoback.haingon.core.helper.LanguageHelper
import com.conchoback.haingon.data.model.IntroModel
import com.conchoback.haingon.databinding.ItemIntroBinding
import kotlin.apply

class IntroAdapter(val context: Context) : BaseAdapter<IntroModel, ItemIntroBinding>(ItemIntroBinding::inflate) {
    override fun onBind(binding: ItemIntroBinding, item: IntroModel, position: Int) {
        binding.apply {
            LanguageHelper.setLocale(context)
            loadImage(item.image, imvImage, false)
            tvTitle.text = context.strings( item.content)
            tvTitle.select()
        }
    }
}