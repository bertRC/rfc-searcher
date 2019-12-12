package ru.itpark.files;

import lombok.val;

import javax.servlet.http.Part;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class FileServiceDefaultImpl implements FileService {
    private final Path uploadPath;
    private final Path rfcPath;

    static final Comparator<String> proStringComparator = new Comparator<String>() {
        int extractInt(String str) {
            String num = str.replaceAll("\\D", "");
            return num.isEmpty() ? 0 : Integer.parseInt(num);
        }

        @Override
        public int compare(String o1, String o2) {
            return extractInt(o1) - extractInt(o2);
        }
    };

    public FileServiceDefaultImpl() {
        try {
            uploadPath = Paths.get(System.getenv("UPLOAD_PATH"));
            rfcPath = uploadPath.resolve("rfc");
            Files.createDirectories(rfcPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String writeFile(Part part) {
        try {
            String filename = Paths.get(part.getSubmittedFileName()).getFileName().toString();
            part.write(rfcPath.resolve(filename).toString());
            return filename;
        } catch (IOException e) {
            throw new RuntimeException(e);
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
        try (val paths = Files.walk(rfcPath)) {
            return paths
                    .filter(Files::isRegularFile)
                    .map(rfcPath::relativize)
                    .map(Path::toString)
                    .sorted(proStringComparator)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void readFile(String name, PrintWriter printWriter) {
        try (BufferedReader br = new BufferedReader(new FileReader(rfcPath.resolve(name).toString()))) {
            String line;
            while ((line = br.readLine()) != null) {
                printWriter.println(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean removeFile(Path path) {
        try {
            return Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean removeFile(String name) {
        return removeFile(rfcPath.resolve(name));
    }

    @Override
    public void removeAll() {
        final long startTime = System.currentTimeMillis();
        try (val paths = Files.walk(rfcPath)) {
            paths
                    .filter(Files::isRegularFile)
                    .sorted(Comparator.reverseOrder())
                    .forEach(this::removeFile);
            final long duration = System.currentTimeMillis() - startTime;
            System.out.println("Deleting took " + duration + " milliseconds");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean downloadFromUrl(String url, String fileName) {
        try (BufferedInputStream in = new BufferedInputStream(new URL(url).openStream())) {
            Files.copy(in, rfcPath.resolve(fileName), REPLACE_EXISTING);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
