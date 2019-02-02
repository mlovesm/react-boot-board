package com.example.boot.payload;

import java.util.List;

import com.example.boot.domain.ContentCategory;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ContentCategoryRequest {

    private int parentId;

    private int position;

    private String categoryName;

    private String property;
    
    private int vodSize;
    
    private List<ContentCategoryRequest> children;

    public ContentCategory toEntity(){
        return ContentCategory.builder()
                .parentId(parentId)
                .position(position)
                .categoryName(categoryName)
                .property(property)
                .build();
    }
}
