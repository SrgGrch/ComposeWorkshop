package com.srggrch.composeworkshop.ui.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.srggrch.composeworkshop.R
import com.srggrch.composeworkshop.ui.screen.todo.TodoItem
import com.srggrch.composeworkshop.ui.screen.todo.TodoViewModel
import com.srggrch.composeworkshop.ui.screen.todo.sampleList

@Composable
fun TodoScreen(
    onBackClicked: () -> Unit,
    todoViewModel: TodoViewModel = viewModel()
) {
    val state by todoViewModel.state.collectAsState()

    Content(
        state = state,
        onBackClicked = onBackClicked,
        onCheckedChanged = todoViewModel::changeChacked,
        onAddClicked = todoViewModel::addTodoItem
    )
}

@Composable
private fun Content(
    state: TodoViewModel.State,
    onBackClicked: () -> Unit,
    onCheckedChanged: (TodoItem) -> Unit,
    onAddClicked: (String) -> Unit,
) {
    Scaffold(
        Modifier
            .fillMaxSize()
            .statusBarsPadding(),
        topBar = {
            IconButton(onClick = onBackClicked) {
                Icon(
                    painter = rememberVectorPainter(image = Icons.AutoMirrored.Default.ArrowBack),
                    contentDescription = "backButton"
                )
            }
        }
    ) { innerPadding ->
        when (state) {
            is TodoViewModel.State.Data -> Data(
                state,
                onCheckedChanged,
                onAddClicked,
                Modifier.padding(innerPadding)
            )

            TodoViewModel.State.Loading -> Loading(Modifier.padding(innerPadding))
        }

    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun Data(
    state: TodoViewModel.State.Data,
    onCheckedChanged: (TodoItem) -> Unit,
    onAddClicked: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
    ) { paddingValues ->
        LazyColumn(
            Modifier.padding(paddingValues),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(
                state.items,
                key = { it.id }
            ) {
                TodoItem(it, onCheckedChanged, Modifier.animateItemPlacement())
            }

            item("NewTodoItem") {
                NewTodoItem(onAddClicked, Modifier.animateItemPlacement())
            }
        }
    }
}

@Composable
private fun TodoItem(
    todoItem: TodoItem,
    onCheckedChanged: (TodoItem) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = todoItem.name, Modifier.weight(weight = 1f, fill = true), fontSize = 16.sp)

        Checkbox(checked = todoItem.isDone, onCheckedChange = { onCheckedChanged(todoItem) })
    }
}

@Composable
private fun NewTodoItem(onAddClicked: (String) -> Unit, modifier: Modifier = Modifier) {
    var todoName by remember { mutableStateOf("") }

    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    Row(
        modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        OutlinedTextField(
            value = todoName,
            onValueChange = { todoName = it },
            Modifier
                .weight(1f)
                .focusRequester(focusRequester),
            label = {
                Text(text = stringResource(id = R.string.todoScreenNewTodoLabel))
            }
        )

        TextButton(
            onClick = {
                onAddClicked(todoName)
                focusManager.clearFocus()
                todoName = ""
            },
            Modifier.padding(horizontal = 8.dp)
        ) {
            Text(text = stringResource(id = R.string.todoScreenNewTodoButton))
        }
    }
}

@Composable
private fun Loading(modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(
            modifier = Modifier.size(64.dp),
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun LoadingPreview() {
    Content(TodoViewModel.State.Loading, {}, {}, {})
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun DataPreview() {
    Content(TodoViewModel.State.Data(sampleList), {}, {}, {})
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun TodoItemPreview() {
    TodoItem(sampleList.first(), {})
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun NewTodoItemPreview() {
    NewTodoItem({})
}

