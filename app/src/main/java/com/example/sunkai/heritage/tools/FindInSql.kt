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

    fun searchFindCOmmentImageFromSQLWithoutTimeCheck(commentID: Int): Bitmap? {
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

    fun searchFindCommentImageFromSQL(commentID: Int, commentTime: String): Bitmap? {
        val db = MySqliteHandler.GetReadableDatabase()
        val table = "find_comment_image"
        val selection = "imageID=?"
        val cursor: Cursor
        val selectionArgs = arrayOf(commentID.toString())
        cursor = db.query(table, null, selection, selectionArgs, null, null, null)
        cursor.moveToFirst()
        if (!cursor.isAfterLast) {
            val commentTimeIndex = cursor.getColumnIndex("comment_time")
            if (commentTimeIndex > 0) {
                val sqlCommentTime = cursor.getString(commentTimeIndex)
                if (sqlCommentTime == commentTime) {
                    val imageIndex = cursor.getColumnIndex("image")
                    val img = cursor.getBlob(imageIndex)
                    cursor.close()
                    return HandlePic.handlePic(ByteArrayInputStream(img), 0)
                }
            }
        }
        cursor.close()
        return null
    }

    fun searchFolkCommentImageFromSql(id: Int): Bitmap? {
        val db = MySqliteHandler.GetReadableDatabase()
        val table = "folk_image"
        val selection = "id=?"
        val selectionArgs = arrayOf(id.toString())
        val cursor = db.query(table, null, selection, selectionArgs, null, null, null)
        cursor.moveToFirst()
        if (!cursor.isAfterLast) {
            val imageIndex = cursor.getColumnIndex("image")
            val img = cursor.getBlob(imageIndex)
            cursor.close()
            return HandlePic.handlePic(ByteArrayInputStream(img), 0)
        }
        return null
    }

    fun CheckIsHadImageInSql(commentID: Int): Boolean {
        val db = MySqliteHandler.GetReadableDatabase()
        val table = "find_comment_image"
        val selection = "imageID=?"
        val cursor: Cursor
        val selectionArgs = arrayOf(commentID.toString())
        cursor = db.query(table, null, selection, selectionArgs, null, null, null)
        cursor.moveToFirst()
        if (!cursor.isAfterLast) {
            return true
        }
        return false
    }

    fun UpdateFindCommentData(commentID: Int, commentTime: String, imageByte: ByteArray) {
        if (CheckIsHadImageInSql(commentID))
            UpdateFindCommentImageToSql(commentID, commentTime, imageByte)
        else
            AddFindCommentImageToSQL(commentID, commentTime, imageByte)
    }

    private fun UpdateFindCommentImageToSql(commentID: Int, commentTime: String, imageByte: ByteArray) {
        val table = "find_comment_image"
        val contentValues = ContentValues()
        contentValues.put("imageID", commentID)
        contentValues.put("image", imageByte)
        contentValues.put("comment_time", commentTime)
        val db = MySqliteHandler.GetWritableDatabase()
        db.update(table, contentValues, "imageID=?", arrayOf(commentID.toString()))
    }

    private fun AddFindCommentImageToSQL(commentID: Int, commentTime: String, imageByte: ByteArray) {
        val table = "find_comment_image"
        val contentValues = ContentValues()
        contentValues.put("imageID", commentID)
        contentValues.put("image", imageByte)
        contentValues.put("comment_time", commentTime)
        val db = MySqliteHandler.GetWritableDatabase()
        db.insert(table, null, contentValues)
    }

    fun AddFindCommentImageToSQLWithoutTime(commentID: Int, imageByte: ByteArray) {
        val table = "find_comment_image"
        val contentValues = ContentValues()
        contentValues.put("imageID", commentID)
        contentValues.put("image", imageByte)
        val db = MySqliteHandler.GetWritableDatabase()
        db.insert(table, null, contentValues)
    }

    fun AddFolkCommentImageToSQL(id: Int, imageByte: ByteArray) {
        val contentValues = ContentValues()
        contentValues.put("id", id)
        contentValues.put("image", imageByte)
        val db = MySqliteHandler.GetWritableDatabase()
        val table = "folk_image"
        db.insert(table, null, contentValues)
    }
}