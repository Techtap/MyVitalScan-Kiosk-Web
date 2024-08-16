package com.techtap

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class AddQuestion constructor(

    @SerializedName("question_id")
    var questionId: Int = 0,

    @SerializedName("question_option_id")
    var questionOptionId: Long = 0,

    )