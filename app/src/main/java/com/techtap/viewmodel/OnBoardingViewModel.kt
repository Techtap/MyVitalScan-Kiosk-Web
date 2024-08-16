package com.techtap.viewmodel


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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
import com.techtap.network.Status
import com.techtap.repository.OnBoardingRepository
import com.techtap.utils.Utils
import com.techtap.di.ResourcesProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class OnBoardingViewModel @Inject constructor(
    private val resourcesProvider: ResourcesProvider, private val onBoardingRepository: OnBoardingRepository
) : ViewModel() {

    val errorMessage = MutableLiveData<String?>()
    val loading = MutableLiveData<Boolean>()


    val generateCodeState = MutableStateFlow(ApiResponseState(Status.LOADING, GenerateCodeResponse()))

    val sendScanResultState = MutableStateFlow(ApiResponseState(Status.LOADING, SendScanResponse()))

    val getScanInfoState = MutableStateFlow(ApiResponseState(Status.LOADING, MeasurementResponse()))

    val deleteScanIdState = MutableStateFlow(ApiResponseState(Status.LOADING, CommonResponse()))

    val sendVersionCheckState = MutableStateFlow(ApiResponseState(Status.LOADING, VersionCheckResponse()))

    val getQuestionState = MutableStateFlow(ApiResponseState(Status.LOADING, QuestionResponse()))

    val addQuestionState = MutableStateFlow(ApiResponseState(Status.LOADING, CommonResponse()))

    val beforeScanState = MutableStateFlow(ApiResponseState(Status.LOADING, BeforeScanResponse()))

    val scanStatusState = MutableStateFlow(ApiResponseState(Status.LOADING, CommonResponse()))

    fun generateCode() {
        if (Utils.isNetworkAvailable(resourcesProvider.context)) {
            generateCodeState.value = ApiResponseState.loading()
            viewModelScope.launch {
                onBoardingRepository.generateCode().catch {
                    generateCodeState.value = ApiResponseState.error(it.message, 100)
                }.collect {
                    generateCodeState.value = if (it.data != null) ApiResponseState.success(it.data, it.code)
                    else ApiResponseState.error(it.message, it.code)
                }
            }
        } else {
            generateCodeState.value = ApiResponseState.error(
                resourcesProvider.getString(R.string.no_internet_connection), 100
            )
        }
    }

    fun sendScanResult(code: String?, request: SendScanRequest) {
        when {
            (!Utils.isNetworkAvailable(resourcesProvider.context)) -> {
                sendScanResultState.value = ApiResponseState.error(
                    resourcesProvider.getString(R.string.no_internet_connection),
                    100
                )
            }

            else -> {
                sendScanResultState.value = ApiResponseState.loading()
                viewModelScope.launch {
                    onBoardingRepository.sendScanResult(code, request).catch {
                        sendScanResultState.value = ApiResponseState.error(it.message, 100)
                    }.collect {
                        sendScanResultState.value =
                            if (it.data != null) ApiResponseState.success(it.data, it.code)
                            else ApiResponseState.error(it.message, it.code)
                    }
                }
            }
        }
    }

    fun getScanInfo(scanId: String?) {
        when {
            (!Utils.isNetworkAvailable(resourcesProvider.context)) -> {
                getScanInfoState.value = ApiResponseState.error(
                    resourcesProvider.getString(R.string.no_internet_connection),
                    100
                )
            }

            else -> {
                getScanInfoState.value = ApiResponseState.loading()
                viewModelScope.launch {
                    onBoardingRepository.getScanInfo(scanId).catch {
                        getScanInfoState.value = ApiResponseState.error(it.message, 100)
                    }.collect {
                        getScanInfoState.value =
                            if (it.data != null) ApiResponseState.success(it.data, it.code)
                            else ApiResponseState.error(it.message, it.code)
                    }
                }
            }
        }
    }

    fun deleteScanId(scanId: String?) {
        when {
            (!Utils.isNetworkAvailable(resourcesProvider.context)) -> {
                deleteScanIdState.value = ApiResponseState.error(
                    resourcesProvider.getString(R.string.no_internet_connection),
                    100
                )
            }

            else -> {
                deleteScanIdState.value = ApiResponseState.loading()
                viewModelScope.launch {
                    onBoardingRepository.deleteScanId(scanId).catch {
                        deleteScanIdState.value = ApiResponseState.error(it.message, 100)
                    }.collect {
                        deleteScanIdState.value =
                            if (it.data != null) ApiResponseState.success(it.data, it.code)
                            else ApiResponseState.error(it.message, it.code)
                    }
                }
            }
        }
    }

    fun sendVersionCheckRequest(request: VersionCheckRequest) {
        when {
            (!Utils.isNetworkAvailable(resourcesProvider.context)) -> {
                sendVersionCheckState.value = ApiResponseState.error(
                    resourcesProvider.getString(R.string.no_internet_connection),
                    100
                )
            }

            else -> {
                sendVersionCheckState.value = ApiResponseState.loading()
                viewModelScope.launch {
                    onBoardingRepository.versionCheck(request).catch {
                        sendVersionCheckState.value = ApiResponseState.error(it.message, 100)
                    }.collect {
                        sendVersionCheckState.value =
                            if (it.data != null) ApiResponseState.success(it.data, it.code)
                            else ApiResponseState.error(it.message, it.code)
                    }
                }
            }
        }
    }


    fun getQuestion(code: String?) {
        when {
            (!Utils.isNetworkAvailable(resourcesProvider.context)) -> {
                getQuestionState.value = ApiResponseState.error(
                    resourcesProvider.getString(R.string.no_internet_connection),
                    100
                )
            }

            else -> {
                getQuestionState.value = ApiResponseState.loading()
                viewModelScope.launch {
                    onBoardingRepository.getQuestion(code).catch {
                        getQuestionState.value = ApiResponseState.error(it.message, 100)
                    }.collect {
                        getQuestionState.value =
                            if (it.data != null) ApiResponseState.success(it.data, it.code)
                            else ApiResponseState.error(it.message, it.code)
                    }
                }
            }
        }
    }


    fun addQuestion(code: String?, request: AddQuestionRequest) {
        when {
            (!Utils.isNetworkAvailable(resourcesProvider.context)) -> {
                addQuestionState.value = ApiResponseState.error(
                    resourcesProvider.getString(R.string.no_internet_connection),
                    100
                )
            }

            else -> {
                addQuestionState.value = ApiResponseState.loading()
                viewModelScope.launch {
                    onBoardingRepository.addQuestion(code, request).catch {
                        addQuestionState.value = ApiResponseState.error(it.message, 100)
                    }.collect {
                        addQuestionState.value =
                            if (it.data != null) ApiResponseState.success(it.data, it.code)
                            else ApiResponseState.error(it.message, it.code)
                    }
                }
            }
        }
    }


    fun beforeScan() {
        if (Utils.isNetworkAvailable(resourcesProvider.context)) {
            beforeScanState.value = ApiResponseState.loading()
            viewModelScope.launch {
                onBoardingRepository.beforeScan().catch {
                    beforeScanState.value = ApiResponseState.error(it.message, 100)
                }.collect {
                    beforeScanState.value =
                        if (it.data != null) ApiResponseState.success(it.data, it.code)
                        else ApiResponseState.error(it.message, it.code)
                }
            }
        } else {
            beforeScanState.value = ApiResponseState.error(
                resourcesProvider.getString(R.string.no_internet_connection), 100
            )
        }
    }

    fun scanStatus(request: ScanStatusRequest, id: Long) {
        if (Utils.isNetworkAvailable(resourcesProvider.context)) {
            scanStatusState.value = ApiResponseState.loading()
            viewModelScope.launch {
                onBoardingRepository.scanStatus(request, id).catch {
                    scanStatusState.value = ApiResponseState.error(it.message, 100)
                }.collect {
                    scanStatusState.value =
                        if (it.data != null) ApiResponseState.success(it.data, it.code)
                        else ApiResponseState.error(it.message, it.code)
                }
            }
        } else {
            scanStatusState.value = ApiResponseState.error(
                resourcesProvider.getString(R.string.no_internet_connection), 100
            )
        }
    }


}
