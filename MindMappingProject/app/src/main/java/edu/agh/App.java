package edu.agh;


import android.app.Application;

import edu.agh.idziak.dropbox.DropboxHandler;

public class App extends Application{
    public DropboxHandler dbxHandler;

    public DropboxHandler getDbxHandler(){
        if(dbxHandler==null)
            dbxHandler = new DropboxHandler(this);
        return dbxHandler;
    }
}
