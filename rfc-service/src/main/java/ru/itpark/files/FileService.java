package ru.itpark.files;

import javax.servlet.http.Part;

public interface FileService {
    String writeFile(Part part);
}
