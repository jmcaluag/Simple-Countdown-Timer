package com.example.simplecountdowntimer

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.getSystemService
import androidx.databinding.DataBindingUtil
import com.example.simplecountdowntimer.databinding.FragmentCountdownTimerBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CountdownTimerFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CountdownTimerFragment : Fragment() {

    private lateinit var binding: FragmentCountdownTimerBinding

    // Timer values
    private var defaultTime: Long = 60000 // Default time at start up
    private var userTime: Long = 0 // User set time in minutes.
    private var timeLeft: Long = 0 // in milliseconds
    private var referenceTime: Long = 0 // Used for progress bar.

    // Timer settings & statuses
    private var timerRunning: Boolean = false // Initial state: timer is NOT running.
    private var userSetTime: Boolean = false // Indicates if the user set a time or not.
    private lateinit var countDownTimer: CountDownTimer

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_countdown_timer, container, false)

        binding.startButton.text = "Start"
        binding.setTimer.setOnClickListener { setTimer(it) }
        binding.startButton.setOnClickListener { startStop() }
        binding.resetButton.setOnClickListener { resetTime() }
        binding.root.setOnClickListener { resetFocus(it) }

        binding.countdownProgressBar.max = 1000 // Precision of progress bar is increased to 1000. Better for showing visual progression.

        timeLeft = defaultTime
        referenceTime = defaultTime
        updateTimer(timeLeft)

        return binding.root
    }

    private fun resetFocus (view: View) {
        val inputMethodManager = this.context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
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

        val inputMethodManager = this.context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
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
        val factor: Double = 1000 / referenceTimeSeconds.toDouble() // Finding one second percent increase. E.g. 100% / 10 seconds = 10%. "1000%" / 10 seconds = "100%". 1000 is used to increase precision for the progress bar.
        binding.countdownProgressBar.progress = (difference * factor).toInt()
    }

    private fun finishAlert() {
        val builder = AlertDialog.Builder(activity!!)
        builder.setMessage("Time's up!")
        builder.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, id -> })
        builder.show()
        resetTime()
    }
}