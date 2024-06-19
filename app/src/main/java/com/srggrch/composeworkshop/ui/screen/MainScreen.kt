package com.srggrch.composeworkshop.ui.screen

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.srggrch.composeworkshop.R

@Composable
fun MainScreen(
    onNextClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activity = LocalContext.current as Activity

    Column(
        modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(Color.Gray)
                .padding(horizontal = 24.dp, vertical = 36.dp)
        ) {
            Text(text = stringResource(id = R.string.mainScreenTitle), fontSize = 24.sp, color = Color.White)
        }

        Spacer(modifier = Modifier.height(64.dp))

        Button(
            onClick = {
                onNextClicked()
            },
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text(stringResource(id = R.string.mainScreenButtonNext))
        }

        Button(
            onClick = {
                activity.finish()
            },
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text(stringResource(id = R.string.mainScreenButtonExit))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun Preview() {
    MainScreen({})
}
