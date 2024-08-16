package com.techtap

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.techtap.network.CommonResponse

class VersionCheckResponse() : CommonResponse(), Parcelable {

    @SerializedName("data")
    var data: VersionCheck? = null
}

class VersionCheck() : Parcelable {

    @SerializedName("id")
    var id: Int = 0

    @SerializedName("package_name")
    var packageName: String? = ""

    @SerializedName("version")
    var version: String? = ""

    @SerializedName("apk_url")
    var apkUrl: String? = ""

    @SerializedName("need_force_update")
    var needForceUpdate: Int = 0

    @SerializedName("has_update_available")
    var hasUpdateAvailable: Boolean = false

    constructor(parcel: Parcel) : this() {
        id = parcel.readInt()
        packageName = parcel.readString()
        version = parcel.readString()
        apkUrl = parcel.readString()
        needForceUpdate = parcel.readInt()
        hasUpdateAvailable = parcel.readBoolean()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(packageName)
        parcel.writeString(version)
        parcel.writeString(apkUrl)
        parcel.writeInt(needForceUpdate)
        parcel.writeBoolean(hasUpdateAvailable)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Result> {
        override fun createFromParcel(parcel: Parcel): Result {
            return Result(parcel)
        }

        override fun newArray(size: Int): Array<Result?> {
            return arrayOfNulls(size)
        }
    }
}