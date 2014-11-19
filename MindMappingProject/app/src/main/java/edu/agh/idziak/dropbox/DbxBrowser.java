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

    /**
     * Fetches root folder from Dropbox. Should be called after creation of DbxBrowser.
     */
    public void goToRootDir(final ResultListener<DbxFile, DropboxException> listener) {
        dbxHandler.fetchFileInfo(DROPBOX_ROOT, true, new FolderChanger(listener));
    }

    public boolean isInRootDir() {
        if (currentDir == null)
            return false;
        return currentDir.isRoot();
    }

    public DbxFile getCurrentDir() {
        return currentDir;
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
     * Creates new dropbox file in current directory, but file is not uploaded. Create DropboxWorkbookManager with this file to upload.
     */
    public DbxFile createNewFile(String fileName) {
        return new DbxFile(appendNameToPath(currentDir.getPath(), fileName), false);
    }

    /**
     * Creates new directory inside current directory and updates its content.
     *
     * @param dirName        name of directory
     * @param resultListener listener providing new directory instance
     * @return false if directory already exists (creation is cancelled), otherwise true
     */
    public boolean createNewDir(String dirName, final ResultListener<DbxFile, DropboxException> resultListener) {
        if (currentDir.contains(dirName))
            return false;
        String path = appendNameToPath(currentDir.getPath(), dirName);
        dbxHandler.createFolder(path, new ResultListener<DropboxAPI.Entry, DropboxException>() {
            @Override
            public void taskDone(DropboxAPI.Entry result) {
                DbxFile newFolder = new DbxFile(result);
                currentDir.contents.add(newFolder);
                resultListener.taskDone(newFolder);
            }

            @Override
            public void taskFailed(DropboxException exception) {
                taskFailed(exception);
            }
        });
        return true;
    }

    /**
     * Each instance represents a file at Dropbox.
     */
    public static class DbxFile implements Serializable {
        private static final DropboxFilesFilter defaultFilter = new XMindFilesFilter();
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
                parentPath = "";
                fileName = DROPBOX_ROOT;
            } else {
                String[] split = path.split("/");
                fileName = split[split.length - 1];
                parentPath = path.substring(0, path.lastIndexOf(fileName));
            }
            revision = "";
            this.isDir = isDir;
            fullPath = path;
            size = 0;
            modified = "";
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

        /**
         * Returns directories and files with .xmind extension.
         */
        public List<DbxFile> getContents() {
            return defaultFilter.filter(contents);
        }

        /**
         * Returns all files if null is passed. Otherwise filters files with given filter.
         */
        public List<DbxFile> getContents(DropboxFilesFilter filter) {
            if(filter==null)
                return new ArrayList<DbxFile>(contents);
            return filter.filter(new ArrayList<DbxFile>(contents));
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

    private static String appendNameToPath(String path, String name) {
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

    public static class XMindFilesFilter implements DropboxFilesFilter{
        public List<DbxFile> filter(List<DbxFile> files){
            ArrayList<DbxFile> filtered = new ArrayList<DbxFile>();
            for(DbxFile file : files){
                if(file.isDir() || file.getName().endsWith(".xmind"))
                    filtered.add(file);
            }
            return filtered;
        }
    }

    public interface DropboxFilesFilter{
        public List<DbxFile> filter(List<DbxFile> files);
    }
}
