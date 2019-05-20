package com.j7arsen.noteapp.ui.detail;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.j7arsen.noteapp.R;
import com.j7arsen.noteapp.app.Screens;
import com.j7arsen.noteapp.base.BaseActivity;
import com.j7arsen.noteapp.dataclasses.NoteModel;
import com.j7arsen.noteapp.repository.NoteRepository;
import com.j7arsen.noteapp.utils.ResUtils;
import com.j7arsen.noteapp.utils.UI;

import java.util.Calendar;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class NoteDetailActivity extends BaseActivity {

    public static final String NOTE_MODEL = "NoteDetailActivity.NOTE_MODEL";

    private static final String SAVE_NOTE_MODEL = "NoteDetailActivity.SAVE_NOTE_MODEL";

    private Toolbar toolbar;
    private AppCompatEditText etNoteDescription;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    private NoteRepository noteRepository;

    private NoteModel noteModel;

    public static void startActivity(Activity activity, NoteModel noteModel) {
        Intent intent = new Intent(activity, NoteDetailActivity.class);
        intent.putExtra(NOTE_MODEL, noteModel);
        activity.startActivityForResult(intent, Screens.START_NOTE_DETAIL_ACTIVITY);
        UI.animationOpenActivity(activity);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toolbar = (Toolbar) findViewById(R.id.navigation_bar);
        etNoteDescription = (AppCompatEditText) findViewById(R.id.et_note_description);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        getExtras();
        initToolbar();
        noteRepository = new NoteRepository();
        initData(noteModel);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(SAVE_NOTE_MODEL, noteModel);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        noteModel = (NoteModel) savedInstanceState.getSerializable(SAVE_NOTE_MODEL);
        initData(noteModel);
    }

    private void getExtras() {
        if (getIntent() != null && getIntent().getExtras() != null) {
            noteModel = (NoteModel) getIntent().getExtras().getSerializable(NOTE_MODEL);
        }
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        toolbar.setTitle("");
    }

    private void initData(NoteModel noteModel) {
        if (noteModel != null && (noteModel.getDescription() != null && !noteModel.getDescription().isEmpty())) {
            etNoteDescription.setText(noteModel.getDescription());
            etNoteDescription.setSelection(noteModel.getDescription().length());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_note_detail, menu);
        if (noteModel != null) {
            menu.findItem(R.id.share).setVisible(true);
            menu.findItem(R.id.delete).setVisible(true);
        } else {
            menu.findItem(R.id.share).setVisible(false);
            menu.findItem(R.id.delete).setVisible(false);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.share:
                share();
                return true;
            case R.id.delete:
                deleteNote();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void insertOrEditNote() {
        String description = etNoteDescription.getText().toString();
        if (description != null && !description.trim().isEmpty()) {
            if (noteModel != null) {
                if ((noteModel.getDescription() != null && !noteModel.getDescription().isEmpty()) && noteModel.getDescription().equals(description)) {
                    super.onBackPressed();
                } else {
                    editNote(description);
                }
            } else {
                addNote(description);
            }
        } else {
            super.onBackPressed();
        }
    }

    private void addNote(String description) {
        if (noteRepository != null) {
            NoteModel noteModel = new NoteModel();
            noteModel.setDescription(description);
            noteModel.setTime(Calendar.getInstance().getTimeInMillis());
            compositeDisposable.add(noteRepository.insertNote(this, noteModel).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(() -> sendResultToUpdateNoteList(), e -> errorActionOnNote(e)));
        } else {
            Toast.makeText(this, ResUtils.getString(this, R.string.message_universal), Toast.LENGTH_SHORT).show();
            onBackPressed();
        }
    }

    private void editNote(String description) {
        if (noteModel != null && noteRepository != null) {
            noteModel.setDescription(description);
            noteModel.setTime(Calendar.getInstance().getTimeInMillis());
            compositeDisposable.add(noteRepository.updateNote(this, noteModel).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(() -> sendResultToUpdateNoteList(), e -> errorActionOnNote(e)));
        } else {
            Toast.makeText(this, ResUtils.getString(this, R.string.message_universal), Toast.LENGTH_SHORT).show();
            onBackPressed();
        }
    }

    private void deleteNote() {
        if (noteModel != null && noteRepository != null) {
            compositeDisposable.add(noteRepository.deleteNote(this, noteModel).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(() -> sendResultToUpdateNoteList(), e -> errorActionOnNote(e)));
        } else {
            Toast.makeText(this, ResUtils.getString(this, R.string.message_universal), Toast.LENGTH_SHORT).show();
        }
    }

    private void sendResultToUpdateNoteList() {
        setResult(Screens.RESULT_NOTE_DETAIL_ACTIVITY);
        finish();
    }

    private void errorActionOnNote(Throwable t) {
        Toast.makeText(this, ResUtils.getString(this, R.string.message_universal), Toast.LENGTH_SHORT).show();
    }

    private void share() {
        if (noteModel != null) {
            final Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT, String.format(ResUtils.getString(this, R.string.note_detail_share_subject), String.valueOf(noteModel.getId())));
            intent.putExtra(Intent.EXTRA_TEXT, noteModel.getDescription());
            try {
                startActivity(Intent.createChooser(intent, getString(R.string.note_detail_share_action)));
            } catch (ActivityNotFoundException ex) {
                Toast.makeText(this, ResUtils.getString(this, R.string.message_universal), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, ResUtils.getString(this, R.string.message_universal), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        insertOrEditNote();
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_note_detail;
    }

    @Override
    protected void onDestroy() {
        if (compositeDisposable != null && !compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
        }
        super.onDestroy();
    }
}
