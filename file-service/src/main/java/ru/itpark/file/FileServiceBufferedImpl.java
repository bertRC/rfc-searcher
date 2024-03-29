package ru.itpark.file;

import lombok.val;
import ru.itpark.comparator.RfcFileNameComparator;
import ru.itpark.exception.FileAccessException;

import javax.servlet.http.Part;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class FileServiceBufferedImpl implements FileService {
    private final Path uploadPath;
    private final Path rfcPath;
    private final Path resultsPath;

    private final RfcFileNameComparator rfcFileNameComparator = new RfcFileNameComparator();

    public FileServiceBufferedImpl() {
        try {
            uploadPath = Paths.get(System.getenv("UPLOAD_PATH"));
            rfcPath = uploadPath.resolve("rfc");
            resultsPath = uploadPath.resolve("results");
            Files.createDirectories(rfcPath);
            Files.createDirectories(resultsPath);
        } catch (IOException e) {
            throw new FileAccessException(e);
        }
    }

    @Override
    public Path getRfcPath() {
        return rfcPath;
    }

    @Override
    public void writeResultFile(String filename, String query, List<String> lines) {
        writeResultFile(resultsPath.resolve(filename), query, lines);
    }

    @Override
    public void writeResultFile(Path file, String query, List<String> lines) {
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file.toFile()), StandardCharsets.UTF_8))) {
            writer.write("[Search Phrase]: " + query);
            writer.newLine();
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            throw new FileAccessException(e);
        }
    }

    @Override
    public String writeFile(Part part) {
        try {
            String filename = Paths.get(part.getSubmittedFileName()).getFileName().toString();
            part.write(rfcPath.resolve(filename).toString());
            return filename;
        } catch (IOException e) {
            throw new FileAccessException(e);
        }
    }

    @Override
    public void writeFiles(List<Part> parts) {
        final long startTime = System.currentTimeMillis();
        parts.forEach(this::writeFile);
        final long duration = System.currentTimeMillis() - startTime;
        System.out.println("Uploading took " + duration + " milliseconds");
    }

    @Override
    public List<String> getAll() {
        try (val paths = Files.list(rfcPath)) {
            return paths
                    .filter(Files::isRegularFile)
                    .map(rfcPath::relativize)
                    .map(Path::toString)
                    .sorted(rfcFileNameComparator)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new FileAccessException(e);
        }
    }

    @Override
    public void readFile(Path file, PrintWriter printWriter) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file.toFile()), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                printWriter.println(line);
            }
        } catch (IOException e) {
            throw new FileAccessException(e);
        }
    }

    @Override
    public void readRfcFile(String name, PrintWriter printWriter) {
        readFile(rfcPath.resolve(name), printWriter);
    }

    @Override
    public void readResultsFile(String name, PrintWriter printWriter) {
        readFile(resultsPath.resolve(name), printWriter);
    }

    @Override
    public String isResultReady(String id) {
        Path path = resultsPath.resolve(id + ".txt");
        if (Files.exists(path)) {
            return uploadPath.relativize(path).toString();
        }
        return "";
    }

    @Override
    public List<String> searchText(String text, Path path) {
        String filename = rfcPath.relativize(path).toString();
        List<String> result = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(path.toFile()), StandardCharsets.UTF_8))) {
            int lineNumber = 1;
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.toLowerCase().contains(text.toLowerCase())) {
                    result.add("[" + filename + " Line: " + lineNumber + "]: " + line);
                }
                lineNumber++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public boolean removeFile(Path path) {
        try {
            return Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new FileAccessException(e);
        }
    }

    @Override
    public boolean removeFile(String name) {
        return removeFile(rfcPath.resolve(name));
    }

    @Override
    public void removeAll(Path dir) {
        try (val paths = Files.walk(dir)) {
            paths
                    .filter(Files::isRegularFile)
                    .sorted(Comparator.reverseOrder())
                    .forEach(this::removeFile);
        } catch (IOException e) {
            throw new FileAccessException(e);
        }
    }

    @Override
    public void removeAll() {
        removeAll(rfcPath);
    }

    @Override
    public void removeAllResults() {
        removeAll(resultsPath);
    }

    @Override
    public boolean downloadFromUrl(String url, String fileName, boolean replaceIfExists) {
        Path filePath = rfcPath.resolve(fileName);
        if (replaceIfExists || !Files.exists(filePath)) {
            try (BufferedInputStream in = new BufferedInputStream(new URL(url).openStream())) {
                Files.copy(in, filePath, REPLACE_EXISTING);
                return true;
            } catch (IOException e) {
                if (Thread.currentThread().isInterrupted()) {
//                     trying to delete last damaged file
                    removeFile(fileName);
                    System.out.println("Downloading was canceled. Damaged file was deleted: " + fileName);
                }
            }
            return false;
        }
        return true;
    }
}
