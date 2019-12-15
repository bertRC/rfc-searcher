package ru.itpark.file;

import javax.servlet.http.Part;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.List;

public interface FileService {
    Path getRfcPath();

    String writeFile(Part part);

    void writeFiles(List<Part> parts);

    List<String> getAll();

    void readFile(String name, PrintWriter printWriter);

    List<String> searchText(String text, Path path);

    boolean removeFile(String name);

    boolean removeFile(Path path);

    void removeAll(Path dir);

    void removeAll();

    void removeAllResults();

    boolean downloadFromUrl(String url, String fileName, boolean replaceIfExists);
}
