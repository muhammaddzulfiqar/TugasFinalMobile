package com.tugas.todoapp

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tugas.todoapp.database.Todo
import com.tugas.todoapp.databinding.ListItemBinding
import org.w3c.dom.Text
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


class TodoAdapter(private val viewModel: TodoViewModel) :
    ListAdapter<Todo, TodoAdapter.MyViewHolder>(TodoDiffCallBack()) {
    private var list = listOf<Todo>()



    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TodoAdapter.MyViewHolder {

        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemBinding.inflate(inflater)

        return MyViewHolder(binding)
    }

    @SuppressLint("InflateParams")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.todoText.text = getItem(position).task
        holder.titleText.text = getItem(position).title
        holder.dateCrt.text = getItem(position).date
        holder.deadDate.text = getItem(position).deadlineDate
        holder.deadTime.text = getItem(position).deadlineTime
        //Menghapus
        holder.btnDel.setOnClickListener {
            viewModel.removeTodo(getItem(holder.adapterPosition))
        }
        //Edit
        holder.btnEdit.setOnClickListener {
            val context = holder.itemView.context
            val inflater = LayoutInflater.from(context)
            val view = inflater.inflate(R.layout.edit_layout,null)

        //Menganmbil data
            val prevTitle =getItem(position).title
            val prevTask = getItem(position).task
            val prevDate = getItem(position).date
            val prevDeadDate = getItem(position).deadlineDate
            val prevDeadTime = getItem(position).deadlineTime
            val editTitle = view.findViewById<TextView>(R.id.editTitle)
            editTitle.text = prevTitle
            val editTask = view.findViewById<TextView>(R.id.editTask)
            editTask.text = prevTask
            val updateBtn = view.findViewById<TextView>(R.id.add_btn)
            val cancelBtn = view.findViewById<TextView>(R.id.cancel1_btn)
            val dateUpdate = view.findViewById<TextView>(R.id.date_update)
            dateUpdate.text = prevDate
            //Edit time & date
            val editDeadDate = view.findViewById<TextView>(R.id.editDate)
            val editDeadTime = view.findViewById<TextView>(R.id.editTime)
            val deadDateBtn = view.findViewById<Button>(R.id.btn_date)
            val deadTimeBtn = view.findViewById<Button>(R.id.btn_time)
            editDeadDate.text = prevDeadDate
            editDeadTime.text = prevDeadTime
            val calendar = Calendar.getInstance()
            val simpleFormat = SimpleDateFormat("hh:mm:ss a")
            val time = simpleFormat.format(calendar.time)
            val currentDate = DateFormat.getDateInstance(DateFormat.DEFAULT).format(calendar.time)
            dateUpdate.setText("Updated : $currentDate $time")

            //Deadline Date& Time Dialog
            val formatdate = SimpleDateFormat("MMM dd,yyyy ")
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            val month = calendar.get(Calendar.MONTH)
            val year   = calendar.get(Calendar.YEAR)
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)
            deadDateBtn.setOnClickListener {
                val datepd = DatePickerDialog(context,
                    DatePickerDialog.OnDateSetListener { view, mYear, mMonth, dayOfMonth ->
                    calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth)
                    calendar.set(Calendar.MONTH,mMonth)
                    calendar.set(Calendar.YEAR,mYear)
                    var dateDead = formatdate.format(calendar.time)
                    editDeadDate.setText("Deadline at $dateDead ")
                },year,month,day).show()

            }
            deadTimeBtn.setOnClickListener {
                val timePd = TimePickerDialog(context,
                    TimePickerDialog.OnTimeSetListener { view, hourOfDay, mMinute ->
                    calendar.set(Calendar.HOUR_OF_DAY,hourOfDay)
                    calendar.set(Calendar.MINUTE,mMinute)
                    val timeDead = simpleFormat.format(calendar.time)
                    editDeadTime.setText("$timeDead")
                },hour,minute,false).show()
            }
            //Membuat dial9og
            val alertDialog = AlertDialog.Builder(context).setView(view).show()
                updateBtn.setOnClickListener {
                    val editedTitle = editTitle.text.toString()
                    val editedTask = editTask.text.toString()
                    val editedDate = dateUpdate.text.toString()
                    val editedDeadDate = editDeadDate.text.toString()
                    val editedDeadTime = editDeadTime.text.toString()
                    getItem(holder.adapterPosition).title = editedTitle
                    getItem(holder.adapterPosition).task = editedTask
                    getItem(holder.adapterPosition).date = editedDate
                    getItem(holder.adapterPosition).deadlineDate = editedDeadDate
                    getItem(holder.adapterPosition).deadlineTime = editedDeadTime

                    viewModel.updateTodo(getItem(holder.adapterPosition))
                    holder.titleText.text = editedTitle
                    holder.todoText.text = editedTask
                    holder.dateCrt.text = editedDate
                    holder.deadDate.text = editedDeadDate
                    holder.deadTime.text = editedDeadTime
                    alertDialog.dismiss()
                }
                cancelBtn.setOnClickListener{
                    alertDialog.dismiss()
                }
            alertDialog.create()
        }
    }

  // override fun getItemCount() = viewModel.todos.value!!.size
    class MyViewHolder(val binding:ListItemBinding ) : RecyclerView.ViewHolder(binding.root) {

          val titleText = binding.titleText
          val todoText = binding.taskText
          val btnDel = binding.btnDel
          val btnEdit = binding.btnEdit
          val dateCrt = binding.createDate
          val deadDate = binding.deadDateTextRecy
          val deadTime = binding.deadlineTextTimeRecy1
      }
    }

    class TodoDiffCallBack:DiffUtil.ItemCallback<Todo>(){
        override fun areItemsTheSame(oldItem: Todo, newItem: Todo): Boolean {
            return  oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Todo, newItem: Todo): Boolean {
            return oldItem.equals(newItem)
        }


    }

