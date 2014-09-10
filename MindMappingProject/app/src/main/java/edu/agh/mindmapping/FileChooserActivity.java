package edu.agh.mindmapping;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import edu.agh.R;


public class FileChooserActivity extends Activity {

	public static final String CHOSEN_FILE = "chosen_file";
    FileListAdapter filesListAdapter;
    File currentDir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_chooser);

        loadFilesList();
    }

    private void loadFilesList() {
        String storageState = Environment.getExternalStorageState();
        if (storageState.equals(Environment.MEDIA_MOUNTED) || storageState.equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {
            File sd = Environment.getExternalStorageDirectory();
            currentDir = sd;
            File[] sdDirList = sd.listFiles(xmindFilesFilter);
            List<File> filesList = new ArrayList<File>(Arrays.asList(sdDirList));
            filesListAdapter = new FileListAdapter(this, filesList);
            ListView filesListView = (ListView) findViewById(R.id.files_list_view);
            filesListView.setAdapter(filesListAdapter);
            filesListView.setOnItemClickListener(itemClickListener);
        } else {
            Log.e("LOADING", "Storage not mounted");
        }
    }
    
    public void onGoUpButtonClick(View view){
    	currentDir = currentDir.getParentFile();
    	refreshListAdapter();
    }

    private void refreshListAdapter() {
    	if(!currentDir.isDirectory()){
    		Log.e("ASSERT", "currentDir is not a dir");
    		return;
    	}
    	File[] dirContent = currentDir.listFiles(xmindFilesFilter);
    	filesListAdapter.clear();
        filesListAdapter.addAll(Arrays.asList(dirContent));
        filesListAdapter.notifyDataSetChanged();
	}

	FileFilter xmindFilesFilter = new FileFilter() {
        @Override
        public boolean accept(File file) {
            return (file.isDirectory() || file.getName().endsWith(".xmind")) && !file.isHidden();
        }
    };

    AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            File clickedFile = (File) adapterView.getItemAtPosition(position);
            if (clickedFile.isDirectory()) {
                currentDir = clickedFile;
                refreshListAdapter();
            } else {
                Intent resultIntent = new Intent();
                resultIntent.putExtra(CHOSEN_FILE, clickedFile);
                setResult(Activity.RESULT_OK,resultIntent);
                finish();
            }
        }
    };

    private class FileListAdapter extends ArrayAdapter<File> {
        private FileListAdapter(Context context, List<File> files) {
            super(context, R.layout.row_layout, files);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            View view = inflater.inflate(R.layout.row_layout, parent, false);
            File file = getItem(position);
            TextView textView = (TextView) view.findViewById(R.id.file_name_text_view);
            textView.setText(file.getName());
            if(!file.isDirectory()){
                ImageView imageView = (ImageView) view.findViewById(R.id.file_icon_view);
                imageView.setImageResource(R.drawable.xmind_icon);
            }
            return view;
        }
    }
}

