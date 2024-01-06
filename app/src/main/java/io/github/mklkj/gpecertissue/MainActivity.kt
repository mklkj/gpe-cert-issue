package io.github.mklkj.gpecertissue

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.mklkj.gpecertissue.ui.theme.GPECertIssueTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor

class MainActivity : ComponentActivity() {

    private val okHttp = OkHttpClient.Builder()
        .addNetworkInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        .build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GPECertIssueTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val scope = rememberCoroutineScope()
                    var isLoading by remember { mutableStateOf(false) }
                    var result by remember { mutableStateOf<String?>(null) }

                    Column(
                        Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            enabled = !isLoading,
                            onClick = {
                                scope.launch {
                                    isLoading = true
                                    runCatching { makeCall() }
                                        .onFailure {
                                            result = it.message
                                            Log.e("GPE", "ERROR", it)
                                        }
                                        .onSuccess { result = it }
                                    isLoading = false
                                }
                            }) {
                            Text(text = "Make a request to GPE")
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        if (isLoading) {
                            CircularProgressIndicator(Modifier.size(30.dp))
                        } else Text(
                            text = result.toString(),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }

    private suspend fun makeCall() = withContext(Dispatchers.IO) {
        okHttp.newCall(
            Request.Builder()
                .url("https://cufs.edu.gdansk.pl/")
                .build()
        ).execute().body?.string()
    }
}
