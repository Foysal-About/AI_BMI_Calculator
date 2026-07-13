package com.happylens.ai_bmi_calculator.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.happylens.ai_bmi_calculator.ui.glass.GlassIconButton
import dev.chrisbanes.haze.HazeState

@Composable
fun CommonTopBar(
    hazeState: HazeState,
    modifier: Modifier = Modifier,
    title: String? = null,
    titleContent: @Composable (() -> Unit)? = null,
    onBackClick: (() -> Unit)? = null,
    rightContent: @Composable (RowScope.() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            if (onBackClick != null) {
                GlassIconButton(
                    onClick = onBackClick,
                    hazeState = hazeState,
                    size = 40.dp
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
            }

            if (titleContent != null) {
                titleContent()
            } else if (title != null) {
                Text(
                    text = title,
                    fontSize = if (onBackClick != null) 20.sp else 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        if (rightContent != null) {
            rightContent()
        }
    }
}
