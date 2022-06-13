package com.guille.guillexample

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.guille.guillexample.ui.theme.GuillexampleTheme
import com.guille.guillexample.viewmodel.IotViewModel
import kotlinx.coroutines.*
import kotlin.concurrent.thread
import kotlin.coroutines.coroutineContext
import com.pusher.client.PusherOptions
import com.pusher.client.Pusher
import com.pusher.client.channel.Channel
import com.pusher.client.channel.PusherEvent
import com.pusher.client.channel.SubscriptionEventListener
import com.pusher.client.connection.ConnectionEventListener
import com.pusher.client.connection.ConnectionState

import com.pusher.client.connection.ConnectionStateChange
import java.lang.Exception


class MainActivity : ComponentActivity() {

    class PusherConnection : ConnectionEventListener{
        override fun onConnectionStateChange(change: ConnectionStateChange?) {
            if (change != null) {
                Log.i("Pusher", "State changed from " + change.previousState +
                        " to " + change.currentState)
            }
        }

        override fun onError(message: String?, code: String?, e: Exception?) {
            Log.i("Pusher", """
                     There was a problem connecting! 
                     code: $code
                     message: $message
                     Exception: $e
                     """.trimIndent()
            )
        }
    }

    class IotPusherEvent(private val model : IotViewModel) : SubscriptionEventListener{
        override fun onEvent(event: PusherEvent?) {
            Log.i("Pusher", "Received event with data: " + event.toString())
            model.viewModelScope.launch{
                model.increaseNumber()
            }

        }

    }

    fun handleIotNotifications(){
        val model: IotViewModel by viewModels()

        val options = PusherOptions()
        options.setCluster("eu");
        val pusher = Pusher("bb799da9e20a566eb798", options)

        val pusherConnection: PusherConnection = PusherConnection()
        pusher.connect(pusherConnection, ConnectionState.ALL)

        val channel: Channel = pusher.subscribe("iot-channel")

        val iotPusherEvent: IotPusherEvent = IotPusherEvent(model)

        channel.bind("iot-event", iotPusherEvent)

    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        handleIotNotifications()
        setContent {
            GuillexampleTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    Greeting("Android")
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    val mainViewModel: IotViewModel = viewModel()
    val notifications: Int? by mainViewModel.iotNotifications.observeAsState();

    Text(text = notifications!!.toString())

}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    GuillexampleTheme {
        Greeting("Android")
    }
}