package edu.agh.mindmapping;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.xmind.core.Core;
import org.xmind.core.CoreException;
import org.xmind.core.IWorkbook;
import org.xmind.core.IWorkbookBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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

    public void exampleSaveToSdcard(View view) {
        String esState = Environment.getExternalStorageState();
        if(esState.equals(Environment.MEDIA_MOUNTED)){
            Log.i("SAVE", "storage mounted, saving");
            File sdCard = Environment.getExternalStorageDirectory();

            try {
                IWorkbookBuilder builder = Core.getWorkbookBuilder();
                IWorkbook workbook = builder.createWorkbook();

                String location = sdCard.getAbsolutePath()+"/exampleFile.xmind";
                Log.i("SAVE","saving at location "+location);
                File file = new File(location);
                FileOutputStream fos = new FileOutputStream(file);
                workbook.save(fos);
                Log.i("SAVE","save successful");
                fos.close();
            } catch(CoreException e){
                Log.e("SAVE",e.toString());
            } catch(IOException e){
                Log.e("SAVE",e.toString());
            }
        }else{
            Log.i("SAVE","storage not mounted");
        }
    }


    public void onOpenFileBtnClick(View view) {
        Intent fileChooserIntent = 
    }
}
