package com.conchoback.haingon.ui.choose_clothes_before

import android.R.attr.type
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.conchoback.haingon.R
import com.conchoback.haingon.core.base.BaseActivity
import com.conchoback.haingon.core.extension.checkInternet
import com.conchoback.haingon.core.extension.gone
import com.conchoback.haingon.core.extension.handleBackLeftToRight
import com.conchoback.haingon.core.extension.launchIO
import com.conchoback.haingon.core.extension.setImageActionBar
import com.conchoback.haingon.core.extension.tap
import com.conchoback.haingon.core.extension.visible
import com.conchoback.haingon.core.helper.AnimationHelper
import com.conchoback.haingon.core.utils.key.IntentKey
import com.conchoback.haingon.core.utils.key.ValueKey
import com.conchoback.haingon.data.model.PathAPI
import com.conchoback.haingon.databinding.ActivityChooseClothesBeforeBinding
import com.conchoback.haingon.ui.choose_clothes_after.ChooseClothesAccessoryViewModel
import com.conchoback.haingon.ui.choose_clothes_before.adapter.AccessoryAdapter
import com.conchoback.haingon.ui.choose_clothes_before.adapter.ClothesAdapter
import com.conchoback.haingon.ui.home.view_model.DataViewModel
import com.conchoback.haingon.ui.view3d.View3dActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class ChooseClothesBeforeActivity : BaseActivity<ActivityChooseClothesBeforeBinding>() {
    private val viewModel: ChooseClothesBeforeViewModel by viewModels()
    private val chooseClothesViewModel: ChooseClothesAccessoryViewModel by viewModels()

    private val dataViewModel: DataViewModel by viewModels()

    private val clothesAdapter by lazy { ClothesAdapter() }
    private val accessoryAdapter by lazy { AccessoryAdapter() }

    override fun setViewBinding(): ActivityChooseClothesBeforeBinding {
        return ActivityChooseClothesBeforeBinding.inflate(LayoutInflater.from(this))
    }

    override fun initView() {
        lifecycleScope.launch { showLoading() }
        viewModel.setTypeClothes(intent.getStringExtra(IntentKey.INTENT_KEY) ?: ValueKey.SHIRT)
    }

    override fun dataObservable() {
        lifecycleScope.launch {
            launch { viewModel.typeClothes.collect { type -> setupTypeClothes(type) } }
            launch { dataViewModel.allData.collect { data -> setupData(data) } }
            launch { chooseClothesViewModel.allData.collect { data -> setupGetDataFromDataVM(data) } }
        }
    }

    override fun viewListener() {
        binding.actionBar.btnActionBarLeft.tap { handleBackLeftToRight() }
    }

    // Init
    //==================================================================================================================
    override fun initActionBar() = with(binding.actionBar) {
        setImageActionBar(btnActionBarLeft, R.drawable.ic_back)
    }

    // Handle
    //==================================================================================================================
    private fun setupClothesUI() {
        binding.apply {
            rcvClothes.apply {
                adapter = clothesAdapter
                itemAnimator = null
            }
            rcvClothes.visible()
            rcvAccessory.gone()
        }

        launchIO(
            blockIO = { viewModel.loadClothesList(this@ChooseClothesBeforeActivity) },
            blockMain = { clothesList ->
                dismissLoading(true)
                clothesAdapter.submitList(clothesList)
            }
        )

        clothesAdapter.onItemClick = { path -> handleNextScreen(path) }
    }

    private fun setupAccessoryUI() {
        binding.apply {
            rcvAccessory.apply {
                adapter = accessoryAdapter
                itemAnimator = null
            }
            rcvClothes.gone()
            rcvAccessory.visible()
        }

        launchIO(
            blockIO = {
                chooseClothesViewModel.loadAccessoryList("")
                viewModel.mergeAccessoryList(chooseClothesViewModel.accessoryList)
            },
            blockMain = { accessoryList ->
                dismissLoading(true)
                accessoryAdapter.submitList(accessoryList)
            }
        )

        accessoryAdapter.onItemClick = { model -> checkInternet { handleNextScreen(viewModel.convertToJson(model)) } }
    }

    private fun setupComboUI() {
        binding.apply {
            rcvClothes.apply {
                adapter = clothesAdapter
                itemAnimator = null
            }
            rcvClothes.visible()
            rcvAccessory.gone()
        }

        launchIO(
            blockIO = {
                viewModel.loadComboList(
                    this@ChooseClothesBeforeActivity,
                    chooseClothesViewModel.loadClothesList("")
                )
            },
            blockMain = { comboList ->
                dismissLoading(true)
                clothesAdapter.submitList(comboList)
            }
        )

        clothesAdapter.onItemClick = { path -> checkInternet { handleNextScreen(path) } }
    }

    private fun handleNextScreen(path: String) {
        val nextScreen = Intent(this, View3dActivity::class.java)
        nextScreen.apply {
            putExtra(IntentKey.CLOTHES_TYPE, viewModel.typeClothes.value)
            putExtra(IntentKey.PATH_KEY, path)
        }

        val anim = AnimationHelper.intentAnimRL(this)

        startActivity(nextScreen, anim.toBundle())
    }


    // Observable
    //==================================================================================================================
    private fun setupTypeClothes(type: String) {
        when (type) {
            ValueKey.SHIRT -> setupClothesUI()
            ValueKey.PANT -> setupClothesUI()
            ValueKey.COMBO -> {
                viewModel.updateTypeCombo(intent.getStringExtra(IntentKey.COMBO_TYPE) ?: ValueKey.BASIC_SKIN)

                if (viewModel.isSpecialCombo()) {
                    dataViewModel.saveAndReadData(this, sharePreference)
                } else {
                    setupComboUI()
                }
            }

            ValueKey.ACCESSORY -> dataViewModel.saveAndReadData(this, sharePreference)
            else -> return
        }
    }

    private fun setupData(data: PathAPI?) {
        if (data == null) return
        chooseClothesViewModel.setAllData(data)
    }

    private fun setupGetDataFromDataVM(data: PathAPI?) {
        if (data == null) return
        if (viewModel.typeClothes.value == ValueKey.ACCESSORY) {
            setupAccessoryUI()
        } else {
            setupComboUI()
        }
    }
    // Result + Permission
    //==================================================================================================================

    // Ads
    //==================================================================================================================


}