package com.techtap.repository


import androidx.lifecycle.viewModelScope
import com.techtap.AddQuestionRequest
import com.techtap.BeforeScanResponse
import com.techtap.GenerateCodeResponse
import com.techtap.MeasurementResponse
import com.techtap.QuestionResponse
import com.techtap.R
import com.techtap.ScanStatusRequest
import com.techtap.SendScanRequest
import com.techtap.SendScanResponse
import com.techtap.VersionCheckRequest
import com.techtap.VersionCheckResponse
import com.techtap.network.ApiResponseState
import com.techtap.network.CommonResponse
import com.techtap.network.NetworkService
import com.techtap.network.ResponseStatus
import com.techtap.utils.Enums
import com.techtap.utils.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OnBoardingRepository @Inject constructor(private val networkService: NetworkService) {

    suspend fun generateCode(): Flow<ApiResponseState<GenerateCodeResponse>> {
        return flow {
            val response = networkService.api.generateCode()
            if (response.isSuccessful && response.body()?.status != null && response.body()!!.status == Enums.ApiStatus.success.toString()) {
                emit(ApiResponseState.success(response.body(), response.code()))
            } else {
                emit(ApiResponseState.error(Utils.getErrorResponse(response.errorBody()).message, response.code()))
            }
        }.flowOn(Dispatchers.IO)
    }

    fun sendScanResult(code: String?, request: SendScanRequest): Flow<ApiResponseState<SendScanResponse>> {
        return flow {
            val response = networkService.api.sendScanResult(code, request)
            if (response.isSuccessful) {
                emit(ApiResponseState.success(response.body(), response.code()))
            } else {
                emit(
                    ApiResponseState.error(
                        Utils.getErrorResponse(response.errorBody()).message, response.code()
                    )
                )
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getScanInfo(scanId: String?): Flow<ApiResponseState<MeasurementResponse>> {
        return flow {
            val response = networkService.api.getScanInfo(scanId)
            if (response.isSuccessful) {
                emit(ApiResponseState.success(response.body(), response.code()))
            } else {
                emit(
                    ApiResponseState.error(
                        Utils.getErrorResponse(response.errorBody()).message, response.code()
                    )
                )
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun deleteScanId(scanId: String?): Flow<ApiResponseState<CommonResponse>> {
        return flow {
            val response = networkService.api.deleteScanId(scanId)
            if (response.isSuccessful) {
                emit(ApiResponseState.success(response.body(), response.code()))
            } else {
                emit(
                    ApiResponseState.error(
                        Utils.getErrorResponse(response.errorBody()).message, response.code()
                    )
                )
            }
        }.flowOn(Dispatchers.IO)
    }

    fun versionCheck(request: VersionCheckRequest): Flow<ApiResponseState<VersionCheckResponse>> {
        return flow {
            val response = networkService.api.versionCheck(request)
            if (response.isSuccessful) {
                emit(ApiResponseState.success(response.body(), response.code()))
            } else {
                emit(
                    ApiResponseState.error(
                        Utils.getErrorResponse(response.errorBody()).message, response.code()
                    )
                )
            }
        }.flowOn(Dispatchers.IO)
    }

    fun getQuestion(code: String?): Flow<ApiResponseState<QuestionResponse>> {
        return flow {
            val response = networkService.api.getQuestion(code)
            if (response.isSuccessful) {
                emit(ApiResponseState.success(response.body(), response.code()))
            } else {
                emit(
                    ApiResponseState.error(
                        Utils.getErrorResponse(response.errorBody()).message, response.code()
                    )
                )
            }
        }.flowOn(Dispatchers.IO)
    }


    fun addQuestion(code: String?, request: AddQuestionRequest): Flow<ApiResponseState<CommonResponse>> {
        return flow {
            val response = networkService.api.addQuestion(code, request)
            if (response.isSuccessful) {
                emit(ApiResponseState.success(response.body(), response.code()))
            } else {
                emit(
                    ApiResponseState.error(
                        Utils.getErrorResponse(response.errorBody()).message, response.code()
                    )
                )
            }
        }.flowOn(Dispatchers.IO)
    }

    fun beforeScan(): Flow<ApiResponseState<BeforeScanResponse>> {
        return flow {
            val response = networkService.api.beforeScan()
            if (response.isSuccessful) {
                emit(ApiResponseState.success(response.body(), response.code()))
            } else {
                emit(ApiResponseState.error(Utils.getErrorResponse(response.errorBody()).message, response.code()))
            }
        }.flowOn(Dispatchers.IO)
    }

    fun scanStatus(request: ScanStatusRequest, id: Long): Flow<ApiResponseState<CommonResponse>> {
        return flow {
            val response = networkService.api.scanStatus(request, id)
            if (response.isSuccessful) {
                emit(ApiResponseState.success(response.body(), response.code()))
            } else {
                emit(ApiResponseState.error(Utils.getErrorResponse(response.errorBody()).message, response.code()))
            }
        }.flowOn(Dispatchers.IO)
    }


}
