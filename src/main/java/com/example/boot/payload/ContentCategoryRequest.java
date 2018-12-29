package com.example.boot.payload;

import com.example.boot.domain.ContentCategory;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ContentCategoryRequest {

    private int parendId;

    private int position;

    private String category_name;

    private String property;

    public ContentCategory toEntity(){
        return ContentCategory.builder()
                .parendId(parendId)
                .position(position)
                .category_name(category_name)
                .property(property)
                .build();
    }
}
