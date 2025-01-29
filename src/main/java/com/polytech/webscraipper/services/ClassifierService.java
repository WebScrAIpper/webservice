package com.polytech.webscraipper.services;

import com.polytech.webscraipper.dto.ClassifierDto;
import com.polytech.webscraipper.repositories.ClassifierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Service
public class ClassifierService {

    @Autowired
    private ClassifierRepository classifierRepository;

    public boolean addClassifier(String classifier) {
        var toAdd = new ClassifierDto(classifier);
        if (classifierRepository.findAll().contains(toAdd)) {
            return false;
        }
        classifierRepository.save(toAdd);
        return true;
    }

    public List<String> getAllClassifiers() {
        return classifierRepository.findAll().stream().map(ClassifierDto::getName).toList();
    }
}
