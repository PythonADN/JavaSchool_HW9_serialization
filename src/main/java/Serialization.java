import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class Serialization {
    public static void serialize(Serializable o, File file, boolean zip) {
        try {
            OutputStream outputStream = new FileOutputStream(file);
            if (zip) {
                outputStream = new GZIPOutputStream(outputStream);
            }

            try (ObjectOutputStream stream = new ObjectOutputStream(outputStream)) {
                stream.writeObject(o);
                stream.close();
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при сериализации объекта", e);
        }
    }

    public static <T> T deserialize(File file, boolean zip) {
        try {
            InputStream inputStream = new FileInputStream(file);
            if (zip) {
                inputStream = new GZIPInputStream(inputStream);
            }

            try (ObjectInputStream stream = new ObjectInputStream(inputStream)) {
                return (T) stream.readObject();
            }
        } catch (FileNotFoundException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
