package com.yash.edusmart.screens.student

import android.annotation.SuppressLint
import android.media.Image
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSizeIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.yash.edusmart.R
import com.yash.edusmart.navigation.Screens


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AIScreen(navController: NavHostController){
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val ais = listOf<Triple<Int, String, () -> Unit>>(
        Triple(R.drawable.google_logo,"General Chat",{navController.navigate(Screens.GENERAL.name)}),
        Triple(R.drawable.google_logo,"Upload document and find",{navController.navigate(Screens.RAG.name)}),
        Triple(R.drawable.google_logo,"Suggestions",{navController.navigate(Screens.SUGGESTIONS.name)})
    )
    Scaffold(
        topBar = {
            TopAppBar(
                scrollBehavior = scrollBehavior,
                title = {
                    Text(text = "Chat wit AI",
                        fontSize = 35.sp)
                }
            )
        }
    ) {innerPadding->
        LazyColumn(modifier = Modifier.fillMaxSize()
            .padding(innerPadding)) {
            items(ais){
                TextBox(it.second,it.first,it.third)
                Spacer(modifier = Modifier.padding(10.dp))
            }
        }
    }
}

@Composable
fun TextBox(text: String = "hello",
            @DrawableRes image: Int=R.drawable.google_logo,
            onClick : ()-> Unit) {


    Row(
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF0F1B3D).copy(alpha = 0.9f), // deep blue
                        Color(0xFF547298).copy(alpha = 0.7f), // light blue
                        Color(0xFF62A1DF).copy(alpha = 0.7f)  // sky blue
                    )
                ),

                shape = RoundedCornerShape(16.dp)
            )
            .padding(20.dp)
            .clickable(onClick = onClick)
    ) {

        Image(
            painter = painterResource(image),
            contentDescription = null,
            modifier = Modifier.size(60.dp)
        )

        Spacer(Modifier.width(12.dp))

        Text(
            text = text,
            fontSize = 32.sp,
            color = Color.White
        )
    }

}