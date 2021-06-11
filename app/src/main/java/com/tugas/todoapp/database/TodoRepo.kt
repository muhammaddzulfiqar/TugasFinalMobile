package com.tugas.todoapp.database

import androidx.lifecycle.LiveData

class TodoRepo(private val TodoDao:TodoDAO) {
    val allTodos = TodoDao.loadTodo()

    suspend fun insertTodo(todo: Todo){
        TodoDao.insert(todo)
    }
    suspend fun deleteTodo(todo: Todo) {
        TodoDao.delete(todo)
    }
    suspend fun updateTodo(todo: Todo){
        TodoDao.update(todo)
    }

    fun deadlineDesc() : LiveData<List<Todo>>?{
        return TodoDao?.deadlineDesc()
    }

    fun deadlineAsc() : LiveData<List<Todo>>?{
        return TodoDao?.deadlineAsc()
    }

    fun dateDesc() : LiveData<List<Todo>>?{
        return TodoDao?.dateDesc()
    }

    fun dateAsc() : LiveData<List<Todo>>?{
        return TodoDao?.dateAsc()
    }
    fun search(title: String) : LiveData<List<Todo>>?{
        return TodoDao?.search(title)
    }


}