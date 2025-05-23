package myprojects.ai_try.dietmanager.data_and_db

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import myprojects.ai_try.dietmanager.my_entity.DietPlanEntity

@Dao
interface DietPlanDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlan(plan: DietPlanEntity)

    @Query("SELECT * FROM diet_plans ORDER BY id")
    fun getAllPlans(): Flow<List<DietPlanEntity>>

    @Query("DELETE FROM diet_plans")
    suspend fun deleteAll()
}
