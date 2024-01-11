package com.kristianskokars.shotsandbeer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.kristianskokars.shotsandbeer.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
