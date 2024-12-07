package com.example.ecommerceapp.ui.screens.business

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.example.ecommerceapp.data.api.models.BusinessDetails
import com.example.ecommerceapp.ui.AppState
import com.example.ecommerceapp.util.Resource

@Composable
fun BusinessSettingsScreen(
    appState: AppState,
    onNavigateBack: () -> Unit
) {
    val myBusiness by appState.myBusiness.collectAsState()

    when (val businessResource = myBusiness) {
        is Resource.Loading -> CircularProgressIndicator()
        is Resource.Error -> Text("Error: ${businessResource.message}")
        is Resource.Success -> BusinessSettingsScreenContent(
            businessDetails = businessResource.data?.details,
            onSave = { appState.upsertBusinessDetails(it) },
            onNavigateBack = onNavigateBack
        )
    }
}

@Composable
fun BusinessSettingsScreenContent(
    businessDetails: BusinessDetails? = null,
    onSave: (BusinessDetails) -> Unit = { },
    onNavigateBack: () -> Unit = { }
) {
    var name by remember { mutableStateOf(businessDetails?.businessName ?: "") }
    var description by remember { mutableStateOf(businessDetails?.description ?: "") }
    var email by remember { mutableStateOf(businessDetails?.emailAddress ?: "") }
    var address by remember { mutableStateOf(businessDetails?.address ?: "") }
    var phone by remember { mutableStateOf(businessDetails?.phoneNumber ?: "") }

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
                "Business Settings",
                style = MaterialTheme.typography.headlineMedium
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Business Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email Address") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = address,
            onValueChange = { address = it },
            label = { Text("Address") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Phone Number") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                businessDetails?.let {
                    onSave(it.copy(
                        businessName = name,
                        description = description.ifEmpty { null },
                        emailAddress = email,
                        address = address.ifEmpty { null },
                        phoneNumber = phone.ifEmpty { null }
                    ))
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = name.isNotBlank() && email.isNotBlank()
        ) {
            Text("Save Changes")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BusinessSettingsScreenPreview() {
    MaterialTheme {
        BusinessSettingsScreenContent(
            businessDetails = BusinessDetails(
                id = "1",
                businessName = "Sample Business",
                description = "A great local business",
                emailAddress = "business@example.com",
                address = "123 Main St",
                phoneNumber = "555-0123"
            )
        )
    }
}