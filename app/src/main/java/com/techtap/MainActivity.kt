package com.techtap

import ai.binah.sdk.api.HealthMonitorException
import ai.binah.sdk.api.SessionEnabledVitalSigns
import ai.binah.sdk.api.alerts.AlertCodes
import ai.binah.sdk.api.alerts.ErrorData
import ai.binah.sdk.api.alerts.WarningData
import ai.binah.sdk.api.images.ImageData
import ai.binah.sdk.api.images.ImageListener
import ai.binah.sdk.api.images.ImageValidity
import ai.binah.sdk.api.license.LicenseDetails
import ai.binah.sdk.api.license.LicenseInfo
import ai.binah.sdk.api.session.Session
import ai.binah.sdk.api.session.SessionInfoListener
import ai.binah.sdk.api.session.SessionState
import ai.binah.sdk.api.vital_signs.VitalSign
import ai.binah.sdk.api.vital_signs.VitalSignTypes
import ai.binah.sdk.api.vital_signs.VitalSignsListener
import ai.binah.sdk.api.vital_signs.VitalSignsResults
import ai.binah.sdk.api.vital_signs.vitals.VitalSignBloodPressure
import ai.binah.sdk.api.vital_signs.vitals.VitalSignHemoglobin
import ai.binah.sdk.api.vital_signs.vitals.VitalSignHemoglobinA1C
import ai.binah.sdk.api.vital_signs.vitals.VitalSignLFHF
import ai.binah.sdk.api.vital_signs.vitals.VitalSignMeanRRI
import ai.binah.sdk.api.vital_signs.vitals.VitalSignOxygenSaturation
import ai.binah.sdk.api.vital_signs.vitals.VitalSignPNSIndex
import ai.binah.sdk.api.vital_signs.vitals.VitalSignPNSZone
import ai.binah.sdk.api.vital_signs.vitals.VitalSignPRQ
import ai.binah.sdk.api.vital_signs.vitals.VitalSignPulseRate
import ai.binah.sdk.api.vital_signs.vitals.VitalSignRMSSD
import ai.binah.sdk.api.vital_signs.vitals.VitalSignRRI
import ai.binah.sdk.api.vital_signs.vitals.VitalSignRespirationRate
import ai.binah.sdk.api.vital_signs.vitals.VitalSignSD1
import ai.binah.sdk.api.vital_signs.vitals.VitalSignSD2
import ai.binah.sdk.api.vital_signs.vitals.VitalSignSDNN
import ai.binah.sdk.api.vital_signs.vitals.VitalSignSNSIndex
import ai.binah.sdk.api.vital_signs.vitals.VitalSignSNSZone
import ai.binah.sdk.api.vital_signs.vitals.VitalSignStressIndex
import ai.binah.sdk.api.vital_signs.vitals.VitalSignStressLevel
import ai.binah.sdk.api.vital_signs.vitals.VitalSignWellnessIndex
import ai.binah.sdk.api.vital_signs.vitals.VitalSignWellnessLevel
import ai.binah.sdk.session.FaceSessionBuilder
import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.RectF
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.databinding.DataBindingUtil
import com.techtap.databinding.ActivityMainBinding


/**
 * Loads [MainFragment].
 */
class MainActivity : AppCompatActivity(), ImageListener, VitalSignsListener, SessionInfoListener {

    private lateinit var binding: ActivityMainBinding

    companion object {
        const val licenseKey = "B99407-F46969-4ED6AA-D97EE3-B7D52E-F050D5"
        const val measurementDuration = 60L
    }

    private val logTag = "BinahSample"

    private val permissionsRequestCode = 12345
    private var session: Session? = null

    private val faceDetection: Bitmap? by lazy {
        ContextCompat.getDrawable(this, R.drawable.face_detection)?.toBitmap()
    }

//    var textToSpeech: TextToSpeech? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        if (savedInstanceState == null) {

//            textToSpeech = TextToSpeech(applicationContext) { i ->
//                // if No error is found then only it will run
//                if (i != TextToSpeech.ERROR) {
//                    // To Choose language of speech
//                    textToSpeech!!.language = Locale.CHINESE
//                }
//            }

