package edu.agh.idziak.dropbox;


import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.exception.DropboxException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class DbxBrowser {
    private static final String DROPBOX_ROOT = "/";
    private static final String XMIND_EXTENSION = ".xmind";

    private DropboxHandler dbxHandler;
    private DbxFile currentDir;

    public DbxBrowser(DropboxHandler dbxHandler) {
        this.dbxHandler = dbxHandler;
    }

    public void goToRootDir(final ResultListener<DbxFile, DropboxException> listener) {
        dbxHandler.fetchFileInfo(DROPBOX_ROOT, true, new FolderChanger(listener));
    }

    public boolean isInRootDir() {
        return currentDir.isRoot();
    }

    /**
     * Fetches content of given <tt>dir</tt> and sets it as current folder.
     */
    public void changeDir(DbxFile dir, final ResultListener<DbxFile, DropboxException> listener) {
        if (dir == null || !dir.isDir())
            throw new IllegalArgumentException("Null or not a directory");
        dbxHandler.fetchFileInfo(dir.getPath(), true, new FolderChanger(listener));
    }

    /**
     * Returns a parent of current directory. Returned instance is not listable (use <tt>changeDir</tt> to list files).
     */
    public DbxFile getParentDir() {
        String parentPath = currentDir.getParentPath();
        if (parentPath.isEmpty())
            return null;
        return new DbxFile(parentPath, true);
    }

    /**
     * Returns content of current directory.
     */
    public List<DbxFile> listFiles() {
        return new ArrayList<DbxFile>(currentDir.getContents());
    }

    /**
     * Creates new dropbox file in current directory, but file is not uploaded. Create DropboxWorkbookManager with this file to upload.
     */
    public DbxFile createNewFile(String fileName) {
        return new DbxFile(appendNameToPath(currentDir.getPath(), fileName), false);
    }

    /**
     * Creates new directory inside current directory.
     * @param dirName name of directory
     * @param resultListener listener providing new directory instance
     * @return false if directory already exists (creation is cancelled), otherwise true
     */
    public boolean createNewDir(String dirName, final ResultListener<DbxFile, DropboxException> resultListener) {
        if (currentDir.contains(dirName))
            return false;
        String path = appendNameToPath(currentDir.getPath(),dirName);
        ResultListener<DropboxAPI.Entry, DropboxException> folderCreateListener = new ResultListener<DropboxAPI.Entry, DropboxException>() {
            @Override
            public void taskDone(DropboxAPI.Entry result) {
                DbxFile newFolder = new DbxFile(result);
                currentDir.contents.add(newFolder);
                resultListener.taskDone(newFolder);
            }

            @Override
            public void taskFailed(DropboxException exception) {
                resultListener.taskFailed(exception);
            }
        };
        dbxHandler.createFolder(path, folderCreateListener);
        return true;
    }

    public DbxFile getCurrentDir() {
        return currentDir;
    }

    /**
     * Each instance represents a file at Dropbox.
     */
    public static class DbxFile implements Serializable {
        private final String fileName;
        private final String revision;
        private final String parentPath;
        private final boolean isDir;
        private final String fullPath;
        private final long size;
        private final String modified;
        private List<DbxFile> contents;

        DbxFile(DropboxAPI.Entry file) {
            fileName = file.fileName();
            revision = file.rev;
            parentPath = file.parentPath();
            isDir = file.isDir;
            fullPath = file.path;
            size = file.bytes;
            modified = file.modified;
            if (file.contents != null) {
                contents = new LinkedList<DbxFile>();
                for (DropboxAPI.Entry e : file.contents)
                    contents.add(new DbxFile(e));
            }
        }

        private DbxFile(String path, boolean isDir) {
            if (path.equals(DROPBOX_ROOT)) {
                this.parentPath = "";
                this.fileName = DROPBOX_ROOT;
            } else {
                int slash = path.lastIndexOf("/");
                this.fileName = path.substring(slash + 1);
                if (slash == 0)
                    this.parentPath = "/";
                else
                    this.parentPath = path.substring(0, slash);
            }
            this.revision = "";
            this.isDir = isDir;
            this.fullPath = path;
            this.size = 0;
            this.modified = "";
        }

        public boolean isDir() {
            return isDir;
        }

        public String getRevision() {
            return revision;
        }

        public long getSize() {
            return size;
        }

        public String getParentPath() {
            return parentPath;
        }

        public String getPath() {
            return fullPath;
        }

        public List<DbxFile> getContents() {
            return contents;
        }

        public boolean isRoot() {
            return fileName.equals(DROPBOX_ROOT);
        }

        public String getName() {
            return fileName;
        }

        public String getModifiedTime() {
            return modified; // FIXME: should return timestamp
        }

        public boolean contains(String fileName) {
            for (DbxFile file : contents)
                if (file.getName().equals(fileName))
                    return true;
            return false;
        }
    }

    private String appendNameToPath(String path, String name){
        return path.endsWith("/") ? path + name : path + "/" + name;
    }

    private class FolderChanger extends ResultListener<DropboxAPI.Entry, DropboxException> {
        private final ResultListener<DbxFile, DropboxException> listener;

        private FolderChanger(ResultListener<DbxFile, DropboxException> listener) {
            this.listener = listener;
        }

        @Override
        public void taskDone(DropboxAPI.Entry result) {
            currentDir = new DbxFile(result);
            listener.taskDone(currentDir);
        }

        @Override
        public void taskFailed(DropboxException exception) {
            listener.taskFailed(exception);
        }
    }
}
