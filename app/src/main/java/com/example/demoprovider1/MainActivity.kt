package com.example.demoprovider1

import android.content.ContentValues
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.demoprovider1.ui.theme.DemoProvider1Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DemoProvider1Theme  {
                Surface(modifier = Modifier.fillMaxSize()) {
                    WordManagerScreen()
                }
            }
        }
    }
}

@Composable
fun WordManagerScreen() {
    val context = LocalContext.current
    val contentResolver = context.contentResolver

    var word by remember { mutableStateOf("") }
    var mean by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("") }
    var wordsList by remember { mutableStateOf(listOf<String>()) }

    fun loadWords() {
        val cursor = contentResolver.query(WordProvider.CONTENT_URI, null, null, null, null)
        val words = mutableListOf<String>()

        cursor?.use {
            val colWord = it.getColumnIndex("word")
            val colMean = it.getColumnIndex("mean")
            val colType = it.getColumnIndex("type")

            while (it.moveToNext()) {
                val w = it.getString(colWord)
                val m = it.getString(colMean)
                val t = it.getString(colType)
                words.add("$w - $m [$t]")
            }
        }

        wordsList = words
    }

    fun insertWord() {
        val values = ContentValues().apply {
            put("word", word)
            put("mean", mean)
            put("type", type)
        }
        contentResolver.insert(WordProvider.CONTENT_URI, values)
        loadWords()
        word = ""
        mean = ""
        type = ""
    }

    fun deleteWord() {
        val selection = "word = ?"
        val args = arrayOf(word)
        contentResolver.delete(WordProvider.CONTENT_URI, selection, args)
        loadWords()
    }

    LaunchedEffect(Unit) {
        loadWords()
    }

    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(value = word, onValueChange = { word = it }, label = { Text("Từ tiếng Anh") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = mean, onValueChange = { mean = it }, label = { Text("Nghĩa tiếng Việt") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = type, onValueChange = { type = it }, label = { Text("Loại từ") }, modifier = Modifier.fillMaxWidth())

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Button(onClick = { insertWord() }) { Text("Thêm") }
            Button(onClick = { deleteWord() }) { Text("Xoá") }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Danh sách từ vựng:", style = MaterialTheme.typography.titleMedium)
        wordsList.forEach { Text("• $it") }
    }
}