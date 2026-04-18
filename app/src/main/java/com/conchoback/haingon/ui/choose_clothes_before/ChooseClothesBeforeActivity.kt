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
import com.conchoback.haingon.core.extension.dLog
import com.conchoback.haingon.core.extension.gone
import com.conchoback.haingon.core.extension.handleBackLeftToRight
import com.conchoback.haingon.core.extension.setImageActionBar
import com.conchoback.haingon.core.extension.tap
import com.conchoback.haingon.core.extension.visible
import com.conchoback.haingon.core.helper.AnimationHelper
import com.conchoback.haingon.core.helper.InternetHelper
import com.conchoback.haingon.core.helper.InternetHelper.checkInternet
import com.conchoback.haingon.core.utils.key.IntentKey
import com.conchoback.haingon.core.utils.key.ValueKey
import com.conchoback.haingon.data.model.PathAPI
import com.conchoback.haingon.databinding.ActivityChooseClothesBeforeBinding
import com.conchoback.haingon.ui.choose_clothes_after.ChooseClothesAccessoryViewModel
import com.conchoback.haingon.ui.choose_clothes_before.adapter.AccessoryAdapter
import com.conchoback.haingon.ui.choose_clothes_before.adapter.ClothesAdapter
import com.conchoback.haingon.ui.home.DataViewModel
import com.conchoback.haingon.ui.view3d.View3dActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
        lifecycleScope.launch(Dispatchers.IO) {
            val clothesList = viewModel.loadClothesList(this@ChooseClothesBeforeActivity)

            withContext(Dispatchers.Main) {
                binding.rcvClothes.apply {
                    adapter = clothesAdapter
                    itemAnimator = null
                }
                binding.rcvClothes.visible()
                binding.rcvAccessory.gone()

                dismissLoading(true)
                clothesAdapter.submitList(clothesList)
            }
        }
        clothesAdapter.onItemClick = { path -> handleNextScreen(path) }
    }

    private fun setupAccessoryUI() {
        lifecycleScope.launch(Dispatchers.IO) {
            chooseClothesViewModel.loadAccessoryList("")
            val accessoryList = viewModel.mergeAccessoryList(chooseClothesViewModel.accessoryList)

            withContext(Dispatchers.Main) {
                binding.rcvAccessory.apply {
                    adapter = accessoryAdapter
                    itemAnimator = null
                }
                binding.rcvClothes.gone()
                binding.rcvAccessory.visible()

                dismissLoading(true)
                accessoryAdapter.submitList(accessoryList)
            }
        }

        accessoryAdapter.onItemClick = { model -> checkInternet { handleNextScreen(viewModel.convertToJson(model)) } }
    }

    private fun setupComboUI() {
        lifecycleScope.launch(Dispatchers.IO) {
            val comboList = viewModel.loadComboList(
                this@ChooseClothesBeforeActivity,
                chooseClothesViewModel.loadClothesList("")
            )

            withContext(Dispatchers.Main) {
                binding.rcvClothes.apply {
                    adapter = clothesAdapter
                    itemAnimator = null
                }
                binding.rcvClothes.visible()
                binding.rcvAccessory.gone()

                dismissLoading(true)
                clothesAdapter.submitList(comboList)
            }
        }
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

    fun checkInternet(action: () -> Unit) {
        if (InternetHelper.isInternetAvailable(this)) {
            action.invoke()
        } else {
            showToast(R.string.please_check_your_network_connection)
        }
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