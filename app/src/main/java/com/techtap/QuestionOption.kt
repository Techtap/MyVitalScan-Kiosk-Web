package com.techtap

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class QuestionOption() : Parcelable {

    @SerializedName("id")
    var id: Long = 0

    @SerializedName("question_id")
    var questionId: Int = 0

    @SerializedName("option")
    var option: String? = null

    @SerializedName("status")
    var status: String? = null

    @SerializedName("created_at")
    var createdAt: String? = null

    @SerializedName("updated_at")
    var updatedAt: String? = null

    @SerializedName("is_ticked")
    var isTicked: String? = null

    @SerializedName("selected")
    var selected: Boolean = false

    constructor(parcel: Parcel) : this() {
        id = parcel.readLong()
        questionId = parcel.readInt()
        option = parcel.readString()
        status = parcel.readString()
        createdAt = parcel.readString()
        updatedAt = parcel.readString()
        isTicked = parcel.readString()
        selected = parcel.readByte() != 0.toByte()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeInt(questionId)
        parcel.writeString(option)
        parcel.writeString(status)
        parcel.writeString(createdAt)
        parcel.writeString(updatedAt)
        parcel.writeString(isTicked)
        parcel.writeByte(if (selected) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<QuestionOption> {
        override fun createFromParcel(parcel: Parcel): QuestionOption {
            return QuestionOption(parcel)
        }

        override fun newArray(size: Int): Array<QuestionOption?> {
            return arrayOfNulls(size)
        }
    }

}