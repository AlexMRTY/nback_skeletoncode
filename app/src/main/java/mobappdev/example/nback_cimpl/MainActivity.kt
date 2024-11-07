package mobappdev.example.nback_cimpl

import android.os.Bundle
import android.speech.tts.TextToSpeech
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import mobappdev.example.nback_cimpl.ui.screens.AudioPlayScreen
import mobappdev.example.nback_cimpl.ui.screens.HomeScreen
import mobappdev.example.nback_cimpl.ui.screens.PositionalAudioPlayScreen
import mobappdev.example.nback_cimpl.ui.screens.PositionalPlayScreen
import mobappdev.example.nback_cimpl.ui.screens.ResultScreen
import mobappdev.example.nback_cimpl.ui.theme.NBack_CImplTheme
import mobappdev.example.nback_cimpl.ui.viewmodels.GameVM
import java.util.Locale

/**
 * This is the MainActivity of the application
 *
 * Your navigation between the two (or more) screens should be handled here
 * For this application you need at least a homescreen (a start is already made for you)
 * and a gamescreen (you will have to make yourself, but you can use the same viewmodel)
 *
 * Date: 25-08-2023
 * Version: Version 1.0
 * Author: Yeetivity
 *
 */


class MainActivity : ComponentActivity(), TextToSpeech.OnInitListener {
    private lateinit var tts: TextToSpeech

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        tts = TextToSpeech(this, this)

        setContent {
            NBack_CImplTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Instantiate the viewmodel
                    val gameViewModel: GameVM = viewModel(
                        factory = GameVM.Factory
                    )

                    val navController = rememberNavController()
                    NavHost(navController, startDestination = "home") {
                        composable(
                            "home",
                        ) { HomeScreen(vm = gameViewModel, navController = navController) }
                        composable(
                            "positionAudio/{roundCount}/{nBack}/{interval}",
                            arguments = listOf(
                                navArgument("roundCount") { type = NavType.IntType},
                                navArgument("nBack") { type = NavType.IntType},
                                navArgument("interval") { type = NavType.IntType},
                            )
                        ) { backStackEntry ->
                            val roundCount = backStackEntry.arguments?.getInt("roundCount") ?: 10
                            val nBack = backStackEntry.arguments?.getInt("nBack") ?: 1
                            val interval = backStackEntry.arguments?.getInt("interval") ?:1500
                            PositionalAudioPlayScreen(
                                vm = gameViewModel,
                                navController = navController,
                                speakOut = ::speakOut,
                                roundCount = roundCount,
                                nBack = nBack,
                                interval = interval
                            )
                        }
                        composable(
                            "audio/{roundCount}/{nBack}/{interval}",
                            arguments = listOf(
                                navArgument("roundCount") { type = NavType.IntType},
                                navArgument("nBack") { type = NavType.IntType},
                                navArgument("interval") { type = NavType.IntType},
                            )
                        ) { backStackEntry ->
                            val roundCount = backStackEntry.arguments?.getInt("roundCount") ?: 10
                            val nBack = backStackEntry.arguments?.getInt("nBack") ?: 1
                            val interval = backStackEntry.arguments?.getInt("interval") ?:1500
                            AudioPlayScreen(
                                vm = gameViewModel,
                                navController = navController,
                                speakOut = ::speakOut,
                                roundCount = roundCount,
                                nBack = nBack,
                                interval = interval
                            )
                        }
                        composable(
                            "positional/{roundCount}/{nBack}/{interval}",
                            arguments = listOf(
                                navArgument("roundCount") { type = NavType.IntType},
                                navArgument("nBack") { type = NavType.IntType},
                                navArgument("interval") { type = NavType.IntType},
                            )
                        ) { backStackEntry ->
                            val roundCount = backStackEntry.arguments?.getInt("roundCount") ?: 10
                            val nBack = backStackEntry.arguments?.getInt("nBack") ?: 1
                            val interval = backStackEntry.arguments?.getInt("interval") ?:1500
                            PositionalPlayScreen(
                                vm = gameViewModel,
                                navController = navController,
                                roundCount = roundCount,
                                nBack = nBack,
                                interval = interval
                            )
                        }
                        composable(
                            "result/{game}/{roundCount}/{nBack}/{interval}/{score}",
                            arguments = listOf(
                                navArgument("game") { type = NavType.StringType},
                                navArgument("roundCount") { type = NavType.IntType},
                                navArgument("nBack") { type = NavType.IntType},
                                navArgument("interval") { type = NavType.IntType},
                                navArgument("score") { type = NavType.IntType},
                            )
                        ) { backStackEntry ->
                            val game = backStackEntry.arguments?.getString("game") ?: "home"
                            val roundCount = backStackEntry.arguments?.getInt("roundCount") ?: 10
                            val nBack = backStackEntry.arguments?.getInt("nBack") ?: 1
                            val interval = backStackEntry.arguments?.getInt("interval") ?:1500
                            val score = backStackEntry.arguments?.getInt("score") ?: 0
                            ResultScreen(
                                vm = gameViewModel,
                                navController = navController,
                                game = game,
                                roundCount = roundCount,
                                nBack = nBack,
                                interval = interval,
                                score = score
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts.language = Locale.US
        } else {
            // Initialization failed
        }
    }

    private fun speakOut(char: Char) {
        tts.speak(char.toString(), TextToSpeech.QUEUE_FLUSH, null, "")
    }

    override fun onDestroy() {
        if (this::tts.isInitialized) {
            tts.stop()
            tts.shutdown()
        }
        super.onDestroy()
    }
}