package edu.agh.idziak;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.Arrays;

import edu.agh.R;


public class FileBrowserActivity extends Activity {

    public static final String SELECTED_FILE = "selected_file";
    private ListView listView;
    private FileArrayAdapter fileArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_browser);

        File initialDir = Environment.getExternalStorageDirectory();

        listView = (ListView) findViewById(R.id.files_list_view);
        fileArrayAdapter  =new FileArrayAdapter(this, initialDir);
        listView.setAdapter(fileArrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                File file = (File) adapterView.getItemAtPosition(i);
                if(file.isDirectory())
                    fileArrayAdapter.listFolder(file);
                else{
                    Intent result = new Intent().putExtra(SELECTED_FILE,file);
                    setResult(RESULT_OK, result);
                    finish();
                }
            }
        });
    }

    private class FileArrayAdapter extends ArrayAdapter<File> {
        public FileArrayAdapter(Context context, File dir) {
            super(context, android.R.layout.simple_list_item_1);
            add(dir.getParentFile());
            File[] files = dir.listFiles();
            addAll(Arrays.asList(files));
        }

        public void listFolder(File dir) {
            clear();
            File parent = dir.getParentFile();
            if (parent != null)
                add(parent);
            addAll(Arrays.asList(dir.listFiles()));
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


}
