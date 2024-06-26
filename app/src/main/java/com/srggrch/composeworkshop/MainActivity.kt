package com.srggrch.composeworkshop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.srggrch.composeworkshop.ui.Screens
import com.srggrch.composeworkshop.ui.screen.MainScreen
import com.srggrch.composeworkshop.ui.screen.TodoScreen
import com.srggrch.composeworkshop.ui.theme.ComposeWorkshopTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposeWorkshopTheme {
                var currentScreen by remember {
                    mutableStateOf(Screens.MAIN)
                }

                when (currentScreen) {
                    Screens.MAIN -> MainScreen({
                        currentScreen = Screens.TODO
                    })

                    Screens.TODO -> TodoScreen(
                        onBackClicked = { currentScreen = Screens.MAIN }
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ComposeWorkshopTheme {
        Greeting("Android")
    }
}