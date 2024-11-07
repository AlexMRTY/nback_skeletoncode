package mobappdev.example.nback_cimpl.ui.screens

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
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay
import mobappdev.example.nback_cimpl.R
import mobappdev.example.nback_cimpl.ui.composables.GridComp
import mobappdev.example.nback_cimpl.ui.theme.robotoMonoFamily
import mobappdev.example.nback_cimpl.ui.viewmodels.FakeVM
import mobappdev.example.nback_cimpl.ui.viewmodels.GameViewModel

@Composable
fun PositionalPlayScreen(
    vm: GameViewModel,
    navController: NavController,
    roundCount: Int,
    nBack: Int,
    interval: Int
) {
    val score by vm.score.collectAsState()
    val gameState by vm.gameState.collectAsState()
    val buttonState by vm.buttonLock.collectAsState()

    if (gameState.isGameFinished) {
        vm.resetGame()
        navController.navigate("result/${"positional"}/${roundCount}/${nBack}/${interval}/${score}")
    }

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
                text = "⭐ $score",
                color = Color.White,
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp)
            )
        }

        // Progress Bar
        LinearProgressIndicator(
            progress = gameState.roundCounter.toFloat()/roundCount.toFloat(), // Fill this with your progress value
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp)
                .height(16.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF1e1e1e)),
            color = Color(0xFF7ab3ef)
        )

        // Display the grid
        GridComp(gameState.visualEventValue)

        if (!gameState.isGameStarted) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    modifier = Modifier
                        .size(160.dp)
                        .clip(RoundedCornerShape(16.dp)), // Apply rounded corners
                    colors = ButtonDefaults.buttonColors(
                        containerColor =  Color(0xFF7ab3ef)
                    ),
                    onClick = {
                        vm.startGame(roundCount, nBack, interval)
                    }
                ) {
                    Text(
                        text = "Start",
                        fontFamily = robotoMonoFamily,
                        fontWeight = FontWeight.Bold,
                        color = if (gameState.previousValueCounter >= 0) Color.White else Color.Gray,
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Center
                    )
                }
            }
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
                        .size(150.dp)
                        .clip(RoundedCornerShape(16.dp)), // Apply rounded corners
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (buttonState.positionButtonLock) Color(0xFF7ab3ef) else if (gameState.isWrong) Color(0xFFAF1740) else  Color(0xFF1e1e1e)
                    ),
                    onClick = {
                        if (gameState.previousValueCounter >= 0) {
                            vm.togglePositionButton()
                        }
                    }) {
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
fun PreviewPositionalPlayScreen() {
    val navController = rememberNavController()
    PositionalPlayScreen(vm = FakeVM(),navController, roundCount = 10, nBack = 1, interval = 1500)
}