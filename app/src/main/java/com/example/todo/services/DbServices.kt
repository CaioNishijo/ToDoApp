package com.example.todo.services

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todo.adapters.CustomAdapterForTodos
import com.example.todo.entities.Category
import com.example.todo.entities.Todo
import androidx.recyclerview.widget.ItemTouchHelper


class DbServices(
    private val context: Context
) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "TodoApp.db"
        const val DATABASE_VERSION = 2

        // Tabela todo
        const val TABLE_TODOS = "tb_todos"
        const val COLUMN_TODO_ID = "todo_id"
        const val COLUMN_TODO_NAME = "todo_name"
        const val COLUMN_TODO_CONTENT = "todo_content"
        const val COLUMN_TODO_CREATION_DATE = "todo_creation_date"
        const val COLUMN_TODO_START_HOUR = "todo_start_hour"
        const val COLUMN_TODO_DURATION = "todo_duration"
        const val COLUMN_TODO_CATEGORY_ID = "todo_category_id"
        const val COLUMN_TODO_IS_FINISHED = "todo_is_finished"

        // Tabela categorias
        const val TABLE_CATEGORIES = "tb_categories"
        const val COLUMN_CATEGORY_ID = "category_id"
        const val COLUMN_CATEGORY_NAME = "category_name"
    }

    override fun onCreate(p0: SQLiteDatabase?) {
        val createTbTodoQuery = """
            CREATE TABLE $TABLE_TODOS (
                $COLUMN_TODO_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_TODO_NAME TEXT,
                $COLUMN_TODO_CONTENT TEXT,
                $COLUMN_TODO_CREATION_DATE DATETIME DEFAULT CURRENT_TIMESTAMP,
                $COLUMN_TODO_START_HOUR TEXT,
                $COLUMN_TODO_DURATION INTEGER,
                $COLUMN_TODO_CATEGORY_ID INTEGER,
                $COLUMN_TODO_IS_FINISHED INTEGER,
                FOREIGN KEY($COLUMN_TODO_CATEGORY_ID) REFERENCES $TABLE_CATEGORIES($COLUMN_CATEGORY_ID)
                ON DELETE CASCADE
            )
        """.trimIndent()

        val createTbCategoryQuery = """
            CREATE TABLE $TABLE_CATEGORIES (
                $COLUMN_CATEGORY_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_CATEGORY_NAME TEXT
            )
        """.trimIndent()

        p0?.execSQL(createTbCategoryQuery)
        p0?.execSQL(createTbTodoQuery)
        insertCategoriesDefaultValues(p0)
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        p0?.execSQL("DROP TABLE IF EXISTS $TABLE_TODOS")
        p0?.execSQL("DROP TABLE IF EXISTS $TABLE_CATEGORIES")
        onCreate(p0)
    }

    fun createTodo(
        todoName: String,
        todoContent: String,
        todoStartHour: String,
        todoDuration: Int,
        todoCategoryId: Int,
        todoIsFinished: Boolean
    ) {
        val db = this.writableDatabase
        val contentValues = ContentValues()

        contentValues.put(COLUMN_TODO_NAME, todoName)
        contentValues.put(COLUMN_TODO_CONTENT, todoContent)
        contentValues.put(COLUMN_TODO_START_HOUR, todoStartHour)
        contentValues.put(COLUMN_TODO_DURATION, todoDuration)
        contentValues.put(COLUMN_TODO_CATEGORY_ID, todoCategoryId)
        contentValues.put(COLUMN_TODO_IS_FINISHED, todoIsFinished)

        val result = db.insert(TABLE_TODOS, null, contentValues)
        if (result == -1L) {
            Toast.makeText(context, "Falha ao adicionar o todo", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Todo adicionado com sucesso", Toast.LENGTH_SHORT).show()
        }
    }

    private fun insertCategoriesDefaultValues(p0: SQLiteDatabase?){
        p0?.execSQL("""
            INSERT INTO $TABLE_CATEGORIES($COLUMN_CATEGORY_NAME) VALUES ("Fácil")
        """.trimIndent())
        p0?.execSQL("""
            INSERT INTO $TABLE_CATEGORIES($COLUMN_CATEGORY_NAME) VALUES ("Médio")
        """.trimIndent())
        p0?.execSQL("""
            INSERT INTO $TABLE_CATEGORIES($COLUMN_CATEGORY_NAME) VALUES ("Difícil")
        """.trimIndent())
    }

    fun getCategorias(): List<Category>{
        val lista = ArrayList<Category>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_CATEGORIES", null)

        if(cursor.moveToFirst()){
            do{
                val id = cursor.getInt(0)
                val name = cursor.getString(1)
                lista.add(Category(id, name))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return lista
    }

    private fun getTodos(): List<Todo>{
        val lista = ArrayList<Todo>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_TODOS ORDER BY $COLUMN_TODO_START_HOUR ASC", null)

        if(cursor.moveToFirst()){
            do{
                val id = cursor.getInt(0)
                val name = cursor.getString(1)
                val content = cursor.getString(2)
                val creationDate = cursor.getString(3)
                val startHour = cursor.getString(4)
                val duration = cursor.getString(5)
                val categoryId = cursor.getInt(6)
                val isFinished = cursor.getInt(7) == 1
                lista.add(Todo(id, name, content, creationDate, startHour, duration, categoryId, isFinished))
            } while(cursor.moveToNext())
        }
        cursor.close()
        return lista
    }

    fun getTodoById(id: Int): Todo?{
        val db = this.readableDatabase

        val cursor = db.rawQuery("SELECT * FROM $TABLE_TODOS WHERE $COLUMN_TODO_ID = $id", null)

        val todo = if(cursor.moveToFirst()){
            Todo(
                id=id,
                name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TODO_NAME)),
                categoryId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TODO_CATEGORY_ID)),
                content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TODO_CONTENT)),
                creationDate = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TODO_CREATION_DATE)),
                duration = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TODO_DURATION)),
                isFinished = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TODO_IS_FINISHED)) == 1,
                startHour = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TODO_START_HOUR))
            )
        } else {
            null
        }
        cursor.close()
        return todo
    }

    fun updateTodo(id: Int, updatedTodo: Todo){
        val db = this.writableDatabase


        val contentValues = ContentValues().apply {
            put(COLUMN_TODO_NAME, updatedTodo.name)
            put(COLUMN_TODO_CONTENT, updatedTodo.content)
            put(COLUMN_TODO_DURATION, updatedTodo.duration)
            put(COLUMN_TODO_IS_FINISHED, updatedTodo.isFinished)
            put(COLUMN_TODO_START_HOUR, updatedTodo.startHour)
            put(COLUMN_TODO_CATEGORY_ID, updatedTodo.categoryId)
        }

        val whereClause = "$COLUMN_TODO_ID = ?"
        val whereArgs = arrayOf(id.toString())

        val result = db.update(TABLE_TODOS, contentValues, whereClause, whereArgs)

        if(result == -1){
            Toast.makeText(context, "Falha ao atualizar o todo", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Todo atualizado", Toast.LENGTH_SHORT).show()
        }
    }

    fun deleteTodo(id: Int){
        val db = writableDatabase
        val query = "DELETE FROM $TABLE_TODOS WHERE $COLUMN_TODO_ID = ?"

        db.use { dbFun ->
            dbFun.execSQL(query, arrayOf(id))
            Toast.makeText(context, "Todo deletado com sucesso", Toast.LENGTH_SHORT).show()
        }
    }

    fun getCategoriaById(id: Int): Category?{
        val db = readableDatabase
        val category: Category?
        val cursor = db.rawQuery("SELECT * FROM $TABLE_CATEGORIES WHERE $COLUMN_CATEGORY_ID = $id", null)

        if(cursor.moveToFirst()){
            val categoryId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY_ID))
            val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY_NAME))

            category = Category(categoryId, name)
        } else {
            category = null
        }
        cursor.close()
        return category
    }

    fun updateIsFinish(id: Int, isFinished: Boolean){
        val db = this.writableDatabase
        val whereClause = "$COLUMN_TODO_ID = ?"
        val whereArgs = arrayOf(id.toString())
        val contentValues = ContentValues().apply {
            put(COLUMN_TODO_IS_FINISHED, isFinished)
        }

        val result = db.update(TABLE_TODOS, contentValues, whereClause, whereArgs)
        db.close()

        if(result <= 0){
            Toast.makeText(context, "Erro ao finalizar todo", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Todo atualizado com sucesso", Toast.LENGTH_SHORT).show()
        }
    }

    fun clearAll(){
        val db = writableDatabase
        val query = "DELETE FROM $TABLE_TODOS"
        val resetAutoincrement = "DELETE FROM sqlite_sequence WHERE name = '$TABLE_TODOS'"

        db.use { dbFun ->
            dbFun.execSQL(query)
            dbFun.execSQL(resetAutoincrement)
        }
    }

    fun loadTodos(todoList: RecyclerView, filteredTodo: List<Todo>?){
        val db = DbServices(context)
        val list: List<Todo> = filteredTodo ?: db.getTodos()

        val adapter = CustomAdapterForTodos(context, list, db)

        todoList.adapter = adapter
        todoList.layoutManager = LinearLayoutManager(context)

        val touchHelper = TouchHelper(adapter, db)

        ItemTouchHelper(touchHelper).attachToRecyclerView(todoList)
    }

    fun filterService(name: String?, categoryId: String?, isFinished: String?): List<Todo>{
        val db = this.readableDatabase
        val list = ArrayList<Todo>()

        val whereClauses = ArrayList<String>()
        val args = ArrayList<String>()

        if(!name.isNullOrEmpty()){
            whereClauses.add("$COLUMN_TODO_NAME LIKE ?")
            args.add("%$name%")
        }
        if(!categoryId.isNullOrEmpty()){
            if(categoryId != "999"){
                whereClauses.add("$COLUMN_TODO_CATEGORY_ID = ?")
                args.add(categoryId)
            }
        }
        if(!isFinished.isNullOrEmpty()){
            val isFinishedVal: String
            if(isFinished == "Finalizadas"){
                isFinishedVal = "1"
                whereClauses.add("$COLUMN_TODO_IS_FINISHED = ?")
                args.add(isFinishedVal)
            } else if(isFinished == "Não finalizadas"){
                isFinishedVal = "0"
                whereClauses.add("$COLUMN_TODO_IS_FINISHED = ?")
                args.add(isFinishedVal)
            }
        }

        val whereClause = if(whereClauses.isNotEmpty()) whereClauses.joinToString(" AND ") else null

        val query = if (!whereClause.isNullOrEmpty()){
            "SELECT * FROM $TABLE_TODOS WHERE $whereClause"
        } else {
            "SELECT * FROM $TABLE_TODOS"
        }

        val cursor = db.rawQuery(query, args.toTypedArray())

        if(cursor.moveToFirst()){
            do{
                val todo = Todo(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TODO_ID)),
                    name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TODO_NAME)),
                    content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TODO_CONTENT)),
                    creationDate = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TODO_CREATION_DATE)),
                    startHour = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TODO_START_HOUR)),
                    duration = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TODO_DURATION)),
                    categoryId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TODO_CATEGORY_ID)),
                    isFinished = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TODO_IS_FINISHED)) == 1
                )
                list.add(todo)
            } while(cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return list
    }
}