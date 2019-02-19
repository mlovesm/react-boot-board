package com.example.boot.payload;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.example.boot.domain.ContentCategory;
import com.example.boot.domain.DBFile;
import com.example.boot.domain.VodRepo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class VodRepoRequest {

    @Column(length= 100)
    private String vodPath;

    @NotBlank
    @Column(length= 100)
    private String vodTitle;
    
    @Column(length= 300)
    private String vodContent;

    @Column(length= 300)
    private String vodKeyword;

    @Size(max = 10)
    private String vodPlayTime;
    
    @Column(length= 100)
    private String regId;
    
    private String regIp;
    
    @Column(length = 1)
    private String delFlag;
    
    private String transOption;
        
    private String mainThumbnail;
    
    private ContentCategory contentCategory;
    
    private DBFile dbFile;

    public VodRepo toEntity(){
        return VodRepo.builder()
        		.contentCategory(contentCategory)
        		.dbFile(dbFile)
        		.vodTitle(vodTitle)
        		.vodPath(vodPath)
        		.vodContent(vodContent)
        		.mainThumbnail(mainThumbnail)
        		.vodKeyword(vodKeyword)
        		.vodPlayTime(vodPlayTime)
        		.regId(regId)
                .build();
    }
}
