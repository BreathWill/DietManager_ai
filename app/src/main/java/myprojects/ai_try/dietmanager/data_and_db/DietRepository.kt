package myprojects.ai_try.dietmanager.data_and_db

import myprojects.ai_try.dietmanager.my_entity.DietPlanEntity

class DietRepository(private val dao: DietPlanDao) {
    fun getAllPlans() = dao.getAllPlans()
    suspend fun savePlan(plan: DietPlanEntity) = dao.insertPlan(plan)
    suspend fun deleteAll() = dao.deleteAll()
}