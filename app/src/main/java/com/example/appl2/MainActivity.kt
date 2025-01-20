package com.example.appl2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity123"
    private lateinit var inputText: EditText
    private lateinit var translatedText: TextView
    private lateinit var inputLanguageSpinner: Spinner
    private lateinit var outputLanguageSpinner: Spinner
    private lateinit var translateButton: Button
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d(TAG, "onCreate: Initializing UI components.")
        inputText = findViewById(R.id.inputText)
        translatedText = findViewById(R.id.translatedText)
        inputLanguageSpinner = findViewById(R.id.inputLanguageSpinner)
        outputLanguageSpinner = findViewById(R.id.outputLanguageSpinner)
        translateButton = findViewById(R.id.translateButton)
        progressBar = findViewById(R.id.progressBar)

        val languages = mapOf(
            "Afrikaans" to "af",
            "Arabic" to "ar",
            "Belarusian" to "be",
            "Bulgarian" to "bg",
            "Bengali" to "bn",
            "Catalan" to "ca",
            "Czech" to "cs",
            "Welsh" to "cy",
            "Danish" to "da",
            "German" to "de",
            "Greek" to "el",
            "English" to "en",
            "Esperanto" to "eo",
            "Spanish" to "es",
            "Estonian" to "et",
            "Persian" to "fa",
            "Finnish" to "fi",
            "French" to "fr",
            "Irish" to "ga",
            "Galician" to "gl",
            "Gujarati" to "gu",
            "Hebrew" to "he",
            "Hindi" to "hi",
            "Croatian" to "hr",
            "Haitian Creole" to "ht",
            "Hungarian" to "hu",
            "Indonesian" to "id",
            "Icelandic" to "is",
            "Italian" to "it",
            "Japanese" to "ja",
            "Georgian" to "ka",
            "Kannada" to "kn",
            "Korean" to "ko",
            "Lithuanian" to "lt",
            "Latvian" to "lv",
            "Macedonian" to "mk",
            "Marathi" to "mr",
            "Malay" to "ms",
            "Maltese" to "mt",
            "Dutch" to "nl",
            "Norwegian" to "no",
            "Polish" to "pl",
            "Portuguese" to "pt",
            "Romanian" to "ro",
            "Russian" to "ru",
            "Slovak" to "sk",
            "Slovenian" to "sl",
            "Albanian" to "sq",
            "Swedish" to "sv",
            "Swahili" to "sw",
            "Tamil" to "ta",
            "Telugu" to "te",
            "Thai" to "th",
            "Tagalog" to "tl",
            "Turkish" to "tr",
            "Ukrainian" to "uk",
            "Urdu" to "ur",
            "Vietnamese" to "vi",
            "Chinese" to "zh"
        )

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            languages.keys.toList()
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        inputLanguageSpinner.adapter = adapter
        outputLanguageSpinner.adapter = adapter

        Log.d(TAG, "onCreate: Spinner initialized with languages.")

        translateButton.setOnClickListener {
            val sourceLang = languages[inputLanguageSpinner.selectedItem.toString()] ?: "en"
            val targetLang = languages[outputLanguageSpinner.selectedItem.toString()] ?: "es"
            val text = inputText.text.toString()

            Log.d(TAG, "Translate button clicked. Source: $sourceLang, Target: $targetLang, Text: $text")

            if (text.isNotEmpty()) {
                progressBar.visibility = ProgressBar.VISIBLE
                Log.d(TAG, "Starting translation process.")

                CoroutineScope(Dispatchers.Main).launch {
                    val translation = translateText(text, sourceLang, targetLang)
                    translatedText.text = translation
                    progressBar.visibility = ProgressBar.GONE
                    Log.d(TAG, "Translation process completed.")
                }
            } else {
                Log.d(TAG, "Input text is empty. Prompting user to enter text.")
                Toast.makeText(this, "Please enter text to translate", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private suspend fun translateText(
        text: String,
        sourceLang: String,
        targetLang: String
    ): String {
        Log.d(TAG, "translateText: Preparing to translate text: $text from $sourceLang to $targetLang")

        return try {
            val options = TranslatorOptions.Builder()
                .setSourceLanguage(sourceLang)
                .setTargetLanguage(targetLang)
                .build()

            val translator = Translation.getClient(options)
            Log.d(TAG, "translateText: Translation client initialized.")

            translator.downloadModelIfNeeded().await()
            Log.d(TAG, "translateText: Model downloaded successfully.")

            val result = translator.translate(text).await()
            Log.d(TAG, "translateText: Translation completed. Result: $result")

            translator.close()
            Log.d(TAG, "translateText: Translator resources released.")
            result
        } catch (e: Exception) {
            Log.e(TAG, "translateText: Translation failed. Error: ${e.localizedMessage}", e)
            "Translation failed: ${e.localizedMessage}"
        }
    }
}
