package com.example.visondl

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.tooling.preview.Preview
import com.example.visondl.data.DataManager
import com.example.visondl.ui.VisonDlApp
import com.example.visondl.ui.theme.ViSonDlTheme

private const val TAG = "MainActivity"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(TAG, "OnCreate Triggered")

        creationFolderAndFile()
        initLibs(applicationContext)

        enableEdgeToEdge()
        setContent {


            val newIntent by produceState(initialValue = null as Intent?) {
                val consumer = androidx.core.util.Consumer<Intent> {
                    this.value = it
                }
                addOnNewIntentListener(consumer)
                awaitDispose {
                    removeOnNewIntentListener(consumer)
                }
            }

            ViSonDlTheme {
                VisonDlApp(newIntent = newIntent)
            }
        }

    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "OnStop, save data")
        DataManager().saveData()
    }

}




@Preview(showBackground = true, showSystemUi = true)
@Composable
fun GreetingPreview() {
    ViSonDlTheme {
        VisonDlApp()
    }
}