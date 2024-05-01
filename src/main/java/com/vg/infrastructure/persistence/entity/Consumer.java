package com.vg.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "CONSUMERS")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Consumer {

    @Id
    private long id;

    @Column(unique = true, nullable = false, name = "api_key")
    private String apiKey;
}
