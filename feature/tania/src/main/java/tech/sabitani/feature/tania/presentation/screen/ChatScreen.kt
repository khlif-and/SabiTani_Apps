package tech.sabitani.feature.tania.presentation.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.orbitmvi.orbit.compose.collectSideEffect
import tech.sabitani.core.ui.state.EmptyState
import tech.sabitani.feature.tania.presentation.component.ChatInputBar
import tech.sabitani.feature.tania.presentation.component.ChatMessageList
import tech.sabitani.feature.tania.presentation.state.ChatEffect
import tech.sabitani.feature.tania.presentation.state.ChatIntent
import tech.sabitani.feature.tania.presentation.viewmodel.ChatViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ChatScreen(
    modifier: Modifier = Modifier,
    viewModel: ChatViewModel = hiltViewModel(),
) {
    val state by viewModel.container.stateFlow.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    viewModel.collectSideEffect { effect ->
        when (effect) {
            is ChatEffect.ShowError -> snackbarHostState.showSnackbar(effect.message)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Tania") },
                actions = {
                    if (state.messages.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onIntent(ChatIntent.ClearHistoryClicked) }) {
                            Icon(
                                imageVector = Icons.Outlined.DeleteOutline,
                                contentDescription = "Hapus riwayat",
                            )
                        }
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            Box(modifier = Modifier.weight(1f).fillMaxSize()) {
                if (state.messages.isEmpty()) {
                    EmptyState(
                        title = "Halo, saya Tania",
                        description =
                            "Tanya soal penyakit tanaman, dosis pupuk, atau pestisida " +
                                "— saya bantu cari tahu.",
                        icon = Icons.Outlined.AutoAwesome,
                    )
                } else {
                    ChatMessageList(messages = state.messages)
                }
            }
            HorizontalDivider()
            ChatInputBar(
                draft = state.draft,
                isSending = state.isSending,
                onDraftChange = { viewModel.onIntent(ChatIntent.DraftChanged(it)) },
                onSendClick = { viewModel.onIntent(ChatIntent.SendClicked) },
            )
        }
    }
}
