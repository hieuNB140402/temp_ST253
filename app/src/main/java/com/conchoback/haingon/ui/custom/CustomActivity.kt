package com.conchoback.haingon.ui.custom

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.toColorInt
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.signature.ObjectKey
import com.conchoback.haingon.R
import com.conchoback.haingon.core.base.BaseActivity
import com.conchoback.haingon.core.extension.eLog
import com.conchoback.haingon.core.extension.gone
import com.conchoback.haingon.core.extension.handleBackLeftToRight
import com.conchoback.haingon.core.extension.loadImage
import com.conchoback.haingon.core.extension.openImagePicker
import com.conchoback.haingon.core.extension.tap
import com.conchoback.haingon.core.extension.visible
import com.conchoback.haingon.core.utils.key.IntentKey
import com.conchoback.haingon.core.utils.key.RequestKey
import com.conchoback.haingon.core.utils.key.ValueKey
import com.conchoback.haingon.core.utils.state.SaveState
import com.conchoback.haingon.data.model.draw.Draw
import com.conchoback.haingon.data.model.draw.DrawableDraw
import com.conchoback.haingon.databinding.ActivityCustomBinding
import com.conchoback.haingon.dialog.text.TextDialog
import com.conchoback.haingon.listener.listenerdraw.OnDrawListener
import com.conchoback.haingon.ui.custom.adapter.ItemAdapter
import com.raed.rasmview.brushtool.data.Brush
import com.raed.rasmview.brushtool.data.BrushesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.text.toFloat

class CustomActivity : BaseActivity<ActivityCustomBinding>() {

    private val viewModel: CustomViewModel by viewModels()
    private val itemAdapter by lazy { ItemAdapter() }

    override fun setViewBinding(): ActivityCustomBinding {
        return ActivityCustomBinding.inflate(LayoutInflater.from(this))
    }

    override fun initView() {
        loadImage(
            viewModel.fullDomainImage(this, intent.getStringExtra(IntentKey.IMAGE_KEY) ?: ""),
            binding.imvClothes,
            false
        )
        viewModel.updateTypeClothesSelected(intent.getStringExtra(IntentKey.TYPE_CLOTHES_KEY) ?: ValueKey.SHIRT)
        viewModel.setTypeOption(intent.getIntExtra(IntentKey.INTENT_KEY, ValueKey.BRUSH_OPTION))
    }

    override fun dataObservable() {
        lifecycleScope.launch {
            launch { viewModel.typeOption.collect { type -> setupType(type) } }
        }
    }

    override fun viewListener() {
        binding.btnDone.tap { handleDone() }
    }

    // Init
    //==================================================================================================================
    override fun initActionBar() {}

    // Handle
    //==================================================================================================================

    /* List */
    private fun setupList() {
        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.loadDataList(this@CustomActivity)

            withContext(Dispatchers.Main) {
                binding.apply {
                    lnlOption.gone()
                    colorPicker.gone()
                    rasmView.gone()
                    rcvList.visible()

                    rcvList.apply {
                        adapter = itemAdapter
                        itemAnimator = null
                    }

                    initDrawView()
                }

                itemAdapter.submitList(viewModel.itemRcvList)
            }
        }

