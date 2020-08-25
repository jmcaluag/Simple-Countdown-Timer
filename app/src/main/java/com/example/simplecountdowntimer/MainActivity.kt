package com.example.simplecountdowntimer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import androidx.databinding.DataBindingUtil
import com.example.simplecountdowntimer.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var timeLeftInMilliseconds: Long = 600000 //10 minutes
    private var timerRunning: Boolean = false // Initial state: timer is NOT running
    val timer = object : CountDownTimer(timeLeftInMilliseconds, 1000) {
        override fun onTick(p0: Long) {
            timeLeftInMilliseconds = p0
            updateTimer()
        }

        override fun onFinish() {
            // Not needed
        }

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.button.text = "Start"

        binding.button.setOnClickListener { startStop(it) }

        updateTimer()
    }

    private fun startStop(view: View) {
        if(timerRunning) {
            timerRunning = false
            stopTimer()
            binding.button.text = "Start"
        } else {
            timerRunning = true
            startTimer()
            binding.button.text = "Pause"
        }
    }

    private fun startTimer() {
        timer.start()
    }

    private fun stopTimer() {
        timer.cancel()
    }

    private fun updateTimer() {
        var minutes = timeLeftInMilliseconds / 60000
        var seconds = timeLeftInMilliseconds % 60000 / 1000

        var timeLeft: String = ""

        if (minutes < 10) {
            timeLeft += "0"
        }
        timeLeft += "${minutes}:"
        if (seconds < 10) {
            timeLeft += "0"
        }
        timeLeft += seconds

        binding.timeDisplay.text = timeLeft
    }


}