public class ClassRel {
    String up;
    String down;
    public ClassRel(String up,String down){
        this.up=up;
        this.down=down;
    }
    public String toString1(){
        return "\""+up+"\" -> \""+down+"\";";
    }
}
