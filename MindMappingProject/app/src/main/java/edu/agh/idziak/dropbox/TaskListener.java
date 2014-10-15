package edu.agh.idziak.dropbox;

public abstract class TaskListener<T, E extends Exception> {
    public abstract void onTaskDone(T result, E exception);
    public void publishProgress(long bytes){};
}
