package com.bugboo.BookShop.service;

import com.bugboo.BookShop.type.exception.AppException;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class FileUploadService {
    private final Cloudinary cloudinary;

    @Autowired
    public FileUploadService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    boolean isImage(MultipartFile file) {
        return file.getContentType().startsWith("image");
    }
    public Map uploadSingleFile(MultipartFile file, String folderName) throws IOException {
        if (!isImage(file)) {
            throw new AppException("File is not an image", 400);
        }
        return cloudinary.uploader().upload(file.getBytes(),
                ObjectUtils.asMap(
                        "folder", folderName
                ));
    }

}
