package util;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by zhangcy on 2017/9/23.
 * 用来加载类
 */
public final class ClassUtil {
    private static final Logger logger = LoggerFactory.getLogger(ClassUtil.class);

    public static ClassLoader getClassLoader(){
        return Thread.currentThread().getContextClassLoader();
    }

    /**
     * 获得指定包下的Class
     * @param packageName
     * @return
     */
    public static Set<Class<?>> getClassSet(String packageName){
        Set<Class<?>> classSet = new HashSet<Class<?>>();
        try {
            Enumeration<URL> urls = getClassLoader().getResources(packageName.replace(".","/"));
            while(urls.hasMoreElements()){
                URL url = urls.nextElement();
                if(url!=null){
                    String protocol = url.getProtocol();
                    if(protocol.equals("file")){
                        String packagePath = url.getPath().replaceAll("%20","").substring(1);
                        addClass(classSet,packagePath,packageName);
                    }
                }
            }
        } catch (IOException e) {
            logger.error("get class set failure",e);
            throw new RuntimeException(e);
        }
        return classSet;
    }

    /**
     * 由文件路径来加载类
     * @param classSet
     * @param packagePath
     * @param packageName
     */
    public static void addClass(Set<Class<?>> classSet,String packagePath,String packageName){
        try {
            /*File[] files = new File(packagePath).listFiles(new FileFilter(){
                public boolean accept(File file) {
                    return (file.isFile()&& file.getName().endsWith(".class") || file.isDirectory());
                }
            });*/
            File[] files = new File(packagePath).listFiles();
            for(File file:files){
                String fileName = file.getName();
                if(file.isFile()){
                    String className = fileName.substring(0,fileName.lastIndexOf("."));
                    className = packageName + "." +className;
                    Class<?> cls = Class.forName(className,false,getClassLoader());
                }else{
                    String subPackagePath = fileName;
                    subPackagePath = packagePath + "/" + subPackagePath;
                    String subPackageName = fileName;
                    subPackageName = packageName + "." + subPackageName;
                    addClass(classSet,subPackagePath,subPackageName);
                }
            }
        }catch(Exception e){
            logger.error("add class set failure",e);
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args){
        getClassSet("test1");
    }
}
