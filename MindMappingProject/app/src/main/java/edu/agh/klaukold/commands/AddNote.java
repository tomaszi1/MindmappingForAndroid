package edu.agh.klaukold.commands;

import org.xmind.core.INotes;
import org.xmind.core.IPlainNotesContent;

import java.util.Properties;

import edu.agh.klaukold.common.Box;
import edu.agh.klaukold.gui.MainActivity;
import edu.agh.klaukold.interfaces.Command;

public class AddNote implements Command {
    Properties before;
    Properties after;
    Box box;

    @Override
    public void undo() {
        String note = (String) before.get("text");
        IPlainNotesContent plainContent = (IPlainNotesContent) MainActivity.workbook.createNotesContent(INotes.PLAIN);
        plainContent.setTextContent(note);
        box.topic.getNotes().setContent(INotes.PLAIN, plainContent);
    }

    @Override
    public void redo() {
        execute(after);
    }

    @Override
	public void execute(Properties properties) {
		before = (Properties) properties.clone();
        after = (Properties) properties.clone();
		box = (Box) properties.get("box");
        if (box.topic.getNotes().getContent(INotes.PLAIN) != null) {
            before.put("text", ((IPlainNotesContent)box.topic.getNotes().getContent(INotes.PLAIN)).getTextContent() );
        } else {
            before.put("text", "");
        }
        String note = (String) properties.get("text");
        IPlainNotesContent plainContent = (IPlainNotesContent) MainActivity.workbook.createNotesContent(INotes.PLAIN);
        plainContent.setTextContent(note);
        box.topic.getNotes().setContent(INotes.PLAIN, plainContent);
	}



}
