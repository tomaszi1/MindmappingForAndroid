package edu.agh.klaukold.interfaces;

import java.util.Properties;

public interface Command  {
	public void  execute(Properties properties);
    public void undo();
}
