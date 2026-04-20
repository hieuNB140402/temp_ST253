package com.conchoback.haingon.ui.home.fragment

import android.content.Intent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.conchoback.haingon.R
import com.conchoback.haingon.core.base.BaseFragment
import com.conchoback.haingon.core.extension.checkInternet
import com.conchoback.haingon.core.extension.eLog
import com.conchoback.haingon.core.extension.gone
import com.conchoback.haingon.core.extension.launchIO
import com.conchoback.haingon.core.extension.strings
import com.conchoback.haingon.core.extension.tap
import com.conchoback.haingon.core.extension.visible
import com.conchoback.haingon.core.helper.LanguageHelper
import com.conchoback.haingon.core.utils.key.IntentKey
import com.conchoback.haingon.core.utils.state.DeleteState
import com.conchoback.haingon.data.model.MyCreationModel
import com.conchoback.haingon.databinding.FragmentMyCreationBinding
import com.conchoback.haingon.ui.home.HomeActivity
import com.conchoback.haingon.ui.home.adapter.MyCreationAdapter
import com.conchoback.haingon.ui.home.view_model.HomeViewModel
import com.conchoback.haingon.ui.preview.PreviewActivity
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.jvm.java

class MyCreationFragment : BaseFragment<FragmentMyCreationBinding>() {
    private val viewModel: HomeViewModel by activityViewModels()

    private val myCreationAdapter by lazy { MyCreationAdapter(requireActivity()) }

    override fun setViewBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentMyCreationBinding {
        return FragmentMyCreationBinding.inflate(inflater)
    }

    override fun initView() {
        binding.actionBar.btnActionBarRight.setImageResource(R.drawable.ic_delete)
        binding.rcvMyCreation.apply {
            adapter = myCreationAdapter
            itemAnimator = null
        }
    }

    override fun dataObservable() {
        lifecycleScope.launch {
            launch { viewModel.myCreationList.collect { list -> setupMyCreationList(list) } }
            launch { viewModel.isShowSelection.collect { status -> setupIsLongClick(status) } }
        }
    }

    override fun viewListener() {
        val activity = (activity as HomeActivity)
        binding.apply {
            layoutActionBar.tap { viewModel.hideSelectMyCreation() }
            actionBar.btnActionBarRight.tap { handleDelete() }

            rcvMyCreation.addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {
                override fun onInterceptTouchEvent(
                    recyclerView: RecyclerView, motionEvent: MotionEvent
                ): Boolean {
                    return when {
                        motionEvent.action != MotionEvent.ACTION_UP || recyclerView.findChildViewUnder(
                            motionEvent.x, motionEvent.y
                        ) != null -> false

                        else -> {
                            viewModel.hideSelectMyCreation()
                            true
                        }
                    }
                }

                override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
                override fun onTouchEvent(recyclerView: RecyclerView, motionEvent: MotionEvent) {}
            })
        }

        myCreationAdapter.apply {
            onItemClick = { model -> activity.checkInternet { nextScreen(model) } }
            onItemLongClick = { position -> viewModel.showSelectMyCreation(position) }
            onItemSelectClick = { position -> viewModel.touchSelectMyCreation(position) }
        }

    }

    // Init
    //==================================================================================================================

    // Handle
    //==================================================================================================================
    private fun handleDelete() {
        val homeActivity = (activity as HomeActivity)

        launchIO(
            blockIO = { viewModel.deleteMyCreation() },
            blockMain = { state ->
                when (state) {
                    DeleteState.Empty -> homeActivity.showToast(R.string.please_select_an_item)
                    DeleteState.Success -> viewModel.setSelectionState(false)
                    is DeleteState.Failure -> eLog("handleDelete: ${state.error}")
                }
            }
        )
    }

    private fun nextScreen(model: MyCreationModel) {
        val nextScreen = Intent(requireActivity(), PreviewActivity::class.java)

        nextScreen.apply {
            putExtra(IntentKey.INTENT_KEY, Gson().toJson(model))
        }

        (activity as HomeActivity).resultDelete.launch(nextScreen)
    }

    private fun updateText() = with(binding) {
        LanguageHelper.setLocale(requireActivity())
        tvNoItem.text = requireActivity().strings(R.string.your_wardrobe_is_empty)
    }

    // Observable
    //==================================================================================================================
    private fun setupMyCreationList(list: List<MyCreationModel>) {
        if (list.isNotEmpty()) {
            binding.rcvMyCreation.visible()
            binding.lnlNoItem.gone()
        } else {
            binding.rcvMyCreation.gone()
            binding.lnlNoItem.visible()
        }
        myCreationAdapter.submitList(list)

    }

    private fun setupIsLongClick(status: Boolean) {
        binding.actionBar.btnActionBarRight.isVisible = status
    }
    // Result + Permission
    //==================================================================================================================

    override fun onResume() {
        super.onResume()
        updateText()
    }
    // Ads
    //==================================================================================================================
}