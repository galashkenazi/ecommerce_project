package com.example.ecommerceapp.ui.screens.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ecommerceapp.data.api.models.UserModel
import com.example.ecommerceapp.ui.AppState
import com.example.ecommerceapp.ui.theme.EcommerceAppTheme
import com.example.ecommerceapp.util.Resource

@Composable
fun CustomerProfileScreen(
    appState: AppState,
    onNavigateToEnrollments: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val currentUser by appState.currentUser.collectAsState()

    when (val userResource = currentUser) {
        is Resource.Loading -> CircularProgressIndicator()
        is Resource.Error -> Text("Error: ${userResource.message}")
        is Resource.Success -> CustomerProfileScreenContent(
            user = userResource.data,
            onLogout = { appState.logout() },
            onNavigateToEnrollments = onNavigateToEnrollments,
            onNavigateBack = onNavigateBack
        )
    }
}

@Composable
fun CustomerProfileScreenContent(
    user: UserModel? = null,
    onLogout: () -> Unit = { },
    onNavigateToEnrollments: () -> Unit = { },
    onNavigateBack: () -> Unit = { }
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text(
                "Profile",
                style = MaterialTheme.typography.headlineMedium
            )
            IconButton(onClick = onLogout) {
                Icon(Icons.Default.ExitToApp, contentDescription = "Logout")
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                user?.let {
                    ProfileField(
                        label = "Username",
                        value = it.username
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    ProfileField(
                        label = "Email",
                        value = it.emailAddress
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    ProfileField(
                        label = "User ID",
                        value = it.id
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onNavigateToEnrollments,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("View My Enrollments")
        }
    }
}

@Composable
private fun ProfileField(
    label: String,
    value: String
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(
                modifier = Modifier
                    .width(4.dp)
                    .height(48.dp)
                    .background(
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(2.dp)
                    )
            )
            
            Column(
                modifier = Modifier
                    .padding(start = 12.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CustomerProfileScreenPreview() {
    EcommerceAppTheme {
        CustomerProfileScreenContent(
            user = UserModel(
                id = "user123",
                username = "johndoe",
                emailAddress = "john@example.com",
                isBusinessOwner = false
            )
        )
    }
}