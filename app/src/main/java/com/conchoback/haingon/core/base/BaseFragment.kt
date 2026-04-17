package com.conchoback.haingon.core.base

import android.Manifest
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.conchoback.haingon.App
import com.conchoback.haingon.core.helper.LanguageHelper

abstract class BaseFragment<T : ViewBinding> : Fragment() {

    protected lateinit var binding: T

    protected abstract fun setViewBinding(inflater: LayoutInflater, container: ViewGroup?): T

    protected abstract fun initView()

    protected abstract fun viewListener()

    open fun dataObservable() {}

    protected val sharePreference = App.instant.sharePreference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        LanguageHelper.setLocale(requireContext())
        binding = setViewBinding(inflater, container)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        dataObservable()
        viewListener()
    }

}
