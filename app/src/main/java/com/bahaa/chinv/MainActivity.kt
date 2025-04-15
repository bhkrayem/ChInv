package com.bahaa.chinv

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.bahaa.chinv.ui.theme.ChInvTheme
import androidx.activity.viewModels
import com.bahaa.chinv.viewmodel.ItemViewModel

class MainActivity : ComponentActivity() {
    private val itemViewModel: ItemViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChInvTheme {
                AppNavHost()
            }
        }
    }
}
