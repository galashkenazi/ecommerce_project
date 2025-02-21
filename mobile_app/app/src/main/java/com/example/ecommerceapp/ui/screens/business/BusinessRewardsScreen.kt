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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import com.example.ecommerceapp.data.api.models.CreateRewardRequest
import com.example.ecommerceapp.data.api.models.Reward
import com.example.ecommerceapp.ui.AppState
import com.example.ecommerceapp.ui.screens.business.dialogs.CreateRewardDialog
import com.example.ecommerceapp.ui.theme.EcommerceAppTheme
import com.example.ecommerceapp.util.Resource
import java.math.BigDecimal

@Composable
fun BusinessRewardsScreen(
    appState: AppState,
    onNavigateBack: () -> Unit
) {
    val myBusiness by appState.myBusiness.collectAsState()

    when (val businessResource = myBusiness) {
        is Resource.Loading -> CircularProgressIndicator()
        is Resource.Error -> Text("Error: ${businessResource.message}")
        is Resource.Success -> BusinessRewardsScreenContent(
            businessName = businessResource.data?.details?.businessName ?: "",
            rewards = businessResource.data?.rewards ?: emptyList(),
            onCreateReward = { appState.createReward(it) },
            onDeleteReward = { appState.deleteReward(it) },
            onNavigateBack = onNavigateBack
        )
    }
}

@Composable
fun BusinessRewardsScreenContent(
    businessName: String = "",
    rewards: List<Reward> = emptyList(),
    onCreateReward: (CreateRewardRequest) -> Unit = { },
    onDeleteReward: (String) -> Unit = { },
    onNavigateBack: () -> Unit = { }
) {
    var showCreateDialog by remember { mutableStateOf(false) }

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
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
                Text(
                    "Rewards for $businessName",
                    style = MaterialTheme.typography.headlineMedium
                )
            }
            IconButton(onClick = { showCreateDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Reward")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(rewards) { reward ->
                RewardCard(
                    reward = reward,
                    onDelete = { onDeleteReward(reward.id) }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        if (showCreateDialog) {
            CreateRewardDialog(
                onDismiss = { showCreateDialog = false },
                onCreate = {
                    onCreateReward(it)
                    showCreateDialog = false
                }
            )
        }
    }
}

@Composable
private fun RewardCard(
    reward: Reward,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = reward.name,
                    style = MaterialTheme.typography.titleLarge
                )
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                }
            }
            Text(
                text = "Required Points: ${reward.requiredPoints}",
                style = MaterialTheme.typography.bodyLarge
            )
            reward.description?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Text(
                text = "Times Used: ${reward.usageCount}",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BusinessRewardsScreenPreview() {
    EcommerceAppTheme {
        BusinessRewardsScreenContent(
            businessName = "Sample Business",
            rewards = listOf(
                Reward(
                    id = "1",
                    businessId = "1",
                    name = "Free Coffee",
                    description = "Get a free coffee after collecting points",
                    requiredPoints = BigDecimal("100"),
                    usageCount = 0
                )
            )
        )
    }
}