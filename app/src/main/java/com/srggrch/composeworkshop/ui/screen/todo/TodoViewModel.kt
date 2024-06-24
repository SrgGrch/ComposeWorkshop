package com.srggrch.composeworkshop.ui.screen.todo

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.lang.Thread.State
import kotlin.time.Duration.Companion.seconds

val sampleList = listOf(
    TodoItem(1, "Провести грумминг", true),
    TodoItem(2, "Открыть PR", false),
    TodoItem(3, "Поставить себе A/A на перфоманс ревью", true),
)

class TodoViewModel : ViewModel() {
    val state: StateFlow<State> get() = _state
    private val _state = MutableStateFlow<State>(State.Loading)

    private var list: List<TodoItem> = mutableListOf<TodoItem>().apply {
        addAll(sampleList)
    }

    init {
        viewModelScope.launch {
            delay(1.seconds)

            _state.value = State.Data(list)
        }
    }

    fun addTodoItem(name: String) {
        list += (TodoItem(list.maxOf { it.id } + 1, name, false))
        _state.value = State.Data(list)
    }

    fun changeChacked(todoItem: TodoItem) {
        list = list
            .map {
                if (it == todoItem) {
                    it.copy(isDone = !todoItem.isDone)
                } else it
            }
            .sortedWith(compareBy({ !it.isDone }, { it.id }))
            .toMutableList()

        _state.value = State.Data(list)
    }

    @Immutable
    sealed interface State {
        object Loading : State

        data class Data(
            val items: List<TodoItem>
        ) : State
    }
}

@Immutable
data class TodoItem(
    val id: Int,
    val name: String,
    val isDone: Boolean
)