package com.example.simplecountdowntimer

import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import com.example.simplecountdowntimer.databinding.ActivityMainBinding
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var progressPercentage = 0
    private var defaultTime: Long = 30000 // Default time at start up
    private var timeLeft: Long = 0 // in milliseconds
    private var referenceTime: Long = 0 // Used for progress bar.
    private var timerRunning: Boolean = false // Initial state: timer is NOT running.
    private var userSetTime: Boolean = false // Indicates if the user set a time or not.
    private var userTime: Long = 0 // User set time in minutes.
    private lateinit var countDownTimer: CountDownTimer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.startButton.text = "Start"

        binding.setTimer.setOnClickListener { setTimer(it) }

        binding.startButton.setOnClickListener { startStop(it) }

        binding.resetButton.setOnClickListener { resetTime() }

        binding.root.setOnClickListener { resetFocus(it) }

        timeLeft = defaultTime
        referenceTime = defaultTime

        updateTimer(timeLeft)
    }

    private fun resetFocus (view: View) {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        binding.setTimer.clearFocus()
    }

    private fun setTimer(view: View) {

        if(binding.setTimer.text.isNotEmpty()) {
            userSetTime = true
            var setTime: String = binding.setTimer.text.toString()
            userTime = setTime.toLong() * 60000
            timeLeft = userTime
            referenceTime = userTime
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

    private fun resetTime() {
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

        var timeLeftDisplay: String = ""

        if (minutes < 10) {
            timeLeftDisplay += "0"
        }

        timeLeftDisplay += "${minutes}:"

        if (seconds < 10) {
            timeLeftDisplay += "0"
        }

        timeLeftDisplay += seconds

        binding.timeDisplay.text = timeLeftDisplay
        updateProgressCircle(time)

        if (minutes == 0L && seconds == 0L) {

            Timer("schedule", )

            finishAlert()
            resetTime()
        }
    }

    private fun updateProgressCircle(time: Long) {
        var timeDifference: Long = referenceTime - time // time difference is in 1000 milliseconds.
        var progressPercentage: Double = timeDifference / referenceTime.toDouble() * 100 // converts time difference into a percentage for progress bar
        binding.countdownProgressBar.progress = progressPercentage.toInt()
    }

    private fun finishAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Time's up!")
        builder.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, id -> })
        builder.show()
    }
}