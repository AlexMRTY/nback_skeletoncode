package mobappdev.example.nback_cimpl.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay
import mobappdev.example.nback_cimpl.R
import mobappdev.example.nback_cimpl.ui.composables.StartButton
import mobappdev.example.nback_cimpl.ui.theme.robotoMonoFamily
import mobappdev.example.nback_cimpl.ui.viewmodels.FakeVM
import mobappdev.example.nback_cimpl.ui.viewmodels.GameViewModel

@Composable
fun AudioPlayScreen(
    vm: GameViewModel,
    navController: NavController,
    speakOut: (Char) -> Unit,
    roundCount: Int,
    nBack: Int,
    interval: Int
) {
    val score by vm.score.collectAsState()
    val gameState by vm.gameState.collectAsState()
    val buttonState by vm.buttonLock.collectAsState()
    var hoopColor = remember { mutableStateOf(Color.Gray) }

    if (gameState.isGameFinished) {
        vm.resetGame()
        navController.navigate("result/${"positional"}/${roundCount}/${nBack}/${interval}/${score}")
    }
    if (gameState.audioEventValue != ' ') speakOut(gameState.audioEventValue)

    LaunchedEffect(gameState.isWrong) {
        if (gameState.isWrong) {
            delay(500L) // Flash red for 500 milliseconds
            vm.toggleIsWrong()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(0.dp, 40.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box() {
            Text(
                text = "Score: $score",
                color = Color.White,
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp)
            )
        }

        Box(
        ) {
            Column(
            ) {
                if (gameState.audioEventValue != ' ') {
                    LaunchedEffect(Unit) {
                        hoopColor.value = Color.White
                        speakOut(gameState.audioEventValue) // Call speakOut function
                        delay(500L) // Flash white for 1 second
                        hoopColor.value = Color(0xFF1e1e1e)
                    }
                }
                Canvas(modifier = Modifier.size(200.dp)) {
                    drawCircle(
                        color = hoopColor.value,
                        radius = size.minDimension / 2,
                        style = Stroke(width = 16.dp.toPx())
                    )
                }
            }
        }

        if (!gameState.isGameStarted) {
            StartButton(startGame = {vm.startGame(roundCount, nBack, interval)})
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    modifier = Modifier
                        .size(170.dp)
                        .clip(RoundedCornerShape(16.dp)), // Apply rounded corners
                    colors = ButtonDefaults.buttonColors(
                        containerColor =
                        if (buttonState.audioButtonLock) Color(0xFF7ab3ef)
                        else if (gameState.isWrong) Color(0xFFAF1740)
                        else Color(0xFF1e1e1e)
                    ),
                    onClick = {
                        if (gameState.previousValueCounter >= 0) {
                            vm.toggleAudioButton()
                        }
                    }
                ) {
                    Text(
                        text = "Match",
                        fontFamily = robotoMonoFamily,
                        fontWeight = FontWeight.Bold,
                        color = if (gameState.previousValueCounter >= 0) Color.White else Color.Gray,
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewAudioPlayScreen() {
    val navController = rememberNavController()
    AudioPlayScreen(vm = FakeVM(), navController, speakOut = {}, roundCount = 10, nBack = 1, interval = 1500)
}