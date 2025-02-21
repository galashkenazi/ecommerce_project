package com.example.ecommerceapp.ui.screens.business

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ecommerceapp.data.api.models.BusinessDetailsWithRewards
import com.example.ecommerceapp.data.api.models.BusinessDetails
import com.example.ecommerceapp.util.Resource
import com.example.ecommerceapp.ui.AppState

@Composable
fun BusinessListScreen(
    appState: AppState,
    onNavigateToEnrollments: () -> Unit
) {
    val businesses by appState.businesses.collectAsState()

    when (val businessesResource = businesses) {
        is Resource.Loading -> CircularProgressIndicator()
        is Resource.Error -> Text("Error: ${businessesResource.message}")
        is Resource.Success -> BusinessListScreenContent(
            businesses = businessesResource.data,
            onEnroll = { appState.enrollToBusiness(it.details.id) },
            onNavigateToEnrollments = onNavigateToEnrollments
        )
    }
}

@Composable
fun BusinessListScreenContent(
    businesses: List<BusinessDetailsWithRewards> = emptyList(),
    onEnroll: (BusinessDetailsWithRewards) -> Unit = { },
    onNavigateToEnrollments: () -> Unit = { }
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
            Text(
                "Businesses",
                style = MaterialTheme.typography.headlineMedium
            )
            Button(onClick = onNavigateToEnrollments) {
                Text("My Enrollments")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(businesses) { business ->
                BusinessCard(
                    business = business,
                    onEnroll = { onEnroll(business) }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun BusinessCard(
    business: BusinessDetailsWithRewards,
    onEnroll: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(
                text = business.details.businessName,
                style = MaterialTheme.typography.titleLarge
            )
            business.details.description?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            if (business.similarBusinesses.isNotEmpty()) {
                Text(
                    text = "Similar Businesses: ${business.similarBusinesses.joinToString(", ") { it.businessName }}",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            Button(
                onClick = onEnroll,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Enroll")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BusinessListScreenPreview() {
    MaterialTheme {
        BusinessListScreenContent(
            businesses = listOf(
                BusinessDetailsWithRewards(
                    details = BusinessDetails(
                        id = "1",
                        businessName = "Sample Business",
                        description = "A great local business",
                        emailAddress = "sample@business.com"
                    ),
                    rewards = emptyList(),
                    similarBusinesses = listOf(
                        BusinessDetails(
                            id = "2",
                            businessName = "Similar Shop 1",
                            emailAddress = "shop1@test.com"
                        ),
                        BusinessDetails(
                            id = "3",
                            businessName = "Similar Shop 2",
                            emailAddress = "shop2@test.com"
                        )
                    )
                )
            )
        )
    }
}