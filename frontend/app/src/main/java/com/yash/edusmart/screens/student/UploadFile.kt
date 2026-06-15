package com.yash.edusmart.screens.student

import android.os.Build
import android.widget.Button
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Attachment
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.Button

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.halilibo.richtext.markdown.Markdown
import com.halilibo.richtext.ui.material3.RichText
import com.yash.edusmart.api.PresignDownloadRequest
import com.yash.edusmart.api.PresignUploadRequest
import com.yash.edusmart.helper.queryDisplayName
import com.yash.edusmart.helper.uploadToSignedUrl
import com.yash.edusmart.viewmodel.StudentUiState
import com.yash.edusmart.viewmodel.StudentViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun UploadFile(
    studentViewModel: StudentViewModel,
    studentUiState: StudentUiState
) {
    var pickedUri by remember { mutableStateOf<android.net.Uri?>(null) }
    var isUploading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var selectedFileName by remember { mutableStateOf<String>("Select File") }
    var query by remember { mutableStateOf("") }

    val pickFile = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            pickedUri = uri
            selectedFileName = queryDisplayName(context, uri)
        }
    }

    var response by remember { mutableStateOf("null") }
    LaunchedEffect(studentUiState.aiResponse) {
        if(studentUiState.aiResponse!=""){
            response=studentUiState.aiResponse
        }
    }

    LaunchedEffect(pickedUri) {
        pickedUri?.let { uri ->
            scope.launch {
                try {
                    isUploading = true

                    val presign = studentViewModel.presignUploadSuspend(
                        PresignUploadRequest(fileName = selectedFileName)
                    )

                    val supabaseBaseUrl =
                        "https://bjjiztebvvgdfjbmsgsz.supabase.co/storage/v1"

                    uploadToSignedUrl(
                        context = context,
                        uri = uri,
                        fullUploadUrl = supabaseBaseUrl + presign.uploadUrl
                    )

                    val attachmentPath = presign.path

                    val url = studentViewModel.preResponseDownload(
                        PresignDownloadRequest(attachmentPath)
                    )

                    studentViewModel.createVector(url.path)

                } catch (e: Exception) {
                    Toast.makeText(context, e.message ?: "Upload failed", Toast.LENGTH_SHORT).show()
                } finally {
                    isUploading = false
                }
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Bottom
    ) {
        item {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {

                // 📎 FILE CHIP
                if (selectedFileName.isNotEmpty() && selectedFileName != "Select File") {
                    Row(
                        modifier = Modifier
                            .padding(top = 20.dp)
                            .background(
                                Color(0xFF1976D2).copy(alpha = 0.12f),
                                RoundedCornerShape(20.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "📎 $selectedFileName",
                            color = Color(0xFF1976D2),
                            fontSize = 13.sp
                        )
                    }
                }

                // 🤖 AI RESPONSE (CHAT BUBBLE STYLE)
                response.takeIf { it != "null" }?.let { res ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Column(
                            modifier = Modifier
                                .background(
                                    Color.Blue.copy(alpha = 0.2f),
                                    RoundedCornerShape(16.dp)
                                )
                                .padding(12.dp)
                                .fillMaxWidth(0.85f)
                        ) {
                            Text(
                                text = "AI Assistant",
                                fontSize = 12.sp
                            )

                            RichText {
                                val json = JSONObject(res)
                                val responseText = json.getString("response")
                                Markdown(responseText.replace("\\n", "\n"))
                            }
                        }
                    }
                }
            }
        }

        item {

            // 🔥 INPUT BAR (CHATGPT STYLE)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = { Text("Ask something...") },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    shape = RoundedCornerShape(24.dp),
                    trailingIcon = {
                        Row {

                            // 📎 ATTACH
                            IconButton(onClick = {
                                pickFile.launch(
                                    arrayOf(
                                        "application/pdf",
                                        "image/*",
                                        "application/msword",
                                        "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
                                    )
                                )
                            }) {
                                Icon(Icons.Default.Attachment, null)
                            }

                        }
                    }
                )

                // 🚀 SEND BUTTON (CIRCULAR)
                IconButton(
                    onClick = {
                        studentViewModel.rag(query)
                        query = ""
                    },
                    modifier = Modifier
                        .background(Color(0xFF1976D2), RoundedCornerShape(50))
                ) {
                    Icon(
                        Icons.Default.Send,
                        contentDescription = "Send",
                        tint = Color.White
                    )
                }
            }
        }
    }

}