package edu.agh.idziak;

import org.xmind.core.ISheet;
import org.xmind.core.ITopic;
import org.xmind.core.IWorkbook;

import java.util.List;

public class Example {

    public static void howToUseXMind(){
        WorkbookHandler handler = WorkbookHandler.createNewWorkbook();

        // XMind API ponizej
        IWorkbook workbook = handler.getWorkbook();

        ISheet sheet = workbook.getPrimarySheet();

        ITopic rootTopic = sheet.getRootTopic();

        rootTopic.setTitleText("Central topic");

        // stworzenie nowego topica
        ITopic newTopic = workbook.createTopic();
        // dodanie do root topica
        rootTopic.add(newTopic, ITopic.ATTACHED);
        // usuwanie
        rootTopic.remove(newTopic);

        List<ITopic> children = rootTopic.getAllChildren();

        rootTopic.setPosition(10,15);

        rootTopic.isAttached();
    }
}
