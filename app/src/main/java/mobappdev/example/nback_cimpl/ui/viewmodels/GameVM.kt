package mobappdev.example.nback_cimpl.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import mobappdev.example.nback_cimpl.GameApplication
import mobappdev.example.nback_cimpl.NBackHelper
import mobappdev.example.nback_cimpl.data.UserPreferencesRepository

/**
 * This is the GameViewModel.
 *
 * It is good practice to first make an interface, which acts as the blueprint
 * for your implementation. With this interface we can create fake versions
 * of the viewmodel, which we can use to test other parts of our app that depend on the VM.
 *
 * Our viewmodel itself has functions to start a game, to specify a gametype,
 * and to check if we are having a match
 *
 * Date: 25-08-2023
 * Version: Version 1.0
 * Author: Yeetivity
 *
 */


interface GameViewModel {
    val gameState: StateFlow<GameState>
    val buttonLock: StateFlow<ButtonState>
    val score: StateFlow<Int>
    val highscore: StateFlow<Int>
    val nBack: Int

    fun setGameType(gameType: GameType)
    fun startGame(roundCount: Int, nBack: Int, interval: Int)

    fun checkMatch(currentEvent: Int)
    fun checkMatch(currentEvent: Char)
    fun checkMatch(currentEvent: Pair<Int, Char>)

    fun togglePositionButton()
    fun toggleAudioButton()

    fun toggleGameStart()

    fun incrementRound()

    fun toggleIsWrong()

    fun resetGame()

    fun setGameTypeToAudio()
    fun setGameTypeToVisual()
    fun setGameTypeToAudioVisual()
}

