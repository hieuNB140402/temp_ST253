package com.conchoback.haingon.data.model

data class PathAPI(
    val folders: List<ClothesAPI>,
    val models: AccessoryAPI
)

data class ClothesAPI(
    val category: String,
    val quantity: Int
)

data class AccessoryAPI(
    val glasses: List<String>,
    val hair: List<String>,
    val lefthand: List<String>,
    val neck: List<String>,
    val righthand: List<String>,
    val shoulder: List<String>,
    val waist: List<String>,
    val wing: List<String>
)