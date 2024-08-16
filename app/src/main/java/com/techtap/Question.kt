package com.techtap

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class Question() : Parcelable {

    @SerializedName("id")
    var id: Int = 0

    @SerializedName("questions")
    var questions: String? = null

    @SerializedName("impact")
    var impact: String? = null

    @SerializedName("status")
    var status: String? = null

    @SerializedName("options")
    var options: ArrayList<QuestionOption>? = null

    constructor(parcel: Parcel) : this() {
        id = parcel.readInt()
        questions = parcel.readString()
        impact = parcel.readString()
        status = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(questions)
        parcel.writeString(impact)
        parcel.writeString(status)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Question> {
        override fun createFromParcel(parcel: Parcel): Question {
            return Question(parcel)
        }

        override fun newArray(size: Int): Array<Question?> {
            return arrayOfNulls(size)
        }
    }


}