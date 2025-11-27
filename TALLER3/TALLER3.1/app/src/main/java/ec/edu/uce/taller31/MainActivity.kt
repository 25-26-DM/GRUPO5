package ec.edu.uce.taller31

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ec.edu.uce.taller31.ui.theme.TALLER31Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TALLER31Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DiceRollerApp()
                }
            }
        }
    }

    @Preview
    @Composable
    fun DiceRollerApp() {
        DiceWithButtonAndImage(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.Center)
        )
    }

    @Composable
    fun DiceWithButtonAndImage(modifier: Modifier = Modifier) {
        var result by remember { mutableStateOf(1) }

        val imageResource = when (result) {
            1 -> R.drawable.dice_1
            2 -> R.drawable.dice_2
            3 -> R.drawable.dice_3
            4 -> R.drawable.dice_4
            5 -> R.drawable.dice_5
            else -> R.drawable.dice_6
        }

        // Resultado en palabras desde strings.xml traducible
        val numberInWords = when (result) {
            1 -> stringResource(R.string.one)
            2 -> stringResource(R.string.two)
            3 -> stringResource(R.string.three)
            4 -> stringResource(R.string.four)
            5 -> stringResource(R.string.five)
            else -> stringResource(R.string.six)
        }

        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(imageResource),
                contentDescription = result.toString()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Texto con resultado traducido
            Text(
                text = stringResource(R.string.result_label) + " " + numberInWords,
                fontSize = 22.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { result = (1..6).random() }
            ) {
                Text(text = stringResource(R.string.roll), fontSize = 24.sp)
            }
        }
    }
}
