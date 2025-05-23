package myprojects.ai_try.dietmanager.my_view_models

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import myprojects.ai_try.dietmanager.data_and_db.AppDatabase
import myprojects.ai_try.dietmanager.data_and_db.DietPlanDao
import myprojects.ai_try.dietmanager.data_and_db.DietRepository
import myprojects.ai_try.dietmanager.my_entity.DietPlanEntity

class DietViewModel(private val dao: DietPlanDao) : ViewModel() {

    private val repository = DietRepository(dao)

    val savedPlans = repository.getAllPlans()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
    init {
        viewModelScope.launch {
            savedPlans.collect {
                Log.d("DietViewModel", "Updated savedPlans: ${it.size} plans")

                it.forEach { plan ->
                    Log.d(
                        "DietViewModel",
                        "Plan id=${plan.id}, description=${plan.description.take(30)}"
                    )
                }
            }
        }
    }
    fun saveDiet(description: String, json: String) {
        viewModelScope.launch {
            Log.d("Diet", "Зберігаю нову дієту: $description/n $json")
            repository.savePlan(
                DietPlanEntity(
                    id = 0, // або просто не вказуй, якщо є конструктор без нього
                    description = description,
                    json = json
                )
            )
        }
    }
    fun insertPlan(plan: DietPlanEntity) {
        viewModelScope.launch {
            repository.savePlan(plan)
        }
    }
    fun clearAll() {
        viewModelScope.launch {
            repository.deleteAll()
        }
    }
}
class DietViewModelFactory(private val dao: DietPlanDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return DietViewModel(dao) as T
    }
}
