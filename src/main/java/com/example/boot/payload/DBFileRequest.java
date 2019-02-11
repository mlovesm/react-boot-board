package com.example.boot.payload;

import javax.persistence.Column;

import com.example.boot.domain.ContentCategory;
import com.example.boot.domain.DBFile;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DBFileRequest {

    private String fileName;
    
    private String originalFileName;
    
    private int fileSize;

    @Column(length= 100)
    private String fileType;
    
    private int refIdx;
    
    public DBFile toEntity(){
        return DBFile.builder()
                .fileName(fileName)
                .originalFileName(originalFileName)
                .fileSize(fileSize)
                .fileType(fileType)
                .refIdx(refIdx)
                .build();
    }
}
