import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.classLoader.ShrikeBTMethod;
import com.ibm.wala.types.ClassLoaderReference;
import com.ibm.wala.types.TypeReference;
import com.ibm.wala.types.annotations.Annotation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Class_Method {
    private String ClassName;
    private String MethodName;
    private boolean isChanged=false;
    private boolean isTest;
    private boolean isused=false;
    private List<Class_Method> dependents=new ArrayList<>();

    public Class_Method(ShrikeBTMethod shrikeBTMethod){
            this.ClassName= shrikeBTMethod.getDeclaringClass().getName().toString();
            this.MethodName=shrikeBTMethod.getSignature();
            isTest =setTest(shrikeBTMethod);
    }


    public String getClassName() {
        return ClassName;
    }

    public void setClassName(String className) {
        ClassName = className;
    }

    public String getMethodName() {
        return MethodName;
    }

    public void setMethodName(String methodName) {
        MethodName = methodName;
    }

    public boolean isChanged() {
        return isChanged;
    }

    public void setChanged(boolean changed) {
        isChanged = changed;
    }

    public List<Class_Method> getDependents() {
        return dependents;
    }

    public void setDependents(List<Class_Method> dependents) {
        this.dependents = dependents;
    }

    public boolean setTest(IMethod method) {
        String str1=method.getDeclaringClass().getName().toString();
        char str11[]=str1.toCharArray();
        String str2=method.getSignature();
        char str22[]=str2.toCharArray();
        boolean res=false;
        if(str1.length()>4){
            for(int i=0;i<str1.length();i++){
                if(i+4>=str1.length())
                    break;
                else{
                    if(str11[i]=='T'&&str11[i+1]=='e'&&str11[i+2]=='s'&&str11[i+3]=='t'){
                        res=true;
                    }
                }
            }
        }
        if(str2.length()>4){
            for(int i=0;i<str2.length();i++){
                if(i+4>=str2.length())
                    break;
                else{
                    if(str22[i]=='T'&&str22[i+1]=='e'&&str22[i+2]=='s'&&str22[i+3]=='t'){
                        res=true;
                    }
                }
            }
        }
        return res;
    }

    public String toString(){
        return ClassName+ " " +MethodName;
    }

    public boolean getisTest(){
        return isTest;
    }

    public boolean isIsused() {
        return isused;
    }

    public void setIsused(boolean isused) {
        this.isused = isused;
    }
}
