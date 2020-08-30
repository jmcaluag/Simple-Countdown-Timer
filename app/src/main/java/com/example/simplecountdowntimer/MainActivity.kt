package com.example.simplecountdowntimer

import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import com.example.simplecountdowntimer.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // Timer values
    private var defaultTime: Long = 60000 // Default time at start up
    private var userTime: Long = 0 // User set time in minutes.
    private var timeLeft: Long = 0 // in milliseconds
    private var referenceTime: Long = 0 // Used for progress bar.

    // Timer settings & statuses
    private var timerRunning: Boolean = false // Initial state: timer is NOT running.
    private var userSetTime: Boolean = false // Indicates if the user set a time or not.
    private lateinit var countDownTimer: CountDownTimer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.startButton.text = "Start"
        binding.percentageIndicator.text = "0"
        binding.setTimer.setOnClickListener { setTimer(it) }
        binding.startButton.setOnClickListener { startStop() }
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
            val setTime: String = binding.setTimer.text.toString()
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

    private fun startStop() {
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

            override fun onFinish() {}

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

        val minutes = time / 60000
        val seconds = time % 60000 / 1000

        var timeLeftDisplay = ""

        if (minutes < 10) {
            timeLeftDisplay += "0"
        }

        timeLeftDisplay += "${minutes}:"

        if (seconds < 10) {
            timeLeftDisplay += "0"
        }

        timeLeftDisplay += seconds

        binding.timeDisplay.text = timeLeftDisplay
        updateProgressBar(time)

        if (minutes == 0L && seconds == 0L) {
            //TODO: Add a delay before calling this function so that the progress circle can be 100% full
            finishAlert()
        }
    }

    private fun updateProgressBar(timeLeftMilliseconds: Long) {
        //TODO: Increase precision of the progress bar
        val timeLeftSeconds = timeLeftMilliseconds / 1000
        val referenceTimeSeconds = referenceTime / 1000
        val difference = referenceTimeSeconds - timeLeftSeconds
        val factor: Double = 100 / referenceTimeSeconds.toDouble()
        binding.percentageIndicator.text = (difference * factor).toInt().toString()
        binding.countdownProgressBar.progress = (difference * factor).toInt()
    }

    private fun finishAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Time's up!")
        builder.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, id -> })
        builder.show()
        resetTime()
    }
}