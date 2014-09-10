package edu.agh.mindmapping;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.dropbox.sync.android.DbxAccount;
import com.dropbox.sync.android.DbxAccountManager;

import java.io.File;

import edu.agh.R;


public class MainActivity extends Activity {

	private static final int REQUEST_FILE = 1;
    private static final int REQUEST_LINK_DROPBOX_ACCOUNT = 2;
    private WorkbookManager workbookManager;

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
    
    public void onOpenFileButtonClick(View view){
    	Intent getFileToOpenIntent = new Intent(this, FileChooserActivity.class);
    	
    	startActivityForResult(getFileToOpenIntent, REQUEST_FILE);
    }


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode==REQUEST_FILE){
			if(resultCode==RESULT_OK){
				File chosenFile = (File) data.getExtras().get(FileChooserActivity.CHOSEN_FILE);
				Toast toast = Toast.makeText(this, "Wybrano: "+chosenFile.getAbsolutePath(), Toast.LENGTH_LONG);
				toast.show();
			}
		} else if (requestCode==REQUEST_LINK_DROPBOX_ACCOUNT) {
            if (resultCode==RESULT_OK){
                Log.i("DBX","Dropbox account linked successfully");
                Toast toast = Toast.makeText(this, "Powiązano z kontem Dropbox",Toast.LENGTH_SHORT);
                toast.show();
            } else {
                Log.i("DBX","Dropbox account not linked");
                Toast toast = Toast.makeText(this, "Nieudane powiązanie z kontem Dropbox",Toast.LENGTH_SHORT);
            }
        }
	}


    public void onCreateNewWorkbookButtonClick(View view) {
        workbookManager = WorkbookManager.createNewWorkbook();
        Log.i("CREATE","New workbook created");
    }

    public void onSaveCurrentWorkbookButtonClick(View view) {
        if(workbookManager != null){
            File dir = Environment.getExternalStorageDirectory();
            workbookManager.saveWorkbook(new File(dir.getAbsolutePath() + "/saved_file.xmind"));
        }
    }

    public void onConnectDropboxButtonClick(View view) {
        DbxAccountManager dbxAccountManager = DbxAccountManager.getInstance(getApplicationContext(), Utils.APP_KEY, Utils.APP_SECRET);
        DbxAccount linkedAccount = dbxAccountManager.getLinkedAccount();
        if(linkedAccount==null){
            Log.i("DBX","Starting Dropbox account linking service");
            dbxAccountManager.startLink(this,REQUEST_LINK_DROPBOX_ACCOUNT);
        } else {
            Toast toast = Toast.makeText(this, "Aplikacja jest już powiązana z kontem Dropbox", Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}