class GameVM(
    private val userPreferencesRepository: UserPreferencesRepository
): GameViewModel, ViewModel() {
    private val _gameState = MutableStateFlow(GameState())
    override val gameState: StateFlow<GameState>
        get() = _gameState.asStateFlow()

    private val _buttonLock = MutableStateFlow(ButtonState())
    override val buttonLock: StateFlow<ButtonState> = _buttonLock.asStateFlow()

    private val _score = MutableStateFlow(0)
    override val score: StateFlow<Int>
        get() = _score

    private val _highscore = MutableStateFlow(0)
    override val highscore: StateFlow<Int>
        get() = _highscore

    // nBack is currently hardcoded
    override val nBack: Int = 1

    private var job: Job? = null  // coroutine job for the game event
    private val eventInterval: Long = 2000L  // 2000 ms (2s)

    private val nBackHelper = NBackHelper()  // Helper that generate the event array
    private var visualEvents = emptyArray<Int>()  // Array with all events
    private var audioEvents = emptyArray<Char>()  // Array with all events
    private var audioVisualEvents = emptyArray<Pair<Int, Char>>()  // Array with all events

    override fun setGameType(gameType: GameType) {
        // update the gametype in the gamestate
        _gameState.value = _gameState.value.copy(gameType = gameType)
    }

    override fun startGame(roundCount: Int, nBack: Int, interval: Int) {
        job?.cancel()  // Cancel any existing game loop


        // Get the events from our C-model (returns IntArray, so we need to convert to Array<Int>)
        when (gameState.value.gameType) {
            GameType.Visual -> {
                visualEvents = nBackHelper.generateNBackString(roundCount, 9, 30, nBack).toList().toTypedArray()
                Log.d("GameVM", "The following sequence was generated: ${visualEvents.contentToString()}")
            }
            GameType.Audio -> {
                val events = nBackHelper.generateNBackString(roundCount, 9, 30, nBack).toList().toTypedArray()
                audioEvents = mapNumbersToLetters(events.toIntArray()).toTypedArray()
                Log.d("GameVM", "The following sequence was generated: ${audioEvents.contentToString()}")
            }
            GameType.AudioVisual -> {
                val events1 = nBackHelper.generateNBackString(roundCount, 9, 30, nBack).toList().toTypedArray()
                val events2 = nBackHelper.generateNBackString(roundCount, 9, 30, nBack).toList().toTypedArray()
                audioVisualEvents = mapArraysToPairs(events1.toIntArray(), events2.toIntArray())
                Log.d("GameVM", "The following sequence was generated: ${audioVisualEvents.contentToString()}")
            }
        }

        _gameState.value = _gameState.value.copy(previousValueCounter = -nBack+1)

        job = viewModelScope.launch {
            // Wait for better user experience
            // Start game
            toggleGameStart()
            delay(1000L)

            when (gameState.value.gameType) {
                GameType.Audio -> runAudioGame(audioEvents)
                GameType.AudioVisual -> runAudioVisualGame(audioVisualEvents)
                GameType.Visual -> runVisualGame(visualEvents)
            }

            // Update HightScore if needed
            if (score.value > highscore.value) {
                userPreferencesRepository.saveHighScore(score.value)
            }

            // Delay for better user experience
            delay(800L)

            // End game
            toggleGameStart()
            _gameState.value = _gameState.value.copy(isGameFinished = true)
        }
    }

    private suspend fun runAudioGame(events: Array<Char>) {

        var previousValue: Char = ' '
        for (value in events) {
            incrementRound()
            _buttonLock.value = _buttonLock.value.copy(audioButtonLock = false)
            _gameState.value = _gameState.value.copy(previousAudioEventValue = previousValue)
            _gameState.value = _gameState.value.copy(audioEventValue = value)
            delay(800L)
            _gameState.value = _gameState.value.copy(audioEventValue = ' ')
            delay(eventInterval)
            if (_gameState.value.previousValueCounter >= 0) {
                previousValue = events[_gameState.value.previousValueCounter]
                checkMatch(value)
            }
            _gameState.value = _gameState.value.copy(previousValueCounter = _gameState.value.previousValueCounter + 1)
        }

    }

    private suspend fun runVisualGame(events: Array<Int>){

        var previousValue: Int = -1
        for (value in events) {
            _buttonLock.value = _buttonLock.value.copy(positionButtonLock = false)
            _gameState.value = _gameState.value.copy(previousVisualEventValue = previousValue)
            _gameState.value = _gameState.value.copy(visualEventValue = value)
            delay(800L)
            _gameState.value = _gameState.value.copy(visualEventValue = -1)
            delay(eventInterval)
            if (_gameState.value.previousValueCounter >= 0) {
                previousValue = events[_gameState.value.previousValueCounter]
                checkMatch(value)
            }
            _gameState.value = _gameState.value.copy(previousValueCounter = _gameState.value.previousValueCounter + 1)
            incrementRound()
        }

    }

    private suspend fun runAudioVisualGame(events: Array<Pair<Int, Char>>) {

        var previousValue:  Pair<Int, Char> = Pair(-1, ' ')
        for (value in events) {
            incrementRound()
            _buttonLock.value = _buttonLock.value.copy(positionButtonLock = false, audioButtonLock = false)
            _gameState.value = _gameState.value.copy(previousAudioVisualEventValue = previousValue)
            _gameState.value = _gameState.value.copy(audioVisualEventValue = value)
            delay(800L)
            _gameState.value = _gameState.value.copy(audioVisualEventValue = Pair(-1, ' '))
            delay(eventInterval)
            if (_gameState.value.previousValueCounter >= 0) {
                previousValue = events[_gameState.value.previousValueCounter]
                checkMatch(value)
            }
            _gameState.value = _gameState.value.copy(previousValueCounter = _gameState.value.previousValueCounter + 1)
        }

    }

    override fun checkMatch(currentEvent: Int) {
        val previousEvent = _gameState.value.previousVisualEventValue
        val positionBtnLock = _buttonLock.value.positionButtonLock

        // Check if any of the buttons are locked

        if (previousEvent == currentEvent) {
            if (positionBtnLock) {
                _score.value += 1
            } else {
                toggleIsWrong()
            }
        } else if (positionBtnLock) {
            toggleIsWrong()
        }
    }
    override fun checkMatch(currentEvent: Char) {
        val previousEvent = _gameState.value.previousAudioEventValue
        val audioBtnLock = _buttonLock.value.audioButtonLock

        // Check if any of the buttons are locked
        if (previousEvent == currentEvent) {
            if (audioBtnLock) {
                _score.value += 1
            } else {
                toggleIsWrong()
            }
        } else if (audioBtnLock) {
            toggleIsWrong()
        }
    }
    override fun checkMatch(currentEvent: Pair<Int, Char>) {
        val previousEvent = _gameState.value.previousAudioVisualEventValue
        val positionBtnLock = _buttonLock.value.positionButtonLock
        val audioBtnLock = _buttonLock.value.audioButtonLock

        if (previousEvent == currentEvent) {
            if (positionBtnLock && audioBtnLock) {
                _score.value += 1
            } else {
                toggleIsWrong()
            }
        } else if (previousEvent.first == currentEvent.first) {
            if (positionBtnLock && !audioBtnLock) {
                _score.value += 1
            } else {
                toggleIsWrong()
            }
        } else if (previousEvent.second == currentEvent.second) {
            if (audioBtnLock && !positionBtnLock) {
                _score.value += 1
            } else {
                toggleIsWrong()
            }
        } else if (positionBtnLock || audioBtnLock) {
            toggleIsWrong()
        }
    }


    override fun togglePositionButton() {
        _buttonLock.value = if (_buttonLock.value.positionButtonLock) {
            _buttonLock.value.copy(positionButtonLock = false)
        } else {
            _buttonLock.value.copy(positionButtonLock = true)
        }
    }
    override fun toggleAudioButton() {
        _buttonLock.value = if (_buttonLock.value.audioButtonLock) {
            _buttonLock.value.copy(audioButtonLock = false)
        } else {
            _buttonLock.value.copy(audioButtonLock = true)
        }
    }

    override fun incrementRound() {
        _gameState.value = _gameState.value.copy(roundCounter = _gameState.value.roundCounter + 1)
    }

    override fun toggleGameStart() {
        _gameState.value = _gameState.value.copy(isGameStarted = !_gameState.value.isGameStarted)
    }

    override fun toggleIsWrong() {
        _gameState.value = _gameState.value.copy(isWrong = !_gameState.value.isWrong)
    }

    override fun resetGame() {
        _gameState.value = GameState()  // Reset game state
        _score.value = 0  // Reset score
        _buttonLock.value = ButtonState()  // Reset button lock state
    }

    override fun setGameTypeToAudio() {
        _gameState.value = _gameState.value.copy(gameType = GameType.Audio)
    }
    override fun setGameTypeToVisual() {
        _gameState.value = _gameState.value.copy(gameType = GameType.Visual)
    }
    override fun setGameTypeToAudioVisual() {
        _gameState.value = _gameState.value.copy(gameType = GameType.AudioVisual)
    }

    private fun mapNumbersToLetters(numbers: IntArray): List<Char> {
        return numbers.map { number ->
            if (number in 0..25) {
                'A' + number
            } else {
                throw IllegalArgumentException("Number out of range: $number")
            }
        }
    }

    private fun mapArraysToPairs(intArray1: IntArray, intArray2: IntArray): Array<Pair<Int, Char>> {
        val letters = mapNumbersToLetters(intArray2)
        return intArray1.zip(letters).toTypedArray()
    }



    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as GameApplication)
                GameVM(application.userPreferencesRespository)
            }
        }
    }

    init {
        // Code that runs during creation of the vm
        viewModelScope.launch {
            userPreferencesRepository.highscore.collect {
                _highscore.value = it
            }
        }
    }
}

