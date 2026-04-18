package com.conchoback.haingon.ui.custom

import android.R.attr.path
import android.content.Context
import android.graphics.Bitmap
import android.view.View
import android.widget.FrameLayout
import androidx.core.graphics.drawable.toDrawable
import androidx.lifecycle.ViewModel
import com.conchoback.haingon.core.custom.drawview.DrawView
import com.conchoback.haingon.core.helper.AssetHelper
import com.conchoback.haingon.core.helper.BitmapHelper
import com.conchoback.haingon.core.helper.MediaHelper
import com.conchoback.haingon.core.utils.DataLocal
import com.conchoback.haingon.core.utils.key.AssetsKey
import com.conchoback.haingon.core.utils.key.DomainKey
import com.conchoback.haingon.core.utils.key.ValueKey
import com.conchoback.haingon.core.utils.state.SaveState
import com.conchoback.haingon.data.model.draw.Draw
import com.conchoback.haingon.data.model.draw.DrawableDraw
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.File

class CustomViewModel : ViewModel() {
    // Flow Declaration
    //==================================================================================================================
    private val _typeOption = MutableStateFlow(ValueKey.NONE_OPTION)
    val typeOption = _typeOption.asStateFlow()


    // Normal Declaration
    //==================================================================================================================
    var itemRcvList: ArrayList<String> = arrayListOf()

    var currentDraw: Draw? = null

    var drawViewList: ArrayList<Draw> = arrayListOf()


    var brushSizeDefault = 0.3f
    var brushColorDefault = 0

    var isDrawExist = false

    var typeClothesSelected = ValueKey.SHIRT

    // Getter Setter
    //==================================================================================================================
    fun setTypeOption(type: Int) {
        _typeOption.value = type
    }

    fun updateCurrentCurrentDraw(draw: Draw) {
        currentDraw = draw
    }

    fun updateTypeClothesSelected(type: String) {
        typeClothesSelected = type
    }

    fun updateIsDrawExist(isExist: Boolean) {
        isDrawExist = isExist
    }

    // Function feature
    //==================================================================================================================
    suspend fun loadDataList(context: Context) {

        val folder = when (_typeOption.value) {
            ValueKey.IMAGE_OPTION -> AssetsKey.IMAGE_ASSET
            ValueKey.STICKER_OPTION -> AssetsKey.STICKER_ASSET
            ValueKey.EMOJI_OPTION -> AssetsKey.EMOJI_ASSET
            else -> ""
        }

        val list = AssetHelper.getSubfoldersAsset(context, folder)

        itemRcvList.clear()
        itemRcvList.addAll(list)
    }

    fun addDrawView(draw: Draw) {
        drawViewList.add(draw)
    }

    fun deleteDrawView(draw: Draw) {
        drawViewList.removeIf { it == draw }
    }


    fun loadDrawableEmoji(context: Context, bitmap: Bitmap, isCharacter: Boolean = false, isText: Boolean = false): DrawableDraw {
        val drawable = bitmap.toDrawable(context.resources)
        val drawableEmoji = DrawableDraw(drawable, "${System.currentTimeMillis()}.png")
        drawableEmoji.isCharacter = isCharacter
        drawableEmoji.isText = isText
        return drawableEmoji
    }

    fun resetDraw() {
        drawViewList.clear()
    }

    suspend fun saveImageFromView(context: Context, view: View): Flow<SaveState> = flow {
        emit(SaveState.Loading)
        val bitmap = BitmapHelper.createBimapFromView(view)
        MediaHelper.saveBitmapToInternalStorage(context, ValueKey.TEMP_ALBUM, bitmap).collect { state -> emit(state) }
    }.flowOn(Dispatchers.IO)

    fun fullDomainImage(context: Context, image: String): String {
        return when {
            image.contains(ValueKey.TEMP_ALBUM) -> {
                // Internal
                val file = File(context.filesDir, image)
                file.absolutePath
            }

            image.contains(DomainKey.SPECIAL_CATEGORY) -> {
                // api
                val domain = if (DataLocal.isFailBaseURL) DomainKey.BASE_URL_PREVENTIVE else DomainKey.BASE_URL
                "${domain}/${DomainKey.SUB_DOMAIN}/$image"
            }

            else -> {
                // Asset
                "${AssetsKey.ASSET_MANAGER}/$image"
            }
        }
    }

    suspend fun handleDone(context: Context, exportView: FrameLayout) = flow {
        if (_typeOption.value != ValueKey.BRUSH_OPTION && drawViewList.isEmpty()) {
            emit(SaveState.Nothing)
        } else {
            // Luu anh -> Internal
            emitAll(saveImageFromView(context, exportView))
        }

    }.flowOn(Dispatchers.IO)
}