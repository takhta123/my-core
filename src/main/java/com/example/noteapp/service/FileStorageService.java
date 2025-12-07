package com.example.noteapp.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileStorageService {

    private final Cloudinary cloudinary;

    // Hàm upload trả về một Map chứa thông tin (URL, Public ID...)
    public Map uploadFile(MultipartFile file) {
        try {
            // Upload lên Cloudinary
            // folder: "noteapp_uploads" là tên thư mục trên Cloudinary (nó tự tạo nếu chưa có)
            return cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", "noteapp_uploads",
                            "public_id", UUID.randomUUID().toString() // Đặt tên file ngẫu nhiên
                    ));
        } catch (IOException e) {
            throw new RuntimeException("Lỗi upload file lên Cloudinary: " + e.getMessage());
        }
    }

    public void deleteFile(String publicId) {
        try {
            // Xóa file trên Cloudinary dựa vào Public ID
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (IOException e) {
            throw new RuntimeException("Lỗi xóa file trên Cloudinary: " + e.getMessage());
        }
    }

    // Hàm loadFileAsResource không còn cần thiết nữa vì ta dùng URL trực tiếp
}