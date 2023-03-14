package com.example.quadrant
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.util.*
import kotlin.concurrent.fixedRateTimer
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.material.*
import androidx.compose.runtime.*

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PomodoroTimer()
        }
    }
}

@Composable
fun PomodoroTimer() {
    var timerState by remember { mutableStateOf(TimerState.Stopped) }
    var remainingSeconds by remember { mutableStateOf(value=0) }
    var timer: Timer? by remember { mutableStateOf(value=null) }

    Scaffold(
        topBar = { TopAppBar(title = { Text(text = "Pomodoro Timer") }) },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                when (timerState) {
                    TimerState.Running, TimerState.Break -> {
                        Text(text = remainingSeconds.toTimeString(), style = MaterialTheme.typography.h1)
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    TimerState.Stopped -> {
                        Text(text = "Press Start to Begin", style = MaterialTheme.typography.h5)
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
                when (timerState) {
                    TimerState.Running -> {
                        Button(onClick = {
                            timer?.cancel()
                            timerState = TimerState.Stopped
                        }) {
                            Text(text = "Stop")
                        }
                    }
                    TimerState.Break -> {
                        Button(onClick = {
                            timer?.cancel()
                            timerState = TimerState.Stopped
                        }) {
                            Text(text = "Skip Break")
                        }
                    }
                    TimerState.Stopped -> {
                        Button(onClick = {
                            timerState = TimerState.Running
                            remainingSeconds = POMODORO_MINUTES * 60
                            timer = fixedRateTimer(name = "PomodoroTimer", initialDelay = 1000L, period = 1000L) {
                                remainingSeconds -= 1
                                if (remainingSeconds <= 0) {
                                    timer?.cancel()
                                    timerState = TimerState.Break
                                    remainingSeconds = BREAK_MINUTES * 60
                                    timer = fixedRateTimer(name = "BreakTimer", initialDelay = 1000L, period = 1000L) {
                                        remainingSeconds -= 1
                                        if (remainingSeconds <= 0) {
                                            timer?.cancel()
                                            timerState = TimerState.Stopped
                                        }
                                    }
                                }
                            }
                        }) {
                            Text(text = "Start")
                        }
                    }
                }
            }
        }
    )
}

fun Int.toTimeString(): String {
    val minutes = this / 60
    val seconds = this % 60
    return "%02d:%02d".format(minutes, seconds)
}

enum class TimerState {
    Running,
    Stopped,
    Break
}

const val POMODORO_MINUTES = 25
const val BREAK_MINUTES = 5


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DefaultPreview() {
    PomodoroTimer()
}