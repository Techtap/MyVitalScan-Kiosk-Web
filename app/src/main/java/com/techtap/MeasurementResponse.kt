package com.techtap

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.techtap.network.CommonResponse

class MeasurementResponse() : CommonResponse(), Parcelable {

    @SerializedName("basic_info")
    var basicInfo: HealthBasicInfo? = null

    @SerializedName("result")
    var result: Result? = null

    @SerializedName("ABNORMAL_RESULT")
    var abnormalResult: Result? = null

    constructor(parcel: Parcel) : this() {
        basicInfo = parcel.readParcelable(HealthBasicInfo::class.java.classLoader)
        result = parcel.readParcelable(Result::class.java.classLoader)
        abnormalResult = parcel.readParcelable(Result::class.java.classLoader)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(basicInfo, flags)
        parcel.writeParcelable(result, flags)
        parcel.writeParcelable(abnormalResult, flags)

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MeasurementResponse> {
        override fun createFromParcel(parcel: Parcel): MeasurementResponse {
            return MeasurementResponse(parcel)
        }

        override fun newArray(size: Int): Array<MeasurementResponse?> {
            return arrayOfNulls(size)
        }
    }
}

class Result() : Parcelable {

    @SerializedName("VITAL_SIGNS")
    var vitalSigns: ArrayList<Reading>? = null

    @SerializedName("BLOOD")
    var blood: ArrayList<Reading>? = null

    @SerializedName("STRESS_LEVEL")
    var stressLevel: ArrayList<Reading>? = null

    @SerializedName("ENERGY")
    var energy: ArrayList<Reading>? = null

    @SerializedName("HEART_RATE_VARIABILITY")
    var heartRateVariability: ArrayList<Reading>? = null

    @SerializedName("BLOOD_TEST")
    var bloodTest: ArrayList<Reading>? = null

    @SerializedName("TEMPERATURE")
    var temperature: ArrayList<Reading>? = null

    constructor(parcel: Parcel) : this() {
        vitalSigns = parcel.createTypedArrayList(Reading.CREATOR)
        blood = parcel.createTypedArrayList(Reading.CREATOR)
        stressLevel = parcel.createTypedArrayList(Reading.CREATOR)
        energy = parcel.createTypedArrayList(Reading.CREATOR)
        heartRateVariability = parcel.createTypedArrayList(Reading.CREATOR)
        bloodTest = parcel.createTypedArrayList(Reading.CREATOR)
        temperature = parcel.createTypedArrayList(Reading.CREATOR)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeTypedList(vitalSigns)
        parcel.writeTypedList(blood)
        parcel.writeTypedList(stressLevel)
        parcel.writeTypedList(energy)
        parcel.writeTypedList(heartRateVariability)
        parcel.writeTypedList(bloodTest)
        parcel.writeTypedList(temperature)
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
