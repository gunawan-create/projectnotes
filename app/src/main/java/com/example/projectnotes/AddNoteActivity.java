package com.example.projectnotes;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView; // Pastikan import ini ada

import com.example.projectnotes.model.Note;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddNoteActivity extends BaseActivity {

    private EditText edtTitle, edtContent;
    private ImageView btnSave, btnBack;
    // Tambahkan variabel untuk Label dan Judul sesuai XML terbaru
    private TextView txtTitleBar, lblTitle, lblContent;
    private SharedPreferences draftPref, notePref;
    private int editIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        // Inisialisasi komponen sesuai ID di XML
        edtTitle = findViewById(R.id.edtTitle);
        edtContent = findViewById(R.id.edtContent);
        btnSave = findViewById(R.id.btnAdd); // Sesuai ID @id/btnAdd
        btnBack = findViewById(R.id.btnBack);

        // Inisialisasi Label & Title Bar
        txtTitleBar = findViewById(R.id.txtTitleBar);
        lblTitle = findViewById(R.id.lblTitle);
        lblContent = findViewById(R.id.lblContent);

        draftPref = getSharedPreferences("draft_note", MODE_PRIVATE);
        notePref = getSharedPreferences("notes", MODE_PRIVATE);

        Intent intent = getIntent();
        if (intent.hasExtra("index")) {
            editIndex = intent.getIntExtra("index", -1);
            edtTitle.setText(intent.getStringExtra("title"));
            edtContent.setText(intent.getStringExtra("content"));
        } else {
            edtTitle.setText(draftPref.getString("title", ""));
            edtContent.setText(draftPref.getString("content", ""));
        }

        // Jalankan applyLanguage SETELAH editIndex diketahui agar judul header pas
        applyLanguage();

        if (editIndex == -1) {
            TextWatcher watcher = new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int a, int b, int c) {}
                @Override public void onTextChanged(CharSequence s, int a, int b, int c) {}
                @Override
                public void afterTextChanged(Editable s) {
                    draftPref.edit()
                            .putString("title", edtTitle.getText().toString())
                            .putString("content", edtContent.getText().toString())
                            .apply();
                }
            };
            edtTitle.addTextChangedListener(watcher);
            edtContent.addTextChangedListener(watcher);
        }

        btnSave.setOnClickListener(v -> saveNote());
        btnBack.setOnClickListener(v -> finish());
    }

    private void applyLanguage() {
        SharedPreferences pref = getSharedPreferences("UserSetting", MODE_PRIVATE);
        String country = pref.getString("country", "id");

        if (country.equals("ja")) {
            // Bahasa Jepang
            if (txtTitleBar != null) txtTitleBar.setText(editIndex == -1 ? "メモを追加" : "メモを編集");
            if (lblTitle != null) lblTitle.setText("タイトル");
            if (lblContent != null) lblContent.setText("内容");
            edtTitle.setHint("タイトルを入力...");
            edtContent.setHint("内容を入力...");
        } else if (country.equals("en")) {
            // Bahasa Inggris
            if (txtTitleBar != null) txtTitleBar.setText(editIndex == -1 ? "Add Note" : "Edit Note");
            if (lblTitle != null) lblTitle.setText("Title");
            if (lblContent != null) lblContent.setText("Content");
            edtTitle.setHint("Enter title...");
            edtContent.setHint("Enter content...");
        } else {
            // Bahasa Indonesia
            if (txtTitleBar != null) txtTitleBar.setText(editIndex == -1 ? "Tambah Catatan" : "Edit Catatan");
            if (lblTitle != null) lblTitle.setText("Judul");
            if (lblContent != null) lblContent.setText("Isi");
            edtTitle.setHint("Masukkan judul...");
            edtContent.setHint("Masukkan isi...");
        }
    }

    private void saveNote() {
        String title = edtTitle.getText().toString().trim();
        String content = edtContent.getText().toString().trim();

        if (title.isEmpty() && content.isEmpty()) {
            finish();
            return;
        }

        SharedPreferences pref = getSharedPreferences("UserSetting", MODE_PRIVATE);
        String country = pref.getString("country", "id");
        Locale currentLocale;

        if (country.equals("ja")) currentLocale = Locale.JAPAN;
        else if (country.equals("en")) currentLocale = Locale.UK;
        else currentLocale = new Locale("id", "ID");

        // Format tanggal otomatis mengikuti bahasa negara terpilih
        String date = new SimpleDateFormat("dd MMMM yyyy", currentLocale).format(new Date());

        Note note = new Note(title, date, content);

        String oldData = notePref.getString("data", "");
        String[] lines = oldData.isEmpty() ? new String[0] : oldData.split("\n");
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < lines.length; i++) {
            if (i == editIndex) sb.append(note.toStorage());
            else sb.append(lines[i]);
            sb.append("\n");
        }

        if (editIndex == -1) {
            sb.append(note.toStorage()).append("\n");
        }

        notePref.edit().putString("data", sb.toString().trim()).apply();
        draftPref.edit().clear().apply();
        setResult(RESULT_OK);
        finish();
    }
}