package com.example.boot.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.boot.domain.LiveCategory;
import com.example.boot.payload.ApiResponse;
import com.example.boot.payload.categoryRequest;
import com.example.boot.repository.live.LiveCategoryRepository;
import com.example.boot.repository.live.LiveRepository;
import com.example.boot.service.LiveService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SuppressWarnings({ "unchecked", "rawtypes" })
@RestController
@RequestMapping("/live")
public class LiveController {
    
    @Autowired
    private LiveCategoryRepository liveCategoryRepository;
    
    @Autowired
    private LiveRepository liveRepository;
	
	@Autowired
	private LiveService liveService;

  
	@GetMapping("/category/liveCategory")
	public ResponseEntity<?> liveCategory() {
	  
		List<HashMap<String, Object>> categoryList = liveService.getLiveCategory(0);

		return ResponseEntity.ok().body(categoryList);
	}
	
	@GetMapping("/category/liveCategory/{idx}")
	public LiveCategory liveCategoryItem(@PathVariable(value = "idx") int idx) {	  
		return liveService.getContentCategoryItem(idx);
	}
	
	// liveCategory ������ ����� IDX (���� ������ ����)
	@GetMapping("/category/liveCategoryLastNodeIdx")
	public int liveCategoryLastNodeIdx() {	  
		return liveService.getContentCategoryLastNodeIdx();
	}
	
	// liveCategory ���� ��� �ִ��� üũ
	@GetMapping("/category/childrenIdx/{idx}")
	public int liveCategoryChildrenIdx(@PathVariable(value = "idx") int idx) {	  
		return liveService.getContentCategoryChildrenIdx(idx);
	}
	
	// liveCategory �ش� ī�װ��� ���� �׷� ��� (�迭 ��)
	@GetMapping("/category/groupIdx/{idx}")
	public ResponseEntity<?> liveCategoryGroupIdx(@PathVariable(value = "idx") long idx) {	  
		ArrayList<Integer> categoryIdxList = liveService.getContentCategoryGroupIdx(idx);
		
		return ResponseEntity.ok().body(categoryIdxList);
	}
	
	// liveCategory �ش� ī�װ��� ���� ��� ���� ��� (�迭 ��)
//	@GetMapping("/category/allChildIdx/{parentId}")
//	public ResponseEntity<?> liveCategoryAllChildIdx(@PathVariable(value = "parentId") int parentId) {	  
//		ArrayList<Long> categoryIdxList = liveService.getliveCategoryAllChildIdx(parentId);
//		
//		return ResponseEntity.ok().body(categoryIdxList);
//	}
	
	// parentId�� liveCategory ���� ���� �� Position
	@GetMapping("/category/liveCategoryMaxPosition/{parentId}")
	public int liveCategoryMaxPosition(@PathVariable(value = "parentId") int parentId) {	  
		LiveCategory categoryItem = liveService.getContentCategoryMaxPosition(parentId);
		int position = 1;
		if (categoryItem != null) {
			position = categoryItem.getPosition()+1;
		}
		return position;
	}
	
	// ī�װ� ���
    @PostMapping("/category/liveCategory")
    public ResponseEntity<?> createliveCategory(@Valid @RequestBody categoryRequest request) {
    	try {
    		liveCategoryRepository.save(request.liveToEntitys());
			
		} catch (Exception e) {
			e.printStackTrace();
            return new ResponseEntity(new ApiResponse(false, "Category created failed"),HttpStatus.BAD_REQUEST);
		}   
    	return ResponseEntity.ok().body(new ApiResponse(true, "Category created successfully"));
    }
    
    // ī�װ� ����
	@DeleteMapping("/category/liveCategory")
    public ResponseEntity<?> removeliveCategory(@RequestParam(value = "idx") long idx) {
    	try {
			liveService.removeContentCategory(idx);
			
		} catch (Exception e) {
			e.printStackTrace();
            return new ResponseEntity(new ApiResponse(false, "Category removed failed"),HttpStatus.BAD_REQUEST);
		}
    	return ResponseEntity.ok().body(new ApiResponse(true, "Category removed successfully"));
    }

}
