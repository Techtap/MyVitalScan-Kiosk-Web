package com.techtap

import com.google.gson.annotations.SerializedName
import com.techtap.network.CommonResponse

class GenerateCodeResponse : CommonResponse() {

    @SerializedName("id")
    var id: Long = 0

    @SerializedName("url")
    var url: String? = null

    @SerializedName("generate_code")
    var generateCode: String? = null

}