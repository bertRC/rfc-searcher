package ru.itpark.files;

import javax.servlet.http.Part;
import java.nio.file.Path;
import java.util.List;

public interface FileService {
    String writeFile(Part part);

    void writeFiles(List<Part> parts);

    List<String> getAll();

    boolean removeFile(String name);

    boolean removeFile(Path path);

    void removeAll();
}
