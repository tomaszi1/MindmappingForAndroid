package idziak.dropbox;

import android.os.AsyncTask;

import com.dropbox.client2.ProgressListener;
import com.dropbox.client2.exception.DropboxException;

import idziak.Utils;

public abstract class DbxTask<R> extends AsyncTask<Void, Long, R> {
    private final ResultListener<R, DropboxException> resultListener;
    private DropboxException exception;

    public DbxTask(ResultListener<R, DropboxException> resultListener) {
        Utils.checkNotNull(resultListener);
        this.resultListener = resultListener;
    }

    void setException(DropboxException ex) {
        exception = ex;
    }

    @Override
    protected void onPostExecute(R result) {
        super.onPostExecute(result);
        if (exception != null)
            resultListener.taskFailed(exception);
        else
            resultListener.taskDone(result);
    }

    @Override
    protected void onProgressUpdate(Long... bytes) {
        super.onProgressUpdate(bytes);
        resultListener.publishProgress(bytes[0]);
    }

    protected ProgressListener getProgressListener() {
        return new ProgressListener() {
            @Override
            public void onProgress(long bytes, long total) {
                publishProgress(bytes);
            }
        };
    }

    public TaskCanceller getTaskCanceller() {
        return new TaskCanceller() {
            @Override
            public void cancel() {
                DbxTask.this.cancel(true);
            }
        };
    }
}
