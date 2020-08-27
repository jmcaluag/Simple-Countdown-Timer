package com.example.simplecountdowntimer

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.databinding.DataBindingUtil
import com.example.simplecountdowntimer.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var defaultTime: Long = 600000 //10 minutes
    private var timeLeft: Long = 0
    private var timerRunning: Boolean = false // Initial state: timer is NOT running
    private var userSetTime: Boolean = false
    private var userTime: Long = 0
    private lateinit var countDownTimer: CountDownTimer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.startButton.text = "Start"

        binding.setTimer.setOnClickListener { setTimer(it) }

        binding.startButton.setOnClickListener { startStop(it) }

        binding.resetButton.setOnClickListener { resetTime(it) }

        //TODO: Implement "tap out of EditText" to clear focus and hide keyboard when tapping/clicking on the root view or just outside the EditText view.

        timeLeft = defaultTime

        updateTimer(timeLeft)
    }

    private fun setTimer(view: View) {

        if(binding.setTimer.text.isNotEmpty()) {
            userSetTime = true
            var setTime: String = binding.setTimer.text.toString()
            userTime = setTime.toLong() * 60000
            timeLeft = userTime
            updateTimer(timeLeft)
            binding.setTimer.text.clear()
        }

        binding.setTimer.clearFocus()

        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun startStop(view: View) {
        if(timerRunning) {
            timerRunning = false
            pauseTimer()
            binding.startButton.text = "Start"
        } else {
            timerRunning = true
            startTimer()
            binding.startButton.text = "Pause"
            binding.resetButton.visibility = View.VISIBLE
        }
    }

    private fun startTimer() {
        countDownTimer = object : CountDownTimer(timeLeft, 1000) {
            override fun onTick(p0: Long) {
                timeLeft = p0
                updateTimer(timeLeft)
            }

            override fun onFinish() {} // Not needed just yet

        }.start()
    }

    private fun pauseTimer() {
        countDownTimer.cancel()
    }

    private fun resetTime(view: View) {
        countDownTimer.cancel()

        if (userSetTime) {
            timeLeft = userTime
        } else {
            timeLeft = defaultTime
        }
        updateTimer(timeLeft)
        timerRunning = false
        binding.startButton.text = "Start"
        binding.resetButton.visibility = View.GONE
    }

    private fun updateTimer(time: Long) {
        var minutes = time / 60000
        var seconds = time % 60000 / 1000

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