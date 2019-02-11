package com.example.boot.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.example.boot.domain.audit.DateAudit;
import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@SuppressWarnings("serial")
@NoArgsConstructor
@Getter
@Entity
@Table(name = "vod_repository")
public class VodRepo extends DateAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @Column(length= 100)
    private String vodPath;

    @NotBlank
    @Column(length= 100)
    private String vodTitle;
    
    @Column(length= 300)
    private String vodContent;

    @Column(columnDefinition = "TEXT")
    private String vodKeyword;

    @Size(max = 10)
    private String vodPlayTime;
    
    @Column(length= 100)
    private String regId;
    
    private String regIp;
    
    @Column(length = 1)
    private String delFlag;
    
    private String transOption;
    
    @Column(length = 10)
    private int favoriteCount;
    
    @Column(length = 10)
    private int viewCount;
        
    private String mainThumbnail;


	@ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name ="category_idx", nullable = false)
	@JsonBackReference	//child
    private ContentCategory contentCategory;
	
	
    @Builder
	public VodRepo(Long idx, String vodPath, @NotBlank String vodTitle, String vodContent,
			String vodKeyword, @Size(max = 10) String vodPlayTime, 
			String regId, String regIp, String delFlag, String transOption,
			int favoriteCount, int viewCount, String mainThumbnail, ContentCategory contentCategory) {
		this.vodPath = vodPath;
		this.vodTitle = vodTitle;
		this.vodContent = vodContent;
		this.vodKeyword = vodKeyword;
		this.vodPlayTime = vodPlayTime;
		this.regId = regId;
		this.regIp = regIp;
		this.delFlag = delFlag;
		this.transOption = transOption;
		this.favoriteCount = favoriteCount;
		this.viewCount = viewCount;
		this.mainThumbnail = mainThumbnail;
		this.contentCategory = contentCategory;
	}


}