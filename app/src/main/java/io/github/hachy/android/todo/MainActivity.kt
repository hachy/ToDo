package io.github.hachy.android.todo

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import kotlinx.android.synthetic.main.activity_main.*
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.text.TextUtils
import io.github.hachy.android.todo.MyApplication.Companion.database
import io.github.hachy.android.todo.room.Task
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var imm: InputMethodManager
    private lateinit var adapter: TaskRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager

        fab.setOnClickListener { showKeyboard() }
        hideKeyboardBtn.setOnClickListener { hideKeyboard() }

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)

        loadAllTasks()

        addTaskBtn.setOnClickListener {
            val content = "${editTask.text}"
            if (!TextUtils.isEmpty(content)) {
                val new = Task(content = content, created_at = Date())
                addTask(new)
            }
        }

        itemTouchHelper().attachToRecyclerView(recyclerView)
    }

    private fun loadAllTasks() =
            database.taskDao().loadAll()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        adapter = TaskRecyclerViewAdapter(it as MutableList<Task>)
                        recyclerView.adapter = adapter
                        adapter.notifyDataSetChanged()
                    }

    private fun addTask(new: Task) =
            Observable.fromCallable { database.taskDao().insertTask(new) }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        adapter.addItem(new)
                        loadAllTasks()
                        recyclerView.smoothScrollToPosition(adapter.itemCount)
                        editTask.text.clear()
                    }

    private fun itemTouchHelper() =
            ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN, ItemTouchHelper.LEFT) {
                override fun onMove(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                    val fromPos = viewHolder.adapterPosition
                    val toPos = target.adapterPosition
                    adapter.swapItems(fromPos, toPos)
                    swapTasks(fromPos, toPos)
                    return true
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int) {
                    val pos = viewHolder?.adapterPosition
                    val task = pos?.let { adapter.getItem(pos) }
                    deleteTask(task, pos)
                }

                override fun clearView(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?) {
                    super.clearView(recyclerView, viewHolder)
                    // reallyMoved
                    database.taskDao().loadAll()
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe()
                }
            })

    private fun swapTasks(from: Int, pos: Int) {
        val f = adapter.getItem(from)
        val t = adapter.getItem(pos)
        val temp = f.created_at
        f.created_at = t.created_at
        t.created_at = temp

        Observable.fromCallable {
            database.taskDao().updateTask(f)
            database.taskDao().updateTask(t)
        }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
    }

    private fun deleteTask(task: Task?, pos: Int?) =
            Observable.fromCallable { task?.let { database.taskDao().deleteTask(it) } }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        pos?.let { p -> adapter.removeItem(p) }
                    }

    private fun showKeyboard() {
        editLayout.visibility = View.VISIBLE
        editTask.isFocusableInTouchMode = true
        editTask.isFocusable = true
        editTask.requestFocus()
        imm.showSoftInput(editTask, InputMethodManager.SHOW_IMPLICIT)
        fab.animate().scaleX(0F).scaleY(0F).setDuration(500).start()
    }

    private fun hideKeyboard() {
        fab.animate().scaleX(1F).scaleY(1F).setDuration(500).start()
        editLayout.visibility = View.GONE
        if (currentFocus != null) {
            imm.hideSoftInputFromWindow(currentFocus.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }
}
