package com.conchoback.haingon.ui.choose_clothes_after

import android.content.Intent
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.conchoback.haingon.R
import com.conchoback.haingon.core.base.BaseActivity
import com.conchoback.haingon.core.extension.gone
import com.conchoback.haingon.core.extension.handleBackLeftToRight
import com.conchoback.haingon.core.extension.setImageActionBar
import com.conchoback.haingon.core.extension.tap
import com.conchoback.haingon.core.extension.visible
import com.conchoback.haingon.core.helper.InternetHelper
import com.conchoback.haingon.core.utils.key.IntentKey
import com.conchoback.haingon.core.utils.key.ValueKey
import com.conchoback.haingon.data.model.clothes.AccessoryModel
import com.conchoback.haingon.data.model.PathAPI
import com.conchoback.haingon.databinding.ActivityChooseClothesAccessoryBinding
import com.conchoback.haingon.ui.home.DataViewModel
import com.conchoback.haingon.ui.choose_clothes_after.adapter.CategoryAccessoryAdapter
import com.conchoback.haingon.ui.choose_clothes_after.adapter.ClothesAdapter
import com.conchoback.haingon.ui.choose_clothes_after.adapter.SubAccessoryAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChooseClothesAccessoryActivity : BaseActivity<ActivityChooseClothesAccessoryBinding>() {
    private val viewModel: ChooseClothesAccessoryViewModel by viewModels()
    private val dataViewModel: DataViewModel by viewModels()

    private val clothesAdapter by lazy { ClothesAdapter() }
    private val categoryAccessoryAdapter by lazy { CategoryAccessoryAdapter() }
    private val subAccessoryAdapter by lazy { SubAccessoryAdapter() }

    override fun setViewBinding(): ActivityChooseClothesAccessoryBinding {
        return ActivityChooseClothesAccessoryBinding.inflate(LayoutInflater.from(this))
    }

    override fun initView() {
        dataViewModel.ensureData(this, sharePreference)

    }

    override fun dataObservable() {
        lifecycleScope.launch {
            launch { dataViewModel.allData.collect { data -> setupData(data) } }
            launch { viewModel.allData.collect { data -> setupGetDataFromDataVM(data) } }
            launch { viewModel.typeClothes.collect { type -> setupTypeClothes(type) } }
        }
    }

    override fun viewListener() {
        binding.apply {
            actionBar.apply {
                btnActionBarLeft.tap { handleBackLeftToRight() }
                btnActionBarRight.tap { handleDone() }
            }
        }
    }

    override fun initActionBar() = with(binding.actionBar) {
        setImageActionBar(btnActionBarLeft, R.drawable.ic_back)
        setImageActionBar(btnActionBarRight, R.drawable.ic_done)
    }

    // Init
    //==================================================================================================================


    // Handle
    //==================================================================================================================
    private fun handleClothesUI() {
        binding.rcvClothes.apply {
            adapter = clothesAdapter
            itemAnimator = null
        }

        lifecycleScope.launch(Dispatchers.IO) {
            val list = viewModel.loadClothesList(intent.getStringExtra(IntentKey.PATH_KEY) ?: "")

            withContext(Dispatchers.Main) {
                binding.rcvClothes.visible()
                binding.lnlAccessory.gone()

                clothesAdapter.submitList(list)
            }
        }

        clothesAdapter.onItemClick = { path, position -> viewModel.updatePathClothesSelected(path) }
    }

    private fun handleAccessoryUI() {
        binding.rcvCategoryAccessory.apply {
            adapter = categoryAccessoryAdapter
            itemAnimator = null
        }
        binding.rcvAccessory.apply {
            adapter = subAccessoryAdapter
            itemAnimator = null
        }

        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.loadAccessoryList(intent.getStringExtra(IntentKey.PATH_KEY) ?: "")

            withContext(Dispatchers.Main) {
                binding.rcvClothes.gone()
                binding.lnlAccessory.visible()
                submitCategoryAccessory(0)
            }
        }

        categoryAccessoryAdapter.onItemClick = { position -> checkInternet { changeFocusCategoryAccessory(position) } }
        subAccessoryAdapter.onItemClick = { model, position -> checkInternet { changeFocusSubAccessory(model, position) } }
    }

    private fun changeFocusCategoryAccessory(position: Int) {
        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.updateCategoryAccessoryList(position)

            withContext(Dispatchers.Main) {
                submitCategoryAccessory(position)
            }
        }
    }

    private fun changeFocusSubAccessory(model: AccessoryModel, position: Int) {
        lifecycleScope.launch(Dispatchers.IO) {
            val positionCategoryAccessory = viewModel.changeSubAccessory(model, position)

            withContext(Dispatchers.Main) {
                submitCategoryAccessory(positionCategoryAccessory)
            }
        }
    }


    private fun submitCategoryAccessory(position: Int) {
        categoryAccessoryAdapter.submitList(viewModel.accessoryList)
        subAccessoryAdapter.submitList(viewModel.accessoryList[position].subAccessoryList)
    }

    private fun handleDone() {
        checkInternet {
            val path = viewModel.getPathClotheAccessorySelected()

            val resultIntent = Intent()
            resultIntent.apply {
                putExtra(IntentKey.TYPE_CLOTHES_KEY, viewModel.typeClothes.value)
                putExtra(IntentKey.CHOOSE_CLOTHES_KEY, path)
            }
            setResult(RESULT_OK, resultIntent)
            handleBackLeftToRight()
        }
    }


    private fun checkInternet(action: () -> Unit) {
        if (InternetHelper.isInternetAvailable(this)) {
            action.invoke()
        } else {
            showToast(R.string.please_check_your_network_connection)
        }
    }

    // Observable
    //==================================================================================================================
    private fun setupData(data: PathAPI?) {
        if (data == null) return
        viewModel.setAllData(data)
    }

    private fun setupGetDataFromDataVM(data: PathAPI?) {
        if (data == null) return
        viewModel.setTypeClothes(intent.getStringExtra(IntentKey.TYPE_KEY) ?: ValueKey.SHIRT)
    }

    private fun setupTypeClothes(type: String) {
        when (type) {
            ValueKey.SHIRT -> handleClothesUI()
            ValueKey.PANT -> handleClothesUI()
            ValueKey.ACCESSORY -> handleAccessoryUI()
            else -> return
        }
    }

    // Result + Permission
    //==================================================================================================================

    // Ads
    //==================================================================================================================

}