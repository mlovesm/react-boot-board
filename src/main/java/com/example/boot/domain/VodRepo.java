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

    @NotBlank
    @Column(length= 100)
    private String vod_path;

    @NotBlank
    @Column(length= 100)
    private String vod_title;

    @Size(max = 40)
    private String vod_keyword;

    @Size(max = 10)
    private String vod_play_time;
    
    @Column(length= 100)
    private String regId;
    
    private String regIp;
    
    @Column(length = 1)
    private String delFlag;
    
    private String transOption;
    
    private int favoriteCount= 0;
    
    private int viewCount= 0;
        
    private String main_thumbnail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="categoryIdx")
    @JsonBackReference	//child
    private ContentCategory contentCategory;



}