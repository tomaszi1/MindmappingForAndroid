package org.test.testing;


import android.app.Application;

import idziak.dropbox.DropboxHandler;


public class App extends Application{
    public DropboxHandler dbxHandler;

    public DropboxHandler getDbxHandler(){
        if(dbxHandler==null)
            dbxHandler = new DropboxHandler(this);
        return dbxHandler;
    }
}
