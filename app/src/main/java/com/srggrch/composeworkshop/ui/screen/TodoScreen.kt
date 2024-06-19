package com.srggrch.composeworkshop.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.srggrch.composeworkshop.ui.screen.todo.TodoItem
import com.srggrch.composeworkshop.ui.screen.todo.TodoViewModel
import com.srggrch.composeworkshop.ui.screen.todo.sampleList

@Composable
fun TodoScreen(
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier,
    todoViewModel: TodoViewModel = viewModel()
) {
    val state by todoViewModel.state.collectAsState()

    Content(
        state = state,
        onBackClicked = onBackClicked,
        onCheckedChanged = todoViewModel::changeChacked,
        modifier = modifier
    )
}

@Composable
private fun Content(
    state: TodoViewModel.State,
    onBackClicked: () -> Unit,
    onCheckedChanged: (TodoItem) -> Unit,
    modifier: Modifier = Modifier
) {
    when (state) {
        is TodoViewModel.State.Data -> Data(state, onBackClicked, onCheckedChanged, modifier)
        TodoViewModel.State.Loading -> Loading(modifier)
    }
}

@Composable
private fun Data(
    state: TodoViewModel.State.Data,
    onBackClicked: () -> Unit,
    onCheckedChanged: (TodoItem) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            Column {
                IconButton(onClick = onBackClicked) {
                    Icon(
                        painter = rememberVectorPainter(image = Icons.AutoMirrored.Default.ArrowBack),
                        contentDescription = "backButton"
                    )
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            Modifier.padding(paddingValues),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(state.items,
                key = { it.id }
            ) {
                TodoItem(it, onCheckedChanged)
            }
        }
    }
}

@Composable
private fun TodoItem(todoItem: TodoItem, onCheckedChanged: (TodoItem) -> Unit) {
    Row(
        Modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = todoItem.name, Modifier.weight(weight = 1f, fill = true), fontSize = 16.sp)

        Checkbox(checked = todoItem.isDone, onCheckedChange = { onCheckedChanged(todoItem) })
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
    Content(TodoViewModel.State.Loading, {}, {})
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun DataPreview() {
    Content(TodoViewModel.State.Data(sampleList), {}, {})
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun TodoItemPreview() {
    TodoItem(sampleList.first(), {})
}

