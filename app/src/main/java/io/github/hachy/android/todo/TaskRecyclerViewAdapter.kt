package io.github.hachy.android.todo

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import io.github.hachy.android.todo.databinding.HeaderTaskBinding
import io.github.hachy.android.todo.databinding.RowTaskBinding
import io.github.hachy.android.todo.room.Task


class TaskRecyclerViewAdapter(
        private var tasks: MutableList<Task>,
        private val listener: OnRecyclerItemClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface OnRecyclerItemClickListener {
        fun onCheckBoxClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 0) {
            val view = DataBindingUtil.inflate<RowTaskBinding>(LayoutInflater.from(parent?.context), R.layout.row_task, parent, false)
            ContentViewHolder(view)
        } else {
            val view = DataBindingUtil.inflate<HeaderTaskBinding>(LayoutInflater.from(parent?.context), R.layout.header_task, parent, false)
            HeaderViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        val task = tasks[position]
        if (holder?.itemViewType == 0) {
            val cvh = holder as ContentViewHolder
            cvh.binding.task = task
            cvh.binding.executePendingBindings()
            cvh.binding.checkBox.setOnClickListener {
                listener.onCheckBoxClick(holder.adapterPosition)
            }
        } else {
            val hvh = holder as HeaderViewHolder
            hvh.binding.task = task
            hvh.binding.executePendingBindings()
        }
    }

    override fun getItemCount(): Int {
        return tasks.size
    }

    override fun getItemViewType(position: Int): Int {
        super.getItemViewType(position)
        return tasks[position].viewType
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

    inner class ContentViewHolder constructor(val binding: RowTaskBinding) : RecyclerView.ViewHolder(binding.root)

    inner class HeaderViewHolder constructor(val binding: HeaderTaskBinding) : RecyclerView.ViewHolder(binding.root)
}
