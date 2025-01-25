package com.example.chitchatapp.screens

import android.annotation.SuppressLint
import android.graphics.drawable.shapes.Shape
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.trace
import coil3.compose.rememberAsyncImagePainter
import com.example.chitchatapp.others.CommonProgressBar
import com.example.chitchatapp.others.createClickHandler
import com.example.chitchatapp.others.rememberImagePicker
import com.example.chitchatapp.others.uploadImageToFirebase
import com.example.chitchatapp.viewmodel.ChitChatViewmodel
import kotlinx.coroutines.launch

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun ProfileScreen(navController: NavHostController, vm: ChitChatViewmodel) {
    val progress = vm.loginProgressBar.value
    val userData = vm.userData.value
    if (progress) CommonProgressBar()
    var name by rememberSaveable { mutableStateOf(userData?.name ?: "") }
    var number by rememberSaveable { mutableStateOf(userData?.userNumber ?: "") }
    Scaffold(bottomBar = {
        NavigationBar(navController, BottomNavigationItems.PROFILE)
    }) { innerPadding ->
        ProfileContent(
            modifier = Modifier.padding(innerPadding),
            vm=vm,
            name =name ,
            number = number,
            onBack = {navController.popBackStack()},
            onNameChange = {name=it},
            onNumberChange = {number=it},
            onSave = {vm.createOrUpdateProfile(name,number)}
        )
    }
}

@Composable
fun ProfileContent(
    modifier: Modifier = Modifier,
    vm: ChitChatViewmodel,
    name: String,
    number: String,
    onBack: () -> Unit,
    onNameChange: (String) -> Unit,
    onNumberChange: (String) -> Unit,
    onSave: () -> Unit
) {
    // Generate click handlers for the arrow and save button
    createClickHandler(
        exitButton = true,
        saveButton = true,
        firstAction = onBack,
        secondAction = onSave
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Image Picker Composable
        ImagePickerComposable(vm)

        Spacer(modifier = Modifier.height(16.dp))

        // Update Name and Number Fields
        UpdateNameAndNumber(
            name = name,
            number = number,
            onNameChange = onNameChange,
            onNumberChange = onNumberChange,
            onLogout = {vm.signOut()}
        )
    }
}



@Composable
fun ImagePickerComposable(vm: ChitChatViewmodel) {
    var selectedImage by remember { mutableStateOf<Uri?>(null) }
    var imageUrl by remember { mutableStateOf<String?>(null) }

    val pickImage = rememberImagePicker { uri ->
        selectedImage = uri
    }

    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .padding(top = 150.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(shape = CircleShape,
            modifier = Modifier
                .size(180.dp)
                .shadow(8.dp, CircleShape)
                .clickable {
                    pickImage()
                }) {
            if (selectedImage != null) {
                // Display the selected image temporarily
                Image(
                    painter = rememberAsyncImagePainter(selectedImage),
                    contentDescription = "Selected Profile Image",
                    modifier = Modifier.fillMaxSize()
                )

                // Upload the image to Firebase when selected
                LaunchedEffect(selectedImage) {
                    coroutineScope.launch {
                        val uploadedImageUrl = uploadImageToFirebase(selectedImage!!)
                        if (uploadedImageUrl != null) {
                            imageUrl = uploadedImageUrl

                        }
                    }
                }
            } else if (imageUrl != null) {
                // Display the uploaded image from Firebase Storage
                vm.createOrUpdateProfile(imageUrl = imageUrl)
                Image(
                    painter = rememberAsyncImagePainter(imageUrl),
                    contentDescription = "Profile Image from Firebase",
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                // Placeholder when no image is selected
                Text(
                    text = "Tap to select an image",
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(100.dp)
                )
            }
        }
    }
}

@Composable
fun UpdateNameAndNumber(
    name: String,
    number: String,
    onNameChange: (String) -> Unit,
    onNumberChange: (String) -> Unit,
    onLogout:()->Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Name Field
        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text("Name") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            shape = RoundedCornerShape(12.dp)
        )

        // Number Field
        OutlinedTextField(
            value = number,
            onValueChange = onNumberChange,
            label = { Text("Phone Number") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            shape = RoundedCornerShape(12.dp)
        )
        // Logout Button
        Button(
            onClick = onLogout,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(16.dp)
                .shadow(8.dp, RoundedCornerShape(12.dp)),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
        ) {
            Text(
                text = "Logout",
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}





