package edu.agh.idziak.dropbox;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.DropboxFileInfo;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.DropboxAPI.UploadRequest;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AppKeyPair;

import java.io.InputStream;
import java.io.OutputStream;

import edu.agh.idziak.Utils;

public class DropboxHandler {
    private static final String TAG = "DropboxManager";

    private static final String APP_KEY = "2i1l1l9s6pymrgd";
    private static final String APP_SECRET = "vhlayhvmxdy1dsy";

    private static final String ACCOUNT_PREFS_NAME = "dropbox_prefs";
    private static final String ACCESS_SECRET_NAME = "DROPBOX_ACCESS_SECRET";

    private DropboxAPI<AndroidAuthSession> dbxApi;
    private Context context;

    public DropboxHandler(Context context) {
        if (context == null)
            throw new NullPointerException("Application context was null");
        AndroidAuthSession session = new AndroidAuthSession(new AppKeyPair(APP_KEY, APP_SECRET));
        this.context = context;
        loadAuth(session);
        dbxApi = new DropboxAPI<AndroidAuthSession>(session);
    }

    /**
     * Starts activity in which user allows app to access Dropbox.
     * After authentication callbackContext is run.
     * This method doesn't do anything if account is already linked!
     * @param callbackContext activity to which the result is returned
     */
    public void linkAccount(Context callbackContext) {
        if (dbxApi.getSession().isLinked()) {
            Log.i(TAG, "Account already linked");
            return;
        }
        dbxApi.getSession().startOAuth2Authentication(callbackContext);
    }

    /**
     * Deletes link to Dropbox.
     */
    public void unlinkAccount() {
        dbxApi.getSession().unlink();
        SharedPreferences prefs = context.getSharedPreferences(ACCOUNT_PREFS_NAME, Context.MODE_PRIVATE);
        Editor e = prefs.edit();
        e.clear();
        e.apply();
    }

    /**
     * Needs to be called in onResume method to finish authentication.
     */
    public void onResume() {
        AndroidAuthSession session = dbxApi.getSession();
        if (session.authenticationSuccessful()) {
            try {
                session.finishAuthentication();
                storeAuth(session);
            } catch (IllegalStateException ex) {
                Log.i(TAG, "Authentication error", ex);
                throw ex;
            }
        }
    }

    private void loadAuth(AndroidAuthSession session) {
        SharedPreferences prefs = context.getSharedPreferences(ACCOUNT_PREFS_NAME, Context.MODE_PRIVATE);
        String secret = prefs.getString(ACCESS_SECRET_NAME, null);
        if (secret == null || secret.length() == 0) return;
        session.setOAuth2AccessToken(secret);
    }

    private void storeAuth(AndroidAuthSession session) {
        String accessToken = session.getOAuth2AccessToken();
        if (accessToken != null) {
            SharedPreferences prefs = context.getSharedPreferences(ACCOUNT_PREFS_NAME, Context.MODE_PRIVATE);
            Editor edit = prefs.edit();
            edit.putString(ACCESS_SECRET_NAME, accessToken);
            edit.apply();
        } else
            Log.w(TAG, "Access token was null");
    }

    /**
     * Checks if app is linked to Dropbox.
     * @return True if app is linked to Dropbox, false otherwise.
     */
    public boolean isLinked() {
        return dbxApi.getSession().isLinked();
    }

    /**
     * Fetches info about file or folder under given path asynchronously.
     * @param path path to file at Dropbox
     * @param listFolder true if content of given folder should be fetched
     * @param resultListener taskDone is called if fetch was successful, otherwise taskFailed is called
     */
    public void fetchFileInfo(String path, boolean listFolder, ResultListener<Entry, DropboxException> resultListener) {
        new MetadataFetcher(path, listFolder, resultListener).execute();
    }

