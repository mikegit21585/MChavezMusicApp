package com.example.mchavezmusicapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.mchavezmusicapp.navigation.AppNavigation
import com.example.mchavezmusicapp.ui.theme.MChavezMusicAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MChavezMusicAppTheme {
                AppNavigation()
            }
        }
    }
}