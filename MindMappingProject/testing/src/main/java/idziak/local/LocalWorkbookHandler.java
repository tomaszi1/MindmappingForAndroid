package idziak.local;

import android.os.AsyncTask;
import android.util.Log;

import org.xmind.core.Core;
import org.xmind.core.CoreException;
import org.xmind.core.IRevision;
import org.xmind.core.IRevisionManager;
import org.xmind.core.IRevisionRepository;
import org.xmind.core.ISheet;
import org.xmind.core.IWorkbook;
import org.xmind.core.io.ByteArrayStorage;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import idziak.Utils;
import idziak.dropbox.ResultListener;


public class LocalWorkbookHandler {
    public static final String TAG = LocalWorkbookHandler.class.getSimpleName();

    public void saveWorkbook(File file, IWorkbook workbook, ResultListener<Void, Exception> resultListener) {
        Utils.checkNotNull(file, workbook, resultListener);
        new WorkbookSaver(file, workbook, resultListener).execute();
    }

    public static void loadWorkbook(File file, ResultListener<IWorkbook, Exception> resultListener) {
        Utils.checkNotNull(file, resultListener);
        new WorkbookLoader(file, resultListener).execute();
    }

    public static IWorkbook createNewWorkbook() {
        IWorkbook workbook = Core.getWorkbookBuilder().createWorkbook();
        workbook.setTempStorage(new ByteArrayStorage());
        return workbook;
    }

    private static class WorkbookSaver extends AsyncTask<Void, Void, Void> {
        public final String TAG = LocalWorkbookHandler.TAG + "." + WorkbookSaver.class.getSimpleName();
        private final ResultListener<Void, Exception> resultListener;
        private Exception ex;
        private final IWorkbook workbook;
        private final File file;

        public WorkbookSaver(File file, IWorkbook workbook, ResultListener<Void, Exception> resultListener) {
            this.resultListener = resultListener;
            this.workbook = workbook;
            this.file = file;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                IRevisionRepository revRep = workbook.getRevisionRepository();
                for (ISheet sheet : workbook.getSheets()) {
                    IRevisionManager revMan = revRep.getRevisionManager(sheet.getId(), IRevision.SHEET);
                    revMan.addRevision(sheet);
                }
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

    private static class WorkbookLoader extends AsyncTask<Void, Void, IWorkbook> {
        private final String TAG = LocalWorkbookHandler.TAG + "." + WorkbookLoader.class.getSimpleName();
        private ResultListener<IWorkbook, Exception> resultListener;
        private File file;
        private Exception ex;

        public WorkbookLoader(File file, ResultListener<IWorkbook, Exception> resultListener) {
            this.file = file;
            this.resultListener = resultListener;
        }

        @Override
        protected IWorkbook doInBackground(Void... voids) {
            try {
                return Core.getWorkbookBuilder().loadFromStream(new BufferedInputStream(new FileInputStream(file)), new ByteArrayStorage());
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
        protected void onPostExecute(IWorkbook workbook) {
            if (workbook == null)
                resultListener.taskFailed(ex);
            else
                resultListener.taskDone(workbook);
        }
    }


}
