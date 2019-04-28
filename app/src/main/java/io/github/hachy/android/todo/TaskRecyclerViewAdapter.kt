package io.github.hachy.android.todo

import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import io.github.hachy.android.todo.room.Task
import kotlinx.android.synthetic.main.row_task.view.*


class TaskRecyclerViewAdapter(
        private var tasks: MutableList<Task>,
        private val listener: OnRecyclerItemClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface OnRecyclerItemClickListener {
        fun onCheckBoxClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 0) {
            val view = DataBindingUtil.inflate<ViewDataBinding>(LayoutInflater.from(parent.context), R.layout.row_task, parent, false)
            ContentViewHolder(view)
        } else {
            val view = DataBindingUtil.inflate<ViewDataBinding>(LayoutInflater.from(parent.context), R.layout.header_task, parent, false)
            HeaderViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val task = tasks[position]
        if (holder.itemViewType == 0) {
            val cvh = holder as ContentViewHolder
            cvh.binding.setVariable(BR.task, task)
            cvh.binding.executePendingBindings()
            cvh.binding.root.checkBox.setOnClickListener {
                listener.onCheckBoxClick(holder.adapterPosition)
            }
        } else {
            val hvh = holder as HeaderViewHolder
            hvh.binding.setVariable(BR.task, task)
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

    fun reInsertItem(pos: Int, task: Task) {
        tasks.add(pos, task)
        notifyItemInserted(pos)
    }

    fun checkItem(task: Task) {
        task.completed = !task.completed
    }

    inner class ContentViewHolder constructor(val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root)

    inner class HeaderViewHolder constructor(val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root)
}
