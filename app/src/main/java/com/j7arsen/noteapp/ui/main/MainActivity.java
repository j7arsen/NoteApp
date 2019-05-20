package com.j7arsen.noteapp.ui.main;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.j7arsen.noteapp.R;
import com.j7arsen.noteapp.app.Screens;
import com.j7arsen.noteapp.base.BaseActivity;
import com.j7arsen.noteapp.dataclasses.NoteFilterDataModel;
import com.j7arsen.noteapp.dataclasses.NoteModel;
import com.j7arsen.noteapp.repository.NoteRepository;
import com.j7arsen.noteapp.ui.detail.NoteDetailActivity;
import com.j7arsen.noteapp.ui.main.adapter.NoteListAdapter;
import com.j7arsen.noteapp.utils.ResUtils;
import com.j7arsen.noteapp.view.StubView;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.disposables.Disposables;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends BaseActivity implements NoteListAdapter.NoteListListener, SearchView.OnQueryTextListener {

    private static final String SAVE_NOTE_LIST_DATA = "MainActivity.SAVE_NOTE_LIST_DATA";
    private static final String SAVE_NOTE_FILTER_DATA = "MainActivity.SAVE_NOTE_FILTER_DATA";

    private Toolbar toolbar;
    private SearchView svSearchNote;
    private RecyclerView rvNoteList;
    private StubView svNoteListEmpty;
    private FloatingActionButton fabNoteAdd;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    private Disposable disposable = Disposables.empty();

    private NoteListAdapter noteListAdapter;

    private NoteRepository noteRepository;
    private ArrayList<NoteModel> noteList;
    private NoteFilterDataModel noteFilterDataModel;

    private PopupWindow sortPopup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toolbar = (Toolbar) findViewById(R.id.navigation_bar);
        svSearchNote = (SearchView) findViewById(R.id.sv_search_note);
        rvNoteList = (RecyclerView) findViewById(R.id.rv_note_list);
        svNoteListEmpty = (StubView) findViewById(R.id.sv_note_list_empty);
        fabNoteAdd = (FloatingActionButton) findViewById(R.id.fab_add_note);
        initToolbar();

        noteRepository = new NoteRepository();
        noteList = new ArrayList<>();
        noteFilterDataModel = new NoteFilterDataModel();

        initAdapters();
        setListeners();
        getNoteList();
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        toolbar.setTitle(ResUtils.getString(this, R.string.note_screen_title));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(SAVE_NOTE_LIST_DATA, noteList);
        outState.putSerializable(SAVE_NOTE_FILTER_DATA, noteFilterDataModel);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        noteList = (ArrayList<NoteModel>) savedInstanceState.getSerializable(SAVE_NOTE_LIST_DATA);
        noteFilterDataModel = (NoteFilterDataModel) savedInstanceState.getSerializable(SAVE_NOTE_FILTER_DATA);
        noteListAdapter.updateData(noteList);
    }

    private void initAdapters() {
        noteListAdapter = new NoteListAdapter();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvNoteList.setLayoutManager(linearLayoutManager);
        rvNoteList.setAdapter(noteListAdapter);
        noteListAdapter.setNoteListListener(this);
    }

    private void setListeners() {
        fabNoteAdd.setOnClickListener(view -> NoteDetailActivity.startActivity(this, null));
        svSearchNote.setOnQueryTextListener(this);
    }

    @Override
    public void onItemClick(NoteModel noteModel) {
        if (noteModel != null) {
            NoteDetailActivity.startActivity(this, noteModel);
        } else {
            Toast.makeText(this, ResUtils.getString(this, R.string.message_universal), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        if (noteFilterDataModel != null) {
            if(s.trim().length() == 0){
                noteFilterDataModel.setSearchQuery(null);
                reloadNoteList();
            } else{
                if(s.trim().length() >= 2){
                    noteFilterDataModel.setSearchQuery(s);
                    reloadNoteList();
                }
            }
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_note_list_sort, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sort:
                showSortPopup();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void reloadNoteList() {
        if (noteFilterDataModel != null) {
            noteFilterDataModel.resetPaging();
            if (noteList != null) {
                noteList.clear();
            }
            undisposable();
            getNoteList();
        } else {
            Toast.makeText(this, ResUtils.getString(this, R.string.message_universal), Toast.LENGTH_SHORT).show();
        }
    }

    private void getNoteList() {
        if (noteRepository != null && noteFilterDataModel != null) {
            disposable = noteRepository.getAllNotes(this, noteFilterDataModel).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(s -> {
                undisposable();
                Log.i("Load note list", "Load note list");
                initData((s != null && s.size() != 0) ? new ArrayList<>(s) : null);
            }, e -> {
                undisposable();
                if (noteListAdapter != null) {
                    noteListAdapter.finishLoadMore();
                }
                Toast.makeText(this, ResUtils.getString(this, R.string.message_universal), Toast.LENGTH_SHORT).show();
            });
            compositeDisposable.add(disposable);
        } else {
            Toast.makeText(this, ResUtils.getString(this, R.string.message_universal), Toast.LENGTH_SHORT).show();
        }
    }

    private void undisposable() {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    @Override
    public void loadData() {
        if (noteFilterDataModel != null) {
            int page = noteFilterDataModel.getPage() + 1;
            noteFilterDataModel.setPage(page);
            getNoteList();
        }
    }

    private void initData(ArrayList<NoteModel> noteModelList) {
        if (noteModelList == null || noteModelList.size() == 0) {
            if (noteFilterDataModel != null && noteFilterDataModel.getPage() == 0) {
                showEmptyListView();
            } else {
                finishLoadMore(true);
            }
        } else {
            if (noteList == null) {
                noteList = new ArrayList<>();
            }
            noteList.addAll(noteModelList);
            hideEmptyListView();
            noteListAdapter.updateData(noteList);
            if (noteFilterDataModel != null) {
                finishLoadMore(noteModelList.size() < noteFilterDataModel.getLimit());
            }
        }
    }

    public void finishLoadMore(boolean isFinish) {
        if (noteListAdapter != null) {
            if (isFinish) {
                noteListAdapter.finishLoadMore();
            } else {
                noteListAdapter.reset();
            }
        }
    }

    public void showEmptyListView() {
        rvNoteList.setVisibility(View.GONE);
        svNoteListEmpty.show();
    }

    public void hideEmptyListView() {
        svNoteListEmpty.hide();
        rvNoteList.setVisibility(View.VISIBLE);
    }

    public void showSortPopup() {
        if (sortPopup == null || !sortPopup.isAboveAnchor()) {
            LayoutInflater layoutInflater = LayoutInflater.from(this);
            View popupView = layoutInflater.inflate(R.layout.view_note_sort, null);
            sortPopup = new PopupWindow(popupView,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            sortPopup.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            sortPopup.setOutsideTouchable(true);
            sortPopup.showAsDropDown(toolbar, 0, 0, Gravity.RIGHT | Gravity.BOTTOM);
            sortPopup.getContentView().findViewById(R.id.rL_sort_from_new_to_old).setOnClickListener(
                    v -> {
                        hideSortPopup();
                        if (noteFilterDataModel != null) {
                            noteFilterDataModel.setSortType(NoteFilterDataModel.SORT_DESC, onChangeListener);
                        }
                    }
            );
            sortPopup.getContentView().findViewById(R.id.rL_sort_from_old_to_new).setOnClickListener(
                    v -> {
                        hideSortPopup();
                        if (noteFilterDataModel != null) {
                            noteFilterDataModel.setSortType(NoteFilterDataModel.SORT_ASC, onChangeListener);
                        }
                    }
            );
        } else {
            hideSortPopup();
        }
    }

    private void hideSortPopup() {
        if (sortPopup != null) {
            sortPopup.dismiss();
        }
        sortPopup = null;
    }

    public NoteFilterDataModel.OnChangeListener onChangeListener = new NoteFilterDataModel.OnChangeListener() {
        @Override
        public void onChange() {
            reloadNoteList();
        }
    };

    @Override
    protected int getLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case Screens.START_NOTE_DETAIL_ACTIVITY:
                switch (resultCode) {
                    case Screens.RESULT_NOTE_DETAIL_ACTIVITY:
                        reloadNoteList();
                        break;
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        if (compositeDisposable != null && !compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
        }
        super.onDestroy();
    }

}
