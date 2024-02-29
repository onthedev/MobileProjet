package com.example.readhub

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun CheckboxGroup(items: List<String>, onSelectionChange: (List<String>) -> Unit) {
    var selectedItems by remember { mutableStateOf(mutableListOf<String>()) }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        items.forEach { item ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = selectedItems.contains(item),
                    onCheckedChange = {
                        if (it) {
                            selectedItems.add(item)
                        } else {
                            selectedItems.remove(item)
                        }
                        onSelectionChange(selectedItems.toList())
                    }
                )
                Text(text = item, fontSize = 15.sp)
            }
        }
    }
}

@Composable
fun UserInterested(navController: NavHostController) {
    val categoryList = listOf(
        "Love Novel", "Boy Love", "Girl Love", "Fantasy", "Horror",
        "Turn back time", "Historical"
    )
    val createClient = UserApi.create()
    var studentItems = remember { mutableStateListOf<LoginClass>() }

    var selectedItems by remember { mutableStateOf(mutableListOf<String>()) }
    val contextForToast = LocalContext.current.applicationContext

    lateinit var sharedPreferences: SharedPreferencesManager
    sharedPreferences = SharedPreferencesManager(context = contextForToast)

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF7F00FF), Color(0xFFE100FF))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .height(600.dp)
                .width(390.dp)
                .background(Color(0xFFFFFFFF))
                .padding(top = 10.dp)
                .clip(RoundedCornerShape(15.dp)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "your interested category",
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.height(26.dp))
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    CheckboxGroup(items = categoryList) { newSelectedItems ->
                        selectedItems = newSelectedItems.toMutableList()
                        Log.d("CheckboxGroup", "Selected items: $selectedItems")
                    }
                }


                Spacer(modifier = Modifier.height(50.dp))
                TextButton(
                    onClick = {
                        keyboardController?.hide()
                        focusManager.clearFocus()

                        createClient.addUserInterested(selectedItems.toString())
                        navController.navigate(Screen.Login.route)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(Color(0xFF7F00FF), Color(0xFFE100FF)),
                                start = Offset(50f, 50f),
                                end = Offset(50f, 50f)
                            )
                        )
                ) {
                    Text(text = "Register",
                        color = Color.White
                    )
                }
            }
        }
    }
}