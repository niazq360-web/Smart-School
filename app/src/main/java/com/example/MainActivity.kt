package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.data.SchoolDatabase
import com.example.data.SchoolRepository
import com.example.ui.MainApp
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = SchoolDatabase.getInstance(this)
        val repository = SchoolRepository(database.schoolDao())

        setContent {
            MyApplicationTheme {
                MainApp(repository = repository)
            }
        }
    }
}
