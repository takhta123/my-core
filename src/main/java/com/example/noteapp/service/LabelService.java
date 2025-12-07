package com.example.noteapp.service;

import com.example.noteapp.dto.request.LabelRequest;
import com.example.noteapp.entity.Label;
import java.util.List;

public interface LabelService {
    Label createLabel(String email, LabelRequest request);
    List<Label> getAllLabels(String email);
    void deleteLabel(Long labelId, String email);
}