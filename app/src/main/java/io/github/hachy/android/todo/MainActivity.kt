package io.github.hachy.android.todo

import android.content.Context
import android.graphics.Canvas
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.view.View
import android.view.inputmethod.InputMethodManager
import kotlinx.android.synthetic.main.activity_main.*
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import io.github.hachy.android.todo.MyApplication.Companion.database
import io.github.hachy.android.todo.room.Task
import io.github.hachy.android.todo.room.TaskDao
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*
import android.graphics.Bitmap
import android.graphics.Paint


class MainActivity : AppCompatActivity() {

    private lateinit var adapter: TaskRecyclerViewAdapter
    private lateinit var taskDao: TaskDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        taskDao = database.taskDao()

        setSupportActionBar(toolbar)

        fab.setOnClickListener { showKeyboard() }

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)

        if (Prefs.isAsc) loadTasksAsc() else loadTasksDesc()

        addTaskBtn.setOnClickListener {
            val content = "${editTask.text}"
            if (TextUtils.isEmpty(content)) {
                editTask.error = getString(R.string.task_empty)
            } else {
                val new = Task(content = content, created_at = Date())
                addTask(new)
            }
        }

        itemTouchHelper().attachToRecyclerView(recyclerView)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.add_header, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.add_header -> {
                val dialog = HeaderDialogFragment()
                dialog.show(supportFragmentManager, "dialog")
                hideEditTask()
            }
            R.id.settings -> {
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPause() {
        super.onPause()
        hideEditTask()
    }

    override fun onBackPressed() {
        if (editLayout.visibility == View.VISIBLE) {
            hideEditTask()
            return
        }
        super.onBackPressed()
    }

    private fun loadTasksAsc() =
            taskDao.loadAsc()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        adapter = TaskRecyclerViewAdapter(it as MutableList<Task>, onRecyclerItemClickListener)
                        recyclerView.adapter = adapter
                        adapter.notifyDataSetChanged()
                    }

    private fun loadTasksDesc() =
            taskDao.loadDesc()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        adapter = TaskRecyclerViewAdapter(it as MutableList<Task>, onRecyclerItemClickListener)
                        recyclerView.adapter = adapter
                        adapter.notifyDataSetChanged()
                    }

    private val onRecyclerItemClickListener = object : TaskRecyclerViewAdapter.OnRecyclerItemClickListener {
        override fun onCheckBoxClick(position: Int) {
            val task = adapter.getItem(position)
            toggleCheckBox(task)
        }
    }

    private fun toggleCheckBox(task: Task) =
            Observable.fromCallable { taskDao.updateTasks(Task(task.id, task.content, !task.completed, task.viewType, task.created_at)) }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { adapter.checkItem(task) }

    private fun addTask(new: Task) =
            Observable.fromCallable { taskDao.insertTask(new) }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        adapter.addItem(new)
                        editTask.text?.clear()
                        if (Prefs.isAsc) {
                            loadTasksAsc()
                            recyclerView.smoothScrollToPosition(adapter.itemCount)
                        } else {
                            loadTasksDesc()
                        }
                    }

    private fun itemTouchHelper() =
            ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN, ItemTouchHelper.LEFT) {
                override fun onMove(p0: RecyclerView, p1: RecyclerView.ViewHolder, p2: RecyclerView.ViewHolder): Boolean {
                    val fromPos = p1.adapterPosition
                    val toPos = p2.adapterPosition
                    adapter.swapItems(fromPos, toPos)
                    swapTasks(fromPos, toPos)
                    return true
                }

                override fun onSwiped(p0: RecyclerView.ViewHolder, p1: Int) {
                    val pos = p0.adapterPosition
                    val task = adapter.getItem(pos)
                    deleteTask(pos, task)
                    Snackbar.make(coordinator, R.string.delete_task, Snackbar.LENGTH_LONG)
                            .setActionTextColor(ContextCompat.getColor(applicationContext, R.color.yellow))
                            .setAction(R.string.undo) { reInsertTask(pos, task) }.show()
                }

                override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
                    super.clearView(recyclerView, viewHolder)
                    // reallyMoved
                    taskDao.loadAsc()
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe()
                }

                override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
                    if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                        val itemView = viewHolder.itemView
                        val paint = Paint()
                        val bitmap: Bitmap
                        if (dX < 0) { // swipe left
                            paint.color = ContextCompat.getColor(applicationContext, R.color.colorAccent)
                            bitmap = bitmapFromVectorDrawable(applicationContext, R.drawable.ic_delete_24dp)
                            val height = itemView.height.div(2).minus(bitmap.height / 2)
                            val bitmapWidth = bitmap.width
                            c.drawRect(
                                    itemView.right.plus(dX),
                                    itemView.top.toFloat(),
                                    itemView.right.toFloat(),
                                    itemView.bottom.toFloat(),
                                    paint
                            )
                            c.drawBitmap(
                                    bitmap,
                                    itemView.right.minus(bitmapWidth).minus(24f),
                                    itemView.top.plus(height).toFloat(),
                                    null
                            )
                        }
                    }
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                }
            })

    private fun swapTasks(from: Int, pos: Int) {
        val f = adapter.getItem(from)
        val t = adapter.getItem(pos)
        val temp = f.created_at
        f.created_at = t.created_at
        t.created_at = temp

        Observable.fromCallable { taskDao.updateTasks(f, t) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
    }

    private fun deleteTask(pos: Int?, task: Task?) =
            Observable.fromCallable { task?.let { taskDao.deleteTask(it) } }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { pos?.let { p -> adapter.removeItem(p) } }

    private fun reInsertTask(pos: Int, task: Task) =
            Observable.fromCallable { taskDao.insertTask(task) }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        adapter.reInsertItem(pos, task)
                    }

    private fun bitmapFromVectorDrawable(context: Context, drawableId: Int): Bitmap {
        val drawable = ContextCompat.getDrawable(context, drawableId)
        val canvas = Canvas()
        val bitmap = Bitmap.createBitmap(drawable!!.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        canvas.setBitmap(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    private fun showKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        editLayout.visibility = View.VISIBLE
        editTask.isFocusableInTouchMode = true
        editTask.isFocusable = true
        editTask.requestFocus()
        imm.showSoftInput(editTask, InputMethodManager.SHOW_IMPLICIT)
        fab.animate().scaleX(0F).scaleY(0F).setDuration(500).start()
    }

    private fun hideEditTask() {
        fab.animate().scaleX(1F).scaleY(1F).setDuration(500).start()
        editLayout.visibility = View.GONE
    }

    fun doPositiveClick(headerText: String) {
        val header = Task(content = headerText, viewType = 1, created_at = Date())
        addHeader(header)
    }

    private fun addHeader(header: Task) =
            Observable.fromCallable { taskDao.insertTask(header) }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        adapter.addItem(header)
                        if (Prefs.isAsc) {
                            loadTasksAsc()
                            recyclerView.smoothScrollToPosition(adapter.itemCount)
                        } else {
                            loadTasksDesc()
                        }
                    }
}
