package com.example.triviacompose

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.triviacompose.api.ApiRequest
import com.example.triviacompose.api.response.Categories
import com.example.triviacompose.api.response.TriviaCategory
import com.example.triviacompose.api.response.TriviaQuestion
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.awaitResponse
import timber.log.Timber

@ExperimentalCoroutinesApi
class MainActivityViewModel(private val networkClient: ApiRequest): ViewModel() {

    sealed class State {
        object Idle : State()
        object Loading : State()
        data class Success(val questionsList: List<TriviaQuestion>) : State()
        data class Error(val errorMessage: String?) : State()
    }
    private val _state = MutableStateFlow<State>(State.Idle)
    val state: StateFlow<State> = _state

    private val _index = MutableStateFlow(0)
    val questionIndex: StateFlow<Int> = _index

    private val _listOfQuestions = MutableStateFlow<List<TriviaQuestion>>(mutableListOf())
    private val listOfQuestions: StateFlow<List<TriviaQuestion>> = _listOfQuestions

    private var _listOfCategories : MutableStateFlow<List<TriviaCategory>> = MutableStateFlow(mutableListOf())
    val listOfCategories : MutableStateFlow<List<TriviaCategory>> = _listOfCategories

    private lateinit var _currentQuestion : MutableStateFlow<TriviaQuestion>
    lateinit var currentQuestion: MutableStateFlow<TriviaQuestion>

    private var _currentQuestionText : MutableStateFlow<String> = MutableStateFlow("")
    val currentQuestionText: StateFlow<String> = _currentQuestionText


    private val _answersMap = MutableStateFlow<MutableMap<String, Boolean>>(mutableMapOf())
    val answersMap: StateFlow<MutableMap<String, Boolean>> = _answersMap

    private val _answersColorsMap = MutableStateFlow<MutableMap<String, Color>>(mutableMapOf())
    val answersColorsMap: StateFlow<MutableMap<String, Color>> = _answersColorsMap

    private val _answersPointsMap = MutableStateFlow<MutableMap<Int, Boolean>>(mutableMapOf())
    val answersPointsMap: StateFlow<MutableMap<Int, Boolean>> = _answersPointsMap

    private val postsExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
        _state.value = State.Error(throwable.message)
    }

    fun onNextClicked() {
        if (questionIndex.value < 9) {
            _index.value ++
        }
        else {
            resetGame()
        }
        setCurrentQuestion(listOfQuestions.value[questionIndex.value])

    }
    fun onPreviousClicked() {
        if (_index.value > 0) {
            _index.value --
        }
        setCurrentQuestion(listOfQuestions.value[questionIndex.value])
    }

    fun setColor(answer: String, color: Color) {
        _answersColorsMap.value[answer] = color
    }

    fun setAnswers(map: MutableMap<String, Boolean>) {
        _answersMap.value = map
    }
    fun setCurrentQuestion(item: TriviaQuestion) {
        _currentQuestion = MutableStateFlow(item)
        currentQuestion = _currentQuestion
        _currentQuestionText.value = item.question

        val mapOfAnswers = mutableMapOf<String, Boolean>()
        if (_answersPointsMap.value[_index.value] != true) {
            val answers = item.incorrect_answers + item.correct_answer
            answers.shuffled()
            answers.forEach {
                mapOfAnswers[it] = it == item.correct_answer
            }
        }
        _answersMap.value = mapOfAnswers
    }

    fun onAnswerSelected(selectedItem: String) {
        _answersPointsMap.value[_index.value] = true

        if (currentQuestion.value.correct_answer == selectedItem) {
            Timber.e("Awesome!! $selectedItem is correct")
        }
        onNextClicked()
    }

    fun resetGame() {
        getQuestions()
        _index.value = 0
        _answersPointsMap.value = mutableMapOf()
    }

    fun getCategories() {
        viewModelScope.launch(Dispatchers.IO + postsExceptionHandler) {
            _state.value = State.Loading
            val questionsResponse = networkClient.getCategory()
                .awaitResponse()
            if (questionsResponse.isSuccessful) {
                val data = questionsResponse.body()!!
                _listOfCategories.value = data.trivia_categories
            }
        }
    }

    fun getQuestions() {
        viewModelScope.launch(Dispatchers.IO + postsExceptionHandler) {
            _state.value = State.Loading
            val questionsResponse = networkClient.getQuestions(10, 18)
                .awaitResponse()
            if (questionsResponse.isSuccessful) {
                val data = questionsResponse.body()!!
                _state.value = State.Success(data.results)
                _listOfQuestions.value = data.results
                setCurrentQuestion(listOfQuestions.value[questionIndex.value])
            }
            else {
                _state.value = State.Error("Something went wrong")
            }
        }
    }
}
