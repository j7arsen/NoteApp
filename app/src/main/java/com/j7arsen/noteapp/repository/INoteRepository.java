package com.j7arsen.noteapp.repository;

import android.content.Context;

import com.j7arsen.noteapp.dataclasses.NoteFilterDataModel;
import com.j7arsen.noteapp.dataclasses.NoteModel;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

public interface INoteRepository {

    Single<List<NoteModel>> getAllNotes(Context context, NoteFilterDataModel noteFilterDataModel);

    Completable insertNote(Context context, NoteModel model);

    Completable deleteNote(Context context, NoteModel noteModel);

    Completable updateNote(Context context, NoteModel noteModel);

}
