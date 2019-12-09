package ru.itpark.files;

import javax.servlet.http.Part;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class MyFileServiceImpl implements FileService {
    private final String uploadPath;
    private final String rfcPath;

    public MyFileServiceImpl() {
        try {
            uploadPath = System.getenv("UPLOAD_PATH");
//            rfcPath = uploadPath + "/rfc";
            rfcPath = Paths.get(uploadPath).resolve("rfc").toString();
//            Files.createDirectories(Paths.get(uploadPath));
            Files.createDirectories(Paths.get(rfcPath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String writeFile(Part part) {
        try {
            String filename = Paths.get(part.getSubmittedFileName()).getFileName().toString();
            part.write(Paths.get(rfcPath).resolve(filename).toString());
            return filename;
        } catch (IOException e) {
//            e.printStackTrace();
            throw new RuntimeException(e);
//            return null;
        }
    }

    @Override
    public void writeFiles(List<Part> parts) {
        final long startTime = System.currentTimeMillis();
        parts.forEach(this::writeFile);
        final long duration = System.currentTimeMillis() - startTime;
        System.out.println("Uploading took " + duration + " milliseconds");
    }
}
