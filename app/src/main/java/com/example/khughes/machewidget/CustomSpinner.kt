package com.example.khughes.machewidget

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp

@Composable
fun CustomSpinner(
    initialLabel: String,
    items: List<String>,
    onClick: (String) -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }
    var label by remember { mutableStateOf(initialLabel) }

    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(8.dp)
            )
    ) {
        Text(
            text = label,
            color = Color.White,
            modifier = Modifier
                .padding(all = 4.dp)
                .clickable {
                    isVisible = true
                }
        )

        DropdownMenu(
            expanded = isVisible,
            onDismissRequest = { isVisible = false }
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = { Text(text = item) },
                    onClick = {
                        onClick(item)
                        label = item
                        isVisible = false
                    }
                )
            }
        }

        Image(
            imageVector = ImageVector.vectorResource(id = R.drawable.arrow_drop_down),
            contentDescription = "",
        )
    }
}
