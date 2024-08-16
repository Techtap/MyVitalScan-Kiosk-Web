package com.techtap

import com.google.gson.annotations.SerializedName
import com.techtap.network.CommonResponse

class BeforeScanResponse : CommonResponse() {

    @SerializedName("id")
    var id: Long = 0
}