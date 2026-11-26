package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.PI
import kotlin.math.sin

class TtsAudioController(context: Context) {

    private val appContext = context.applicationContext
    private val audioScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private var tts: TextToSpeech? = null

    private val _isReady = MutableStateFlow(false)
    val isReady: StateFlow<Boolean> = _isReady.asStateFlow()

    private val _isAvailable = MutableStateFlow(true)
    val isAvailable: StateFlow<Boolean> = _isAvailable.asStateFlow()

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    init {
        runCatching {
            tts = TextToSpeech(appContext) { status ->
                if (status == TextToSpeech.SUCCESS) {
                    val result = tts?.setLanguage(Locale.US)
                    val available = result != TextToSpeech.LANG_MISSING_DATA &&
                        result != TextToSpeech.LANG_NOT_SUPPORTED
                    _isReady.value = available
                    _isAvailable.value = available
                    tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                        override fun onStart(utteranceId: String?) {
                            _isSpeaking.value = true
                        }

                        override fun onDone(utteranceId: String?) {
                            _isSpeaking.value = false
                        }

                        @Deprecated("Deprecated in Java")
                        override fun onError(utteranceId: String?) {
                            _isSpeaking.value = false
                        }

                        override fun onError(utteranceId: String?, errorCode: Int) {
                            _isSpeaking.value = false
                        }
                    })
                } else {
                    _isReady.value = false
                    _isAvailable.value = false
                }
            }
        }.onFailure {
            _isReady.value = false
            _isAvailable.value = false
        }
    }

    fun speak(text: String, slow: Boolean): Boolean {
        val engine = tts
        if (engine == null || !_isReady.value) {
            return false
        }
        return runCatching {
            engine.setSpeechRate(if (slow) 0.7f else 1.0f)
            engine.speak(text, TextToSpeech.QUEUE_FLUSH, null, "owl_${System.currentTimeMillis()}")
            true
        }.getOrDefault(false)
    }

    fun playCorrectChime(enabled: Boolean) {
        if (!enabled) return
        audioScope.launch {
            playNotes(listOf(523.25 to 90, 659.25 to 140))
        }
    }

    fun playIncorrectChime(enabled: Boolean) {
        if (!enabled) return
        audioScope.launch {
            playNotes(listOf(311.13 to 110, 261.63 to 150))
        }
    }

    fun playCelebrationChime(enabled: Boolean) {
        if (!enabled) return
        audioScope.launch {
            playNotes(listOf(523.25 to 90, 659.25 to 90, 783.99 to 110, 1046.50 to 210))
        }
    }

    private fun playNotes(notes: List<Pair<Double, Int>>) {
        runCatching {
            val sampleRate = 22050
            val totalSamples = notes.sumOf { (_, ms) -> (sampleRate * ms) / 1000 }
            if (totalSamples <= 0) return

            val pcm = ShortArray(totalSamples)
            var offset = 0
            for ((freq, ms) in notes) {
                val count = (sampleRate * ms) / 1000
                for (i in 0 until count) {
                    val progress = i.toDouble() / count.toDouble()
                    val envelope = when {
                        progress < 0.1 -> progress / 0.1
                        progress > 0.7 -> (1.0 - progress) / 0.3
                        else -> 1.0
                    }.coerceIn(0.0, 1.0)
                    val sample = sin(2.0 * PI * freq * i / sampleRate) * envelope * 0.28
                    pcm[offset + i] = (sample * Short.MAX_VALUE).toInt().toShort()
                }
                offset += count
            }

            val bufferBytes = pcm.size * 2
            val track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(bufferBytes)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            track.write(pcm, 0, pcm.size)
            track.play()
            val totalMs = notes.sumOf { it.second }.toLong() + 60L
            Thread.sleep(totalMs)
            track.stop()
            track.release()
        }
    }

    fun release() {
        runCatching {
            tts?.stop()
            tts?.shutdown()
        }
        tts = null
    }
}
