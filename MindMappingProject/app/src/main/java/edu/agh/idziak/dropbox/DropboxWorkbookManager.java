package edu.agh.idziak.dropbox;


import android.os.AsyncTask;
import android.util.Log;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.exception.DropboxException;

import org.xmind.core.Core;
import org.xmind.core.CoreException;
import org.xmind.core.IWorkbook;
import org.xmind.core.io.ByteArrayStorage;
import org.xmind.core.io.IStorage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import edu.agh.idziak.Utils;

public class DropboxWorkbookManager {
    private static final String TAG = DropboxWorkbookManager.class.getSimpleName();
    private IWorkbook workbook;
    private IStorage tempStorage;
    private DbxBrowser.DbxFile dbxFile;
    private final DropboxHandler dbxHandler;

    private DropboxWorkbookManager(IWorkbook workbook, DbxBrowser.DbxFile dbxFile, DropboxHandler dbxHandler) {
        Utils.checkNotNull(workbook, dbxFile, dbxHandler);
        this.workbook = workbook;
        this.dbxFile = dbxFile;
        this.dbxHandler = dbxHandler;
        tempStorage = workbook.getTempStorage();
        if (!(tempStorage instanceof ByteArrayStorage)) {
            tempStorage = new ByteArrayStorage();
            workbook.setTempStorage(tempStorage);
        }
    }

    public IWorkbook getWorkbook() {
        return workbook;
    }

    public DbxBrowser.DbxFile getDropboxFile() {
        return dbxFile;
    }

    public void checkForNewVersion(final ResultListener<Boolean, DropboxException> resultListener) {
        Utils.checkNotNull(resultListener);
        dbxHandler.fetchFileInfo(dbxFile.getPath(), true, new ResultListener<DropboxAPI.Entry, DropboxException>() {
            @Override
            public void taskDone(DropboxAPI.Entry result) {
                resultListener.taskDone(!result.rev.equals(dbxFile.getRevision()));
            }

            @Override
            public void taskFailed(DropboxException exception) {
                resultListener.taskFailed(exception);
            }
        });
    }

    /**
     * Uploads file to Dropbox, creating it or overwriting existing file.
     *
     * @param resultListener methods taskDone or taskFailed are invoked if task wasn't cancelled through TaskCanceller.
     * @return TaskCanceller allows to cancel task at any time.
     */
    public TaskCanceller uploadWithOverwrite(ResultListener<Void, Exception> resultListener) {
        UploadCanceller canceller = new UploadCanceller();
        new WorkbookUploader(resultListener, canceller).execute();
        return canceller;
    }

    private class UploadCanceller extends TaskCanceller {
        private TaskCanceller dropboxTaskCanceller;
        private boolean cancelled = false;

        @Override
        public void cancel() {
            cancelled = true;
            if (dropboxTaskCanceller != null)
                dropboxTaskCanceller.cancel();
        }

        public boolean isCancelled() {
            return cancelled;
        }

        public void setDropboxTaskCanceller(TaskCanceller dropboxTaskCanceller) {
            this.dropboxTaskCanceller = dropboxTaskCanceller;
        }
    }

    /**
     * Binds a workbook instance to a dropbox file. Creates instance of DropboxWorkbookManager but does not upload a file.
     */
    public static DropboxWorkbookManager bindWorkbookToDropboxFile(IWorkbook workbook, DbxBrowser.DbxFile file, DropboxHandler handler) {
        if (file.isDir())
            throw new IllegalArgumentException("File is a directory");
        return new DropboxWorkbookManager(workbook, file, handler);
    }

    /**
     * Downloads workbook from Dropbox and provides <tt>DropboxWorkbookManager</tt> containing downloaded workbook.
     */
    public static void downloadWorkbook(DbxBrowser.DbxFile workbookFile, final ResultListener<DropboxWorkbookManager, Exception> resultListener, final DropboxHandler handler) {
        Utils.checkNotNull(workbookFile, resultListener, handler);
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        handler.downloadFile(workbookFile.getPath(), baos, new ResultListener<DropboxAPI.DropboxFileInfo, DropboxException>() {
            @Override
            public void taskDone(DropboxAPI.DropboxFileInfo result) {
                new WorkbookLoader(baos, result.getMetadata(), resultListener, handler).execute();
            }

            @Override
            public void taskFailed(DropboxException exception) {
                resultListener.taskFailed(exception);
            }
        });
    }

    private static class WorkbookLoader extends AsyncTask<Void, Void, DropboxWorkbookManager> {
        private final ByteArrayOutputStream baos;
        private final DropboxAPI.Entry dbxFile;
        private final ResultListener<DropboxWorkbookManager, Exception> resultListener;
        private final DropboxHandler dropboxHandler;
        private Exception ex;

        public WorkbookLoader(ByteArrayOutputStream input, DropboxAPI.Entry dbxFile, ResultListener<DropboxWorkbookManager, Exception> resultListener, DropboxHandler handler) {
            this.dbxFile = dbxFile;
            this.baos = input;
            this.resultListener = resultListener;
            this.dropboxHandler = handler;
        }

        @Override
        protected DropboxWorkbookManager doInBackground(Void... voids) {
            try {
                byte[] bytes = baos.toByteArray();
                IWorkbook workbook = Core.getWorkbookBuilder().loadFromStream(new ByteArrayInputStream(bytes), new ByteArrayStorage());
                return new DropboxWorkbookManager(workbook, new DbxBrowser.DbxFile(dbxFile), dropboxHandler);
            } catch (IOException e) {
                ex = e;
            } catch (CoreException e) {
                ex = e;
            }
            return null;
        }

        @Override
        protected void onPostExecute(DropboxWorkbookManager dropboxWorkbookManager) {
            if (dropboxWorkbookManager != null)
                resultListener.taskDone(dropboxWorkbookManager);
            else
                resultListener.taskFailed(ex);
        }
    }

    private class WorkbookUploader extends AsyncTask<Void, Void, Void> {
        private ResultListener<Void, Exception> resultListener;
        private UploadCanceller canceller;
        private Exception ex;

        public WorkbookUploader(ResultListener<Void, Exception> resultListener, UploadCanceller canceller) {
            this.resultListener = resultListener;
            this.canceller = canceller;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                workbook.save(baos);
                byte[] bytes = baos.toByteArray();
                ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
                if (canceller.isCancelled())
                    return null;
                TaskCanceller dbxCanceller = dbxHandler.uploadFile(dbxFile.getPath(), bais, bytes.length, new ResultListener<DropboxAPI.Entry, DropboxException>() {
                    @Override
                    public void taskDone(DropboxAPI.Entry result) {
                        if (canceller.isCancelled()) {
                            Log.wtf(TAG, "Cancel invoked after condition check, download was not cancelled!!!");
                            return;
                        }
                        dbxFile = new DbxBrowser.DbxFile(result);
                        resultListener.taskDone(null);
                    }

                    @Override
                    public void taskFailed(DropboxException exception) {
                        if (canceller.isCancelled()) {
                            Log.wtf(TAG, "Cancel invoked after condition check, download was not cancelled!!!");
                            return;
                        }
                        resultListener.taskFailed(exception);
                    }
                });
                canceller.setDropboxTaskCanceller(dbxCanceller);
            } catch (IOException e) {
                ex = e;
            } catch (CoreException e) {
                ex = e;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (ex != null)
                resultListener.taskFailed(ex);
        }
    }
}
