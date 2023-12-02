package me.skizzme.loader;

import me.skizzme.Main;
import me.skizzme.util.TempManager;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;

public class AClassLoader extends ClassLoader {

    private HashMap<String, byte[]> classCache = new HashMap<>();
    private HashMap<String, byte[]> resourceCache = new HashMap<>();
    public long cachedObjects = 0;
    private boolean verbose;

    public AClassLoader(boolean verbose) {
        super(AClassLoader.class.getClassLoader());
        this.verbose = verbose;
    }

    public void cacheClass(String binaryName, byte[] data) {
        this.classCache.put(binaryName, data);
        this.cachedObjects++;
        if (verbose) System.out.println("Cached class \"" + binaryName + "\"");
    }

    public void cacheResource(String path, byte[] data) {
        path = path.toLowerCase();
        if (path.endsWith(".dll")) {
            Main.launcher.getNativeLoader().registerNative(path);
        }
        this.resourceCache.put(path, data);
        this.cachedObjects++;
        if (verbose) System.out.println("Cached resource \"" + path + "\"");
    }

//    @Override
//    public URL getResource(String name) {
//        System.out.println("f31: " + name);
//        return super.getResource(name);
//    }
//
//    @Override
//    public Enumeration<URL> getResources(String name) throws IOException {
//        System.out.println("f32: " + name);
//        return super.getResources(name);
//    }
//
    @Override
    public InputStream getResourceAsStream(String name) {
        if (verbose) System.out.println("Getting resource \"" + name + "\"");
        String modNamed = name.replaceAll("\\\\", "/").toLowerCase();
        if (this.resourceCache.containsKey(modNamed)) {
            byte[] resource_bytes = this.resourceCache.get(modNamed);
            if (resource_bytes != null) {
                return new ByteArrayInputStream(resource_bytes);
            }
        }
        return null;
    }

    @Override
    protected URL findResource(String name) {
        try {
            if (verbose) System.out.println("Finding resource with name \"" + name + "\"");
            String ex = TempManager.getFile(name);
            if (ex == null) {
                return new URL("file:\\" + TempManager.writeTempFile(name, this.resourceCache.get(name)));
            } else {
                return new URL("file:\\" + ex);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private byte[] getClassData(String binaryName) {
        if (verbose) System.out.println("Getting class \"" + binaryName + "\"");
        if (classCache.containsKey(binaryName)) {
            byte[] data = classCache.get(binaryName);
            classCache.remove(binaryName);
            return data;
        }
        return null;
    }

    protected Enumeration<URL> findResources(String name) throws IOException {
        if (verbose) System.out.println("Finding resources matching \"" + name + "\"");
        return java.util.Collections.emptyEnumeration();
    }

    public Class<?> getClass(String name) throws ClassNotFoundException {
        if (verbose) System.out.println("Getting class \"" + name + "\"");
        return findClass(name);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        Class<?> r = findLoadedClass(name);
        if (verbose) System.out.println("Finding class \"" + name + "\"");
        if (r == null) {
            byte[] data = this.getClassData(name);
            if (data == null) {
                return null;
            }
            r = loadClass(name, data);
        }
        return r;
    }

    public Class<?> loadClass(String name, byte[] classdata) throws ClassNotFoundException {
        if (classdata == null) {
            return null;
        }
        if (name.startsWith("java.")) {
            return super.loadClass(name, true);
        }
        try{
            Class<?> check = this.findLoadedClass(name);
            if (check != null) {
                this.resolveClass(check);
                return check;
            }
            String[] nameSplit = name.split("\\.");
            String packageName = String.join(".", Arrays.copyOfRange(nameSplit, 0, nameSplit.length-1));
            if (this.getPackage(packageName) == null) {
                super.definePackage(packageName, null, null, null, null, null, null, null);
            }
            Class<?> result = this.defineClass(name, classdata, 0, classdata.length);
            if (verbose) System.out.println("Defined class \"" + name + "\"");
            this.resolveClass(result);
            if (verbose) System.out.println("Resolved class \"" + name + "\"");

            return result;
        }catch(SecurityException se){
            se.printStackTrace();
            return super.loadClass(name, true);
        }
        catch (IllegalAccessError e) {
            String className = e.getMessage().replaceFirst("class ", "").split(" cannot access its super(class|interface) ")[1];
            Class<?> ca = this.findClass(className);
            this.findClass(name);
            return null;
        }
        catch (NoClassDefFoundError e) { // POSSIBLY A VULNERABILITY BECAUSE OF OTHER CLASSES BEING PUT IN AND PREVENT PROPER CLASSES FROM BEING RECEIVED??
            e.printStackTrace();
            try {
                Class<?> c = super.loadClass(e.getMessage());
                return c;
            } catch (Exception ignored) {}
            return null;
        }
    }
}