package com.conchoback.haingon.ui.how_to_use

import android.content.Context
import com.conchoback.haingon.core.base.BaseAdapter
import com.conchoback.haingon.core.extension.loadImage
import com.conchoback.haingon.core.extension.select
import com.conchoback.haingon.core.extension.strings
import com.conchoback.haingon.core.helper.LanguageHelper
import com.conchoback.haingon.data.model.IntroModel
import com.conchoback.haingon.databinding.ItemHowToUseBinding

class HowToUseAdapter(val context: Context) : BaseAdapter<IntroModel, ItemHowToUseBinding>(ItemHowToUseBinding::inflate) {
    override fun onBind(binding: ItemHowToUseBinding, item: IntroModel, position: Int) {
        binding.apply {
            LanguageHelper.setLocale(context)
            loadImage(item.image, imvImage, false)
            tvTitle.text = context.strings( item.content)
            tvTitle.select()
        }
    }
}