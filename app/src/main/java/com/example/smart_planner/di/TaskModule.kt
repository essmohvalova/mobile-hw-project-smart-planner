package com.example.smart_planner.di

import com.example.smart_planner.data.models.tasks.TaskEntity
import com.example.smart_planner.data.repository.tasks.TaskRepository
import com.example.smart_planner.data.repository.tasks.TaskRepositoryImpl
import com.example.smart_planner.domain.models.tasks.Task
import com.example.smart_planner.domain.models.tasks.Priority
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TaskModule {

    @Binds
    @Singleton
    abstract fun bindTaskRepository(
        taskRepositoryImpl: TaskRepositoryImpl
    ): TaskRepository
}

// Если нужны дополнительные провайдеры
@Module
@InstallIn(SingletonComponent::class)
object TaskModuleProviders {

    @Provides
    @Singleton
    fun provideTaskMapper(): TaskMapper {
        return TaskMapper()
    }
}

// Маппер для преобразования между слоями
class TaskMapper {

    fun mapEntityToDomain(entity: TaskEntity): Task {
        return entity.toDomain()
    }

    fun mapDomainToEntity(domain: Task): TaskEntity {
        return TaskEntity.fromDomain(domain)
    }

    fun mapEntityListToDomain(entities: List<TaskEntity>): List<Task> {
        return entities.map { it.toDomain() }
    }

    fun mapDomainListToEntity(domains: List<Task>): List<TaskEntity> {
        return domains.map { TaskEntity.fromDomain(it) }
    }
}