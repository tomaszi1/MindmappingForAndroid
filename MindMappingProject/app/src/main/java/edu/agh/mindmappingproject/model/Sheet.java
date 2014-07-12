package idziak.agh.mindmapping.xmind.model;


import java.util.LinkedList;
import java.util.List;

public class Sheet {
    // attributes
    private String id;
    private String styleId;
    private String theme;
    // elements
    private String title;
    private List<Relationship> relationships = new LinkedList<Relationship>();
    private Topic rootTopic;
    private Legend legend;

}
