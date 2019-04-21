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

import com.example.boot.domain.audit.DateAudit;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@SuppressWarnings("serial")
@NoArgsConstructor
@Getter
@Entity
@Table(name = "live_repository")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "idx")
public class LiveRepo extends DateAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @NotBlank
    @Column(length= 100)
    private String liveTitle;
    
    @Column(length= 300)
    private String liveContent;
    
    @Column(length= 100)
    private String regId;
    
    private String regIp;
    
    @Column(length = 1)
    private String delFlag;
    
    @Column(length = 10)
    private int viewCount;
        
    private String mainThumbnail;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name ="category_idx", nullable = false)
	@JsonBackReference("liveCategory")
    private LiveCategory liveCategory;
	
	
    @Builder
	public LiveRepo(Long idx, String livePath, @NotBlank String liveTitle, String liveContent,
			String liveKeyword, String regId, String regIp, String delFlag,
			int favoriteCount, int viewCount, String mainThumbnail, LiveCategory liveCategory) {
		this.liveTitle = liveTitle;
		this.liveContent = liveContent;
		this.regId = regId;
		this.regIp = regIp;
		this.delFlag = delFlag;
		this.viewCount = viewCount;
		this.mainThumbnail = mainThumbnail;
		this.liveCategory = liveCategory;
	}


}