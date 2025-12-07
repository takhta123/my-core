package com.example.noteapp.service.impl;

import com.example.noteapp.dto.request.LabelRequest;
import com.example.noteapp.entity.Label;
import com.example.noteapp.entity.User;
import com.example.noteapp.repository.LabelRepository;
import com.example.noteapp.repository.UserRepository;
import com.example.noteapp.service.LabelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LabelServiceImpl implements LabelService {

    private final LabelRepository labelRepository;
    private final UserRepository userRepository;

    @Override
    public Label createLabel(String email, LabelRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 1. Chuẩn hóa tên nhãn (xóa khoảng trắng thừa đầu đuôi)
        String labelName = request.getName().trim();

        // 2. Kiểm tra trùng lặp
        if (labelRepository.existsByUserIdAndName(user.getId(), labelName)) {
            throw new RuntimeException("Nhãn '" + labelName + "' đã tồn tại!");
        }

        Label label = new Label();
        label.setName(labelName); // Lưu tên đã trim()
        label.setUser(user);

        return labelRepository.save(label);
    }

    @Override
    public List<Label> getAllLabels(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return labelRepository.findByUserId(user.getId());
    }

    @Override
    public void deleteLabel(Long labelId, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Label label = labelRepository.findById(labelId)
                .orElseThrow(() -> new RuntimeException("Nhãn không tồn tại"));

        // Kiểm tra quyền sở hữu trước khi xóa
        if (!label.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Bạn không có quyền xóa nhãn này");
        }

        labelRepository.delete(label);
    }
}