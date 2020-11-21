import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Class {
    private String name;
    private List<ClassRel> ClassRel=new ArrayList<>();
    public Class(String name){
        this.name=name;
    }
    public void addRelations(List<ClassRel> relations) {
        for(int i=0;i<relations.size();i++){
            if(!this.ClassRel.contains(relations.get(i)))
                this.ClassRel.add(relations.get(i));
        }
    }
    public List<ClassRel> getRelations()
    {
        return ClassRel;
    }
    public String toString() {
        final String BLANK = " ";
        final String NL = System.lineSeparator();
        StringBuilder sub = new StringBuilder();
        String start="digraph "+name+" {";
        String end="}";
        sub.append(start).append(NL);
        for (ClassRel relation : ClassRel)
            sub.append(relation.toString1()).append(NL);
        sub.append(end);
        return sub.toString();
    }

}
