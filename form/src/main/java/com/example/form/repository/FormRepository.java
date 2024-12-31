package com.example.form.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.form.model.Form;

@Repository
public interface FormRepository extends JpaRepository<Form, Long> {
    List<Form> findByPublicationStatus(Form.PublicationStatus status);
}
