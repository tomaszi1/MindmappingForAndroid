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

    public void linkAccount(Context callbackContext) {
        if (dbxApi.getSession().isLinked()) {
            Log.i(TAG, "Account already linked");
            return;
        }
        dbxApi.getSession().startOAuth2Authentication(callbackContext);
    }

    public void unlinkAccount() {
        dbxApi.getSession().unlink();
        SharedPreferences prefs = context.getSharedPreferences(ACCOUNT_PREFS_NAME, Context.MODE_PRIVATE);
        Editor e = prefs.edit();
        e.clear();
        e.apply();
    }

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

    public boolean isLinked() {
        return dbxApi.getSession().isLinked();
    }

    public void fetchFileInfo(String path, boolean listFolder, TaskListener<Entry, DropboxException> listener) {
        new MetadataFetcher(path, listFolder, listener).execute();
    }

    public void downloadFile(String dropboxPath, OutputStream dest, TaskListener<DropboxFileInfo, DropboxException> taskListener) {
        new FileDownloader(dropboxPath, dest, taskListener).execute();
    }

    public TaskCanceller uploadFile(String dropboxPath, InputStream source, long inputLength, TaskListener<Boolean, DropboxException> taskListener){
        FileUploader fu = new FileUploader(dropboxPath, source, inputLength, taskListener);
        fu.execute();
        return fu.getTaskCanceller();
    }

    private class MetadataFetcher extends DbxTask<Entry> {
        private final String path;
        private final boolean listFolder;

        private MetadataFetcher(String path, boolean listFolder, TaskListener<Entry, DropboxException> listener) {
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

        private FileDownloader(String dbxPath, OutputStream out, TaskListener<DropboxFileInfo, DropboxException> taskListener) {
            super(taskListener);
            Utils.checkNotNull(dbxPath, out, taskListener);
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

    private class FileUploader extends DbxTask<Boolean> {
        private final String path;
        private final InputStream input;
        private final long inputLength;
        private UploadRequest req;

        private FileUploader(String dropboxPath, InputStream input, long inputLength, TaskListener<Boolean, DropboxException> listener) {
            super(listener);
            this.input = input;
            this.path = dropboxPath;
            this.inputLength = inputLength;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try{
                req = DropboxHandler.this.dbxApi.putFileOverwriteRequest(path, input, inputLength, getProgressListener());
                req.upload();
            }catch (DropboxException ex){
                Log.i(TAG, "Upload failed", ex);
                setException(ex);
            }
            return null;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            req.abort();
        }
    }

}

