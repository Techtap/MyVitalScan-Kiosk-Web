package com.techtap.network

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

open class CommonResponse() : ResponseStatus() {

    @SerializedName("status")
    var status: String? = null

    @SerializedName("success")
    var success: Boolean = false

    @SerializedName("access_token")
    var accessToken: String? = null

    @SerializedName("refreshToken")
    var refreshToken: String? = null

    @SerializedName("errorInfo")
    var error: Error? = null

    constructor(parcel: Parcel) : this() {
        status = parcel.readString()
        success = parcel.readByte() != 0.toByte()
        accessToken = parcel.readString()
        refreshToken = parcel.readString()
        error = parcel.readParcelable(Error::class.java.classLoader)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(status)
        parcel.writeByte(if (success) 1 else 0)
        parcel.writeString(accessToken)
        parcel.writeString(refreshToken)
        parcel.writeParcelable(error, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CommonResponse> {
        override fun createFromParcel(parcel: Parcel): CommonResponse {
            return CommonResponse(parcel)
        }

        override fun newArray(size: Int): Array<CommonResponse?> {
            return arrayOfNulls(size)
        }
    }

}