package com.example.triviacompose

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.triviacompose.api.ApiRequest
import com.example.triviacompose.model.AnsweredQuestion
import com.example.triviacompose.model.TriviaCategory
import com.example.triviacompose.model.TriviaQuestion
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.awaitResponse
import kotlin.system.exitProcess

@ExperimentalCoroutinesApi
class MainActivityViewModel(private val networkClient: ApiRequest): ViewModel() {

    sealed class State {
        object Idle : State()
        object Loading : State()
        data class Success(val questionsList: List<TriviaQuestion>) : State()
        data class Error(val errorMessage: String?) : State()
        data class SelectCategory(val categoriesList: List<TriviaCategory>) : State()
        data class Complete(val questionsList: List<AnsweredQuestion>) : State()
    }
    private val _state = MutableStateFlow<State>(State.Idle)
    val state: StateFlow<State> = _state

    private val _index = MutableStateFlow(0)
    private val questionIndex: StateFlow<Int> = _index

    private val _currentCategory = MutableStateFlow(-1)
    private val currentCategory: StateFlow<Int> = _currentCategory

    private val _listOfQuestions = MutableStateFlow<List<TriviaQuestion>>(mutableListOf())
    private val listOfQuestions: StateFlow<List<TriviaQuestion>> = _listOfQuestions

    private val _listOfAnsweredQuestions = MutableStateFlow<MutableList<AnsweredQuestion>>(mutableListOf())
    val listOfAnsweredQuestions: StateFlow<List<AnsweredQuestion>> = _listOfAnsweredQuestions

    private var _listOfCategories : MutableStateFlow<List<TriviaCategory>> = MutableStateFlow(mutableListOf())
    val listOfCategories : MutableStateFlow<List<TriviaCategory>> = _listOfCategories

    private lateinit var _currentQuestion : MutableStateFlow<TriviaQuestion>
    lateinit var currentQuestion: MutableStateFlow<TriviaQuestion>

    private var _currentQuestionText : MutableStateFlow<String> = MutableStateFlow("")
    val currentQuestionText: StateFlow<String> = _currentQuestionText


    private val _answersMap = MutableStateFlow<MutableMap<String, Boolean>>(mutableMapOf())
    val answersMap: StateFlow<MutableMap<String, Boolean>> = _answersMap

    private val _answeredQuestionsMap = MutableStateFlow<MutableMap<Int, Boolean>>(mutableMapOf())
    private val answeredQuestionsMap: MutableStateFlow<MutableMap<Int, Boolean>> = _answeredQuestionsMap

    private val postsExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
        _state.value = State.Error(throwable.message)
    }

    fun onNextClicked() {
        if (questionIndex.value < 9) {
            _index.value ++
        }
        else {
            if (_listOfAnsweredQuestions.value.size >= 9) {
                _state.value = State.Complete(listOfAnsweredQuestions.value)
            }
        }
        setCurrentQuestion(listOfQuestions.value[questionIndex.value])
    }
    fun onPreviousClicked() {
        if (_index.value > 0) {
            _index.value --
        }
        setCurrentQuestion(listOfQuestions.value[questionIndex.value])
    }

    private fun setCurrentQuestion(item: TriviaQuestion) {
        _currentQuestion = MutableStateFlow(item)
        currentQuestion = _currentQuestion
        _currentQuestionText.value = item.question
        val mapOfAnswers = mutableMapOf<String, Boolean>()
        if (answeredQuestionsMap.value[_index.value] != true) {
            val answers = item.incorrect_answers + item.correct_answer
            answers.shuffled()
            answers.forEach {
                mapOfAnswers[it] = it == item.correct_answer
            }
        }
        else {
            if(item.correct_answer == listOfAnsweredQuestions.value[_index.value].selectedAnswer) {
                val text = "You selected ${ item.correct_answer } correctly"
                mapOfAnswers[text] = false
            }
            else {
                val text = "You selected ${ listOfAnsweredQuestions.value[_index.value].selectedAnswer } incorrectly"
                mapOfAnswers[text] = false
                val correctAnswer = "Correct answer: ${ item.correct_answer }"
                mapOfAnswers[correctAnswer] = false
            }
        }
        _answersMap.value = mapOfAnswers
    }

    fun onItemClicked(selectedItem: Any) {
        when (selectedItem) {
            is String -> {
                when {
                    _answersMap.value.containsKey(selectedItem) -> {
                        val questionAnswered = AnsweredQuestion(currentQuestion.value.question,
                                selectedItem, currentQuestion.value.correct_answer)
                        _answeredQuestionsMap.value[_index.value] = true
                        _listOfAnsweredQuestions.value.add(questionAnswered)
                        onNextClicked()
                    }
                    selectedItem == "Reset Game" -> resetGame()
                    selectedItem == "Select Category" ->
                        _state.value = State.SelectCategory(listOfCategories.value)
                    selectedItem == "Quit" -> exitProcess(0)
                }
            }
            is Int -> {
                _currentCategory.value = selectedItem
                resetGame()
            }
        }
    }

    private fun resetGame() {
        _index.value = 0
        _listOfAnsweredQuestions.value = mutableListOf()
        _answeredQuestionsMap.value = mutableMapOf()
        _answersMap.value = mutableMapOf()
        if(currentCategory.value != -1)
            getQuestions()
    }

    fun getCategories() {
        viewModelScope.launch(Dispatchers.IO + postsExceptionHandler) {
            _state.value = State.Loading
            val categoriesResponse = networkClient.getCategory()
                .awaitResponse()
            if (categoriesResponse.isSuccessful) {
                val data = categoriesResponse.body()!!
                _listOfCategories.value = data.trivia_categories
                _state.value = State.SelectCategory(data.trivia_categories)
            }
        }
    }

    fun getQuestions() {
        viewModelScope.launch(Dispatchers.IO + postsExceptionHandler) {
            _state.value = State.Loading
            val questionsResponse = networkClient.getQuestions(10, currentCategory.value)
                .awaitResponse()
            if (questionsResponse.isSuccessful) {
                val data = questionsResponse.body()!!
                _listOfQuestions.value = cleanQuestions(data.results)
                setCurrentQuestion(listOfQuestions.value[questionIndex.value])
                _state.value = State.Success(data.results)
            }
            else {
                _state.value = State.Error("Something went wrong")
            }
        }
    }

    private fun cleanText(strData: String): String {
        return strData.replace("&lt;", "<").replace("&gt;", ">")
            .replace("&apos;", "'").replace("&quot;", "\"")
            .replace("&amp;", "&").replace("&#039;", "\'")
    }

    private fun cleanQuestions(results: List<TriviaQuestion>): List<TriviaQuestion> {
        val cleanQuestions = mutableListOf<TriviaQuestion>()
        results.forEach { triviaQuestion ->
            val listOfIncorrectAnswers = mutableListOf<String>()
            triviaQuestion.incorrect_answers.forEach { answer ->
                listOfIncorrectAnswers.add(cleanText(answer))
            }
            val newCleanQuestion = triviaQuestion.copy(question = cleanText(triviaQuestion.question),
            incorrect_answers = listOfIncorrectAnswers, correct_answer = cleanText(triviaQuestion.correct_answer))
            cleanQuestions.add(newCleanQuestion)
        }
        return cleanQuestions
    }
}
