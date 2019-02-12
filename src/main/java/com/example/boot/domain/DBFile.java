package com.example.boot.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import com.example.boot.domain.audit.DateAudit;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@SuppressWarnings("serial")
@NoArgsConstructor
@Getter
@Entity
@Table(name = "files")
public class DBFile extends DateAudit {
	
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;

    private String fileName;
    
    private String originalFileName;
    
    private int fileSize;

    @Column(length= 100)
    private String fileType;
    
    private int refIdx;

//    @Lob
//    private byte[] data;

    @Builder
    public DBFile(String fileName, String originalFileName, int fileSize, String fileType, int refIdx) {
        this.fileName = fileName;
        this.originalFileName = originalFileName;
        this.fileSize = fileSize;
        this.fileType = fileType;
        this.refIdx = refIdx;
    }

}
