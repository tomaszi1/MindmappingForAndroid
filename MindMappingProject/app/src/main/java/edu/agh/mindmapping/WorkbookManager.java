package edu.agh.mindmapping;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.MissingResourceException;

import org.xmind.core.Core;
import org.xmind.core.CoreException;
import org.xmind.core.IWorkbook;
import org.xmind.core.IWorkbookBuilder;

import android.util.Log;

public class WorkbookManager {
	private IWorkbook workbook;
	private File storedFile;
	
	private WorkbookManager(){
	}
	
	public static WorkbookManager createNewWorkbook() {
		WorkbookManager wm = new WorkbookManager();
		IWorkbookBuilder builder = Core.getWorkbookBuilder();
		wm.workbook = builder.createWorkbook();
		return wm;
	}

    public static WorkbookManager loadFromFile(File xmindFile) throws IOException, CoreException {
        IWorkbookBuilder builder = Core.getWorkbookBuilder();
        WorkbookManager wm = new WorkbookManager();
        wm.workbook = builder.loadFromFile(xmindFile);
        wm.storedFile = xmindFile;
        return wm;
    }
	
	public IWorkbook getWorkbook(){
		return workbook;
	}
	
	public void saveWorkbook(File file){
		OutputStream stream = null;
		try {
			stream = new BufferedOutputStream(new FileOutputStream(file));
			workbook.save(stream);
            Log.i("SAVE","Workbook saved to: "+file.getAbsolutePath());
		} catch (FileNotFoundException e) {
			Log.e("SAVE", "Cannot save file: "+e.getMessage());
		} catch (IOException e) {
			Log.e("SAVE", "Error while saving: "+e.getMessage());
		} catch (CoreException e) {
			Log.e("SAVE", "XMind exception: "+e.getMessage());
		} finally {
			Utils.closeQuietly(stream);
		}
	}

    public void saveWorkbook() throws WorkbookException {
        if(storedFile==null)
            throw new WorkbookException("Workbook file not set");
        saveWorkbook(storedFile);
    }

    public File getFile(){
        return storedFile;
    }

    public boolean hasFile(){
        return storedFile!=null;
    }
}
