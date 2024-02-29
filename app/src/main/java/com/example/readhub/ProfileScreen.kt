package com.example.readhub

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun ProfileScreen(navController: NavHostController){
    val contextForToast = LocalContext.current.applicationContext

    lateinit var sharedPreferences: SharedPreferencesManager
    sharedPreferences = SharedPreferencesManager(context = contextForToast)

    var checkedState by remember { mutableStateOf(false) }
    var logoutDialog by remember { mutableStateOf(false) }
    println("now logged in: "+sharedPreferences.userId)
    if (sharedPreferences.isLoggedIn) {
        Scaffold(
            topBar = { MyTopAppBar(navController, contextForToast) },
            bottomBar = { MyBottomBar(navController, contextForToast) },
            floatingActionButtonPosition = FabPosition.End,
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues = paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "จัดการบัญชีผู้ใช้",
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color.Black
                    )
                )
                Text(text = "username: "+sharedPreferences.userId.toString())
                Button(
                    modifier = Modifier.padding(16.dp),
                    onClick = { logoutDialog=true })
                {
                    Text(text = "Log Out")
                }
                if (logoutDialog) {
                    AlertDialog(
                        onDismissRequest = { logoutDialog = false },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    logoutDialog = false
                                    if(checkedState){
                                        sharedPreferences.clearUserLogin()
                                        Toast.makeText(contextForToast, "Clear user login.", Toast.LENGTH_SHORT).show()
                                    }else{
                                        sharedPreferences.clearUserAll()
                                        Toast.makeText(contextForToast, "Clear user login and username.", Toast.LENGTH_SHORT).show()
                                    }
                                    if(navController.currentBackStack.value.size >=2){
                                        navController.popBackStack()
                                    }
                                    navController.navigate(Screen.Login.route)
                                }
                            ) {
                                Text(text = "Yes")
                            }
                        },
                        dismissButton = {
                            TextButton(
                                onClick = {
                                    logoutDialog = false
                                    Toast.makeText(contextForToast, "Click on no", Toast.LENGTH_SHORT).show()
                                }
                            ){ Text(text = "No")}
                        },
                        title = { Text(text = "Logout")},
                        text = { Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "Are you sure you want to log out?")
                            Row (
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Start
                            ){
                                Checkbox(
                                    checked = checkedState,
                                    onCheckedChange = {
                                            isChecked->
                                        checkedState = isChecked
                                    })
                                Spacer(modifier = Modifier.size(8.dp))
                                Text(text = "Remember my username")
                            }
                        }
                        }
                    )
                }
            }
        }
    } else {
        navController.navigate(Screen.Login.route)
    }


}