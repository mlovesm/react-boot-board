package com.example.boot.payload;

import com.example.boot.domain.ContentCategory;
import com.example.boot.domain.LiveCategory;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class categoryRequest {

    private int parentId;

    private int position;

    private String categoryName;

    private String property;
    
    private int vodSize;
    
    public ContentCategory contentToEntity(){
        return ContentCategory.builder()
                .parentId(parentId)
                .position(position)
                .categoryName(categoryName)
                .property(property)
                .build();
    }
    
    public LiveCategory liveToEntitys(){
        return LiveCategory.builder()
                .parentId(parentId)
                .position(position)
                .categoryName(categoryName)
                .property(property)
                .build();
    }
}
