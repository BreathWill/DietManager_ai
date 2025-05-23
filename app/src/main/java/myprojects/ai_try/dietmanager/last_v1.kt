package myprojects.ai_try.dietmanager

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.ai.client.generativeai.GenerativeModel
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import myprojects.ai_try.dietmanager.api_key.api_key_kt

class last_v1 {


//    package myprojects.ai_try.dietmanager
//
//    import android.os.Bundle
//    import android.util.Log
//    import androidx.activity.ComponentActivity
//    import androidx.activity.compose.setContent
//    import androidx.compose.foundation.clickable
//    import androidx.compose.foundation.layout.Column
//    import androidx.compose.foundation.layout.Row
//    import androidx.compose.foundation.layout.fillMaxWidth
//    import androidx.compose.foundation.layout.padding
//    import androidx.compose.foundation.lazy.LazyColumn
//    import androidx.compose.foundation.lazy.items
//    import androidx.compose.material3.Button
//    import androidx.compose.material3.Checkbox
//    import androidx.compose.material3.MaterialTheme
//    import androidx.compose.material3.OutlinedTextField
//    import androidx.compose.material3.Text
//    import androidx.compose.runtime.Composable
//    import androidx.compose.runtime.getValue
//    import androidx.compose.runtime.mutableStateOf
//    import androidx.compose.runtime.remember
//    import androidx.compose.runtime.setValue
//    import androidx.compose.ui.Alignment
//    import androidx.compose.ui.Modifier
//    import androidx.compose.ui.text.font.FontStyle
//    import androidx.compose.ui.text.font.FontWeight
//    import androidx.compose.ui.tooling.preview.Preview
//    import androidx.compose.ui.unit.dp
//    import androidx.compose.ui.unit.sp
//    import com.google.ai.client.generativeai.GenerativeModel
//    import com.google.gson.Gson
//    import kotlinx.coroutines.CoroutineScope
//    import kotlinx.coroutines.Dispatchers
//    import kotlinx.coroutines.launch
//    import kotlinx.coroutines.withContext
//    import myprojects.ai_try.dietmanager.api_key.api_key_kt
//    class MainActivity : ComponentActivity() {
//        override fun onCreate(savedInstanceState: Bundle?) {
//            super.onCreate(savedInstanceState)
//            setContent {
//                MaterialTheme {
//                    DietPlannerScreen()
//                }
//            }
//        }
//    }
//
//    // --- Моделі ---
//    data class MealItem(val name: String, val recipe: String? = null, val note: String? = null)
//    data class ProductItem(val name: String, val amount: String)
//    data class DietPlan(
//        val breakfast: List<MealItem> = emptyList(),
//        val lunch: List<MealItem> = emptyList(),
//        val dinner: List<MealItem> = emptyList(),
//        val snack: List<MealItem> = emptyList(),
//        val shoppingList: List<ProductItem> = emptyList()
//    )
//
//    // --- Основний екран ---
//    @Composable
//    fun DietPlannerScreen() {
//        var input by remember { mutableStateOf("") }
//        var resultJson by remember { mutableStateOf("") }
//        var plan by remember { mutableStateOf<DietPlan?>(null) }
//
//        Column(modifier = Modifier.padding(16.dp)) {
//            OutlinedTextField(
//                value = input,
//                onValueChange = { input = it },
//                label = { Text("Опиши дієту") },
//                modifier = Modifier.fillMaxWidth()
//            )
//
//            Button(
//                onClick = {
//                    fetchDiet(input) { json ->
//                        resultJson = json
//                        plan = Gson().fromJson(json, DietPlan::class.java)
//                    }
//                },
//                modifier = Modifier.padding(top = 8.dp)
//            ) {
//                Text("Згенерувати")
//            }
//
//            plan?.let {
//                LazyColumn(modifier = Modifier.padding(top = 16.dp)) {
//                    item { Text("Сніданок", fontWeight = FontWeight.Bold) }
//                    items(items = it.breakfast, key = { it.name }) { meal -> ExpandableMealItem(meal) }
//
//                    item { Text("Обід", fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp)) }
//                    items(items = it.lunch, key = { it.name }) { meal -> ExpandableMealItem(meal) }
//
//                    item { Text("Вечеря", fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp)) }
//                    items(items = it.dinner, key = { it.name }) { meal -> ExpandableMealItem(meal) }
//
//                    item { Text("Перекус", fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp)) }
//                    items(items = it.snack, key = { it.name }) { meal -> ExpandableMealItem(meal) }
//
//                    item { Text("\uD83D\uDED2 Список покупок", fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 16.dp)) }
//                    items(items = it.shoppingList, key = { it.name }) { product ->
//                        var checked by remember { mutableStateOf(false) }
//                        Row(
//                            verticalAlignment = Alignment.CenterVertically,
//                            modifier = Modifier.fillMaxWidth()
//                        ) {
//                            Checkbox(checked = checked, onCheckedChange = { checked = it })
//                            Text("${product.name} — ${product.amount}")
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//    @Composable
//    fun ExpandableMealItem(meal: MealItem) {
//        var expanded by remember { mutableStateOf(false) }
//        Column(modifier = Modifier
//            .fillMaxWidth()
//            .clickable { expanded = !expanded }
//            .padding(4.dp)) {
//            Text(meal.name, fontWeight = FontWeight.SemiBold)
//            if (expanded) {
//                meal.recipe?.let { Text("Рецепт: $it", fontSize = 12.sp) }
//                meal.note?.let { Text("Примітка: $it", fontSize = 12.sp, fontStyle = FontStyle.Italic) }
//            }
//        }
//    }
//
//    // --- Запит до Gemini ---
//    fun fetchDiet(query: String, onResult: (String) -> Unit) {
//        val finalPrompt = """
//        Створи меню на день та список продуктів для покупки для дієти:
//        "$query".
//        Формат JSON:
//        {
//            "breakfast": [{"name": "...", "recipe": "...", "note": "..."}],
//            "lunch": [...],
//            "dinner": [...],
//            "snack": [...],
//            "shoppingList": [{"name": "...", "amount": "..."}]
//        }
//    """.trimIndent()
//        val api = api_key_kt()
//
//        val generativeModel = GenerativeModel(
//            modelName = "models/gemini-1.5-flash",
//            apiKey = api.get_key()
//        )
//
//        CoroutineScope(Dispatchers.IO).launch {
//            try {
//                Log.d("Gemini", "Запит сформовано: $finalPrompt")
//                val response = generativeModel.generateContent(finalPrompt)
//                val text = response.text ?: return@launch
//                Log.d("Gemini", "Отримано відповідь: $text")
//
//                val cleaned = text
//                    .removePrefix("```json").removePrefix("```").trim()
//                    .removeSuffix("```").trim()
//
//                val jsonStart = cleaned.indexOfFirst { it == '{' }
//                val jsonEnd = cleaned.indexOfLast { it == '}' }
//
//                if (jsonStart != -1 && jsonEnd != -1 && jsonEnd > jsonStart) {
//                    val cleanJson = cleaned.substring(jsonStart, jsonEnd + 1)
//                    Log.d("Gemini", "Очищений JSON: $cleanJson")
//                    withContext(Dispatchers.Main) {
//                        onResult(cleanJson)
//                    }
//                } else {
//                    Log.e("Gemini", "Не вдалося знайти JSON в очищеному тексті")
//                    withContext(Dispatchers.Main) {
//                        onResult(text)
//                    }
//                }
//            } catch (e: Exception) {
//                Log.e("Gemini", "Помилка: ${e.message}")
//            }
//        }
//    }
//
//    @Composable
//    @Preview
//    fun AppPreview() {
//        MaterialTheme {
//            DietPlannerScreen()
//        }
//    }







}