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
import androidx.compose.foundation.layout.waterfallPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import mobappdev.example.nback_cimpl.R
import mobappdev.example.nback_cimpl.ui.theme.robotoMonoFamily
import mobappdev.example.nback_cimpl.ui.viewmodels.FakeVM
import mobappdev.example.nback_cimpl.ui.viewmodels.GameViewModel

/**
 * This is the Home screen composable
 *
 * Currently this screen shows the saved highscore
 * It also contains a button which can be used to show that the C-integration works
 * Furthermore it contains two buttons that you can use to start a game
 *
 * Date: 25-08-2023
 * Version: Version 1.0
 * Author: Yeetivity
 *
 */

@Composable
fun HomeScreen(
    vm: GameViewModel,
    navController: NavController
) {
    val highscore by vm.highscore.collectAsState()  // Highscore is its own StateFlow


//    val snackBarHostState = remember { SnackbarHostState() }
//    val scope = rememberCoroutineScope()

    var roundCount by remember { mutableStateOf(10f) }
    var nBack by remember { mutableStateOf(1f) }
    var interval by remember { mutableStateOf(1500f) }


    Scaffold(
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .background(Color.Black),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier.padding(32.dp),
                fontFamily = robotoMonoFamily,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                text = "High-Score: $highscore",
                style = MaterialTheme.typography.headlineLarge
            )

            Box(
                modifier = Modifier
                    .wrapContentSize()
                    .padding(50.dp, 0.dp)
            ) {
                Column() {
                    Row(
                        modifier = Modifier
                            .wrapContentSize(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Size: ${roundCount.toInt()}",
                            fontFamily = robotoMonoFamily,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            style = MaterialTheme.typography.bodyLarge)
                        Spacer(modifier = Modifier.width(16.dp))
                        Slider(
                            modifier = Modifier.height(20.dp),
                            value = roundCount,
                            onValueChange = { roundCount = it },
                            valueRange = 10f..20f,
                            steps = 9,
                            colors = SliderDefaults.colors(
                                thumbColor = Color(0xFF7ab3ef),
                                activeTrackColor = Color(0xFF7ab3ef),
                                inactiveTrackColor = Color(0xFF1e1e1e)
                            ),
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier
                            .wrapContentSize(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "N-Back: ${nBack.toInt()}",
                            fontFamily = robotoMonoFamily,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            style = MaterialTheme.typography.bodyLarge)
                        Spacer(modifier = Modifier.width(16.dp))
                        Slider(
                            modifier = Modifier.height(20.dp),
                            value = nBack,
                            onValueChange = { nBack = it },
                            valueRange = 1f..6f,
                            steps = 4,
                            colors = SliderDefaults.colors(
                                thumbColor = Color(0xFF7ab3ef),
                                activeTrackColor = Color(0xFF7ab3ef),
                                inactiveTrackColor = Color(0xFF1e1e1e)
                            ),
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier
                            .wrapContentSize(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Interval: ${interval.toInt()}",
                            fontFamily = robotoMonoFamily,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            style = MaterialTheme.typography.bodyLarge)
                        Spacer(modifier = Modifier.width(16.dp))
                        Slider(
                            modifier = Modifier.height(20.dp),
                            value = interval,
                            onValueChange = { interval = it },
                            valueRange = 1500f..3000f,
                            steps = 2,
                            colors = SliderDefaults.colors(
                                thumbColor = Color(0xFF7ab3ef),
                                activeTrackColor = Color(0xFF7ab3ef),
                                inactiveTrackColor = Color(0xFF1e1e1e)
                            ),
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            Box(
                modifier = Modifier
                    .wrapContentSize()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .wrapContentSize(),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Button(
                        modifier = Modifier
                            .size(250.dp, 70.dp)
                            .clip(RoundedCornerShape(30.dp)),
                        shape = RectangleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1e1e1e)
                        ),
                        onClick = {
                            // Todo: change this button behaviour
                            vm.setGameTypeToVisual()
//                            vm.startGame(roundCount, nBack)
                            navController.navigate("positional/${roundCount.toInt()}/${nBack.toInt()}/${interval.toInt()}")
                        }) {
                        Text(
                            modifier = Modifier.padding(10.dp),
                            fontFamily = robotoMonoFamily,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            text = "Positional",
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        modifier = Modifier
                            .size(250.dp, 70.dp)
                            .clip(RoundedCornerShape(30.dp)),
                        shape = RectangleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1e1e1e)
                        ),
                        onClick = {
                            // Todo: change this button behaviour
                            vm.setGameTypeToAudio()
//                            vm.startGame(roundCount, nBack)
                            navController.navigate("audio/${roundCount.toInt()}/${nBack.toInt()}/${interval.toInt()}")
                        }) {
                        Text(
                            modifier = Modifier.padding(10.dp),
                            fontFamily = robotoMonoFamily,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            text = "Audio",
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        modifier = Modifier
                            .size(250.dp, 70.dp)
                            .clip(RoundedCornerShape(30.dp)),
                        shape = RectangleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1e1e1e)
                        ),
                        onClick = {
                            // Todo: change this button behaviour
                            vm.setGameTypeToAudioVisual()
//                            vm.startGame(roundCount, nBack)
                            navController.navigate("positionAudio/${roundCount.toInt()}/${nBack.toInt()}/${interval.toInt()}")
                        }) {
                        Text(
                            modifier = Modifier.padding(10.dp),
                            fontFamily = robotoMonoFamily,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            text = "Both",
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                }
            }
            // empty space to push the buttons up
            Box(modifier = Modifier.fillMaxWidth().height(40.dp)) {}
//
        }
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    // Since I am injecting a VM into my homescreen that depends on Application context, the preview doesn't work.
    val navController = rememberNavController()
    Surface(){
        HomeScreen(FakeVM(), navController)
    }
}