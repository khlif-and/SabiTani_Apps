package tech.sabitani.feature.auth.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import org.orbitmvi.orbit.compose.collectSideEffect
import tech.sabitani.feature.auth.presentation.state.LoginEffect
import tech.sabitani.feature.auth.presentation.state.LoginIntent
import tech.sabitani.feature.auth.presentation.viewmodel.LoginViewModel

@Composable
internal fun LoginScreen(
    onLoginSuccess: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = hiltViewModel(),
) {
    val state by viewModel.container.stateFlow.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    viewModel.collectSideEffect { effect ->
        when (effect) {
            LoginEffect.LoginSucceeded -> onLoginSuccess()
            is LoginEffect.ShowError -> snackbarHostState.showSnackbar(effect.message)
        }
    }

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .padding(24.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "Masuk",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp),
        )
        OutlinedTextField(
            value = state.email,
            onValueChange = { viewModel.onIntent(LoginIntent.EmailChanged(it)) },
            label = { Text("Email") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
            value = state.password,
            onValueChange = { viewModel.onIntent(LoginIntent.PasswordChanged(it)) },
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
        )
        Button(
            onClick = { viewModel.onIntent(LoginIntent.SubmitClicked) },
            enabled = !state.isSubmitting,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
        ) {
            if (state.isSubmitting) {
                CircularProgressIndicator(modifier = Modifier.padding(end = 8.dp))
            }
            Text("Masuk")
        }
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
    }
}

fun NavGraphBuilder.loginScreen(onLoginSuccess: () -> Unit) {
    composable<LoginRoute> {
        LoginScreen(onLoginSuccess = onLoginSuccess)
    }
}