        itemAdapter.onItemClick = { path, position ->
            if (position == 0 && viewModel.typeOption.value == ValueKey.IMAGE_OPTION) {
                openImagePicker()
            } else {
                addDrawable(path)
            }
        }
    }

    /* Brush */
    private fun setupBrush() {
        binding.apply {
            lnlOption.visible()
            colorPicker.visible()
            rasmView.visible()

            rcvList.gone()
            drawView.gone()

            viewModel.brushColorDefault = "#000000".toColorInt()
            sbSize.setProgress((viewModel.brushSizeDefault * 100).toInt())

            rasmView.rasmContext.apply {
                setBackgroundColor(getColor(R.color.transparent))
                brushConfig = BrushesRepository(resources).get(Brush.Pen)
                brushConfig.size = viewModel.brushSizeDefault
                brushColor = viewModel.brushColorDefault
            }

            colorPicker.setInitialColor(viewModel.brushColorDefault)

            btnChooseBrush.tap { handleBrush() }
            btnEraser.tap { handleEraser() }
            handleSeekbarListener()
            handleChangeColor()
        }
    }

    private fun handleSeekbarListener() = with(binding) {
        sbSize.onSizeChanged = { progress ->
            val value = maxOf(2f, progress.toFloat())

            rasmView.rasmContext.brushConfig.size = value / 100f
        }
    }

    private fun handleChangeColor() {
        binding.colorPicker.subscribe { color, fromUser, shouldPropagate ->
            binding.rasmView.rasmContext.brushColor = color
        }
    }

    private fun handleBrush() = with(binding) {
        rasmView.rasmContext.apply {
            brushConfig = BrushesRepository(resources).get(Brush.Pen)
        }
        btnChooseBrush.setBackgroundResource(R.drawable.bg_1000_solid_white)
        btnEraser.setBackgroundResource(R.color.transparent)

        colorPicker.visible()
    }

    private fun handleEraser() = with(binding) {
        rasmView.rasmContext.apply {
            brushConfig = BrushesRepository(resources).get(Brush.HardEraser)
        }
        btnChooseBrush.setBackgroundResource(R.color.transparent)
        btnEraser.setBackgroundResource(R.drawable.bg_1000_solid_white)

        colorPicker.gone()
    }

    /* Text */
    private fun setupText() {
        setupDefaultOption()

        initDrawView()

        val dialog = TextDialog(this@CustomActivity)
        dialog.show()

        dialog.onDoneClick = { bitmap ->
            if (bitmap != null) {
                addDrawable("", false, bitmap)
            }
        }
    }

    private fun setupDefaultOption() = with(binding) {
        lnlOption.gone()
        colorPicker.gone()
        rasmView.gone()
        rcvList.gone()
    }

    /* Other */
    private fun initDrawView() {
        binding.drawView.apply {
            setConstrained(true)
            setLocked(false)
            setOnDrawListener(object : OnDrawListener {
                override fun onAddedDraw(draw: Draw) {
                    viewModel.updateCurrentCurrentDraw(draw)
                    viewModel.addDrawView(draw)
                    checkExist()
                }

                override fun onClickedDraw(draw: Draw) {}

                override fun onDeletedDraw(draw: Draw) {
                    viewModel.deleteDrawView(draw)
                    checkExist()
                }

                override fun onDragFinishedDraw(draw: Draw) {}

                override fun onTouchedDownDraw(draw: Draw) {
                    viewModel.updateCurrentCurrentDraw(draw)
                }

                override fun onZoomFinishedDraw(draw: Draw) {}

                override fun onFlippedDraw(draw: Draw) {}

                override fun onDoubleTappedDraw(draw: Draw) {}

                override fun onHideOptionIconDraw() {}

                override fun onUndoDeleteDraw(draw: List<Draw?>) {}

                override fun onUndoUpdateDraw(draw: List<Draw?>) {}

                override fun onUndoDeleteAll() {}

                override fun onRedoAll() {}

                override fun onReplaceDraw(draw: Draw) {}

                override fun onEditText(draw: DrawableDraw) {}

                override fun onReplace(draw: Draw) {}
            })
        }
    }

    private fun checkExist() {
        viewModel.updateIsDrawExist(binding.drawView.getDraws().isNotEmpty())
    }

    private fun addDrawable(path: String, isCharacter: Boolean = false, bitmap: Bitmap? = null) {
        val contextActivity = this
        lifecycleScope.launch(Dispatchers.IO) {
            val bitmapDefault = if (bitmap == null) {
                Glide.with(contextActivity)
                    .load(path)
                    .signature(ObjectKey(File(path).lastModified()))
                    .submit().get().toBitmap()
            } else {
                bitmap
            }
            val drawableEmoji = viewModel.loadDrawableEmoji(contextActivity, bitmapDefault, isCharacter)

            withContext(Dispatchers.Main) {
                drawableEmoji.let { binding.drawView.addDraw(it) }
            }
        }
    }

    private fun handleDone() {
        binding.drawView.hideSelect()

        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.handleDone(this@CustomActivity, binding.flExport).collect { state ->
                when (state) {
                    SaveState.Loading -> showLoading()
                    
                    SaveState.Nothing -> {
                        withContext(Dispatchers.Main) {
                            handleBackLeftToRight()
                        }
                    }

                    is SaveState.Error -> {
                        eLog("handleDone: ${state.exception}")
                        dismissLoading(true)
                        withContext(Dispatchers.Main) {
                            showToast(R.string.an_error_occurred_please_try_again_later)
                        }
                    }

                    is SaveState.Success -> {
                        dismissLoading(true)
                        
                        withContext(Dispatchers.Main) {
                            val resultIntent = Intent()
                            resultIntent.putExtra(IntentKey.EDITED_CLOTHES_KEY, state.path)
                            resultIntent.putExtra(IntentKey.TYPE_CLOTHES_KEY, viewModel.typeClothesSelected)
                            setResult(RESULT_OK, resultIntent)
                            handleBackLeftToRight()
                        }
                    }
                }
            }
        }
    }

    // Observable
    //==================================================================================================================
    private fun setupType(type: Int) {
        when (type) {
            ValueKey.IMAGE_OPTION -> setupList()
            ValueKey.BRUSH_OPTION -> setupBrush()
            ValueKey.STICKER_OPTION -> setupList()
            ValueKey.EMOJI_OPTION -> setupList()
            ValueKey.TEXT_OPTION -> setupText()
            else -> setupDefaultOption()
        }
    }

    // Result + Permission
    //==================================================================================================================
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RequestKey.PICK_IMAGE_REQUEST_CODE && resultCode == RESULT_OK) {
            val selectedImageUri: Uri? = data?.data
            addDrawable(selectedImageUri.toString())
        }
    }

    // Ads
    //==================================================================================================================

}