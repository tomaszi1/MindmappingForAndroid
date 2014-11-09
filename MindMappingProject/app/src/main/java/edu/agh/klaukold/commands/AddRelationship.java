package edu.agh.klaukold.commands;

import org.xmind.core.IRelationship;

import java.util.LinkedList;
import java.util.Properties;

import edu.agh.klaukold.common.Box;
import edu.agh.klaukold.gui.MainActivity;
import edu.agh.klaukold.interfaces.Command;

/**
 * Created by Klaudia on 2014-10-30.
 */
public class AddRelationship implements Command {
    LinkedList<Box> boxes;
    IRelationship relation;

    @Override
    public void execute(Properties properties) {
        boxes = (LinkedList<Box>)((LinkedList<Box>) properties.get("boxes")).clone();
        relation = MainActivity.workbook.createRelationship();
        relation.setEnd1Id(boxes.getFirst().topic.getId());
        relation.setEnd2Id(boxes.getLast().topic.getId());
        relation.setTitleText("Relationship Description");
        MainActivity.sheet1.addRelationship(relation);
        boxes.getFirst().related = boxes.getLast();
    }

    @Override
    public void undo() {
        MainActivity.sheet1.removeRelationship(relation);
        boxes.getFirst().related = null;
    }

    @Override
    public void redo() {
        MainActivity.sheet1.addRelationship(relation);
        boxes.getFirst().related = boxes.getLast();
    }
}
