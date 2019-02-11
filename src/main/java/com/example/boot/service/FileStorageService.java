package com.example.boot.service;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.example.boot.exception.FileStorageException;
import com.example.boot.exception.MyFileNotFoundException;
import com.example.boot.file.FileStorageProperties;
import com.example.boot.payload.DBFileRequest;
import com.example.boot.repository.DBFileRepository;
import com.example.boot.repository.VODRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class FileStorageService {

    private final Path fileStorageLocation;
    
    @Autowired
    private DBFileRepository  dbFileRepository;
    
    @Autowired
    public FileStorageService(FileStorageProperties fileStorageProperties) {
        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir())
                .toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    
	//폴더 생성 함수
	private String calcPath() {
		Calendar cal = Calendar.getInstance();
		
		String order = "\\VOD";
		String yearPath = order + File.separator + cal.get(Calendar.YEAR);
		String monthPath = yearPath + File.separator + new DecimalFormat("00").format(cal.get(Calendar.MONTH)+1);
		String datePath = monthPath + File.separator + new DecimalFormat("00").format(cal.get(Calendar.DATE));
		
		makeDir(order, yearPath, monthPath, datePath);
		
		log.info(datePath);
		
		return datePath;
	}//calcPath
	
	//폴더 생성 함수
	private void makeDir(String... paths) {
		System.out.println("paths[paths.length -1] = "+this.fileStorageLocation + paths[paths.length -1]);
		if(new File(this.fileStorageLocation + paths[paths.length -1]).exists()) {
			return;
		}//if
		
		for(String path : paths) {
			Path dirPath = Paths.get(this.fileStorageLocation + path);
			System.out.println("dirPath="+dirPath);
			try {
				Files.createDirectories(dirPath);
			} catch (IOException e) {
				e.printStackTrace();
				throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", e);
			}
		}//for	
	}//makeDir

    public String storeFile(MultipartFile file) { 
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            // 파일 이름에 잘못된 문자가 포함되어 있는지
            if(fileName.contains("..")) {
                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
            }
            
            // DB 저장
            DBFileRequest dbFileRequest = new DBFileRequest();
            UUID uid = UUID.randomUUID();
            dbFileRequest.setFileName(uid.toString());
            dbFileRequest.setOriginalFileName(fileName);
            dbFileRequest.setFileType(file.getContentType());
            dbFileRequest.setFileSize((int) file.getSize());
            
            String id = dbFileRepository.save(dbFileRequest.toEntity()).getId();     

            // 대상 위치로 파일 복사(동일한 이름으로 기존 파일 교체)
            Path targetLocation = Paths.get(this.fileStorageLocation + calcPath()).resolve(fileName);
            System.out.println(targetLocation);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return id;
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }

    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if(resource.exists()) {
                return resource;
            } else {
                throw new MyFileNotFoundException("File not found " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new MyFileNotFoundException("File not found " + fileName, ex);
        }
    }
}
