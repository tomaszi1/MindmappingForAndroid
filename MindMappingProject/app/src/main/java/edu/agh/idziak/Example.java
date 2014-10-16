package edu.agh.idziak;

import org.xmind.core.INotes;
import org.xmind.core.IPlainNotesContent;
import org.xmind.core.ISheet;
import org.xmind.core.ITopic;
import org.xmind.core.IWorkbook;
import org.xmind.core.style.IStyle;
import org.xmind.core.style.IStyleSheet;
import org.xmind.ui.style.Styles;

import java.util.List;
import java.util.Set;

public class Example {

    public static void howToUseXMind(){
        // to jest moja klasa pomocnicza dla ułatwienia pracy
        WorkbookHandler handler = WorkbookHandler.createNewWorkbook();

        // XMind API ponizej
        IWorkbook workbook = handler.getWorkbook();

        ISheet sheet = workbook.getPrimarySheet();
        sheet.getTheme();

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

    public static void stylesExample(){
        WorkbookHandler handler = WorkbookHandler.createNewWorkbook();
        IWorkbook workbook = handler.getWorkbook();
        ISheet sheet = workbook.getPrimarySheet();
        ITopic rootTopic = sheet.getRootTopic();

        // Klasa przechowujaca wszystkie style. Wiele elementów może mieć ten sam styl.
        IStyleSheet styleSheet = workbook.getStyleSheet();

        // Tworzymy styl dla topica
        IStyle style = styleSheet.createStyle(IStyle.TOPIC);

        // Dodajemy styl do arkusza styli
        styleSheet.addStyle(style, IStyleSheet.NORMAL_STYLES);

        // Edytujemy styl (możliwe wartości masz na stronce UsingXMindAPI):
        style.setProperty(Styles.TextColor,Utils.colorAsHex(255,0,0)); // trzeba podać kolor w formacie "0xffffff"
        style.setProperty(Styles.FillColor,Utils.colorAsHex(255,255,255));
        style.setProperty(Styles.ShapeClass,Styles.TOPIC_SHAPE_ROUNDEDRECT);
        style.setProperty(Styles.FontSize,"20pt");
        style.setProperty(Styles.TextDecoration, Styles.TEXT_DECORATION_UNDERLINE);

        // Nadajemy topikowi dany styl przez podanie ID
        rootTopic.setStyleId(style.getId());

        // Jeśli mamy topika i chcemy się dobrać do jego stylu to:
        String styleId = rootTopic.getStyleId(); // bierzemy ID, zwróci NULL jeśli nie ma przypisanego stylu
        IStyle rootTopicStyle = styleSheet.findStyle(styleId); // wyciągamy styl z arkusza

        // UWAGA: Elementy nie mają żadnych domyślnych styli.
    }

    public static void notesExample(){
        WorkbookHandler handler = WorkbookHandler.createNewWorkbook();
        IWorkbook workbook = handler.getWorkbook();
        ISheet sheet = workbook.getPrimarySheet();
        ITopic rootTopic = sheet.getRootTopic();

        // wyciągamy klasę do przechowywania notatek danego topica (może być wiele notatek)
        INotes notes = rootTopic.getNotes();
        // tworzymy notatkę (tekstową albo HTML)
        IPlainNotesContent content =(IPlainNotesContent)workbook.createNotesContent(INotes.PLAIN);
        content.setTextContent("Notatki yeeeaaahh");
        // dodajemy notatkę do klasy z notatkami topica
        notes.setContent(INotes.PLAIN, content);
    }

    public static void mutipleSheets(){
        // jeden Workbook może mieć wiele arkuszy

        WorkbookHandler handler = WorkbookHandler.createNewWorkbook();
        IWorkbook workbook = handler.getWorkbook();
        // wyciąganie pierwszego arkusza
        ISheet sheet1 = workbook.getPrimarySheet();

        // tworzymy nowy arkusz i oodajemy do workbooka
        ISheet sheet2 = workbook.createSheet();
        workbook.addSheet(sheet2);

        // usuwamy
        workbook.removeSheet(sheet2);

        // wszystkie arkusze
        List<ISheet> allSheets = workbook.getSheets();
    }

    public static void labelsFilesAndImages(){
        WorkbookHandler handler = WorkbookHandler.createNewWorkbook();
        IWorkbook workbook = handler.getWorkbook();
        ISheet sheet = workbook.getPrimarySheet();
        ITopic rootTopic = sheet.getRootTopic();

        // możemy nadawać etykietki
        rootTopic.addLabel("Etykietka");
        Set<String> labels = rootTopic.getLabels(); // wszystkie etykietki

        // TODO
    }


}
