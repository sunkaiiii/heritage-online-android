package com.example.sunkai.heritage.tools

import android.content.ContentValues
import android.database.Cursor
import android.graphics.Bitmap
import com.example.sunkai.heritage.Data.HandlePic
import com.example.sunkai.heritage.Data.MySqliteHandler
import java.io.ByteArrayInputStream

/**
 * Created by sunkai on 2018/1/8.
 */
object FindInSql {
    fun searchFindCommentImageFromSQL(commentID:Int):Bitmap?{
        val db = MySqliteHandler.GetReadableDatabase()
        val table = "find_comment_image"
        val selection = "imageID=?"
        val cursor: Cursor
        val selectionArgs = arrayOf(commentID.toString())
        cursor = db.query(table, null, selection, selectionArgs, null, null, null)
        cursor.moveToFirst()
        if (!cursor.isAfterLast) {
            val imageIndex = cursor.getColumnIndex("image")
            val img = cursor.getBlob(imageIndex)
            cursor.close()
            return HandlePic.handlePic(ByteArrayInputStream(img), 0)
        }
        cursor.close()
        return null
    }

    fun addFindCommentImageToSQL(commentID: Int,imageByte:ByteArray){
        val table = "find_comment_image"
        val contentValues = ContentValues()
        contentValues.put("imageID", commentID)
        contentValues.put("image", imageByte)
        val db = MySqliteHandler.GetWritableDatabase()
        db.insert(table, null, contentValues)
    }
}