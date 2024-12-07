package com.example.ecommerceapp.ui.screens.business

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ecommerceapp.data.api.models.BusinessDetails
import com.example.ecommerceapp.data.api.models.Enrollment
import com.example.ecommerceapp.data.api.models.UserEnrollment
import com.example.ecommerceapp.ui.AppState
import com.example.ecommerceapp.util.Resource
import java.math.BigDecimal


@Composable
fun AddPointsScreen(
    appState: AppState,
    onNavigateBack: () -> Unit
) {
    val enrollments by appState.enrollments.collectAsState()

    when (val enrollmentsResource = enrollments) {
        is Resource.Loading -> CircularProgressIndicator()
        is Resource.Error -> Text("Error: ${enrollmentsResource.message}")
        is Resource.Success -> AddPointsScreenContent(
            enrollments = enrollmentsResource.data,
            onAddPoints = { userId, points ->
                appState.addPoints(userId, points)
            },
            onNavigateBack = onNavigateBack
        )
    }
}

@Composable
fun AddPointsScreenContent(
    enrollments: List<UserEnrollment> = emptyList(),
    onAddPoints: (userId: String, points: BigDecimal) -> Unit = { _, _ -> },
    onNavigateBack: () -> Unit = { }
) {
    var selectedEnrollment by remember { mutableStateOf<UserEnrollment?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text(
                "Add Points",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(enrollments) { enrollment ->
                CustomerPointsCard(
                    enrollment = enrollment,
                    onAddPoints = { selectedEnrollment = enrollment }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }

    selectedEnrollment?.let { enrollment ->
        AddPointsDialog(
            customerId = enrollment.enrollment.userId,
            onDismiss = { selectedEnrollment = null },
            onConfirm = { points ->
                onAddPoints(enrollment.enrollment.userId, points)
                selectedEnrollment = null
            }
        )
    }
}

@Composable
private fun CustomerPointsCard(
    enrollment: UserEnrollment,
    onAddPoints: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = enrollment.enrollment.userId,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Current Points: ${enrollment.enrollment.points}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Button(onClick = onAddPoints) {
                Text("Add Points")
            }
        }
    }
}

@Composable
private fun AddPointsDialog(
    customerId: String,
    onDismiss: () -> Unit,
    onConfirm: (BigDecimal) -> Unit
) {
    var pointsText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Points for $customerId") },
        text = {
            TextField(
                value = pointsText,
                onValueChange = { pointsText = it },
                label = { Text("Points") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    pointsText.toBigDecimalOrNull()?.let { onConfirm(it) }
                },
                enabled = pointsText.toBigDecimalOrNull() != null
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun AddPointsScreenPreview() {
    MaterialTheme {
        AddPointsScreenContent(
            enrollments = listOf(
                UserEnrollment(
                    business = BusinessDetails(
                        id = "1",
                        businessName = "Coffee Shop",
                        description = "Best coffee in town",
                        emailAddress = "coffee@shop.com",
                        address = "123 Main St",
                        phoneNumber = "555-0123"
                    ),
                    enrollment = Enrollment(
                        id = "1",
                        userId = "1",
                        businessId = "1",
                        points = BigDecimal("100")
                    )
                )
            )
        )
    }
}