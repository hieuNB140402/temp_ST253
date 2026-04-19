package com.conchoback.haingon.core.extension

import com.conchoback.haingon.R
import com.conchoback.haingon.core.base.BaseActivity
import com.conchoback.haingon.core.helper.InternetHelper

fun BaseActivity<*>.checkInternet(action: () -> Unit) {
    if (InternetHelper.isInternetAvailable(this)) {
        action.invoke()
    } else {
        showToast(R.string.please_check_your_network_connection)
    }
}