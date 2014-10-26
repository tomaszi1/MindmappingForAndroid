package edu.agh.idziak.local;

import android.os.AsyncTask;
import android.util.Log;

import org.xmind.core.Core;
import org.xmind.core.CoreException;
import org.xmind.core.IWorkbook;
import org.xmind.core.io.ByteArrayStorage;
import org.xmind.core.io.IStorage;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import edu.agh.idziak.Utils;
import edu.agh.idziak.dropbox.ResultListener;

public class LocalWorkbookManager {
    public static final String TAG = LocalWorkbookManager.class.getSimpleName();
    private IWorkbook workbook;
    private File file;

    private LocalWorkbookManager(IWorkbook workbook, File file) {
        this.workbook = workbook;
        this.file = file;
    }

    private void saveWorkbook(ResultListener<Void, Exception> resultListener){
        new WorkbookSaver(resultListener).execute();
    }

    public static void loadWorkbook(File workbookFile, ResultListener<LocalWorkbookManager, Exception> resultListener) {
        Utils.checkNotNull(workbookFile, resultListener);
        new WorkbookLoader(workbookFile, resultListener).execute();
    }

    private class WorkbookSaver extends AsyncTask<Void, Void, Void>{
        public final String TAG = LocalWorkbookManager.TAG + "." + WorkbookSaver.class.getSimpleName();
        private ResultListener<Void, Exception> resultListener;
        private Exception ex;

        public WorkbookSaver(ResultListener<Void, Exception> resultListener){
            this.resultListener = resultListener;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                workbook.save(new FileOutputStream(file));
            } catch (IOException e) {
                ex = e;
                Log.i(TAG, "Save error", e);
            } catch (CoreException e) {
                ex = e;
                Log.i(TAG, "Save error", e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            if (ex != null)
                resultListener.taskFailed(ex);
            else
                resultListener.taskDone(null);
        }
    }

    private static class WorkbookLoader extends AsyncTask<Void, Void, LocalWorkbookManager> {
        private final String TAG = LocalWorkbookManager.TAG + "." + WorkbookLoader.class.getSimpleName();
        private ResultListener<LocalWorkbookManager, Exception> resultListener;
        private File file;
        private Exception ex;

        public WorkbookLoader(File file, ResultListener<LocalWorkbookManager, Exception> resultListener) {
            this.file = file;
            this.resultListener = resultListener;
        }

        @Override
        protected LocalWorkbookManager doInBackground(Void... voids) {
            try {
                IStorage storage = new ByteArrayStorage();
                IWorkbook workbook = Core.getWorkbookBuilder().loadFromStream(new BufferedInputStream(new FileInputStream(file)), storage);
                return new LocalWorkbookManager(workbook, file);
            } catch (IOException e) {
                Log.e(TAG, "Error while loading file", e);
                ex = e;
            } catch (CoreException e) {
                Log.e(TAG, "Incorrect XMind file", e);
                ex = e;
            }
            return null;
        }

        @Override
        protected void onPostExecute(LocalWorkbookManager localWorkbookManager) {
            if (localWorkbookManager == null)
                resultListener.taskFailed(ex);
            else
                resultListener.taskDone(localWorkbookManager);
        }
    }
}
