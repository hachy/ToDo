package io.github.hachy.android.todo

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import io.github.hachy.android.todo.databinding.RowTaskBinding
import io.github.hachy.android.todo.room.Task


class TaskRecyclerViewAdapter(
        private var tasks: MutableList<Task>,
        private val listener: OnRecyclerItemClickListener
) : RecyclerView.Adapter<TaskRecyclerViewAdapter.ViewHolder>() {

    interface OnRecyclerItemClickListener {
        fun onCheckBoxClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val view = DataBindingUtil.inflate<RowTaskBinding>(LayoutInflater.from(parent?.context), R.layout.row_task, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder?.binding?.task = tasks[position]
        holder?.binding?.executePendingBindings()
        holder?.binding?.checkBox?.setOnClickListener {
            listener.onCheckBoxClick(holder.adapterPosition)
        }
    }

    override fun getItemCount(): Int {
        return tasks.size
    }

    fun getItem(position: Int): Task {
        return tasks[position]
    }

    fun addItem(task: Task) {
        tasks.add(tasks.size, task)
        notifyItemInserted(tasks.size)
    }

    fun removeItem(position: Int) {
        tasks.removeAt(position)
        notifyItemRemoved(position)
    }

    fun swapItems(from: Int, to: Int) {
        tasks.add(to, tasks.removeAt(from))
        notifyItemMoved(from, to)
    }

    fun checkItem(task: Task) {
        task.completed = !task.completed
    }

    inner class ViewHolder constructor(val binding: RowTaskBinding) : RecyclerView.ViewHolder(binding.root)
}
