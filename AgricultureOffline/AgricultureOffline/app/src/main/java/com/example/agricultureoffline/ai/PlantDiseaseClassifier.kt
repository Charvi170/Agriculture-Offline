package com.example.agricultureoffline.ai

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.core.graphics.scale
import com.example.agricultureoffline.data.model.PredictionResult
import org.json.JSONObject
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel

class PlantDiseaseClassifier(
    private val context: Context
) {
    private val TAG = "PlantDiseaseClassifier"
    private var interpreter: Interpreter? = null
    private var classNames: Map<Int, String> = emptyMap()
    private var isInitialized = false

    /**
     * Lazily initialize the TensorFlow Lite interpreter and labels.
     * Prevents loading model into memory or crashing on app navigation/screen opening.
     */
    @Synchronized
    fun initialize(): Boolean {
        if (isInitialized && interpreter != null) return true
        return try {
            val modelBuffer = loadModelFile()
            val options = Interpreter.Options()
            interpreter = Interpreter(modelBuffer, options)
            classNames = loadClassNames()
            isInitialized = true
            Log.d(TAG, "TFLite interpreter initialized successfully.")
            true
        } catch (t: Throwable) {
            Log.e(TAG, "Failed to initialize TensorFlow Lite model", t)
            isInitialized = false
            false
        }
    }

    private fun loadModelFile(): ByteBuffer {
        return try {
            val fileDescriptor = context.assets.openFd("AgriVision_EfficientNetB0.tflite")
            val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
            val fileChannel = inputStream.channel
            fileChannel.map(
                FileChannel.MapMode.READ_ONLY,
                fileDescriptor.startOffset,
                fileDescriptor.declaredLength
            )
        } catch (e: Exception) {
            Log.w(TAG, "openFd failed, reading direct byte buffer from assets", e)
            context.assets.open("AgriVision_EfficientNetB0.tflite").use { stream ->
                val bytes = stream.readBytes()
                val buffer = ByteBuffer.allocateDirect(bytes.size)
                buffer.order(ByteOrder.nativeOrder())
                buffer.put(bytes)
                buffer.rewind()
                buffer
            }
        }
    }

    private fun loadClassNames(): Map<Int, String> {
        return try {
            val jsonText = context.assets
                .open("class_indices.json")
                .bufferedReader()
                .use { it.readText() }

            val jsonObject = JSONObject(jsonText)
            val result = mutableMapOf<Int, String>()
            val keys = jsonObject.keys()
            while (keys.hasNext()) {
                val key = keys.next()
                result[key.toInt()] = jsonObject.getString(key)
            }
            result
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load class_indices.json", e)
            emptyMap()
        }
    }

    fun predict(bitmap: Bitmap): PredictionResult? {
        if (!initialize()) {
            Log.e(TAG, "Prediction aborted: Classifier model could not be initialized")
            return null
        }
        val tflite = interpreter ?: return null

        return try {
            val inputShape = tflite.getInputTensor(0).shape()
            val inputHeight = inputShape[1]
            val inputWidth = inputShape[2]
            val inputChannels = inputShape[3]

            // Ensure bitmap is software-backed to avoid Config#HARDWARE getPixels crash
            val softwareBitmap = if (bitmap.config == Bitmap.Config.HARDWARE) {
                bitmap.copy(Bitmap.Config.ARGB_8888, false)
            } else {
                bitmap
            } ?: bitmap

            val resizedBitmap = softwareBitmap.scale(inputWidth, inputHeight)

            val inputBuffer = ByteBuffer.allocateDirect(
                4 * inputHeight * inputWidth * inputChannels
            ).apply {
                order(ByteOrder.nativeOrder())
            }

            val pixels = IntArray(inputWidth * inputHeight)
            resizedBitmap.getPixels(pixels, 0, inputWidth, 0, 0, inputWidth, inputHeight)

            for (pixel in pixels) {
                val red = ((pixel shr 16) and 0xFF).toFloat()
                val green = ((pixel shr 8) and 0xFF).toFloat()
                val blue = (pixel and 0xFF).toFloat()

                // AgriVision EfficientNetB0 contains internal rescaling (x / 255.0) and normalization layers,
                // expecting raw RGB pixel values in [0.0f, 255.0f].
                inputBuffer.putFloat(red)
                inputBuffer.putFloat(green)
                inputBuffer.putFloat(blue)
            }
            inputBuffer.rewind()

            val outputShape = tflite.getOutputTensor(0).shape()
            val numberOfClasses = outputShape[1]
            val output = Array(1) { FloatArray(numberOfClasses) }

            tflite.run(inputBuffer, output)

            val probabilities = output[0]
            var bestIndex = 0
            var bestConfidence = probabilities[0]

            for (i in 1 until probabilities.size) {
                if (probabilities[i] > bestConfidence) {
                    bestConfidence = probabilities[i]
                    bestIndex = i
                }
            }

            // Get class name or fallback safely if class mapping is missing
            val diseaseName = classNames[bestIndex] ?: "Class_$bestIndex"
            Log.d(TAG, "Inference successful: bestIndex=$bestIndex, predictedClass='$diseaseName', confidence=$bestConfidence")

            PredictionResult(
                diseaseName = diseaseName,
                confidence = bestConfidence
            )
        } catch (t: Throwable) {
            Log.e(TAG, "Inference error in PlantDiseaseClassifier", t)
            null
        }
    }

    fun close() {
        try {
            interpreter?.close()
            interpreter = null
            isInitialized = false
        } catch (e: Exception) {
            Log.e(TAG, "Error closing interpreter", e)
        }
    }
}
