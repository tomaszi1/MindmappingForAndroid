package edu.agh.idziak.dropbox;

import android.os.AsyncTask;

import com.dropbox.client2.ProgressListener;
import com.dropbox.client2.exception.DropboxException;

import edu.agh.idziak.Utils;


public abstract class DbxTask<R> extends AsyncTask<Void, Long, R> {
    private final TaskListener<R, DropboxException> taskListener;
    private DropboxException exception;

    public DbxTask(TaskListener<R, DropboxException> taskListener) {
        Utils.checkNotNull(taskListener);
        this.taskListener = taskListener;
    }

    void setException(DropboxException ex) {
        exception = ex;
    }

    @Override
    protected void onPostExecute(R result) {
        super.onPostExecute(result);
        taskListener.onTaskDone(result, exception);
    }

    @Override
    protected void onProgressUpdate(Long... bytes) {
        super.onProgressUpdate(bytes);
        taskListener.publishProgress(bytes[0]);
    }

    protected ProgressListener getProgressListener() {
        return progressListener;
    }

    private final ProgressListener progressListener = new ProgressListener() {
        @Override
        public void onProgress(long bytes, long total) {
            publishProgress(bytes);
        }
    };

    public TaskCanceller getTaskCanceller() {
        return new TaskCanceller() {
            @Override
            public void cancel() {
                DbxTask.this.cancel(true);
            }
        };
    }
}
