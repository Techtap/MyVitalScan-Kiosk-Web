package com.techtap

import com.google.gson.annotations.SerializedName
import com.techtap.network.CommonResponse

class QuestionResponse : CommonResponse() {

    @SerializedName("list")
    var list: ArrayList<Question>? = null

}