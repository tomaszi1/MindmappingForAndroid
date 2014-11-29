package edu.agh.klaukold.commands;

import org.xmind.core.IRelationship;

import java.util.LinkedList;
import java.util.Properties;

import edu.agh.klaukold.common.Box;
import edu.agh.klaukold.gui.MainActivity;
import edu.agh.klaukold.interfaces.Command;
import edu.agh.klaukold.utilities.Utils;

/**
 * Created by Klaudia on 2014-10-30.
 */
public class AddRelationship implements Command {
    LinkedList<Box> boxes;
    IRelationship relation;
    String s;
    Properties prop;

    @Override
    public void execute(Properties properties) {
        prop = (Properties) properties.clone();
        boxes = (LinkedList<Box>)((LinkedList<Box>) properties.get("boxes")).clone();
            relation = MainActivity.workbook.createRelationship();
            relation.setEnd1Id(boxes.getFirst().topic.getId());
            relation.setEnd2Id(boxes.getLast().topic.getId());
            s = (String) properties.get("text");
            relation.setTitleText(s);
            MainActivity.sheet1.addRelationship(relation);
            boxes.getFirst().relationships.put(boxes.getLast(), relation);
    }

    @Override
    public void undo() {

            MainActivity.sheet1.removeRelationship(relation);
            boxes.getFirst().relationships.remove(boxes.getLast());


    }

    @Override
    public void redo() {
       execute(prop);
    }
}
