package com.j7arsen.noteapp.repository;

import android.arch.persistence.db.SimpleSQLiteQuery;
import android.content.Context;

import com.j7arsen.noteapp.dataclasses.NoteFilterDataModel;
import com.j7arsen.noteapp.dataclasses.NoteModel;
import com.j7arsen.noteapp.db.AppDatabase;
import com.j7arsen.noteapp.db.DatabaseColumnInfo;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Completable;
import io.reactivex.Single;

public class NoteRepository implements INoteRepository, DatabaseColumnInfo {

    @Override
    public Single<List<NoteModel>> getAllNotes(Context context, NoteFilterDataModel noteFilterDataModel) {
        if(noteFilterDataModel.getSearchQuery() != null && !noteFilterDataModel.getSearchQuery().isEmpty()){
            SimpleSQLiteQuery simpleSQLiteQuery = new SimpleSQLiteQuery("SELECT * FROM " + DatabaseColumnInfo.TABLE_NOTE + " WHERE " + DatabaseColumnInfo.NOTE_DESCRIPTION + " LIKE " + "'%" + noteFilterDataModel.getSearchQuery() + "%'" +" ORDER BY " + DatabaseColumnInfo.NOTE_TIME + " " + noteFilterDataModel.getSortType() + " LIMIT " + noteFilterDataModel.getLimit() + " OFFSET " + noteFilterDataModel.getOffset());
            return Single.fromCallable(() -> AppDatabase.getInstance(context).noteDao().getAllNotes(simpleSQLiteQuery)).delay(500, TimeUnit.MILLISECONDS);
        } else {
            SimpleSQLiteQuery simpleSQLiteQuery = new SimpleSQLiteQuery("SELECT * FROM " + DatabaseColumnInfo.TABLE_NOTE + " ORDER BY " + DatabaseColumnInfo.NOTE_TIME + " " + noteFilterDataModel.getSortType() + " LIMIT " + noteFilterDataModel.getLimit() + " OFFSET " + noteFilterDataModel.getOffset());
            return Single.fromCallable(() -> AppDatabase.getInstance(context).noteDao().getAllNotes(simpleSQLiteQuery));
        }
    }

    @Override
    public Completable insertNote(Context context, final NoteModel noteModel) {
        return Completable.fromAction(() -> AppDatabase.getInstance(context).noteDao().insertNote(noteModel));
    }

    @Override
    public Completable deleteNote(Context context, NoteModel noteModel) {
        return Completable.fromAction(() -> AppDatabase.getInstance(context).noteDao().deleteNote(noteModel));
    }

    @Override
    public Completable updateNote(Context context, NoteModel noteModel) {
        return Completable.fromAction(() -> AppDatabase.getInstance(context).noteDao().updateNote(noteModel));
    }
}
