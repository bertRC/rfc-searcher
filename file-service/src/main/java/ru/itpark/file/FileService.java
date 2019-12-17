package ru.itpark.file;

import javax.servlet.http.Part;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.List;

public interface FileService {
    Path getRfcPath();

    Path getResultPath();

    void writeResultFile(String filename, String query, List<String> lines);

    void writeResultFile(Path file, String query, List<String> lines);

    String writeFile(Part part);

    void writeFiles(List<Part> parts);

    List<String> getAll();

    void readFile(Path file, PrintWriter printWriter);

    void readRfcFile(String name, PrintWriter printWriter);

    void readResultsFile(String name, PrintWriter printWriter);

    List<String> searchText(String text, Path path);

    boolean removeFile(String name);

    boolean removeFile(Path path);

    void removeAll(Path dir);

    void removeAll();

    void removeAllResults();

    boolean downloadFromUrl(String url, String fileName, boolean replaceIfExists);
}
