package com.example.ecommerceapp.ui.screens.business

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ecommerceapp.data.api.models.Reward
import com.example.ecommerceapp.ui.AppState
import com.example.ecommerceapp.ui.theme.EcommerceAppTheme
import com.example.ecommerceapp.util.Resource
import java.math.BigDecimal

@Composable
fun RedeemRewardScreen(
    appState: AppState,
    onNavigateBack: () -> Unit
) {
    val myBusiness by appState.myBusiness.collectAsState()

    when (val businessResource = myBusiness) {
        is Resource.Loading -> CircularProgressIndicator()
        is Resource.Error -> Text("Error: ${businessResource.message}")
        is Resource.Success -> RedeemRewardScreenContent(
            businessName = businessResource.data?.details?.businessName ?: "",
            rewards = businessResource.data?.rewards ?: emptyList(),
            onRedeem = { userId, rewardId -> appState.redeemReward(userId, rewardId) },
            onNavigateBack = onNavigateBack
        )
    }
}

@Composable
fun RedeemRewardScreenContent(
    businessName: String = "",
    rewards: List<Reward> = emptyList(),
    onRedeem: (userId: String, rewardId: String) -> Unit = { _, _ -> },
    onNavigateBack: () -> Unit = { }
) {
    var userId by remember { mutableStateOf("") }
    var selectedReward by remember { mutableStateOf<Reward?>(null) }

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
                "Redeem Reward",
                style = MaterialTheme.typography.headlineMedium
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        TextField(
            value = userId,
            onValueChange = { userId = it },
            label = { Text("Customer ID") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            "Select Reward",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn {
            items(rewards) { reward ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    onClick = { selectedReward = reward }
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
                                text = reward.name,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "Required Points: ${reward.requiredPoints}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        RadioButton(
                            selected = reward == selectedReward,
                            onClick = { selectedReward = reward }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                selectedReward?.let { reward ->
                    onRedeem(userId, reward.id)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = userId.isNotBlank() && selectedReward != null
        ) {
            Text("Redeem Reward")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RedeemRewardScreenPreview() {
    EcommerceAppTheme {
        RedeemRewardScreenContent(
            businessName = "Sample Business",
            rewards = listOf(
                Reward(
                    id = "1",
                    businessId = "1",
                    name = "Free Coffee",
                    description = "Get a free coffee",
                    requiredPoints = BigDecimal("100"),
                    usageCount = 5
                ),
                Reward(
                    id = "2",
                    businessId = "1",
                    name = "10% Discount",
                    description = "Get 10% off",
                    requiredPoints = BigDecimal("50"),
                    usageCount = 10
                )
            )
        )
    }
}