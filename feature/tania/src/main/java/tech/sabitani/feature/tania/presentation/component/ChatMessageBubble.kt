package tech.sabitani.feature.tania.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import tech.sabitani.feature.tania.domain.model.ChatMessage
import tech.sabitani.feature.tania.domain.model.ChatRole

@Composable
internal fun ChatMessageBubble(
    message: ChatMessage,
    modifier: Modifier = Modifier,
) {
    val isUser = message.role == ChatRole.USER
    val container =
        if (isUser) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
    val onContainer =
        if (isUser) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
    val alignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart
    val shape =
        if (isUser) {
            RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 4.dp)
        } else {
            RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 4.dp, bottomEnd = 16.dp)
        }
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = alignment,
    ) {
        Text(
            text = message.text,
            color = onContainer,
            style = MaterialTheme.typography.bodyMedium,
            modifier =
                Modifier
                    .widthIn(max = MAX_BUBBLE_WIDTH.dp)
                    .clip(shape)
                    .background(container)
                    .padding(horizontal = 14.dp, vertical = 10.dp),
        )
    }
}

private const val MAX_BUBBLE_WIDTH = 320
