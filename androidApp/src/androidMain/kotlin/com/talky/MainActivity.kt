package com.talky

import MainView
import ProvideComponentContext
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.arkivanov.decompose.defaultComponentContext

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val rootComponentContext = defaultComponentContext()

        setContent {
            MaterialTheme {
                Surface {
                    ProvideComponentContext(rootComponentContext) {
                        MainView()
                    }
                }
            }
        }
    }
}