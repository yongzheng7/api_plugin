package com.atom.plugin.utils

import com.atom.plugin.RegisterTransform
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes

import java.util.jar.JarEntry
import java.util.jar.JarFile

/**
 * 扫描 com/atom.proxy 所有的class文件
 * <p>寻找到自动生成的class文件，通过指定的继承类和实现的接口进行过滤</p>
 */
class ScanUtils {

    /**
     * 扫描jar文件
     * @param jarFile 所有被打包依赖进apk的jar文件
     * @param destFile dest file after this transform
     */
    static void scanJar(File jarFile, File destFile) {
        if (jarFile) {
            def file = new JarFile(jarFile)
            Enumeration enumeration = file.entries()
            while (enumeration.hasMoreElements()) {
                JarEntry jarEntry = (JarEntry) enumeration.nextElement()
                // 获取jar包中每个 class的名称
                String entryName = jarEntry.getName()
                // 判断是否是指定的包明
                if (entryName.startsWith(ScanSetting.ROUTER_CLASS_PACKAGE_NAME)) {
                    InputStream inputStream = file.getInputStream(jarEntry)
                    scanClass(inputStream)
                    inputStream.close()
                }  else if (ScanSetting.GENERATE_TO_CLASS_FILE_NAME == entryName) {
                    // 标记这个jar文件中是否存在 AtomApi.class -- 需要动态注入注册代码的类
                    // 在扫描完成后,将向 AtomApi.class 的loadProxyClass方法中注入注册代码
                    RegisterTransform.fileContainsInitClass = destFile
                }
            }
            file.close()
        }
    }

    /**
     * 判断jar文件是否可能注册了路由【android的library可以直接排除】
     * @param jarFilepath jar文件的路径
     */
    static boolean shouldProcessPreDexJar(String jarFilepath) {
        return !jarFilepath.contains("com.android.support") && !jarFilepath.contains("/android/m2repository")
    }

    /**
     * 判断扫描的类的包名是否是 annotationProcessor自动生成路由代码的包名：com/atom/proxy
     * @param classFilePath 扫描的class文件的路径
     */
    static boolean shouldProcessClass(String classFilePath) {
        return classFilePath != null && classFilePath.startsWith(ScanSetting.ROUTER_CLASS_PACKAGE_NAME)
    }

    /**
     * 扫描class文件
     * @param file class文件
     */
    static void scanClass(File file) {
        scanClass(new FileInputStream(file))
    }

    /**
     * 扫描class文件
     * @param inputStream 文件流
     */
    static void scanClass(InputStream inputStream) {
        ClassReader cr = new ClassReader(inputStream)
        ClassWriter cw = new ClassWriter(cr, 0)
        // 创建一个class并进行过滤根据指定的父类和实现的接口
        ScanClassVisitor cv = new ScanClassVisitor(Opcodes.ASM5, cw)
        cr.accept(cv, ClassReader.EXPAND_FRAMES)
        inputStream.close()
    }

    static class ScanClassVisitor extends ClassVisitor {

        ScanClassVisitor(int api, ClassVisitor cv) {
            super(api, cv)
        }
        void visit(int version, int access, String name, String signature,
                   String superName, String[] interfaces) {
            super.visit(version, access, name, signature, superName, interfaces)
            RegisterTransform.registerList.each { ext ->
                if (!ext.interfaceName.isEmpty() && interfaces != null) {
                    interfaces.each { itName ->
                        if (itName == ext.interfaceName) {
                            // 搜索实现指定接口的类
                            ext.classList.add(name)
                        }
                    }
                }
                if(!ext.superName.isEmpty() && ext.superName == superName){
                    // 搜索实现继承指定的类
                    ext.classList.add(name)
                }
            }
        }
    }

}