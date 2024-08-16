package com.techtap

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class ReadingDesc() : Parcelable {

    @SerializedName("desc1")
    var desc1: String? = null

    @SerializedName("desc2")
    var desc2: String? = null

    constructor(parcel: Parcel) : this() {
        desc1 = parcel.readString()
        desc2 = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(desc1)
        parcel.writeString(desc2)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ReadingDesc> {
        override fun createFromParcel(parcel: Parcel): ReadingDesc {
            return ReadingDesc(parcel)
        }

        override fun newArray(size: Int): Array<ReadingDesc?> {
            return arrayOfNulls(size)
        }
    }

}