    /**
     * Downloads file content from Dropbox.
     * @param path path to file which should be downloaded.
     * @param dest stream into which data will be written.
     * @param resultListener taskDone or taskFailed are called after download.
     */
    public void downloadFile(String path, OutputStream dest, ResultListener<DropboxFileInfo, DropboxException> resultListener) {
        new FileDownloader(path, dest, resultListener).execute();
    }

    /**
     * Uploads file to Dropbox.
     * @param dropboxPath path to which file should be uploaded.
     * @param source stream from which data will be read and uploaded.
     * @param inputLength number of bytes in uploaded file.
     * @param resultListener taskDone or taskFailed are called after upload.
     * @return TaskCanceller allows to cancel upload in progress.
     */
    public TaskCanceller uploadFile(String dropboxPath, InputStream source, long inputLength, ResultListener<Entry, DropboxException> resultListener){
        FileUploader uploader = new FileUploader(dropboxPath, source, inputLength, resultListener);
        uploader.execute();
        return uploader.getTaskCanceller();
    }

    /**
     * Creates folder in Dropbox.
     * @param path path to folder.
     * @param resultListener taskDone or taskFailed are called after creation. TaskDone method provides folder info.
     */
    public void createFolder(String path, ResultListener<Entry, DropboxException> resultListener){
        new FolderCreator(path, resultListener).execute();
    }

    private class MetadataFetcher extends DbxTask<Entry> {
        private final String path;
        private final boolean listFolder;

        private MetadataFetcher(String path, boolean listFolder, ResultListener<Entry, DropboxException> listener) {
            super(listener);
            Utils.checkNotNull(path, listener);
            this.path = path;
            this.listFolder = listFolder;
        }

        @Override
        protected Entry doInBackground(Void... voids) {
            if (!DropboxHandler.this.isLinked())
                return null;
            try {
                Log.i(TAG, "Starting metadata fetch");
                return dbxApi.metadata(path, 1000, null, listFolder, null);
            } catch (DropboxException e) {
                Log.i(TAG, "Metadata fetch failed", e);
                setException(e);
            }
            return null;
        }

    }

    private class FileDownloader extends DbxTask<DropboxFileInfo> {
        private final String dbxPath;
        private final OutputStream out;

        private FileDownloader(String dbxPath, OutputStream out, ResultListener<DropboxFileInfo, DropboxException> resultListener) {
            super(resultListener);
            Utils.checkNotNull(dbxPath, out, resultListener);
            this.dbxPath = dbxPath;
            this.out = out;
        }

        @Override
        protected DropboxFileInfo doInBackground(Void... voids) {
            try {
                return DropboxHandler.this.dbxApi.getFile(dbxPath, null, out, getProgressListener());
            } catch (DropboxException ex) {
                Log.i(TAG, "Download failed", ex);
                setException(ex);
            } finally {
                Utils.closeQuietly(out);
            }
            return null;
        }
    }

    private class FileUploader extends DbxTask<Entry> {
        private final String path;
        private final InputStream input;
        private final long inputLength;
        private UploadRequest req;

        private FileUploader(String dropboxPath, InputStream input, long inputLength, ResultListener<Entry, DropboxException> listener) {
            super(listener);
            this.input = input;
            this.path = dropboxPath;
            this.inputLength = inputLength;
        }

        @Override
        protected Entry doInBackground(Void... voids) {
            try{
                req = dbxApi.putFileOverwriteRequest(path, input, inputLength, getProgressListener());
                return req.upload();
            }catch (DropboxException ex){
                Log.i(TAG, "Upload failed", ex);
                setException(ex);
            }
            return null;
        }

        @Override
        protected void onCancelled() {
            req.abort();
        }

    }

    private class FolderCreator extends DbxTask<Entry>{
        private final String path;

        private FolderCreator(String path, ResultListener<Entry, DropboxException> resultListener) {
            super(resultListener);
            this.path = path;
        }

        @Override
        protected Entry doInBackground(Void... voids) {
            try {
                return dbxApi.createFolder(path);
            } catch (DropboxException e) {
                setException(e);
            }
            return null;
        }
    }

}

