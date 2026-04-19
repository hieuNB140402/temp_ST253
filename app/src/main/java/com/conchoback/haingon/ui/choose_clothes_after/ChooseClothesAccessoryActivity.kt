package com.conchoback.haingon.ui.choose_clothes_after

import android.content.Intent
import android.view.LayoutInflater
import androidx.activity.viewModels
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
import com.conchoback.haingon.core.utils.key.IntentKey
import com.conchoback.haingon.core.utils.key.ValueKey
import com.conchoback.haingon.data.model.clothes.AccessoryModel
import com.conchoback.haingon.data.model.PathAPI
import com.conchoback.haingon.data.model.clothes.SubAccessoryModel
import com.conchoback.haingon.databinding.ActivityChooseClothesAccessoryBinding
import com.conchoback.haingon.ui.home.view_model.DataViewModel
import com.conchoback.haingon.ui.choose_clothes_after.adapter.CategoryAccessoryAdapter
import com.conchoback.haingon.ui.choose_clothes_after.adapter.ClothesAdapter
import com.conchoback.haingon.ui.choose_clothes_after.adapter.SubAccessoryAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
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

        // allData (dataModel) -> allData (viewModel) -> typeClothes (viewmodel)
        lifecycleScope.launch {
            launch { dataViewModel.allData.collect { data -> setupData(data) } }
            launch { viewModel.allData.collect { data -> setupGetDataFromDataVM(data) } }
            launch { viewModel.typeClothes.collect { type -> setupTypeClothes(type) } }
            launch { viewModel.clothesList.collect { list -> clothesAdapter.submitList(list) } }
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
        binding.apply {
            rcvClothes.apply {
                adapter = clothesAdapter
                itemAnimator = null
            }

            rcvClothes.visible()
            lnlAccessory.gone()
        }

        launchIO(
            blockIO = { viewModel.loadClothesList(intent.getStringExtra(IntentKey.PATH_KEY) ?: "") }
        )

        clothesAdapter.onItemClick = { path, position -> viewModel.selectClothes(path, position) }
    }

    private fun handleAccessoryUI() {
        binding.apply {
            rcvCategoryAccessory.apply {
                adapter = categoryAccessoryAdapter
                itemAnimator = null
            }
            rcvAccessory.apply {
                adapter = subAccessoryAdapter
                itemAnimator = null
            }

            rcvClothes.gone()
            lnlAccessory.visible()
        }

        launchIO(
            blockIO = { viewModel.loadAccessoryList(intent.getStringExtra(IntentKey.PATH_KEY) ?: "") },
            blockMain = { submitCategoryAccessory(0) }
        )

        categoryAccessoryAdapter.onItemClick = { position -> checkInternet { submitCategoryAccessory(position) } }
        subAccessoryAdapter.onItemClick = { model, position -> checkInternet { changeFocusSubAccessory(model, position) } }
    }


    private fun changeFocusSubAccessory(model: AccessoryModel, position: Int) {
        launchIO(
            blockIO = { viewModel.refocusSubAccessory(model, position) },
            blockMain = { newList -> submitSubAccessory(newList) }
        )
    }


    private fun submitCategoryAccessory(position: Int) {
        launchIO(
            blockIO = { viewModel.refocusAccessory(position) },
            blockMain = {
                categoryAccessoryAdapter.submitList(viewModel.accessoryList)
                submitSubAccessory(viewModel.accessoryList[position].subAccessoryList)
            }
        )
    }

    private fun submitSubAccessory(subAccessoryList: List<SubAccessoryModel>) {
        subAccessoryAdapter.submitList(subAccessoryList)
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