package edu.agh.klaukold.commands;

import org.xmind.core.IRelationship;

import java.util.Properties;

import edu.agh.klaukold.common.Box;
import edu.agh.klaukold.interfaces.Command;

/**
 * Created by Klaudia on 2014-11-29.
 */
public class EditRelationship implements Command {
    Box box1;
    Box box2;
    IRelationship rel;
    String text;
    Properties after;
    String old_text;

    @Override
    public void execute(Properties properties) {
        after = (Properties) properties.clone();
        rel = (IRelationship) properties.get("relation");
        text = (String) properties.get("text");
        old_text = rel.getTitleText();

        rel.setTitleText(text);
        if (properties.containsKey("new_start")) {
            box2 = (Box) properties.get("new_start");
            box1 = (Box) properties.get("box");
            box1.relationships.remove(rel);
            box2.relationships.put(rel, box1);
        }

    }

    @Override
    public void undo() {
        if (after.containsKey("new_start")) {
            box2.relationships.remove(rel);
            box1.relationships.put(rel, box2);
        }
        rel.setStyleId(old_text);
    }

    @Override
    public void redo() {
        execute(after);
    }
}
