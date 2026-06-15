package com.yash.edusmart.screens.student


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.halilibo.richtext.markdown.Markdown
import com.halilibo.richtext.ui.material3.RichText
import com.yash.edusmart.api.HistoryMessage
import com.yash.edusmart.viewmodel.StudentUiState
import com.yash.edusmart.viewmodel.StudentViewModel
import org.json.JSONObject


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Suggestions(
    studentViewModel: StudentViewModel,
    studentUiState: StudentUiState
) {

    var query by remember { mutableStateOf("") }
    var isResponding by remember { mutableStateOf(false) }

    // Each entry: "user" or "ai" -> message text
    val chatHistory = remember { mutableStateListOf<Pair<String, String>>() }

    LaunchedEffect(studentUiState.aiResponse) {
        if (studentUiState.aiResponse != "") {
            try {
                val json = JSONObject(studentUiState.aiResponse)
                val responseText = json.getString("response")
                chatHistory.add("ai" to responseText.replace("\\n", "\n"))
            } catch (e: Exception) {
                chatHistory.add("ai" to studentUiState.aiResponse)
            }
        }
        isResponding = false
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Bottom
    ) {

        items(chatHistory) { (sender, message) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                horizontalArrangement = if (sender == "user") Arrangement.End else Arrangement.Start
            ) {
                Column(
                    modifier = Modifier
                        .background(
                            if (sender == "user") Color.Gray.copy(alpha = 0.2f)
                            else Color.Blue.copy(alpha = 0.2f),
                            RoundedCornerShape(16.dp)
                        )
                        .padding(12.dp)
                        .fillMaxWidth(0.85f)
                ) {
                    Text(
                        text = if (sender == "user") "You" else "AI Assistant",
                        fontSize = 12.sp
                    )

                    if (sender == "ai") {
                        RichText {
                            Markdown(message)
                        }
                    } else {
                        Text(text = message)
                    }
                }
            }
        }

        if (isResponding) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Column(
                        modifier = Modifier
                            .background(
                                Color.Blue.copy(alpha = 0.2f),
                                RoundedCornerShape(16.dp)
                            )
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "AI Assistant",
                            fontSize = 12.sp
                        )
                        Text(
                            text = "Thinking...",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        }

        item {

            // ? INPUT BAR (CHATGPT STYLE)
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
                    enabled = !isResponding
                )

                // ? SEND BUTTON (CIRCULAR)
                IconButton(
                    onClick = {
                        if (query.isNotBlank()) {
                            chatHistory.add("user" to query)

                            val historyMessages = chatHistory.dropLast(1).map { (sender, msg) ->
                                HistoryMessage(
                                    role = if (sender == "user") "user" else "ai",
                                    content = msg
                                )
                            }
                            isResponding = true

                            studentViewModel.suggestions(query, historyMessages)
                            query = ""
                        }
                    },
                    enabled = !isResponding,
                    modifier = Modifier
                        .background(Color(0xFF1976D2), RoundedCornerShape(50))
                ) {
                    if (isResponding) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
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

}