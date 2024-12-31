package com.example.form.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.form.dto.FieldDTO;
import com.example.form.model.Field;
import com.example.form.model.Form;
import com.example.form.repository.FormRepository;

@Service
public class FormService {

    private final FormRepository formRepository;

    public FormService(FormRepository formRepository) {
        this.formRepository = formRepository;
    }

    public List<Form> getAllForms() {
        return formRepository.findAll();
    }

    public Form createForm(String name) {
        Form form = new Form();
        form.setName(name);
        return formRepository.save(form);
    }

    public Optional<Form> getFormById(Long id) {
        return formRepository.findById(id);
    }

    public boolean deleteFormById(Long id) {
        if (formRepository.existsById(id)) {
            formRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public Optional<Form> updateFormName(Long id, String name) {
        return formRepository.findById(id).map(form -> {
            form.setName(name);
            return formRepository.save(form);
        });
    }

    public List<Form> getPublishedForms() {
        return formRepository.findByPublicationStatus(Form.PublicationStatus.PUBLISHED);
    }

    public Optional<Form> togglePublicationStatus(Long id) {
        return formRepository.findById(id).map(form -> {
            if (form.getPublicationStatus() == Form.PublicationStatus.NOT_PUBLISHED) {
                form.setPublicationStatus(Form.PublicationStatus.PUBLISHED);
            } else {
                form.setPublicationStatus(Form.PublicationStatus.NOT_PUBLISHED);
            }
            return formRepository.save(form);
        });
    }

    public boolean addFieldsToForm(Long formId, List<FieldDTO> fields) {
        Optional<Form> formOptional = formRepository.findById(formId);
        if (formOptional.isEmpty()) {
            return false;
        }

        Form form = formOptional.get();
        List<Field> fieldEntities = fields.stream().map(dto -> {
            Field field = new Field();
            field.setName(dto.name());
            field.setLabel(dto.label());
            field.setType(dto.type());
            field.setForm(form);
            return field;
        }).collect(Collectors.toList());

        form.getFields().addAll(fieldEntities);
        formRepository.save(form);
        return true;
    }



}
