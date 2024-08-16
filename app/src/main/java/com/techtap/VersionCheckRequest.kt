package com.techtap

import com.google.gson.annotations.SerializedName

data class VersionCheckRequest(
    @SerializedName("package_name") val packageName: String?,
    @SerializedName("version") val version: String?
)
