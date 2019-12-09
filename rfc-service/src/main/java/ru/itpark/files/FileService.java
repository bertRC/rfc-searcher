package ru.itpark.files;

import javax.servlet.http.Part;
import java.util.List;

public interface FileService {
    String writeFile(Part part);

    void writeFiles(List<Part> parts);

    List<String> getAll();

    boolean removeFile(String name);

    void removeAll();

    void downloadRfcFromUrl(String urlRegex, int fromIndex, int toIndex);
}
