package com.example.boot.domain;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

import com.example.boot.domain.audit.DateAudit;

import lombok.Getter;
import lombok.NoArgsConstructor;

@SuppressWarnings("serial")
@NoArgsConstructor
@Getter
@Entity
@Table(name = "content_category")
public class ContentCategory extends DateAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @NotBlank
    private int parendId;

    @NotBlank
    private String position;

    private String category_name;

    private String property;

    
    @OneToMany(fetch = FetchType.LAZY, mappedBy="contentCategory")
    private Set<VodRepo> vodReposotory = new HashSet<>();


}