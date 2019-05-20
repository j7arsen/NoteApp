package com.j7arsen.noteapp.dataclasses;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.j7arsen.noteapp.db.DatabaseColumnInfo;

import java.io.Serializable;

@Entity(tableName = DatabaseColumnInfo.TABLE_NOTE)
public class NoteModel implements Serializable {

    @ColumnInfo(name = DatabaseColumnInfo.NOTE_ID)
    @PrimaryKey(autoGenerate = true)
    @NonNull
    private Long id;
    @ColumnInfo(name = DatabaseColumnInfo.NOTE_TIME)
    private long time;
    @ColumnInfo(name = DatabaseColumnInfo.NOTE_DESCRIPTION)
    private String description;

    @NonNull
    public Long getId() {
        return id;
    }

    public void setId(@NonNull Long id) {
        this.id = id;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
