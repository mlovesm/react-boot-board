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

import com.example.boot.domain.audit.DateAudit;

import lombok.Builder;
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

    private int parentId;

    private int position;

    private String category_name;

    private String property;

    
//    @JsonManagedReference
    @OneToMany(fetch = FetchType.LAZY, mappedBy="contentCategory")
    private Set<VodRepo> vodRepository = new HashSet<>();

    @Builder
	public ContentCategory(int parentId, int position, String category_name, String property,
			Set<VodRepo> vodRepository) {
		this.parentId = parentId;
		this.position = position;
		this.category_name = category_name;
		this.property = property;
		this.vodRepository = vodRepository;
	}
    
    


}