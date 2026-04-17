package com.conchoback.haingon.core.utils

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import com.conchoback.haingon.R
import com.conchoback.haingon.core.utils.key.AssetsKey
import com.conchoback.haingon.data.model.IntroModel
import com.conchoback.haingon.data.model.LanguageModel
import com.conchoback.haingon.data.model.SelectedModel
import com.facebook.shimmer.Shimmer


object DataLocal {
    val shimmer = Shimmer.AlphaHighlightBuilder().setDuration(1800).setBaseAlpha(0.7f).setHighlightAlpha(0.6f)
            .setDirection(Shimmer.Direction.LEFT_TO_RIGHT).setAutoStart(true).build()

    var lastClickTime = 0L

    var isFailBaseURL = false
    var isCallDataAlready = false

    val sortedClothes = listOf<String>(
        AssetsKey.SHIRT,
        AssetsKey.T_SHIRT,
        AssetsKey.PANT,
    )

    fun getLanguageList(): ArrayList<LanguageModel> {
        return arrayListOf(
            LanguageModel("hi", "Hindi", R.drawable.ic_flag_hindi),
            LanguageModel("es", "Spanish", R.drawable.ic_flag_spanish),
            LanguageModel("fr", "French", R.drawable.ic_flag_french),
            LanguageModel("en", "English", R.drawable.ic_flag_english),
            LanguageModel("pt", "Portuguese", R.drawable.ic_flag_portugeese),
            LanguageModel("de", "German", R.drawable.ic_flag_germani),
            LanguageModel("in", "Indonesian", R.drawable.ic_flag_indo)
        )
    }

    val itemIntroList = listOf(
        IntroModel(R.drawable.img_intro_1, R.string.title_1),
        IntroModel(R.drawable.img_intro_2, R.string.title_2),
        IntroModel(R.drawable.img_intro_3, R.string.title_3),
    )

    val itemHowToUseList = listOf(
        IntroModel(R.drawable.img_how_1, R.string.title_how_1),
        IntroModel(R.drawable.img_how_2, R.string.title_how_2),
        IntroModel(R.drawable.img_how_3, R.string.title_how_3),
        IntroModel(R.drawable.img_how_4, R.string.title_how_4),
        IntroModel(R.drawable.img_how_5, R.string.title_how_5),
        IntroModel(R.drawable.img_how_6, R.string.title_how_6),
    )

    fun getBackgroundColorDefault(context: Context): ArrayList<SelectedModel> {
        return arrayListOf(
            SelectedModel(color = ContextCompat.getColor(context, R.color.color_1)),
            SelectedModel(color = ContextCompat.getColor(context, R.color.color_2)),
            SelectedModel(color = ContextCompat.getColor(context, R.color.color_3)),
            SelectedModel(color = ContextCompat.getColor(context, R.color.color_4)),
            SelectedModel(color = ContextCompat.getColor(context, R.color.color_5)),
            SelectedModel(color = ContextCompat.getColor(context, R.color.color_6)),
            SelectedModel(color = ContextCompat.getColor(context, R.color.color_7)),
            SelectedModel(color = ContextCompat.getColor(context, R.color.color_8)),
            SelectedModel(color = ContextCompat.getColor(context, R.color.color_9)),
            SelectedModel(color = ContextCompat.getColor(context, R.color.color_10)),
            SelectedModel(color = ContextCompat.getColor(context, R.color.color_11)),
            SelectedModel(color = ContextCompat.getColor(context, R.color.color_12)),
            SelectedModel(color = ContextCompat.getColor(context, R.color.color_13)),
            SelectedModel(color = ContextCompat.getColor(context, R.color.color_14)),
            SelectedModel(color = ContextCompat.getColor(context, R.color.color_15)),
            SelectedModel(color = ContextCompat.getColor(context, R.color.color_16)),
            SelectedModel(color = ContextCompat.getColor(context, R.color.color_17)),
            SelectedModel(color = ContextCompat.getColor(context, R.color.color_18)),
            SelectedModel(color = ContextCompat.getColor(context, R.color.color_19)),
        )
    }

    val bottomNavigationNotSelect = arrayListOf(
        R.drawable.ic_home,
        R.drawable.ic_my_creation,
        R.drawable.ic_settings,
    )

    val bottomNavigationSelected = arrayListOf(
        R.drawable.ic_home_selected,
        R.drawable.ic_my_creation_selected,
        R.drawable.ic_settings_selected,
    )

    fun getTextFontDefault(): ArrayList<SelectedModel> {
        return arrayListOf(
            SelectedModel(color = R.font.roboto_regular, isSelected = true),
            SelectedModel(color = R.font.aldrich),
            SelectedModel(color = R.font.brush_script),
            SelectedModel(color = R.font.nova_script),
            SelectedModel(color = R.font.carattere),
            SelectedModel(color = R.font.digital_numbers),
            SelectedModel(color = R.font.dynalight),
            SelectedModel(color = R.font.edwardian_script_itc),
            SelectedModel(color = R.font.vni_ongdo)
        )
    }

    fun getTextColorDefault(context: Context): ArrayList<SelectedModel> {
        return arrayListOf(
            SelectedModel(color = ContextCompat.getColor(context, R.color.color_9)),
            SelectedModel(color = ContextCompat.getColor(context, R.color.black), isSelected = true),
            SelectedModel(color = ContextCompat.getColor(context, R.color.white)),
            SelectedModel(color = ContextCompat.getColor(context, R.color.color_19)),
            SelectedModel(color = ContextCompat.getColor(context, R.color.color_2)),
            SelectedModel(color = ContextCompat.getColor(context, R.color.color_3)),
            SelectedModel(color = ContextCompat.getColor(context, R.color.color_4)),
            SelectedModel(color = ContextCompat.getColor(context, R.color.color_5)),
            SelectedModel(color = ContextCompat.getColor(context, R.color.color_6)),
            SelectedModel(color = ContextCompat.getColor(context, R.color.color_7)),
            SelectedModel(color = ContextCompat.getColor(context, R.color.color_8))
        )
    }
}
