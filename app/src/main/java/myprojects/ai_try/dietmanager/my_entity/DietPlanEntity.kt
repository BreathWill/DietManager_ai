package myprojects.ai_try.dietmanager.my_entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "diet_plans")
data class DietPlanEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val description: String, // Ввід користувача
    val json: String         // JSON згенерованої дієти
)
