package com.techtap.network

import android.content.Context
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.techtap.AddQuestionRequest
import com.techtap.BeforeScanResponse
import com.techtap.GenerateCodeResponse
import com.techtap.MeasurementResponse
import com.techtap.QuestionResponse
import com.techtap.ScanStatusRequest
import com.techtap.SendScanRequest
import com.techtap.SendScanResponse
import com.techtap.VersionCheckRequest
import com.techtap.VersionCheckResponse
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

class NetworkService @JvmOverloads constructor(
    context: Context, baseUrl: String = domainName, okHttpClientFactory: OkHttpClientFactory
) {

    lateinit var api: NetworkAPI

    private var mContext: Context? = null

    @JvmOverloads
    fun createNetworkService(
        context: Context?, baseUrl: String = domainName, okHttpClientFactory: OkHttpClientFactory
    ) {
        mContext = context
        val okHttpClient = okHttpClientFactory.getOkHttpClient()
        val retrofit = Retrofit.Builder().baseUrl(baseUrl).addCallAdapterFactory(LiveDataCallAdapterFactory()).addConverterFactory(NullOnEmptyConverterFactory()).addConverterFactory(
            GsonConverterFactory.create(
                GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create()
            )
        ).client(okHttpClient).build()
        api = retrofit.create(NetworkAPI::class.java)
    }

    /**
     * all the Service all s to use for the retrofit requests.
     */
    interface NetworkAPI {

        @GET("/api/v2/generate-code")
        suspend fun generateCode(): Response<GenerateCodeResponse?>

        @POST("/api/v2/scan/{code}")
        suspend fun sendScanResult(@Path("code") code: String?, @Body request: SendScanRequest): Response<SendScanResponse?>

        @GET("/api/v2/scan/{scanId}")
        suspend fun getScanInfo(@Path("scanId") scanId: String?): Response<MeasurementResponse?>

        @GET("/api/v2/delete-code/{scanId}")
        suspend fun deleteScanId(@Path("scanId") scanId: String?): Response<CommonResponse?>

        @POST("/api/v2/version/check")
        suspend fun versionCheck(@Body request: VersionCheckRequest): Response<VersionCheckResponse?>


        @GET("/api/v2/question/{code}")
        suspend fun getQuestion(@Path("code") code: String?): Response<QuestionResponse?>

        @POST("/api/v2/question/{code}")
        suspend fun addQuestion(@Path("code") code: String?, @Body request: AddQuestionRequest): Response<CommonResponse?>

//        @POST("/api/user_address/add")
//        suspend fun addAddress(@Body request: AddAddressRequest?): Response<AddressResponse?>

        @POST("/api/v2/before-scan")
        suspend fun beforeScan(): Response<BeforeScanResponse?>

        @POST("/api/v2/scan/status/{scanId}")
        suspend fun scanStatus(@Body request: ScanStatusRequest, @Path("scanId") scanId: Long): Response<CommonResponse?>


    }

    companion object {
        const val HEADER_USER_AGENT = "User-Agent"

        /**
         * For Header Request Key
         */
        const val HEADER_BEAR = "HeaderBear"
        const val HEADER_BASIC = "HeaderBasic"
        const val HEADER_OPTION = "HeaderOption"
        const val HEADER_APP_VERSION = "app_version"
        const val HEADER_UUID = "x-uuid"
        const val HEADER_APP_VERSION_NAME = "X-App-Version-Name"
        const val HEADER_API_VERSION = "X-Api-Version-Code"
        const val HEADER_AUTHORIZATION = "Authorization"

        private val domainName: String
            get() = Url.HOST
    }

    init {
        createNetworkService(context, baseUrl, okHttpClientFactory)
    }
}