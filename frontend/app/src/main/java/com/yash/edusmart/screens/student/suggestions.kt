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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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


    var response by remember { mutableStateOf("null") }
    LaunchedEffect(studentUiState.aiResponse) {
        if(studentUiState.aiResponse!=""){
            response=studentUiState.aiResponse
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
                )

                // 🚀 SEND BUTTON (CIRCULAR)
                IconButton(
                    onClick = {
                        studentViewModel.suggestions(query)
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