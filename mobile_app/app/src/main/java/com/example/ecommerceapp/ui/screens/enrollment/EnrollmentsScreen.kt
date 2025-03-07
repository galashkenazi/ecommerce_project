package com.example.ecommerceapp.ui.screens.enrollment

import com.example.ecommerceapp.data.api.models.BusinessDetails
import com.example.ecommerceapp.data.api.models.Enrollment
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ecommerceapp.data.api.models.UserEnrollment
import com.example.ecommerceapp.util.Resource
import com.example.ecommerceapp.ui.AppState
import com.example.ecommerceapp.ui.theme.EcommerceAppTheme
import java.math.BigDecimal

@Composable
fun EnrollmentsScreen(
    appState: AppState,
    onNavigateBack: () -> Unit
) {
    val enrollments by appState.enrollments.collectAsState()

    when (val enrollmentsResource = enrollments) {
        is Resource.Loading -> CircularProgressIndicator()
        is Resource.Error -> Text("Error: ${enrollmentsResource.message}")
        is Resource.Success -> EnrollmentsScreenContent(
            enrollments = enrollmentsResource.data,
            onUnenroll = { appState.cancelEnrollment(it.business.id) },
            onNavigateBack = onNavigateBack
        )
    }
}

@Composable
fun EnrollmentsScreenContent(
    enrollments: List<UserEnrollment> = emptyList(),
    onUnenroll: (UserEnrollment) -> Unit = { },
    onNavigateBack: () -> Unit = { }
) {
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
                "My Enrollments",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(enrollments) { enrollment ->
                EnrollmentCard(
                    enrollment = enrollment,
                    onUnenroll = { onUnenroll(enrollment) }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun EnrollmentCard(
    enrollment: UserEnrollment,
    onUnenroll: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = enrollment.business.businessName,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Points: ${enrollment.enrollment.points}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
                
                IconButton(
                    onClick = onUnenroll,
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Unenroll",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EnrollmentsScreenPreview() {
    EcommerceAppTheme {
        EnrollmentsScreenContent(
            enrollments = listOf(
                UserEnrollment(
                    business = BusinessDetails(
                        id = "1",
                        businessName = "Sample Business",
                        emailAddress = "sample@business.com"
                    ),
                    enrollment = Enrollment(
                        id = "1",
                        userId = "user1",
                        businessId = "1",
                        points = BigDecimal("100")
                    )
                )
            )
        )
    }
}