package fr.acart.melojj

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.acart.melojj.ui.theme.MeloJJTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MeloJJTheme {
                Box(
                    modifier = Modifier.screenWithStatusBar(),
                ) {
                    Greeting("Android")
                    Icon(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(64.dp),
                        painter = painterResource(id = R.drawable.music_note),
                        contentDescription = null,
                        tint = MeloJJTheme.colors.content.primary
                    )
                }
            }
        }
    }
}

fun Modifier.screenWithStatusBar() = composed {
    this
        .fillMaxSize()
        .background(MeloJJTheme.colors.background.secondary)
        .statusBarsPadding()
        .background(MeloJJTheme.colors.background.primary)
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
    MeloJJTheme {
        Greeting("Android")
    }
}