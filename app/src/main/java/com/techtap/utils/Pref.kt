package com.techtap.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.techtap.BuildConfig
import com.techtap.GenerateCodeResponse
import com.techtap.base.MyApplication

object Pref {
    object PrefConstant {
        const val IS_LOW_VIEW = "is_low_view"
        const val GENERATE_CODE_OBJ = "generate_code_obj"
        const val IS_INTERNAL = "is_internal"
        const val IS_AUDIO_ON = "is_audio_on"
    }

    private val prefs: SharedPreferences = MyApplication.instance!!.applicationContext!!.getSharedPreferences(
        BuildConfig.PREF_FILE, Context.MODE_PRIVATE
    )
    private val gson = Gson()


    var isLowView: Boolean
        get() = prefs.getBoolean(PrefConstant.IS_LOW_VIEW, false)
        set(value) = prefs.edit().putBoolean(PrefConstant.IS_LOW_VIEW, value).apply()

    var isInternal: Boolean
        get() = prefs.getBoolean(PrefConstant.IS_INTERNAL, false)
        set(value) = prefs.edit().putBoolean(PrefConstant.IS_INTERNAL, value).apply()


    var generateCodeObject: GenerateCodeResponse?
        get() = gson.fromJson(
            prefs.getString(PrefConstant.GENERATE_CODE_OBJ, null), GenerateCodeResponse::class.java
        )
        set(value) = prefs.edit().putString(PrefConstant.GENERATE_CODE_OBJ, gson.toJson(value)).apply()


    var isAudioOn: Boolean
        get() = prefs.getBoolean(PrefConstant.IS_AUDIO_ON, true)
        set(value) = prefs.edit().putBoolean(PrefConstant.IS_AUDIO_ON, value).apply()


//
//    var uuid: String?
//        get() = prefs.getString(PrefConstant.UUID, "")
//        set(value) {
//            prefs.edit().putString(PrefConstant.UUID, value).apply()
//            NetworkUtils.putAuthParam(
//                NetworkUtils.X_UUID, if (!value.isNullOrEmpty()) value else ""
//            )
//        }
//
//
//    var userId: String?
//        get() = prefs.getString(PrefConstant.USER_ID, "")
//        set(value) = prefs.edit().putString(PrefConstant.USER_ID, value).apply()
//
//    var accessToken: String?
//        get() = prefs.getString(PrefConstant.ACCESS_TOKEN, "")
//        set(value) {
//            prefs.edit().putString(PrefConstant.ACCESS_TOKEN, value).apply()
//            NetworkUtils.putAuthParam(
//                NetworkUtils.AUTHORIZATION, if (!value.isNullOrEmpty()) "Bearer $value" else ""
//            )
//        }
//
//    var contactSyncDate: String?
//        get() = prefs.getString(PrefConstant.CONTACT_SYNC_DATE, "")
//        set(value) = prefs.edit().putString(PrefConstant.CONTACT_SYNC_DATE, value).apply()
//
//    var homeSyncDate: String?
//        get() = prefs.getString(PrefConstant.HOME_SYNC_DATE, "")
//        set(value) = prefs.edit().putString(PrefConstant.HOME_SYNC_DATE, value).apply()
//
//    var appLocale: String
//        get() = prefs.getString(PrefConstant.APP_LOCALE, "en")!!
//        set(value) = prefs.edit().putString(PrefConstant.APP_LOCALE, value).apply()
//
//
//    var fcmToken: String?
//        get() = prefs.getString(PrefConstant.FCM_TOKEN, "")
//        set(value) = prefs.edit().putString(PrefConstant.FCM_TOKEN, value).apply()
//
//    var isSendFcmToken: Boolean
//        get() = prefs.getBoolean(PrefConstant.IS_SEND_FCM_TOKEN, false)
//        set(value) = prefs.edit().putBoolean(PrefConstant.IS_SEND_FCM_TOKEN, value).apply()
//
//    var publicAddress: String?
//        get() = prefs.getString(PrefConstant.PUBLIC_ADDRESS, "")
//        set(value) = prefs.edit().putString(PrefConstant.PUBLIC_ADDRESS, value).apply()
//
//    var user: User?
//        get() = gson.fromJson(
//            prefs.getString(PrefConstant.USER_OBJECT, null), User::class.java
//        )
//        set(value) = prefs.edit().putString(PrefConstant.USER_OBJECT, gson.toJson(value)).apply()
//
//
//    var station: Station?
//        get() = gson.fromJson(
//            prefs.getString(PrefConstant.STATION_OBJECT, null), Station::class.java
//        )
//        set(value) = prefs.edit().putString(PrefConstant.STATION_OBJECT, gson.toJson(value)).apply()
//
//
//    var mfaMethods: MfaMethod?
//        get() = gson.fromJson(
//            prefs.getString(PrefConstant.MFA_METHODS_OBJECT, null), MfaMethod::class.java
//        )
//        set(value) = prefs.edit().putString(PrefConstant.MFA_METHODS_OBJECT, gson.toJson(value)).apply()
//
//    var isMfaMethodSet: Boolean
//        get() = prefs.getBoolean(PrefConstant.IS_MFA_METHOD_SET, false)
//        set(value) = prefs.edit().putBoolean(PrefConstant.IS_MFA_METHOD_SET, value).apply()
//
//    var isMfa: Boolean
//        get() = prefs.getBoolean(PrefConstant.IS_MFA, false)
//        set(value) = prefs.edit().putBoolean(PrefConstant.IS_MFA, value).apply()
//
//    var mfaIsEnabled: Boolean
//        get() = prefs.getBoolean(PrefConstant.MFA_IS_ENABLED, false)
//        set(value) = prefs.edit().putBoolean(PrefConstant.MFA_IS_ENABLED, value).apply()


//    var customSizes: ArrayList<Size1>?
//        get() {
//            return try {
//                val gson = Gson()
//                val json = prefs.getString(PrefConst.CUSTOM_SIZES, null)
//                val type = object : TypeToken<ArrayList<Size1>>() {}.type
//                gson.fromJson(json, type)
//            } catch (ex: Exception) {
//                Logger.logError("Prefs", ex.toString())
//                null
//            }
//        }
//        set(value) {
//            val editor = prefs.edit()
//            val gson = Gson()
//            val json = gson.toJson(value)
//            editor.putString(Constants.PrefConst.CUSTOM_SIZES, json)
//            editor.apply()
//        }
//
}