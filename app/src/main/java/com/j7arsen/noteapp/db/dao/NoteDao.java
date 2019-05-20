package com.j7arsen.noteapp.db.dao;

import android.arch.persistence.db.SupportSQLiteQuery;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.RawQuery;
import android.arch.persistence.room.Update;

import com.j7arsen.noteapp.dataclasses.NoteModel;

import java.util.List;

@Dao
public interface NoteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertNote(NoteModel noteModel);

    @Delete
    void deleteNote(NoteModel noteModel);

    @Update
    void updateNote(NoteModel noteModel);

    @RawQuery
    List<NoteModel> getAllNotes(SupportSQLiteQuery query);


}
