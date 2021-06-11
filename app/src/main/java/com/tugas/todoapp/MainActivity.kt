package com.tugas.todoapp

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tugas.todoapp.databinding.ActivityMainBinding
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: TodoAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var viewModel: TodoViewModel


    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val binding: ActivityMainBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_main)
        viewModel = ViewModelProviders.of(this).get(TodoViewModel::class.java)

        viewManager = LinearLayoutManager(this)
        viewAdapter = TodoAdapter(viewModel)

        recyclerView = binding.myRecyclerView
        recyclerView.apply {
            layoutManager = viewManager
            adapter = viewAdapter


        }
        val btnSort = binding.sortBtn
        val btnNew = binding.btnNew
        val inflater1 = LayoutInflater.from(this)
        val view1 = inflater1.inflate(R.layout.activity_main, null)


        //Deadline


        // Add data
        btnNew.setOnClickListener {
            val inflater = LayoutInflater.from(this)
            val view = inflater.inflate(R.layout.new_layout, null)
            val newTitle = view.findViewById<TextView>(R.id.editTitle)
            val newTask = view.findViewById<TextView>(R.id.editTask)
            val addBtn = view.findViewById<TextView>(R.id.add_btn)
            val cancelBtn = view.findViewById<TextView>(R.id.cancel1_btn)
            val dateCreate = view.findViewById<TextView>(R.id.date_new)
            val deadDateText = view.findViewById<TextView>(R.id.dead_date_text)
            val deadTimeText = view.findViewById<TextView>(R.id.dead_time_text)
            val deadTime = view.findViewById<TextView>(R.id.deadline_time)
            val deadDate = view.findViewById<TextView>(R.id.deadline)
            val calendar = Calendar.getInstance()
            val simpleFormat = SimpleDateFormat("hh:mm:ss a")
            val formatdate = SimpleDateFormat("MMM dd,yyyy ")
            val time = simpleFormat.format(calendar.time)
            val currentDate = DateFormat.getDateInstance(DateFormat.DEFAULT).format(calendar.time)
            dateCreate.setText("Created at $currentDate $time")

            val day = calendar.get(Calendar.DAY_OF_MONTH)
            val month = calendar.get(Calendar.MONTH)
            val year = calendar.get(Calendar.YEAR)
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)



            deadDate.setOnClickListener {
                val datepd = DatePickerDialog(
                    this,
                    DatePickerDialog.OnDateSetListener { view, mYear, mMonth, dayOfMonth ->
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                        calendar.set(Calendar.MONTH, mMonth)
                        calendar.set(Calendar.YEAR, mYear)
                        var dateDead = formatdate.format(calendar.time)
                        deadDateText.setText("Deadline at $dateDead ")
                    },
                    year,
                    month,
                    day
                ).show()

            }


            deadTime.setOnClickListener {
                val timePd = TimePickerDialog(
                    this,
                    TimePickerDialog.OnTimeSetListener { view, hourOfDay, mMinute ->
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                        calendar.set(Calendar.MINUTE, mMinute)
                        val timeDead = simpleFormat.format(calendar.time)
                        deadTimeText.setText("$timeDead")
                    },
                    hour,
                    minute,
                    false
                ).show()
            }


            //Dialog
            var alertDialog = AlertDialog.Builder(this).setView(view).show()
            //Dialog pick deadline

            addBtn.setOnClickListener {
                viewModel.createDoes(
                    newTitle.text.toString(),
                    newTask.text.toString(),
                    dateCreate.text.toString(),
                    deadDateText.text.toString(),
                    deadTimeText.text.toString()
                )
                alertDialog.dismiss()

            }
            cancelBtn.setOnClickListener {
                alertDialog.dismiss()
            }

            alertDialog.create()
        }
        btnSort.setOnClickListener {

            val items = arrayOf("Sort By Date Created", "Sort By Deadline")
            val builder =
                AlertDialog.Builder(this)
            val alert = AlertDialog.Builder(this)
            builder.setItems(items) { dialog, which ->
                // the user clicked on colors[which]
                when (which) {
                    0 -> {
                        alert.setTitle(items[which])
                            .setPositiveButton("Newest to Oldest") { dialog, which ->
                                viewModel.dateDesc()?.observe(this, Observer {
                                    viewAdapter.submitList(it)
                                })
                            }
                            .setNegativeButton("Oldest to Newest") { dialog, which ->
                                viewModel.dateAsc()?.observe(this, Observer {
                                    viewAdapter.submitList(it)
                                })
                            }
                        alert.show()

                    }
                    1 -> {
                        alert.setTitle(items[which])
                            .setPositiveButton("Newest to Oldest") { dialog, which ->
                                viewModel.deadlineDesc()?.observe(this, Observer {
                                    viewAdapter.submitList(it)
                                })
                            }
                            .setNegativeButton("Oldest to Newest") { dialog, which ->
                                viewModel.deadlineAsc()?.observe(this, Observer {
                                    viewAdapter.submitList(it)
                                })
                            }
                        alert.show()

                    }
                }
            }
            builder.show()
        }

            viewModel.getData().observe(this, Observer { list ->
                viewAdapter.submitList(list)
            })






    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu,menu)
        if (menu != null) {
            search(menu)
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item)
    }
    private fun search(menu:Menu){
        val item = menu?.findItem(R.id.search)

        val searchView = item?.actionView as androidx.appcompat.widget.SearchView?
        searchView?.isSubmitButtonEnabled = true

        searchView?.setOnQueryTextListener(
            object: androidx.appcompat.widget.SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(query: String?): Boolean {
                    if(query != null){
                        getItems(query)
                    }
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    if(newText != null){
                        getItems(newText)
                    }
                    return true
                }
            }
        )

    }

    private fun getItems(searchText: String){
        var searchText = searchText
        searchText = "%$searchText%"

        viewModel.search(searchText)?.observe(this, Observer {
            viewAdapter.submitList(it)
        })
    }

}


















