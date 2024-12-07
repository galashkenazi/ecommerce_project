package com.example.ecommerceapp.ui.screens.register

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.example.ecommerceapp.ui.AppState

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun RegisterScreen(
    appState: AppState,
    onNavigateBack: () -> Unit
) {
    RegisterScreenContent(
        onRegister = { username, password, email, isBusinessOwner ->
            appState.register(username, password, email, isBusinessOwner)
        },
        onNavigateBack = onNavigateBack
    )
}

@Composable
fun RegisterScreenContent(
    onRegister: (username: String, password: String, email: String, isBusinessOwner: Boolean) -> Unit = { _, _, _, _ -> },
    onNavigateBack: () -> Unit = {}
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var isBusinessOwner by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButton(
            onClick = onNavigateBack,
            modifier = Modifier.align(Alignment.Start)
        ) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            "Register",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        TextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isBusinessOwner,
                onCheckedChange = { isBusinessOwner = it }
            )
            Text("Register as Business Owner")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                onRegister(username, password, email, isBusinessOwner)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Register")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    MaterialTheme {
        RegisterScreenContent()
    }
}