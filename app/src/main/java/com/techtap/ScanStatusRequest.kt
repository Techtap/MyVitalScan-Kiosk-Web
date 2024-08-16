package com.techtap

import com.google.gson.annotations.SerializedName

class ScanStatusRequest constructor(
    @SerializedName("scan_status") var scanStatus: String? = null,

    @SerializedName("error_code") var errorCode: Int = 0,

    @SerializedName("error_message")
    var errorMessage: String? = null

)