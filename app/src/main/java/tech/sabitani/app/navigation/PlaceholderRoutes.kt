package tech.sabitani.app.navigation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object ProfileRoute

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileScreen(
    onOpenSecurity: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { TopAppBar(title = { Text("Profil") }) },
    ) { padding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(top = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            ListItem(
                headlineContent = { Text("Akun") },
                supportingContent = { Text("Pengaturan akun & sinkronisasi (segera)") },
                leadingContent = { Icon(Icons.Outlined.PersonOutline, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
            )
            ListItem(
                headlineContent = { Text("Keamanan") },
                supportingContent = { Text("PIN aplikasi & unlock biometrik") },
                leadingContent = { Icon(Icons.Outlined.Lock, contentDescription = null) },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onOpenSecurity),
            )
        }
    }
}

fun NavGraphBuilder.profileScreen(onOpenSecurity: () -> Unit) {
    composable<ProfileRoute> {
        ProfileScreen(onOpenSecurity = onOpenSecurity)
    }
}
