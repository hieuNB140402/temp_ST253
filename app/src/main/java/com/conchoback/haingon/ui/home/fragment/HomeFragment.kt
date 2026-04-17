package com.conchoback.haingon.ui.home.fragment

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.conchoback.haingon.R
import com.conchoback.haingon.core.base.BaseFragment
import com.conchoback.haingon.core.extension.dLog
import com.conchoback.haingon.core.extension.startIntent
import com.conchoback.haingon.core.extension.startIntentRightToLeft
import com.conchoback.haingon.core.extension.strings
import com.conchoback.haingon.core.extension.tap
import com.conchoback.haingon.core.helper.AnimationHelper
import com.conchoback.haingon.core.helper.LanguageHelper
import com.conchoback.haingon.core.utils.key.IntentKey
import com.conchoback.haingon.core.utils.key.ValueKey
import com.conchoback.haingon.databinding.FragmentHomeBinding
import com.conchoback.haingon.ui.choose_clothes_before.ChooseClothesBeforeActivity
import com.conchoback.haingon.ui.home.HomeActivity
import com.conchoback.haingon.ui.home.HomeViewModel
import com.conchoback.haingon.ui.how_to_use.HowToUseActivity
import com.conchoback.haingon.ui.view3d.View3dActivity

class HomeFragment : BaseFragment<FragmentHomeBinding>() {
    val viewModel: HomeViewModel by activityViewModels()

    override fun setViewBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentHomeBinding {
        return FragmentHomeBinding.inflate(inflater)
    }

    override fun initView() {

    }

    override fun viewListener() {
        val homeActivity = (activity as HomeActivity)
        binding.apply {
            btnHowToUse.tap { homeActivity.startIntentRightToLeft(HowToUseActivity::class.java) }

            btnAccessory.tap {
                homeActivity.checkInternet {
                    homeActivity.startIntentRightToLeft(
                        ChooseClothesBeforeActivity::class.java,
                        ValueKey.ACCESSORY
                    )
                }
                btnCombo.tap { homeActivity.startIntentRightToLeft(ChooseClothesBeforeActivity::class.java, ValueKey.COMBO) }
                btnShirt.tap { homeActivity.startIntentRightToLeft(ChooseClothesBeforeActivity::class.java, ValueKey.SHIRT) }
                btnPant.tap { homeActivity.startIntentRightToLeft(ChooseClothesBeforeActivity::class.java, ValueKey.PANT) }

                btnTrending1.tap { handleTrending(1) }
                btnTrending2.tap { handleTrending(2) }
                btnTrending3.tap { handleTrending(3) }
            }
        }
    }


    // Init
    //==================================================================================================================

    // Handle
    //==================================================================================================================
    private fun updateText() {
        val context = requireActivity()
        LanguageHelper.setLocale(context)
        binding.apply {
            tv1.text = context.strings(R.string.special_clothes)
            tv2.text = context.strings(R.string.special)
            tv3.text = context.strings(R.string.clothes)
            tv4.text = context.strings(R.string.how_to_use_clothes_for_rbx)
            tv5.text = context.strings(R.string.accessory)
            tv6.text = context.strings(R.string.basic_skin)
            tv7.text = context.strings(R.string.trending)
        }
    }

    private fun handleTrending(index: Int) {
        val nextScreen = Intent(requireActivity(), View3dActivity::class.java)
        nextScreen.apply {
            putExtra(IntentKey.CLOTHES_TYPE, ValueKey.COMBO)
            putExtra(IntentKey.PATH_KEY, viewModel.getClothesPath(index))
        }
        val anim = AnimationHelper.intentAnimRL(requireActivity())
        startActivity(nextScreen, anim.toBundle())
    }

    // Observable
    //==================================================================================================================

    // Result + Permission
    //==================================================================================================================
    override fun onResume() {
        super.onResume()
        updateText()
    }

    // Ads
    //==================================================================================================================


}