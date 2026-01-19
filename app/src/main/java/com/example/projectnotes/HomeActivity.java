package com.example.projectnotes;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;

import com.example.projectnotes.model.Note;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class HomeActivity extends BaseActivity {

    private LinearLayout noteContainer;
    private TextView txtEmpty, txtHomeLocation, txtHomeTitle; // Nama variabel disamakan dengan XML
    private FloatingActionButton fabAdd;
    private String[] noteLines;

    private final ActivityResultLauncher<Intent> launcher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK) {
                            loadNotes();
                        }
                    }
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        noteContainer = findViewById(R.id.noteContainer);
        txtEmpty = findViewById(R.id.txtEmpty);
        fabAdd = findViewById(R.id.fabAdd);

        // INISIALISASI (ID disesuaikan dengan XML kamu: txtHomeLocation & txtHomeTitle)
        txtHomeLocation = findViewById(R.id.txtHomeLocation);
        txtHomeTitle = findViewById(R.id.txtHomeTitle);

        applyLocalization();

        fabAdd.setOnClickListener(v ->
                launcher.launch(new Intent(this, AddNoteActivity.class))
        );

        loadNotes();
    }

    private void applyLocalization() {
        SharedPreferences pref = getSharedPreferences("UserSetting", MODE_PRIVATE);
        String country = pref.getString("country", "id");
        String location = pref.getString("location", "Jakarta, Indonesia");

        // Set lokasi di UI
        if (txtHomeLocation != null) txtHomeLocation.setText(location);

        // Set Judul & Empty State sesuai Negara
        switch (country) {
            case "ja":
                if (txtHomeTitle != null) txtHomeTitle.setText("私のメモ");
                txtEmpty.setText("メモがありません");
                break;
            case "en":
                if (txtHomeTitle != null) txtHomeTitle.setText("My Notes");
                txtEmpty.setText("No notes available");
                break;
            default:
                if (txtHomeTitle != null) txtHomeTitle.setText("Catatan Saya");
                txtEmpty.setText("Belum ada catatan");
                break;
        }
    }

    private void loadNotes() {
        SharedPreferences pref = getSharedPreferences("notes", MODE_PRIVATE);
        String saved = pref.getString("data", "").trim();
        noteContainer.removeAllViews();

        if (saved.isEmpty()) {
            txtEmpty.setVisibility(View.VISIBLE);
            noteContainer.setVisibility(View.GONE);
            return;
        }

        txtEmpty.setVisibility(View.GONE);
        noteContainer.setVisibility(View.VISIBLE);
        noteLines = saved.split("\n");

        for (int i = 0; i < noteLines.length; i++) {
            Note note = Note.fromStorage(noteLines[i]);
            if (note == null) continue;

            View v = LayoutInflater.from(this).inflate(R.layout.item_note, noteContainer, false);
            ((TextView) v.findViewById(R.id.txtTitle)).setText(note.getTitle());
            ((TextView) v.findViewById(R.id.txtDate)).setText(note.getDate());
            ((TextView) v.findViewById(R.id.txtContent)).setText(note.getContent());

            int index = i;
            v.setOnClickListener(c -> {
                Intent edit = new Intent(this, AddNoteActivity.class);
                edit.putExtra("index", index);
                edit.putExtra("title", note.getTitle());
                edit.putExtra("content", note.getContent());
                launcher.launch(edit);
            });

            v.setOnLongClickListener(c -> {
                new AlertDialog.Builder(this)
                        .setTitle("Hapus")
                        .setMessage("Yakin?")
                        .setPositiveButton("Ya", (d, w) -> delete(index))
                        .setNegativeButton("Tidak", null)
                        .show();
                return true;
            });
            noteContainer.addView(v);
        }
    }

    private void delete(int index) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < noteLines.length; i++) {
            if (i != index) sb.append(noteLines[i]).append("\n");
        }
        getSharedPreferences("notes", MODE_PRIVATE).edit().putString("data", sb.toString().trim()).apply();
        loadNotes();
    }
}