// Class with the different game types
enum class GameType{
    Audio,
    Visual,
    AudioVisual
}

data class GameState(
    // You can use this state to push values from the VM to your UI.
    val gameType: GameType = GameType.Visual,  // Type of the game

    val previousValueCounter: Int = -1,

    // TODO: Remove from here
    val previousVisualEventValue: Int = -1,  // The previous value of the array string
    val previousAudioEventValue: Char = ' ',  // The previous value of the audio string
    val previousAudioVisualEventValue: Pair<Int, Char> = Pair(-1, ' '),  // The previous value of the audio visual string

    val visualEventValue: Int = -1,
    val audioEventValue: Char = ' ',
    val audioVisualEventValue: Pair<Int, Char> = Pair(-1, ' '),

    val isWrong: Boolean = false,  // If the user made a mistake

    val roundCounter: Int = 0,  // The current round

    val isGameStarted: Boolean = false,

    val isGameFinished: Boolean = false

//    val positionButtonLock: Boolean = false,  // Lock the buttons in the visual game
//    val audioButtonLock: Boolean = false,  // Lock the buttons in the audio game
)

data class ButtonState(
    var positionButtonLock: Boolean = false,
    var audioButtonLock: Boolean = false
)

class FakeVM: GameViewModel{
    override val gameState: StateFlow<GameState>
        get() = MutableStateFlow(GameState()).asStateFlow()
    override val buttonLock: StateFlow<ButtonState>
        get() = MutableStateFlow(ButtonState()).asStateFlow()
    override val score: StateFlow<Int>
        get() = MutableStateFlow(2).asStateFlow()
    override val highscore: StateFlow<Int>
        get() = MutableStateFlow(42).asStateFlow()
    override val nBack: Int
        get() = 2

    override fun setGameType(gameType: GameType) {}
    override fun startGame(roundCount: Int, nBack: Int, interval: Int) {}
    override fun checkMatch(currentEvent: Int) {}
    override fun checkMatch(currentEvent: Char) {}
    override fun checkMatch(currentEvent: Pair<Int, Char>) {}
    override fun togglePositionButton() {}
    override fun toggleAudioButton() {}
    override fun toggleGameStart() {}
    override fun incrementRound() {}
    override fun toggleIsWrong() {}
    override fun resetGame() {}
    override fun setGameTypeToAudio() {}
    override fun setGameTypeToVisual() {}
    override fun setGameTypeToAudioVisual() {}
}