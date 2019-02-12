package com.example.boot.service;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.example.boot.domain.DBFile;
import com.example.boot.exception.FileStorageException;
import com.example.boot.exception.MyFileNotFoundException;
import com.example.boot.file.FileStorageProperties;
import com.example.boot.payload.DBFileRequest;
import com.example.boot.repository.DBFileRepository;

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

    
	//���� ���� �Լ�
	private String calcPath() {
		LocalDate currentDate = LocalDate.now();
		
		String order = File.separator + "VOD";
		String yearPath = order + File.separator + currentDate.getYear();
		String monthPath = yearPath + File.separator + new DecimalFormat("00").format(currentDate.getMonthValue());
		String datePath = monthPath + File.separator + new DecimalFormat("00").format(currentDate.getDayOfMonth());
		
		makeDir(order, yearPath, monthPath, datePath);
		
		log.info(datePath);
		
		return datePath;
	}//calcPath
	
	//���� ���� �Լ�
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
            // ���� �̸��� �߸��� ���ڰ� ���ԵǾ� �ִ���
            if(fileName.contains("..")) {
                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
            }
            
            // DB ����
            DBFileRequest dbFileRequest = new DBFileRequest();
            UUID uid = UUID.randomUUID();
            dbFileRequest.setFileName(uid.toString()+"_"+fileName);
            dbFileRequest.setOriginalFileName(fileName);
            dbFileRequest.setFileType(file.getContentType());
            dbFileRequest.setFileSize((int) file.getSize());
            
            String id = dbFileRepository.save(dbFileRequest.toEntity()).getId();     

            // ��� ��ġ�� ���� ����(������ �̸����� ���� ���� ��ü)
            Path targetLocation = Paths.get(this.fileStorageLocation + calcPath()).resolve(fileName);
            System.out.println(targetLocation);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return id;
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }
    
    public DBFile getFile(String fileId) {
        return dbFileRepository.findById(fileId)
                .orElseThrow(() -> new MyFileNotFoundException("File not found with id " + fileId));
    }

    public Resource loadFileAsResource(String order, String fileName, LocalDateTime createDate) {
        try {
        	order = "VOD";
        	String calcPath = File.separator + order + File.separator + createDate.getYear()+
        			File.separator + new DecimalFormat("00").format(createDate.getMonthValue())+
        			File.separator + new DecimalFormat("00").format(createDate.getDayOfMonth());
        	
            Path filePath = Paths.get(this.fileStorageLocation + calcPath).resolve(fileName).normalize();
            System.out.println(filePath);
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
