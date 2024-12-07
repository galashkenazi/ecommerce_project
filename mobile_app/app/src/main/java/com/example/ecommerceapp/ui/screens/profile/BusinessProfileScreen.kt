package com.example.ecommerceapp.ui.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ecommerceapp.data.api.models.BusinessDetails
import com.example.ecommerceapp.ui.AppState
import com.example.ecommerceapp.util.Resource

@Composable
fun BusinessProfileScreen(appState: AppState) {
    val myBusiness by appState.myBusiness.collectAsState()

    when (val businessResource = myBusiness) {
        is Resource.Loading -> CircularProgressIndicator()
        is Resource.Error -> Text("Error: ${businessResource.message}")
        is Resource.Success -> {
            if (businessResource.data != null) {
                BusinessProfileScreenContent(
                    businessDetails = businessResource.data.details,
                    onSave = { appState.upsertBusinessDetails(it) }
                )
            } else {
                // Show create new business form
                BusinessProfileScreenContent(
                    businessDetails = null,
                    onSave = { appState.upsertBusinessDetails(it) }
                )
            }
        }
    }
}

@Composable
fun BusinessProfileScreenContent(
    businessDetails: BusinessDetails? = null,
    onSave: (BusinessDetails) -> Unit = { }
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
        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Business Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = address,
            onValueChange = { address = it },
            label = { Text("Address") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Phone") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                businessDetails?.let {
                    onSave(it.copy(
                        businessName = name,
                        description = description,
                        emailAddress = email,
                        address = address,
                        phoneNumber = phone
                    ))
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BusinessProfileScreenPreview() {
    val sampleBusiness = BusinessDetails(
        id = "1",
        businessName = "Sample Business",
        description = "A sample business description",
        emailAddress = "sample@business.com",
        address = "123 Sample St",
        phoneNumber = "555-0123"
    )
    BusinessProfileScreenContent(businessDetails = sampleBusiness)
}