            binding.startStopButton.setOnClickListener {
                handleStartStopButtonClicked()
//                textToSpeech?.speak(getString(R.string.dummy), TextToSpeech.QUEUE_FLUSH, null, "")
            }

//            Glide.with(this).load(getImage("heart")).into(binding.ivWebp);


//            textToSpeech?.stop()
        }
    }

    fun getImage(imageName: String?): Int {
        return resources.getIdentifier(imageName, "drawable", packageName)
    }

    override fun onStart() {
        super.onStart()
        val permissionStatus = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
            createSession()
        } else {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), permissionsRequestCode)
        }
    }

    override fun onStop() {
        super.onStop()
        session?.terminate()
        session = null
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == permissionsRequestCode && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            createSession()
        }
    }

    override fun onImage(imageData: ImageData) {
        runOnUiThread {
            if (imageData.imageValidity != ImageValidity.VALID) {
                Log.i(logTag, "Image Validity Error: ${imageData.imageValidity}")
            }

            binding.cameraView.lockCanvas()?.let { canvas ->
                // Drawing the bitmap on the TextureView canvas
                val image = imageData.image
                canvas.drawBitmap(
                    image, null, Rect(0, 0, binding.cameraView.width, binding.cameraView.bottom - binding.cameraView.top), null
                )

                //Drawing the face detection (if not null..)
                imageData.roi?.let roi@{ faceDetectionRect ->
                    //First we scale the SDK face detection rectangle to fit the TextureView size
                    val targetRect = RectF(faceDetectionRect)
                    val m = Matrix()
                    m.postScale(1f, 1f, image.width / 2f, image.height / 2f)
                    m.postScale(
                        binding.cameraView.width.toFloat() / image.width.toFloat(), binding.cameraView.height.toFloat() / image.height.toFloat()
                    )
                    m.mapRect(targetRect)
                    // Then we draw it on the Canvas
                    canvas.drawBitmap(faceDetection ?: return@roi, null, targetRect, null)
                }

                binding.cameraView.unlockCanvasAndPost(canvas)
            }
        }
    }

    override fun onVitalSign(vitalSign: VitalSign) {
        runOnUiThread {
            (vitalSign as? VitalSignPulseRate)?.let { pulseRate ->
                "PR: ${pulseRate.value}".also { binding.pulseRate.text = it }
            }
        }
    }

    override fun onFinalResults(finalResults: VitalSignsResults) {
        runOnUiThread {
            val pulseRate = (finalResults.getResult(VitalSignTypes.PULSE_RATE) as? VitalSignPulseRate)?.value ?: "N/A"
            val bloodPressureValue = (finalResults.getResult(VitalSignTypes.BLOOD_PRESSURE) as? VitalSignBloodPressure)?.let { bloodPressure ->
                "${bloodPressure.value.systolic}/${bloodPressure.value.diastolic}"
            } ?: "N/A"
            val LFHF = (finalResults.getResult(VitalSignTypes.LFHF) as? VitalSignLFHF)?.value ?: "N/A"
            val meanRRI = (finalResults.getResult(VitalSignTypes.MEAN_RRI) as? VitalSignMeanRRI)?.value ?: "N/A"
            val meanRRIConfidence = (finalResults.getResult(VitalSignTypes.MEAN_RRI) as? VitalSignMeanRRI)?.confidence?.level?.name ?: "N/A"
            val oxygenSaturation = (finalResults.getResult(VitalSignTypes.OXYGEN_SATURATION) as? VitalSignOxygenSaturation)?.value ?: "N/A"
            val pnsIndex = (finalResults.getResult(VitalSignTypes.PNS_INDEX) as? VitalSignPNSIndex)?.value ?: "N/A"
            val pnsZone = (finalResults.getResult(VitalSignTypes.PNS_ZONE) as? VitalSignPNSZone)?.value ?: "N/A"
            val prq = (finalResults.getResult(VitalSignTypes.PRQ) as? VitalSignPRQ)?.value ?: "N/A"
            val prqConfidence = (finalResults.getResult(VitalSignTypes.PRQ) as? VitalSignPRQ)?.confidence?.level?.name ?: "N/A"
            val RMSSD = (finalResults.getResult(VitalSignTypes.RMSSD) as? VitalSignRMSSD)?.value ?: "N/A"
            val RRI = (finalResults.getResult(VitalSignTypes.RRI) as? VitalSignRRI)?.value ?: "N/A"
            val respirationRate = (finalResults.getResult(VitalSignTypes.RESPIRATION_RATE) as? VitalSignRespirationRate)?.value ?: "N/A"
            val SD1 = (finalResults.getResult(VitalSignTypes.SD1) as? VitalSignSD1)?.value ?: "N/A"
            val SD2 = (finalResults.getResult(VitalSignTypes.SD2) as? VitalSignSD2)?.value ?: "N/A"
            val HRV_SDNN = (finalResults.getResult(VitalSignTypes.SDNN) as? VitalSignSDNN)?.value ?: "N/A"
            val SNS_INDEX = (finalResults.getResult(VitalSignTypes.SNS_INDEX) as? VitalSignSNSIndex)?.value ?: "N/A"

            val SNS_ZONE = (finalResults.getResult(VitalSignTypes.SNS_ZONE) as? VitalSignSNSZone)?.value ?: "N/A"
            val STRESS_LEVEL = (finalResults.getResult(VitalSignTypes.STRESS_LEVEL) as? VitalSignStressLevel)?.value ?: "N/A"
            val STRESS_INDEX = (finalResults.getResult(VitalSignTypes.STRESS_INDEX) as? VitalSignStressIndex)?.value ?: "N/A"
            val WELLNESS_INDEX = (finalResults.getResult(VitalSignTypes.WELLNESS_INDEX) as? VitalSignWellnessIndex)?.value ?: "N/A"
            val WELLNESS_LEVEL = (finalResults.getResult(VitalSignTypes.WELLNESS_LEVEL) as? VitalSignWellnessLevel)?.value ?: "N/A"

            val HEMOGLOBIN = (finalResults.getResult(VitalSignTypes.HEMOGLOBIN) as? VitalSignHemoglobin)?.value ?: "N/A"
            val HEMOGLOBIN_A1C = (finalResults.getResult(VitalSignTypes.HEMOGLOBIN_A1C) as? VitalSignHemoglobinA1C)?.value ?: "N/A"


            val finalResultStr = StringBuilder()
            finalResultStr.append("Pulse Rate : $pulseRate\n")
            finalResultStr.append("Blood Pressure: $bloodPressureValue\n")
            finalResultStr.append("LFHF: $LFHF\n")
            finalResultStr.append("meanRRI: $meanRRI\n")
            finalResultStr.append("meanRRIConfidence: $meanRRIConfidence\n")
            finalResultStr.append("oxygenSaturation: $oxygenSaturation\n")
            finalResultStr.append("pnsIndex: $pnsIndex\n")
            finalResultStr.append("pnsZone: $pnsZone\n")
            finalResultStr.append("prq: $prq\n")
            finalResultStr.append("prqConfidence: $prqConfidence\n")
            finalResultStr.append("RMSSD: $RMSSD\n")
            finalResultStr.append("RRI: $RRI\n")
            finalResultStr.append("respirationRate: $respirationRate\n")
            finalResultStr.append("SD1: $SD1\n")
            finalResultStr.append("SD2: $SD2\n")
            finalResultStr.append("SDNN: $HRV_SDNN\n")
            finalResultStr.append("SNS INDEX: $SNS_INDEX\n")
            finalResultStr.append("SNS ZONE: $SNS_ZONE\n")
            finalResultStr.append("STRESS LEVEL: $STRESS_LEVEL\n")
            finalResultStr.append("STRESS_INDEX: $STRESS_INDEX\n")
            finalResultStr.append("WELLNESS INDEX: $WELLNESS_INDEX\n")
            finalResultStr.append("WELLNESS LEVEL: $WELLNESS_LEVEL\n")

            finalResultStr.append("HEMOGLOBIN: $HEMOGLOBIN\n")
            finalResultStr.append("HEMOGLOBIN_A1C: $HEMOGLOBIN_A1C\n")



            Log.e("finalResultStr == ", "" + finalResultStr.toString())

//            showAlert("Final Results ", "Pulse Rate: $pulseRate\nBlood Pressure: $bloodPressureValue")
            showAlert("Final Result", finalResultStr.toString())
        }
    }

    override fun onSessionStateChange(sessionState: SessionState) {
        runOnUiThread {
            when (sessionState) {
                SessionState.READY -> {
                    binding.startStopButton.isEnabled = true
                    binding.startStopButton.text = getString(R.string.start)
                    window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                }

                SessionState.PROCESSING -> {
                    binding.startStopButton.isEnabled = true
                    binding.startStopButton.text = getString(R.string.stop)
                    window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                }

                else -> {
                    binding.startStopButton.isEnabled = false
                    window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                }
            }
        }
    }

    override fun onWarning(warningData: WarningData) {
        runOnUiThread {
            if (warningData.code == AlertCodes.MEASUREMENT_CODE_MISDETECTION_DURATION_EXCEEDS_LIMIT_WARNING) {
                binding.pulseRate.text = ""
            }

            Toast.makeText(this, "Warning: ${warningData.code}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onError(errorData: ErrorData) {
        runOnUiThread {
            showAlert(message = "Error: ${errorData.code}")
        }
    }

    override fun onLicenseInfo(licenseInfo: LicenseInfo) {
        runOnUiThread {
            licenseInfo.licenseActivationInfo.activationID.takeIf { it.isNotEmpty() }?.let { id ->
                Log.i(logTag, "License Activation ID: $id")
            }

            licenseInfo.licenseOfflineMeasurements?.let { offlineMeasurements ->
                Log.i(
                    logTag, "License Offline Measurements: " + "${offlineMeasurements.totalMeasurements}/" + "${offlineMeasurements.remainingMeasurements}"
                )
            }
        }
    }

    override fun onEnabledVitalSigns(sessionEnabledVitalSigns: SessionEnabledVitalSigns) {
        runOnUiThread {
            Log.i(
                logTag, "Pulse Rate Enabled: ${sessionEnabledVitalSigns.isEnabled(VitalSignTypes.PULSE_RATE)}"
            )
        }
    }


    private fun createSession() {
        val licenseDetails = LicenseDetails(licenseKey)
        try {
            session = FaceSessionBuilder(applicationContext).withImageListener(this).withVitalSignsListener(this).withSessionInfoListener(this).build(licenseDetails)
        } catch (e: HealthMonitorException) {
            showAlert(message = "Error ${e.errorCode}")
        }
    }

    private fun handleStartStopButtonClicked() {
        try {
            if (session?.state == SessionState.READY) {
                binding.pulseRate.text = ""
                session?.start(measurementDuration)
            } else {
                session?.stop()
            }
        } catch (e: HealthMonitorException) {
            showAlert(message = "Error ${e.errorCode}")
        }
    }

    private fun showAlert(title: String? = null, message: String) {
        AlertDialog.Builder(this).setTitle(title).setMessage(message).setPositiveButton("OK", null).setCancelable(false).show()
    }

}