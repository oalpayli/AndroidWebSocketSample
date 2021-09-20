package com.ceng.ozi.websocketchannelsample

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Crypto(
    @Json(name = "MARKET")
    val market: String?,
    @Json(name = "FROMSYMBOL")
    val fromSymbol: String?,
    @Json(name = "TOSYMBOL")
    val toSymbol: String?,
    @Json(name = "PRICE")
    val price: Double?
)
