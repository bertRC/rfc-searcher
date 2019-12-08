package ru.itpark.files;

import javax.servlet.http.Part;

public interface MyFileService {
    String writeFile(Part part);
}
