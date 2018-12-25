package com.example.boot.payload;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.example.boot.domain.VodRepo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class VodRepoRequest {

    @NotBlank
    @Column(length= 100)
    private String vodPath;

    @NotBlank
    @Column(length= 100)
    private String vodTitle;

    @Size(max = 40)
    private String vodKeyword;

    @Size(max = 10)
    private String vodPlayTime;
    
    @Column(length= 100)
    private String regId;
    
    private String regIp;
    
    @Column(length = 1)
    private String delFlag;
    
    private String transOption;
    
    private int favoriteCount= 0;
    
    private int viewCount= 0;
        
    private String mainThumbnail;

    public VodRepo toEntity(){
        return VodRepo.builder()
                .vodPath(vodPath)
                .vodTitle(vodTitle)
                .build();
    }
}
