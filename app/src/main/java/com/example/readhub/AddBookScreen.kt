package com.example.readhub

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FabPosition
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Callback
import java.io.File
import java.io.FileOutputStream


@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun AddBookScreen(navController: NavHostController){
    val contextForToast = LocalContext.current.applicationContext
    val createClient = UserApi.create()
    var textFieldTitle by remember { mutableStateOf("") }
    var textFieldDesc by remember { mutableStateOf("") }

    var textFieldWriter by remember { mutableStateOf("") }
    var textFieldPubl by remember { mutableStateOf("") }

    var selectedItems by remember { mutableStateOf(mutableListOf<String>()) }
    var bookUri by remember { mutableStateOf<Uri?>(null) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    lateinit var sharedPreferences: SharedPreferencesManager
    sharedPreferences = SharedPreferencesManager(context = contextForToast)

    var category by rememberSaveable { mutableStateOf("") }
    var stateDialog by remember { mutableStateOf(false)}

        val documentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri ->
            uri?.let {
                bookUri = it
                println("$uri")
                if (isPdfFile(uri)) {
                    println("ไฟล์ PDF ถูกเพิ่มแล้ว: $uri")
                } else {
                    println("โปรดเลือกไฟล์ PDF")
                }
            }
        }
    )


    val imageLauncher: ActivityResultLauncher<String> = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { result: Uri? ->
            result?.let {
                imageUri = result
            }
        }
    )

    if (sharedPreferences.isLoggedIn) {
        Scaffold(
            topBar = { MyTopAppBar(navController, contextForToast) },
            bottomBar = { MyBottomBar(navController, contextForToast) },
            floatingActionButtonPosition = FabPosition.End,
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(paddingValues = paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "ลงหนังสือ",
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = Color.Black
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .height(270.dp)
                        .border(1.dp, Color.Gray)
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ){
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = "รายละเอียดหนังสือ",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = textFieldTitle,
                            onValueChange = {textFieldTitle = it},
                            label = { Text(text = "Enter Title")}
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = textFieldDesc,
                            onValueChange = { textFieldDesc = it },
                            label = { Text(text = "Enter Book Description") },
                            modifier = Modifier.height(150.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .height(200.dp)
                        .border(1.dp, Color.Gray)
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = "รายละเอียดผู้เขียน",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp)
                        OutlinedTextField(
                            value = textFieldWriter,
                            onValueChange = { textFieldWriter = it },
                            label = { Text(text = "Enter Writer Name") }
                        )
                        OutlinedTextField(
                            value = textFieldPubl,
                            onValueChange = { textFieldPubl = it },
                            label = { Text(text = "Enter Publishing") }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "ประเภทหนังสือ",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp)
                menuDropdown(menu = category, onMenuChange = { category = it })

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .width(270.dp)
                        .height(260.dp)
                        .border(
                            width = 1.dp,
                            color = Color.Gray,
                            shape = RoundedCornerShape(16.dp)
                        )
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(20.dp)

                    ) {
                        Text(text = "ไฟล์หนังสือ",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                        TextButton(
                            modifier = Modifier.weight(1f),
                            onClick = {
                                documentLauncher.launch(arrayOf("application/pdf"))
                            }
                        ) {
                            Text(text = "Add File")
                        }

                        if (bookUri != null) {
                            val fileName = getFileName(contextForToast, bookUri!!)
                            Text(
                                text = "$fileName",
                            )
                        } else {
                            Text(
                                text = "ยังไม่เพิ่มไฟล์",
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        HorizontalDivider(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            thickness = 1.dp,
                            color = Color.Gray
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(text = "ไฟล์หน้าปกหนังสือ",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                        TextButton(
                            modifier = Modifier.weight(1f),
                            onClick = {
                                imageLauncher.launch("image/*")
                            }
                        ) {
                            Text(text = "Add Picture")
                        }
                        if (imageUri != null) {
                            val fileName = getImgName(contextForToast, imageUri!!)
                            Text(
                                text = "$fileName",
                            )
                        } else {
                            Text(
                                text = "ยังไม่เพิ่มไฟล์หน้าปก",
                            )
                        }
                    }
                }
                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ){
                    Button(
                        modifier = Modifier.width(130.dp),
                        onClick = {
                            val inputStreamBook = contextForToast.contentResolver.openInputStream(bookUri!!)
                                ?: throw Exception("Failed to open input stream for book")

                            val inputStreamImg = contextForToast.contentResolver.openInputStream(imageUri!!)
                                ?: throw Exception("Failed to open input stream for img")

                            val bookFile = File.createTempFile("book", ".pdf")
                            val imgFile = File.createTempFile("img", ".jpg")

                            val outputStreamBook = FileOutputStream(bookFile)
                            inputStreamBook.copyTo(outputStreamBook)
                            inputStreamBook.close()
                            outputStreamBook.close()

                            val outputStreamImg = FileOutputStream(imgFile)
                            inputStreamImg.copyTo(outputStreamImg)
                            inputStreamImg.close()
                            outputStreamImg.close()


                            val bookRequestBody = bookFile.asRequestBody("book/pdf".toMediaTypeOrNull())
                            val imgRequestBody = imgFile.asRequestBody("image/jpeg".toMediaTypeOrNull())

                            val bookPart = MultipartBody.Part.createFormData(
                                "bookPart",
                                bookFile.name,
                                bookRequestBody
                            )
                            val imgPart = MultipartBody.Part.createFormData(
                                "imgPart",
                                imgFile.name,
                                imgRequestBody
                            )
                            val user_id = sharedPreferences.userId


                            val titleRequestBody =
                                textFieldTitle.toRequestBody("text/plain".toMediaTypeOrNull())
                            val DescRequestBody =
                                textFieldDesc.toRequestBody("text/plain".toMediaTypeOrNull())
                            val WriterRequestBody =
                                textFieldWriter.toRequestBody("text/plain".toMediaTypeOrNull())
                            val PublRequestBody =
                                textFieldPubl.toRequestBody("text/plain".toMediaTypeOrNull())
                            val CatagRequestBody =
                                category.toRequestBody("text/plain".toMediaTypeOrNull())

                            val useridRequestBody =
                                user_id!!.toRequestBody("text/plain".toMediaTypeOrNull())

                            println("check userid:"+user_id)
                            createClient.uploadFile(
                                bookPart, imgPart,titleRequestBody, DescRequestBody,WriterRequestBody,PublRequestBody,CatagRequestBody, useridRequestBody
                            ).enqueue(object : Callback<Book> {
                                override fun onResponse(
                                    call: retrofit2.Call<Book>,
                                    response: retrofit2.Response<Book>
                                ) {
                                    if (response.isSuccessful) {
                                        Toast.makeText(
                                            contextForToast, "Successfully Insert",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else {
                                        println(bookPart.toString()+titleRequestBody.toString()+DescRequestBody.toString()+WriterRequestBody.toString()+PublRequestBody.toString()+CatagRequestBody.toString())
                                        Toast.makeText(
                                            contextForToast, "Error",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }

                                override fun onFailure(call: retrofit2.Call<Book>, t: Throwable) {
                                    Toast.makeText(
                                        contextForToast, "Error onFailor" + t.message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            })
                            if (navController.currentBackStack.value.size >= 2) {
                                stateDialog = true
                                navController.popBackStack()
                            }
                        }
                    ) {
                        Text(text = "Save")
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(
                        modifier = Modifier.width(130.dp),
                        onClick = {
                            textFieldTitle =""
                            textFieldWriter = ""
                            textFieldPubl = ""
                            if(navController.currentBackStack.value.size >= 2){
                                navController.popBackStack()
                            }
                            navController.navigate(Screen.Home.route)
                        }) {
                        Text(text = "Cancel")
                    }
                            if (stateDialog) {
                                AlertDialog(
                                    onDismissRequest = { stateDialog = false },
                                    title = { Text(text = "ลงหนังสือสำเร็จ")},
                                    text = { Text(text = "เผยแพร่หนังสือ ${textFieldTitle} เรียบร้อยแล้ว") },
                                    icon = {
                                        Icon(imageVector = Icons.Default.Info, contentDescription = "Information")
                                    },
                                    confirmButton = {
                                        TextButton(onClick = { stateDialog = false
                                            navController.navigate(Screen.Profile.route)
                                        }) {
                                            Text("OK")
                                        }
                                    }
                                )
                            }

                }
            }

        }
    } else {
        navController.navigate(Screen.Login.route)
    }
}



fun isPdfFile(uri: Uri): Boolean {
    val fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri.toString())
    val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension)
    return mimeType == "application/pdf"
}

fun getFileName(context: Context, uri: Uri): String? {
    var fileName: String? = null

    if ("content".equals(uri.scheme, ignoreCase = true)) {
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val displayName = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                fileName = cursor.getString(displayName)
            }
        }
    } else if ("file".equals(uri.scheme, ignoreCase = true)) {
        fileName = File(uri.path!!).name
    }

    return fileName
}
fun getImgName(context: Context, uri: Uri): String? {
    var fileName: String? = null

    if ("content".equals(uri.scheme, ignoreCase = true)) {
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val displayName = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                fileName = cursor.getString(displayName)
            }
        }
    } else if ("file".equals(uri.scheme, ignoreCase = true)) {
        fileName = File(uri.path!!).name
    }

    return fileName
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun menuDropdown(
    menu: String,
    onMenuChange: (String) -> Unit,
): String {
    val keyboardController = LocalSoftwareKeyboardController.current
    val categoryList = listOf(
        "Love Novel", "Boy Love", "Girl Love", "Fantasy", "Horror",
        "Turn back time", "Historical"
    )
    var expanded by remember { mutableStateOf(false) }
    var selectCategory by remember { mutableStateOf(menu) } // ให้ selectCategory เริ่มต้นด้วยค่า menu

    ExposedDropdownMenuBox(
        modifier = Modifier
            .clickable { keyboardController?.hide() },
        expanded = expanded,
        onExpandedChange = {
            expanded = !expanded
        }
    ) {
        OutlinedTextField(
            modifier = Modifier
                .width(340.dp)
                .menuAnchor()
                .clickable { keyboardController?.hide() },
            textStyle = TextStyle.Default.copy(fontSize = 12.sp),
            readOnly = true,
            value = selectCategory,
            onValueChange = {},
            label = {
                Text("Book Category:")
            },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            }
        ) {
            categoryList.forEach { selectOption ->
                DropdownMenuItem(
                    text = { Text(selectOption) },
                    onClick = {
                        selectCategory = selectOption
                        expanded = false
                        onMenuChange(selectOption) // เรียกใช้งาน callback เพื่ออัปเดตค่า
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }
        }
    }
    return selectCategory
}


