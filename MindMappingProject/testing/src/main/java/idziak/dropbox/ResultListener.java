package idziak.dropbox;

public abstract class ResultListener<T, E extends Exception> {
    public abstract void taskDone(T result);
    public abstract void taskFailed(E exception);
    public void publishProgress(long bytes){};
}
