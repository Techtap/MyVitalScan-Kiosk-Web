package com.techtap.utils

import com.techtap.BuildConfig

object Constant {

//    const val BINAH_AI_SCANNING_TIME_SECONDS: Long = 60
    const val RESULT_SCREEN_DELAY_TIME = 1500

    const val INTERNAL = "internal"
    const val EXTERNAL = "external"

    const val URL_PRIVACY_POLICY = "static/PRIVACY_POLICY"
    const val URL_POST_PRIVACY_POLICY = "static/AGREE_PRIVACY_POLICY"
    const val URL_TERMS_AND_CONDITION = "static/TERM_AND_CONDITION"

    object IntentConstant {
        const val FROM = "from"
        const val ID = "id"
        const val SCAN_RESULT = "scan_result"
    }

    object StatusCodes {
        const val STATUS_CODE_SUCCESS = 200
        const val STATUS_CODE_CREATED = 201
        const val STATUS_CODE_ERROR_OAUTH = 401
//        const val STATUS_CODE_ERROR_400 = 400
//        const val STATUS_CODE_ERROR_409 = 409
//        const val STATUS_CODE_ERROR_SERVER = 500
//        const val STATUS_CODE_ERROR_404 = 404
//        const val STATUS_CODE_CONFLICT = 409

        // This is custom code for handling timeout error
        const val STATUS_CODE_ERROR_TIMEOUT = 5002
    }


    internal const val APK_URL = "https://vitalscanapi.winayak.com/tech_tap.apk"
    internal const val WEBSITE_URL = "https://pouyaheydari.com"
    internal const val SAMPLE_PACKAGE_NAME = "com.techtap"
    internal const val SAMPLE_VERSION_CODE = BuildConfig.VERSION_NAME

    internal const val FDROID_SAMPLE_PACKAGE_NAME = "de.storchp.fdroidbuildstatus"
    internal const val GET_APP_SAMPLE_PACKAGE_NAME = "com.opera.browser"
    internal const val ONE_STORE_SAMPLE_PACKAGE_NAME = "com.kakao.talk"
}