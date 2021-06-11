package com.tugas.todoapp

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.tugas.todoapp.database.Todo
import com.tugas.todoapp.database.TodoDAO
import com.tugas.todoapp.database.TodoDatabase
import com.tugas.todoapp.database.TodoRepo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class TodoViewModel(application: Application) :AndroidViewModel(application) {
    //add repo
    private val repository: TodoRepo
    private val todoDao: TodoDAO
    private var _todos: LiveData<List<Todo>>
    val todos: LiveData<List<Todo>>
        get() = _todos

    //coroutine
    private var vmJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.IO + vmJob)

    init {
        todoDao = TodoDatabase.getInstance(application).todoDao()
        repository = TodoRepo(todoDao)
        _todos = repository.allTodos
    }

    fun createDoes(
        text: String,
        text1: String,
        text2: String,
        text3: String,
        text4: String
    ) {
        uiScope.launch {
            repository.insertTodo(Todo(0, text, text1, text2, text3, text4))
        }
    }

    fun getData():LiveData<List<Todo>>{
        return  todos
    }

    fun removeTodo(todo: Todo) {
        uiScope.launch {
            repository.deleteTodo(todo)
        }
    }

    fun updateTodo(todo: Todo) {
        uiScope.launch {
            repository.updateTodo(todo)
        }
    }

    fun deadlineDesc():LiveData<List<Todo>>? {
        return repository.deadlineDesc()
    }

    fun deadlineAsc():LiveData<List<Todo>>? {
        return repository.deadlineAsc()
    }

    fun dateDesc():LiveData<List<Todo>>? {
        return repository.dateDesc()
    }

    fun dateAsc():LiveData<List<Todo>>? {
        return repository.dateAsc()
    }
    fun search(title: String):LiveData<List<Todo>>?{
        return repository.search(title)
    }
    override fun onCleared() {
        super.onCleared()
        vmJob.cancel()
    }
}


