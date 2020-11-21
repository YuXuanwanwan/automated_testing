import com.ibm.wala.classLoader.ShrikeBTMethod;
import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.callgraph.CallGraph;
import com.ibm.wala.ipa.callgraph.Entrypoint;
import com.ibm.wala.ipa.callgraph.cha.CHACallGraph;
import com.ibm.wala.ipa.callgraph.impl.AllApplicationEntrypoints;
import com.ibm.wala.ipa.cha.ClassHierarchy;
import com.ibm.wala.ipa.cha.ClassHierarchyException;
import com.ibm.wala.ipa.cha.ClassHierarchyFactory;
import com.ibm.wala.shrikeCT.InvalidClassFileException;
import com.ibm.wala.types.ClassLoaderReference;
import com.ibm.wala.util.CancelException;
import com.ibm.wala.util.config.AnalysisScopeReader;
import org.w3c.dom.NodeList;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class AutoTest {
    static List<Class_Method> list=new ArrayList<>();
    public static void main(String []args) throws IOException, InvalidClassFileException, ClassHierarchyException, CancelException {

        String order[]=args;
        int class_or_method=0;
        String order_class_method=order[0];
        if(order_class_method.equals("-m"))
            class_or_method=1;
        if(order_class_method.equals("-c"))
            class_or_method=-1;

        String targetDir=order[1];
        String changeDir=order[2];
        //System.out.println(order_class_method+"*****"+targetDir+"**********"+changeDir);




        ClassLoader loader=AutoTest.class.getClassLoader();
        AnalysisScope analysisScope = AnalysisScopeReader.readJavaScope("E:/AutoTest/Project/main/resources/scope.txt", new File("E:/AutoTest/Project/main/resources/exclusion.txt"), loader);
        ReadFile(targetDir,analysisScope);
        // 生成类层次关系对象
        ClassHierarchy cha = ClassHierarchyFactory.makeWithRoot(analysisScope);
        // 生成进入点
        Iterable<Entrypoint> eps = new AllApplicationEntrypoints(analysisScope, cha);
        // 利用CHA算法构建调用图
        CHACallGraph chaCG = new CHACallGraph(cha);
        chaCG.init(eps);
        List<Class_Method> myNodes=new ArrayList<>();
        List<CGNode> tempNode=new ArrayList<>();
        for(CGNode node: chaCG) {
            if(node.getMethod() instanceof ShrikeBTMethod) {
                ShrikeBTMethod method = (ShrikeBTMethod) node.getMethod();
                if("Application".equals(method.getDeclaringClass().getClassLoader().toString())) {
                    tempNode.add(node);
                    myNodes.add(new Class_Method(method));
                }
            }
        }
        for(int i = 0 ; i < myNodes.size(); i++) {
            Class_Method myNode = myNodes.get(i);
            CGNode appNode = tempNode.get(i);
            Iterator<CGNode> predIter = chaCG.getPredNodes(appNode);
            List<Class_Method> dependents = new ArrayList<>();
            while (predIter.hasNext()) {
                CGNode node = predIter.next();
                ShrikeBTMethod method = (ShrikeBTMethod) node.getMethod();
                Class_Method class_method=new Class_Method(method);
                int index=-1;
                for(int j=0;j<myNodes.size();j++) {
                    Class_Method myNodetemp = myNodes.get(j);
                    if (myNodetemp.getMethodName().equals(class_method.getMethodName()) && myNodetemp.getClassName().equals(class_method.getClassName())&&myNodetemp.getisTest()==class_method.getisTest()){
                        index = j;
                        break;
                    }
                }
               // System.out.println(index);
                if(index != -1) {
                    dependents.add(myNodes.get(index));
                    //System.out.println(myNodes.get(index).getClassName());
                }
            }
            myNode.setDependents(dependents);
            //System.out.println("--------------------------------------------");
        }
        Class class_graph=new Class("MoreTriangle_Class");
        for(int i=0;i<myNodes.size();i++) {
            for(int j=0;j<myNodes.get(i).getDependents().size();j++){
                ClassRel tempRel=new ClassRel(myNodes.get(i).getClassName(),myNodes.get(i).getDependents().get(j).getClassName());
                boolean temp_exist=false;
                for(int k=0;k<class_graph.getRelations().size();k++){
                    if(class_graph.getRelations().get(k).up.equals(tempRel.up)&&class_graph.getRelations().get(k).down.equals(tempRel.down)){
                        temp_exist=true;
                        break;
                    }
                }
                if(temp_exist==false) {
                    class_graph.getRelations().add(tempRel);
                }
            }
           // for (int j = 0; j < class_graph.getRelations().size();j++){
               // System.out.println(class_graph.getRelations().get(j).toString1());}
            //System.out.println("--------------------------------------------");
        }
        String output2 = "E:/AutoTest/Report/5-MoreTriangle/MoreTriangle-Class.dot";
        File file=new File(output2);
        BufferedWriter bw=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, false), "UTF-8"));
        bw.write(class_graph.toString());
        bw.newLine();
        bw.close();


        Class class_graph1=new Class("MoreTriangle_Method");
        for(int i=0;i<myNodes.size();i++) {
            for(int j=0;j<myNodes.get(i).getDependents().size();j++){
                ClassRel tempRel=new ClassRel(myNodes.get(i).getMethodName(),myNodes.get(i).getDependents().get(j).getMethodName());
                boolean temp_exist=false;
                for(int k=0;k<class_graph1.getRelations().size();k++){
                    if(class_graph1.getRelations().get(k).up.equals(tempRel.up)&&class_graph1.getRelations().get(k).down.equals(tempRel.down)){
                        temp_exist=true;
                        break;
                    }
                }
                if(temp_exist==false) {
                    class_graph1.getRelations().add(tempRel);
                }
            }
           // for (int j = 0; j < class_graph1.getRelations().size();j++){
            //    System.out.println(class_graph1.getRelations().get(j).toString1());}
            //System.out.println("--------------------------------------------");
        }
        String output1 = "E:/AutoTest/Report/5-MoreTriangle/MoreTriangle-Method.dot";
        File file1=new File(output1);
        BufferedWriter bw1=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file1, false), "UTF-8"));
        bw1.write(class_graph1.toString());
        bw1.newLine();
        bw1.close();




        if(class_or_method==1) {//class
            List<Class_Method> classChange = new ArrayList<>();
            FileReader change_info = new FileReader(changeDir);
            BufferedReader br = new BufferedReader(change_info);
            String line = "";
            String[] temp = null;
            while ((line = br.readLine()) != null) {
                temp = line.split(" ");
                for (Class_Method class_method1 : myNodes) {
                    if (class_method1.getClassName().equals(temp[0])) {
                        class_method1.setChanged(true);
                        classChange.add(class_method1);
                    }
                }
            }
            br.close();
            change_info.close();

            //      Class Level Selection.
//        for (Class_Method class_method1 : classChange) {
//            change_set(class_method1);
//        }
            ;
            for (Class_Method method : classChange) {
                change_set(method);
            }
            List<Class_Method> res_Class_change = new ArrayList<>();

            for (int i = 0; i < myNodes.size(); i++) {
                if (myNodes.get(i).isChanged() && myNodes.get(i).getisTest()) {
                    res_Class_change.add(myNodes.get(i));
                }
            }

         //   for (int i = 0; i < res_Class_change.size(); i++) {
              //  System.out.println(res_Class_change.get(i).getClassName() + "*************************************************");
         //   }


            final String BLANK = " ";
            final String NL = System.lineSeparator();
            StringBuilder sub1 = new StringBuilder();
            for (Class_Method class_method : res_Class_change)
                sub1.append(class_method.toString()).append(NL);
            String str11 = sub1.toString();

            String output3 = "Demo/selection-class.txt";
            File file3 = new File(output3);
            BufferedWriter bw3 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file3, false), "UTF-8"));
            bw3.write(str11);
            bw3.newLine();
            bw3.close();

        }
        else if(class_or_method==-1){
            List<Class_Method> methodChange = new ArrayList<>();
            FileReader change_info = new FileReader(changeDir);
            BufferedReader br = new BufferedReader(change_info);
            String line = "";
            String[] temp = null;
            while ((line = br.readLine()) != null) {
                temp = line.split(" ");
                for (Class_Method class_method1 : myNodes) {
                    if (class_method1.getClassName().equals(temp[0]) && class_method1.getMethodName().equals(temp[1])) {
                        class_method1.setChanged(true);
                        methodChange.add(class_method1);
                    }
                }
            }
            br.close();
            change_info.close();


            //      Class Level Selection.
//        for (Class_Method class_method1 : classChange) {
//            change_set(class_method1);
//        }
            ;


            for (Class_Method class_method1 : methodChange) {
                change_set(class_method1);
            }


            List<Class_Method> res_Method_change = new ArrayList<>();
            for (int i = 0; i < myNodes.size(); i++) {
                if (myNodes.get(i).isChanged() && myNodes.get(i).getisTest()) {
                    res_Method_change.add(myNodes.get(i));
                }
            }

            final String BLANK = " ";
            final String NL = System.lineSeparator();
            StringBuilder sub2 = new StringBuilder();
            for (Class_Method class_method : res_Method_change)
                sub2.append(class_method.toString()).append(NL);
            String str12 = sub2.toString();
            String output4 = "Demo/selection-method.txt";
            File file4 = new File(output4);
            BufferedWriter bw4 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file4, false), "UTF-8"));
            bw4.write(str12);
            bw4.newLine();
            bw4.close();
        }

    }
    //递归读取文件
    private static void ReadFile(String fileDir,AnalysisScope analysisScope) throws InvalidClassFileException {
        List<File> fileList = new ArrayList<File>();
        File file = new File(fileDir);
        File[] files = file.listFiles();// 获取目录下的所有文件或文件夹
        if (files == null) {// 如果目录为空，直接退出
            return;
        }
        // 遍历，目录下的所有文件
        for (File f : files) {
            if (f.isFile()) {
                fileList.add(f);
                String Filename=f.getName();
                if(Filename.length()>5){
                    String subName=Filename.substring(Filename.length()-5,Filename.length());
                    if(subName.equals("class")){
                        analysisScope.addClassFileToScope(ClassLoaderReference.Application,f);
                    }
                }
            } else if (f.isDirectory()) {
                ReadFile(f.getAbsolutePath(),analysisScope);
            }
        }
    }

    public static boolean equal_node(Class_Method node1,Class_Method node2) {
        if (!node1.getMethodName().equals(node2.getMethodName())) {
            return false;
        } else if (!node1.getClassName().equals(node2.getClassName())) {
            return false;
        } else if (node1.getisTest() != node2.getisTest()) {
            return false;
        } else {
            if (node1.getDependents().size() != node2.getDependents().size()) {
                return false;
            } else {
                for (int i = 0; i < node1.getDependents().size(); i++) {
                    boolean temp = equal_node(node1.getDependents().get(i), node2.getDependents().get(i));
                    if (!temp) {
                        return false;
                    }
                }
                return true;
            }
        }

    }
    private static void change_set(Class_Method method) {
        if(method.isIsused())
            return;
        method.setChanged(true);
        method.setIsused(true);
        List<Class_Method> dependents = method.getDependents();
        if(dependents.isEmpty())
            return;
        for (Class_Method dependent : dependents)
            change_set(dependent);
    }

//
//    public static void change_set(Class_Method class_method){
//        int index=0;
//        for(int i=0;i<list.size();i++){
//            if(class_method.getClassName().equals(list.get(i).getClassName())){
//                index=i;
//                break;
//            }
//        }
//        Class_Method ttemp=list.get(index);
//        ttemp.setChanged(true);
//        ttemp.setIsused(true);
//        list.set(index,ttemp);
//        if(list.get(index).isIsused()){
//            return;
//        }
//        else{
//            List<Class_Method> temp=list.get(index).getDependents();
//            if(temp.isEmpty()){
//                return;
//            }
//            else{
//                for (Class_Method dependent : temp)
//                    change_set(dependent);
//            }
//        }
//    }





}
