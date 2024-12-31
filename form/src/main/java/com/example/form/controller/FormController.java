package com.example.form.controller;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.form.dto.FieldDTO;
import com.example.form.model.Field;
import com.example.form.model.Form;
import com.example.form.service.FormService;

@RestController
@RequestMapping("/forms")
public class FormController {

    private final FormService formService;

    public FormController(FormService formService) {
        this.formService = formService;
    }

    @GetMapping
    public ResponseEntity<List<FormDTO>> getAllForms() {
        List<Form> forms = formService.getAllForms();
        List<FormDTO> formDTOs = forms.stream()
                .map(form -> new FormDTO(form.getId(), form.getName(), form.getPublicationStatus()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(formDTOs);
    }

    @PostMapping
    public ResponseEntity<FormDTO> createForm(@RequestBody FormCreateRequest request) {
        Form form = formService.createForm(request.name());
        return ResponseEntity.ok(new FormDTO(form.getId(), form.getName(), form.getPublicationStatus()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FormDTO> getFormById(@PathVariable Long id) {
        return formService.getFormById(id)
                .map(form -> ResponseEntity.ok(new FormDTO(form.getId(), form.getName(), form.getPublicationStatus())))
                .orElseGet(() -> ResponseEntity.status(404).body(null));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteFormById(@PathVariable Long id) {
        boolean deleted = formService.deleteFormById(id);
        if (deleted) {
            return ResponseEntity.ok("Form deleted successfully.");
        } else {
            return ResponseEntity.status(404).body("Form with the given ID does not exist.");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<FormDTO> updateFormName(@PathVariable Long id, @RequestBody FormUpdateRequest request) {
        return formService.updateFormName(id, request.name())
                .map(form -> ResponseEntity.ok(new FormDTO(form.getId(), form.getName(), form.getPublicationStatus())))
                .orElseGet(() -> ResponseEntity.status(404).body(null));
    }

    @GetMapping("/{id}/fields")
    public ResponseEntity<List<FieldDTO>> getFieldsByFormId(@PathVariable Long id) {
        Optional<Form> form = formService.getFormById(id);
        if (form.isEmpty()) {
            return ResponseEntity.status(404).body(null);
        }

        Set<Field> fields = form.get().getFields();
        List<FieldDTO> fieldDTOs = fields.stream()
                .map(field -> new FieldDTO(field.getId(), field.getName(), field.getLabel(), field.getType()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(fieldDTOs);
    }

    @PutMapping("/{id}/fields")
    public ResponseEntity<String> addFieldsToForm(@PathVariable Long id, @RequestBody List<FieldDTO> fields) {
        boolean success = formService.addFieldsToForm(id, fields);
        if (success) {
            return ResponseEntity.ok("Fields added successfully.");
        } else {
            return ResponseEntity.status(404).body("Form with the given ID does not exist.");
        }
    }


    @GetMapping("/published")
    public ResponseEntity<List<FormDTO>> getPublishedForms() {
        List<Form> publishedForms = formService.getPublishedForms();
        List<FormDTO> formDTOs = publishedForms.stream()
                .map(form -> new FormDTO(form.getId(), form.getName(), form.getPublicationStatus()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(formDTOs);
    }

    // New API: POST /forms/{id}/publish
    @PostMapping("/{id}/publish")
    public ResponseEntity<FormDTO> togglePublicationStatus(@PathVariable Long id) {
        return formService.togglePublicationStatus(id)
                .map(form -> ResponseEntity.ok(new FormDTO(form.getId(), form.getName(), form.getPublicationStatus())))
                .orElseGet(() -> ResponseEntity.status(404).body(null));
    }


    // Request payloads and DTOs
    record FormCreateRequest(String name) {}

    record FormUpdateRequest(String name) {}

    record FormDTO(Long id, String name, Form.PublicationStatus publicationStatus) {}
}
