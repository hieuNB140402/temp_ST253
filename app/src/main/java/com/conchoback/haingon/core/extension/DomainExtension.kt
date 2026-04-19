package com.conchoback.haingon.core.extension

import com.conchoback.haingon.core.utils.DataLocal
import com.conchoback.haingon.core.utils.key.DomainKey

fun domain(path: String): String {
    val domain = if (DataLocal.isFailBaseURL) DomainKey.DOMAIN_PREVENTIVE else DomainKey.DOMAIN
    return "$domain$path"
}

fun loadAccessory2DURL(path: String) : String{
    val domain = if (DataLocal.isFailBaseURL) DomainKey.DOMAIN_PREVENTIVE else DomainKey.DOMAIN
    return "$domain${DomainKey.BASE_PATH}/${DomainKey.PREVIEW_2D}/${path}.png"
}