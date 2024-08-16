package com.techtap

import com.google.gson.annotations.SerializedName

class AddQuestionRequest constructor(

    @SerializedName("list")
    val list: List<AddQuestion>?,
)