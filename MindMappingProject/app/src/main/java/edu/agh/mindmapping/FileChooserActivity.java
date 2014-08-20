package edu.agh.mindmapping;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.io.FileFilter;


public class FileChooserActivity extends Activity {

    ListAdapter filesListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_chooser);

        loadFilesList();
    }

    private void loadFilesList() {
        String storageState = Environment.getExternalStorageState();

        if (storageState.equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {
            Log.d("LOADING", "Loading files");
            File sd = Environment.getExternalStorageDirectory();
            File[] sdDirList = sd.listFiles(xmindFilter);
            filesListAdapter = new FileListAdapter(this, sdDirList);
            ListView filesListView = (ListView) findViewById(R.id.filesListView);
            filesListView.setAdapter(filesListAdapter);
            filesListView.setOnItemClickListener(itemClickListener);
            Log.d("LOADING", "Files loaded");
        } else {
            Log.e("LOADING", "Storage not mounted");
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.file_browser, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    FileFilter xmindFilter = new FileFilter() {
        @Override
        public boolean accept(File file) {
            return file.isDirectory() || file.getName().endsWith(".xmind");
        }
    };

    AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            File clickedFile = (File) adapterView.getItemAtPosition(position);
            if (clickedFile.isDirectory()) {
                Log.d("CLICK","Clicked directory");
            } else {
                Log.d("CLICK","Clicked file");
            }
        }
    };

    private class FileListAdapter extends ArrayAdapter<File> {
        private FileListAdapter(Context context, File[] files) {
            super(context, R.layout.row_layout, files);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            View view = inflater.inflate(R.layout.row_layout, parent, false);
            File file = getItem(position);
            TextView textView = (TextView) findViewById(R.id.fileNameTextView);
            textView.setText(file.getName());
            if(!file.isDirectory()){
                ImageView imageView = (ImageView) findViewById(R.id.fileImageView);
                imageView.setImageResource(R.drawable.xmind_icon);
            }
            return view;
        }
    }
}

