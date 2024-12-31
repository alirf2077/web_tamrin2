package com.example.form.dto;

import com.example.form.model.Field;

public record FieldDTO(Long id, String name, String label, Field.FieldType type) {}
