package com.techtap.network

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

open class ResponseStatus() : Parcelable {

    var statusCode = 0

    var message: String? = null

    constructor(parcel: Parcel) : this() {
        statusCode = parcel.readInt()
        message = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(statusCode)
        parcel.writeString(message)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ResponseStatus> {
        override fun createFromParcel(parcel: Parcel): ResponseStatus {
            return ResponseStatus(parcel)
        }

        override fun newArray(size: Int): Array<ResponseStatus?> {
            return arrayOfNulls(size)
        }
    }
}