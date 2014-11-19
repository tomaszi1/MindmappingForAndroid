package org.test.testing;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FileSaverActivity extends Activity {
    public static final String FILE = "file";
    private ListView listView;
    private FileArrayAdapter fileArrayAdapter;
    private EditText fileNameEditText;
    private File currentDir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_saver);

        File initialDir = Environment.getExternalStorageDirectory();
        currentDir = initialDir;
        fileNameEditText = (EditText) findViewById(R.id.file_name_edit_text);
        listView = (ListView) findViewById(R.id.files_list_view);
        fileArrayAdapter = new FileArrayAdapter(this, initialDir);
        listView.setAdapter(fileArrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                File file = (File) adapterView.getItemAtPosition(i);
                if (file.isDirectory())
                    fileArrayAdapter.listFolder(file);
                else {
                    showDialog(file);
                }
            }
        });
    }

    private void showDialog(final File file) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("Nadpisać plik?");
        builder.setPositiveButton("Tak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent result = new Intent().putExtra(FILE, file);
                setResult(RESULT_OK, result);
                dialogInterface.dismiss();
                finish();
            }
        });

        builder.setNegativeButton("Nie", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.create().show();
    }

    public void saveFileButtonClicked(View view) {
        String fileName = fileNameEditText.getText().toString();
        if (fileName == null || fileName.isEmpty())
            return;
        if (!fileName.endsWith(".xmind"))
            fileName = fileName + ".xmind";
        File file = new File(currentDir.getAbsolutePath() + "/" + fileName);
        Intent result = new Intent().putExtra(FILE, file);
        setResult(RESULT_OK, result);
        finish();
    }

    private class FileArrayAdapter extends ArrayAdapter<File> {
        public FileArrayAdapter(Context context, File dir) {
            super(context, android.R.layout.simple_list_item_1);
            add(dir.getParentFile());
            List<File> files = Arrays.asList(dir.listFiles(xmindFilter));
            Collections.sort(files,fileSorter);
            addAll(files);
        }

        public void listFolder(File dir) {
            currentDir = dir;
            clear();
            File parent = dir.getParentFile();
            if (parent != null)
                add(parent);
            List<File> files = Arrays.asList(dir.listFiles(xmindFilter));
            Collections.sort(files, fileSorter);
            addAll(files);
            notifyDataSetChanged();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            File file = getItem(position);
            View view = LayoutInflater.from(this.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
            TextView textView = (TextView) view.findViewById(android.R.id.text1);
            textView.setText(file.getName());
            return view;
        }
    }

    private static final FileFilter xmindFilter = new FileFilter() {
        @Override
        public boolean accept(File file) {
            return file.isDirectory() || file.getName().endsWith(".xmind");
        }
    };

    Comparator<File> fileSorter = new Comparator<File>() {
        @Override
        public int compare(File file, File file2) {
            if (file.isDirectory() && file2.isDirectory()) {
                return file.getName().compareToIgnoreCase(file2.getName());
            } else if (file.isDirectory()) {
                return -1;
            } else if (file2.isDirectory()) {
                return 1;
            }
            return file.getName().compareToIgnoreCase(file2.getName());
        }
    };
}
