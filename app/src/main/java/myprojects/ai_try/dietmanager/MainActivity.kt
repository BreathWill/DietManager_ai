package myprojects.ai_try.dietmanager

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
import myprojects.ai_try.dietmanager.data_and_db.AppDatabase
import myprojects.ai_try.dietmanager.my_entity.DietPlanEntity
import myprojects.ai_try.dietmanager.my_view_models.DietViewModel
import myprojects.ai_try.dietmanager.my_view_models.DietViewModelFactory
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                DietPlannerScreen()
            }
        }
    }
}

// --- Моделі ---
data class MealItem(val name: String, val recipe: String? = null, val note: String? = null)
data class ProductItem(val name: String, val amount: String)
data class DietPlan(
    val breakfast: List<MealItem> = emptyList(),
    val lunch: List<MealItem> = emptyList(),
    val dinner: List<MealItem> = emptyList(),
    val snack: List<MealItem> = emptyList(),
    val shoppingList: List<ProductItem> = emptyList()
)

// --- Основний екран ---



@Composable
fun DietPlannerScreen() {
    val context = LocalContext.current.applicationContext

    val viewModel: DietViewModel = viewModel(
        factory = DietViewModelFactory(
            AppDatabase.getDatabase(context).dietPlanDao()
        )
    )

    var input by remember { mutableStateOf("") }
    val savedPlans by viewModel.savedPlans.collectAsState()

    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = input,
            onValueChange = { input = it },
            label = { Text("Опиши дієту") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                fetchDiet(input) { json ->
                    viewModel.saveDiet(input, json)
                }
            },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Згенерувати")
        }

        LazyColumn(modifier = Modifier.padding(top = 16.dp)) {
            itemsIndexed(savedPlans) { index, planEntity ->
                val plan = Gson().fromJson(planEntity.json, DietPlan::class.java)
                ExpandablePlanItem(plan, index)
            }
        }
    }
}


@Composable
fun ExpandablePlanItem(plan: DietPlan, index: Int) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { expanded = !expanded }
            .background(Color(0xFFF0F0F0), shape = RoundedCornerShape(8.dp))
            .padding(8.dp)
            .animateContentSize()
    ) {
        Text("Меню #${index + 1}", fontWeight = FontWeight.Bold)

        if (expanded) {
            MealSection("Сніданок", plan.breakfast)
            MealSection("Обід", plan.lunch)
            MealSection("Вечеря", plan.dinner)
            MealSection("Перекус", plan.snack)

            Text("\uD83D\uDED2 Список покупок", fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
            plan.shoppingList.forEach { product ->
                var checked by remember { mutableStateOf(false) }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(checked = checked, onCheckedChange = { checked = it })
                    Text("${product.name} — ${product.amount}")
                }
            }
        }
    }
}

@Composable
fun MealSection(title: String, meals: List<MealItem>) {
    Text(title, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
    meals.forEach { meal ->
        ExpandableMealItem(meal)
    }
}

@Composable
fun ExpandableMealItem(meal: MealItem) {
    var expanded by remember { mutableStateOf(false) }
    Column(modifier = Modifier
        .fillMaxWidth()
        .clickable { expanded = !expanded }
        .padding(4.dp)) {
        Text(meal.name, fontWeight = FontWeight.SemiBold)
        if (expanded) {
            meal.recipe?.let { Text("Рецепт: $it", fontSize = 12.sp) }
            meal.note?.let { Text("Примітка: $it", fontSize = 12.sp, fontStyle = FontStyle.Italic) }
        }
    }
}

// --- Запит до Gemini ---
fun fetchDiet(query: String, onResult: (String) -> Unit) {
    val finalPrompt = """
        Створи меню на день та список продуктів для покупки для дієти:
        "$query".
        Формат JSON:
        {
            "breakfast": [{"name": "...", "recipe": "...", "note": "..."}],
            "lunch": [...],
            "dinner": [...],
            "snack": [...],
            "shoppingList": [{"name": "...", "amount": "..."}]
        }
    """.trimIndent()
    val api = api_key_kt()

    val generativeModel = GenerativeModel(
        modelName = "models/gemini-1.5-flash",
        apiKey = api.get_key()
    )

    CoroutineScope(Dispatchers.IO).launch {
        try {
            Log.d("Gemini", "Запит сформовано: $finalPrompt")
            val response = generativeModel.generateContent(finalPrompt)
            val text = response.text ?: return@launch
            Log.d("Gemini", "Отримано відповідь: $text")

            val cleaned = text
                .removePrefix("```json").removePrefix("```").trim()
                .removeSuffix("```").trim()

            val jsonStart = cleaned.indexOfFirst { it == '{' }
            val jsonEnd = cleaned.indexOfLast { it == '}' }

            if (jsonStart != -1 && jsonEnd != -1 && jsonEnd > jsonStart) {
                val cleanJson = cleaned.substring(jsonStart, jsonEnd + 1)
                Log.d("Gemini", "Очищений JSON: $cleanJson")
                withContext(Dispatchers.Main) {
                    onResult(cleanJson)
                }
            } else {
                Log.e("Gemini", "Не вдалося знайти JSON в очищеному тексті")
                withContext(Dispatchers.Main) {
                    onResult(text)
                }
            }
        } catch (e: Exception) {
            Log.e("Gemini", "Помилка: ${e.message}")
        }
    }
}

@Composable
@Preview
fun AppPreview() {
    MaterialTheme {
        DietPlannerScreen()
    }
}


@Composable
fun DietPlannerScreen2() {
    var input by remember { mutableStateOf("") }
    var resultJson by remember { mutableStateOf("") }
    var plans by remember { mutableStateOf(listOf<DietPlan>()) }

    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = input,
            onValueChange = { input = it },
            label = { Text("Опиши дієту") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                fetchDiet(input) { json ->
                    resultJson = json
                    val newPlan = Gson().fromJson(json, DietPlan::class.java)
                    plans = plans + newPlan
                }
            },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Згенерувати")
        }

        LazyColumn(modifier = Modifier.padding(top = 16.dp)) {
            itemsIndexed(plans) { index, plan ->
                ExpandablePlanItem(plan, index)
            }
        }
    }
}


// або якщо залишив insertPlan:
// val entity = DietPlanEntity(description = input, json = json)
// viewModel.insertPlan(entity)