package edu.agh.klaukold.commands;

import org.xmind.core.IRelationship;

import java.util.LinkedList;
import java.util.Properties;

import edu.agh.klaukold.common.Box;
import edu.agh.klaukold.gui.MainActivity;
import edu.agh.klaukold.interfaces.Command;
import edu.agh.klaukold.utilities.Utils;

/**
 * Created by Klaudia on 2014-11-11.
 */
public class RemoveRelationship implements Command {
    LinkedList<Box> boxes;
    IRelationship relation;
    String s;
    Properties prop;

    @Override
    public void execute(Properties properties) {
        prop = (Properties) properties.clone();
        boxes = (LinkedList<Box>)((LinkedList<Box>) properties.get("boxes")).clone();
      //  IRelationship rel = Utils.findRelationship(boxes.getFirst(), boxes.getLast());
        MainActivity.sheet1.removeRelationship(boxes.getFirst().relationship);
        boxes.getFirst().relationships.remove(boxes.getLast());
        s = boxes.getFirst().relationship.getTitleText();
    }

    @Override
    public void undo() {
        MainActivity.sheet1.addRelationship(relation);
        boxes.getFirst().relationships.put(boxes.getLast(), s);
    }

    @Override
    public void redo() {
        execute(prop);
    }
}
