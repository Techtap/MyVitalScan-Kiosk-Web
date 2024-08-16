package com.techtap.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Insets
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Parcelable
import android.text.Html
import android.text.Spanned
import android.text.method.PasswordTransformationMethod
import android.util.DisplayMetrics
import android.view.WindowInsets
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import com.techtap.R
import com.techtap.base.BaseActivity
import com.techtap.base.MyApplication
import okhttp3.ResponseBody
import org.aviran.cookiebar2.CookieBar
import java.lang.reflect.Type
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlin.math.roundToInt
import com.techtap.network.Error

object Utils {
    fun <T> getStringToModel(json: String?, clazz: Class<T>?): Any {
        return Gson().fromJson(json, clazz as Type?)
    }

    const val CURRENCY_CODE = "KWD"

    @Suppress("DEPRECATION")
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val nw = connectivityManager.activeNetwork ?: return false
            val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
            return when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                //for other device how are able to connect with Ethernet
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                //for check internet over Bluetooth
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
                else -> false
            }
        } else {
            val nwInfo = connectivityManager.activeNetworkInfo ?: return false
            return nwInfo.isConnected
        }
    }

    fun showToast(activity: Activity?, message: String?) {
        if (activity != null && !activity.isFinishing && !message.isNullOrEmpty()) {
            Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
        }
    }

    fun showErrorMessage(activity: Activity?, error: Error?) {
        showErrorMessage(activity, error?.message)
    }

    fun showErrorMessage(activity: Activity?, message: String?) {
        showTopMessage(activity, message, R.color.red, true)
    }

    fun showSuccessMessage(activity: Activity?, message: String?) {
        showTopMessage(activity, message, R.color.purple, false)
    }

    fun getErrorResponse(error: ResponseBody?): Error {
        val gson = Gson()
        var baseResponse = Error()
        baseResponse.message = ""
        try {
            baseResponse = gson.fromJson(error!!.charStream(), Error::class.java)
            baseResponse.message = baseResponse.message
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return baseResponse
    }

    private fun showTopMessage(
        activity: Activity?, message: String?, @ColorRes bgColor: Int, isError: Boolean
    ) {
        if (activity != null && !activity.isFinishing && !message.isNullOrEmpty()) {
            CookieBar.build(activity as BaseActivity)
                .setTitle(activity.getString(R.string.app_name))
                .setTitleColor(R.color.white)
                .setBackgroundColor(bgColor)
                .setMessage(message).setIcon(0)
//                .setMessageSize(15)
//                .setMessageGravity(Gravity.CENTER_HORIZONTAL)
//                .setMessageFont(ResourcesCompat.getFont(activity, R.font.inter_regular))
                .setIcon(if (isError) R.drawable.icon_error else R.drawable.icon_success).setDuration(2000).show()
        }
    }


    fun updatePasswordView(editText: EditText, show: Boolean) {
        editText.transformationMethod = if (show) null else PasswordTransformationMethod.getInstance()
        editText.setSelection(editText.text.toString().length)
    }

    @JvmStatic
    fun isValidEmail(email: String): Boolean {
        val emailMatcher = Pattern.compile("^[_A-Za-z0-9-+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})\$").matcher(email)
        return email.isNotEmpty() && emailMatcher.find()
    }

    fun isValidPassword(password: String): Boolean {
        val pattern: Pattern
        //        val specialCharacters = "-@%\\[\\}+'!/#$^?:;,\\(\"\\)~`.*=&\\{>\\]<_"
        val passwordExp =
//            "^(?=.*[0-9])(?=.*[a-zA-Z])(?=\\S+$).{8,20}$"
            "^(?=.*[0-9a-zA-Z\\S+$]).{8,20}$"
        pattern = Pattern.compile(passwordExp)
        val matcher: Matcher = pattern.matcher(password)
//        return if (matcher.matches()) {
//            val patternSP: Pattern
//            val matcherSP: Matcher
//            val passwordRegExp =
//                "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[$specialCharacters])(?=\\S+$).{8,20}$"
//            patternSP = Pattern.compile(passwordRegExp)
//            matcherSP = patternSP.matcher(password)
//            !matcherSP.matches()
//        } else {
        return matcher.matches()
//        }
    }

    fun isPackageInstalled(packageName: String, packageManager: PackageManager): Boolean {
        return try {
            packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    @Suppress("DEPRECATION")
    fun getScreenWidth(activity: Activity): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics = activity.windowManager.currentWindowMetrics
            val insets: Insets = windowMetrics.windowInsets.getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
            windowMetrics.bounds.width() - insets.left - insets.right
        } else {
            val displayMetrics = DisplayMetrics()
            activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
            displayMetrics.widthPixels
        }
    }

    fun dpToPx(dp: Int, context: Context): Int {
        val displayMetrics: DisplayMetrics = context.resources.displayMetrics
        return (dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)).roundToInt()
    }


    @Suppress("DEPRECATION")
    fun <T : Parcelable?> getParcelable(intent: Intent?, name: String, clazz: Class<T>): T? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent?.getParcelableExtra(name, clazz)
        } else {
            intent?.getParcelableExtra(name)
        }
    }

    @Suppress("DEPRECATION")
    fun <T : Parcelable?> getParcelableArrayList(intent: Intent?, name: String, clazz: Class<T>): ArrayList<T>? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent?.getParcelableArrayListExtra(name, clazz)
        } else {
            intent?.getParcelableArrayListExtra(name)
        }
    }

    fun setTabMode(tabLayout: TabLayout) {
        tabLayout.post(Runnable {
            // don't forget to add Tab first before measuring..
            if (tabLayout.tabCount <= 4) {
                tabLayout.tabMode = TabLayout.MODE_FIXED
            } else {
                tabLayout.tabMode = TabLayout.MODE_SCROLLABLE
            }
        })
    }

    fun getWellnessLevel(context: Context, wellnessScore: Int): String {
        return when (wellnessScore) {
            8, 9, 10 -> {
                context.getString(R.string.excellent)
            }

            6, 7 -> {
                context.getString(R.string.great)
            }

            else -> {
                context.getString(R.string.can_be_improved)
            }
        }
    }

    fun getStressResponseAndRecoveryAbility(value: Int): String {
        return when (value) {
            1 -> "Low"
            2 -> "Normal"
            3 -> "High"
            else -> ""
        }
    }

    fun getStressLevel(value: String?): String {
        return when (value) {
            "1" -> MyApplication.instance!!.getString(R.string.low)
            "2" -> MyApplication.instance!!.getString(R.string.normal)
            "3" -> MyApplication.instance!!.getString(R.string.mild)
            "4", "5" -> MyApplication.instance!!.getString(R.string.high)
            else -> MyApplication.instance!!.getString(R.string.na)
        }
    }

    @SuppressLint("DiscouragedApi")
    fun getWellnessImage(context: Context, wellnessScore: Int): Int {
        var imageEndName = ""
        when (wellnessScore) {
            8, 9, 10 -> {
                imageEndName = if (Pref.isLowView) {
                    "excellent_r"
                } else {
                    "excellent"
                }
            }

            6, 7 -> {
                imageEndName = if (Pref.isLowView) {
                    "great_r"
                } else {
                    "great"
                }
            }

            else -> {
                imageEndName = if (Pref.isLowView) {
                    "improve_r"
                } else {
                    "improve"
                }
            }
        }

        val image = context.resources.getIdentifier(
            "wellness_score_$imageEndName", "drawable", context.packageName
        )
        return if (image != 0) image else R.drawable.anemone_green
    }

    fun getSuperText(unit: String?, observedValue: String?): Spanned {
        val str: Spanned
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            str = (Html.fromHtml(observedValue + "<sup><small>${unit}</small></sup>", Html.FROM_HTML_MODE_LEGACY))
        } else {
            str = (Html.fromHtml(observedValue + "<sup><small>${unit}</small></sup>"))
        }
        return str
    }


}