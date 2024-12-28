import java.io.*;

public class GugeliusClassLoader extends ClassLoader {

    private final String classPath;

    public GugeliusClassLoader(String classPath, ClassLoader parent) {
        super(parent);
        this.classPath = classPath;
    }

    @Override
    public Class<?> findClass(String name) throws ClassNotFoundException {
        String path = classPath + File.separator + name.replace('.', File.separatorChar) + ".class";
        try {
            byte[] classData = loadClassData(path);
            System.out.println("Загружаю класс: " + name);
            return defineClass(name, classData, 0, classData.length);
        } catch (IOException e) {
            throw new ClassNotFoundException("Class " + name + " not found", e);
        }
    }

    private byte[] loadClassData(String path) throws IOException {
        FileInputStream inputStream = new FileInputStream(path);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, bytesRead);
        }
        return byteArrayOutputStream.toByteArray();
    }
}
