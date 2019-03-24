package com.example.boot.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.boot.domain.ContentCategory;
import com.example.boot.domain.DBFile;
import com.example.boot.domain.VodRepo;
import com.example.boot.payload.ApiResponse;
import com.example.boot.payload.ContentCategoryRequest;
import com.example.boot.payload.UserIdentityAvailability;
import com.example.boot.payload.UserSummary;
import com.example.boot.payload.VodRepoRequest;
import com.example.boot.repository.ContentCategoryRepository;
import com.example.boot.repository.UserRepository;
import com.example.boot.repository.VODRepository;
import com.example.boot.security.CurrentUser;
import com.example.boot.security.UserPrincipal;
import com.example.boot.service.PollService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SuppressWarnings({ "unchecked", "rawtypes" })
@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ContentCategoryRepository contentCategoryRepository;
    
    @Autowired
    private VODRepository vodRepository;
	
	@Autowired
	private PollService pollService;


    @GetMapping("/user/me")
    @PreAuthorize("hasRole('USER')")
    public UserSummary getCurrentUser(@CurrentUser UserPrincipal currentUser) {
        UserSummary userSummary = new UserSummary(currentUser.getId(), currentUser.getUsername(), currentUser.getName());
        return userSummary;
    }

    @GetMapping("/user/checkUsernameAvailability")
    public UserIdentityAvailability checkUsernameAvailability(@RequestParam(value = "username") String username) {
        Boolean isAvailable = !userRepository.existsByUsername(username);
        log.debug("들어옴"+ isAvailable);
        return new UserIdentityAvailability(isAvailable);
    }

    @GetMapping("/user/checkEmailAvailability")
    public UserIdentityAvailability checkEmailAvailability(@RequestParam(value = "email") String email) {
        Boolean isAvailable = !userRepository.existsByEmail(email);
        return new UserIdentityAvailability(isAvailable);
    }
    
	@GetMapping("/vod/list")
	public ResponseEntity<?> getTest(@PageableDefault Pageable pageable) {
		System.out.println("pageable= "+pageable);

//	  Iterable<VodRepo> list= pollService.getVodRepoList();
//	  Iterator<VodRepo> itr = list.iterator();
//      while (itr.hasNext()) {
//          //System.out.println(itr.next());
//    	  VodRepo mc = itr.next();
//          System.out.println("id: " + mc.getIdx());
//          System.out.println("name: " + mc.getVodTitle());
//      } // end while
	  
		Page<VodRepo> vodRepoList= pollService.getVodRepoList(pageable);
		
		HashMap< String, Object> hashMap = new HashMap<>();
		hashMap.put("vodRepoList", vodRepoList);
		
		return new ResponseEntity<HashMap< String, Object>>(hashMap, HttpStatus.OK); 
	}
	
	// 해당 카테고리의 VOD
	@GetMapping("/vod/list/{categoryIdx}")
	public ResponseEntity<?> contentCategory(@PathVariable(value = "categoryIdx") int categoryIdx, Pageable pageable) {	  
		pollService.getContentCategoryChildrenIdx(categoryIdx);
		
		Page<VodRepo> vodRepoList = pollService.getIdxVodRepoList(categoryIdx, pageable);
		HashMap< String, Object> hashMap = new HashMap<>();
		hashMap.put("vodRepoList", vodRepoList);
		hashMap.put("selectedKey", categoryIdx);
		
		return new ResponseEntity<HashMap< String, Object>>(hashMap, HttpStatus.OK); 
	}
	
	// Contents 상세
	@GetMapping("/vod/vodRepo/{idx}")
	public ResponseEntity<?> vodRepoItem(@PathVariable(value = "idx") long idx) {	  
		VodRepo vodRepo = pollService.getVodRepoItem(idx);
		
		DBFile dbFile = Optional.ofNullable(vodRepo.getDbFile()).orElse(new DBFile());
		
		long categoryIdx = Optional.ofNullable(vodRepo)
				.map(VodRepo::getContentCategory)
				.map(ContentCategory::getIdx).orElse(0L);
			
		HashMap< String, Object> hashMap = new HashMap<>();
		LinkedHashMap<String, Object> linkedHashMap = new LinkedHashMap<>();
		linkedHashMap.put("id", dbFile.getId());
		linkedHashMap.put("originalFileName", dbFile.getOriginalFileName());
		linkedHashMap.put("fileSize", dbFile.getFileSize());
		linkedHashMap.put("fileType", dbFile.getFileType());
		
		hashMap.put("vodRepo", vodRepo);
		hashMap.put("categoryIdx", categoryIdx);
		hashMap.put("dbFile", linkedHashMap);
		
		return new ResponseEntity<HashMap< String, Object>>(hashMap, HttpStatus.OK); 
	}
  
	@GetMapping("/vod/contentCategory")
	public ResponseEntity<?> contentCategory() {
	  
		List<HashMap<String, Object>> categoryList = pollService.getContentCategory(0);

		return ResponseEntity.ok().body(categoryList);
	}
	
	@GetMapping("/vod/contentCategory/{idx}")
	public ContentCategory contentCategoryItem(@PathVariable(value = "idx") int idx) {	  
		return pollService.getContentCategoryItem(idx);
	}
	
	// ContentCategory 마지막 노드의 IDX (현재 쓰이지 않음)
	@GetMapping("/category/contentCategoryLastNodeIdx")
	public int contentCategoryLastNodeIdx() {	  
		return pollService.getContentCategoryLastNodeIdx();
	}
	
	// ContentCategory 하위 노드 있는지 체크
	@GetMapping("/category/childrenIdx/{idx}")
	public int contentCategoryChildrenIdx(@PathVariable(value = "idx") int idx) {	  
		return pollService.getContentCategoryChildrenIdx(idx);
	}
	
	// ContentCategory 해당 카테고리가 속한 그룹 노드 (배열 값)
	@GetMapping("/category/groupIdx/{idx}")
	public ResponseEntity<?> contentCategoryGroupIdx(@PathVariable(value = "idx") long idx) {	  
		ArrayList<Integer> categoryIdxList = pollService.getContentCategoryGroupIdx(idx);
		
		return ResponseEntity.ok().body(categoryIdxList);
	}
	
	// ContentCategory 해당 카테고리가 속한 모든 하위 노드 (배열 값)
	@GetMapping("/category/allChildIdx/{parentId}")
	public ResponseEntity<?> contentCategoryAllChildIdx(@PathVariable(value = "parentId") int parentId) {	  
		ArrayList<Integer> categoryIdxList = pollService.getContentCategoryAllChildIdx(parentId);
		
		return ResponseEntity.ok().body(categoryIdxList);
	}
	
	// parentId별 ContentCategory 다음 생성 될 Position
	@GetMapping("/category/contentCategoryMaxPosition/{parentId}")
	public int contentCategoryMaxPosition(@PathVariable(value = "parentId") int parentId) {	  
		ContentCategory categoryItem = pollService.getContentCategoryMaxPosition(parentId);
		int position = 1;
		if (categoryItem != null) {
			position = categoryItem.getPosition()+1;
		}
		return position;
	}
	
	// 카테고리 등록
    @PostMapping("/category/contentCategory")
    public ResponseEntity<?> createContentCategory(@Valid @RequestBody ContentCategoryRequest categoryRequest) {
    	try {
    		contentCategoryRepository.save(categoryRequest.toEntity());
			
		} catch (Exception e) {
			e.printStackTrace();
            return new ResponseEntity(new ApiResponse(false, "Category created failed"),HttpStatus.BAD_REQUEST);
		}   
    	return ResponseEntity.ok().body(new ApiResponse(true, "Category created successfully"));
    }
    
    // 카테고리 삭제
	@DeleteMapping("/category/contentCategory")
    public ResponseEntity<?> removeContentCategory(@RequestParam(value = "idx") long idx) {
    	try {
			pollService.removeContentCategory(idx);
			
		} catch (Exception e) {
			e.printStackTrace();
            return new ResponseEntity(new ApiResponse(false, "Category removed failed"),HttpStatus.BAD_REQUEST);
		}
    	return ResponseEntity.ok().body(new ApiResponse(true, "Category removed successfully"));
    }
	
	
	// mp4 영상 등록
    @PostMapping("/vod/{categoryIdx}/video")
    public ResponseEntity<?> insertVodRepository(@PathVariable (value = "categoryIdx") Long categoryIdx, 
    		@Valid @RequestBody VodRepoRequest vodRepoRequest) {
    	try {
    		System.out.println("categoryIdx="+ categoryIdx);
    		ContentCategory contentCategory = pollService.getContentCategoryItem(categoryIdx);
    		vodRepoRequest.setContentCategory(contentCategory);
    		
    		DBFile dbFile = pollService.getDBFileItem(vodRepoRequest.getVodPath());
    		vodRepoRequest.setDbFile(dbFile);
    		
    		System.out.println(vodRepoRequest.toEntity().getVodTitle());
    		System.out.println(vodRepoRequest.toEntity().getContentCategory().getCategoryName());
    		System.out.println(vodRepoRequest.getVodPath());
    		
    		vodRepository.save(vodRepoRequest.toEntity());
			
		} catch (Exception e) {
			e.printStackTrace();
            return new ResponseEntity(new ApiResponse(false, "Video inserted failed"),HttpStatus.BAD_REQUEST);
		}   
    	return ResponseEntity.ok().body(new ApiResponse(true, "Video inserted successfully"));
    }


//    @GetMapping("/users/{username}")
//    public UserProfile getUserProfile(@PathVariable(value = "username") String username) {
//        User user = userRepository.findByUsername(username)
//                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
//
//        long pollCount = pollRepository.countByCreatedBy(user.getId());
//        long voteCount = voteRepository.countByUserId(user.getId());
//
//        UserProfile userProfile = new UserProfile(user.getId(), user.getUsername(), user.getName(), user.getCreatedAt(), pollCount, voteCount);
//
//        return userProfile;
//    }

//    @GetMapping("/users/{username}/polls")
//    public PagedResponse<PollResponse> getPollsCreatedBy(@PathVariable(value = "username") String username,
//                                                         @CurrentUser UserPrincipal currentUser,
//                                                         @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
//                                                         @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size) {
//        return pollService.getPollsCreatedBy(username, currentUser, page, size);
//    }
//
//
//    @GetMapping("/users/{username}/votes")
//    public PagedResponse<PollResponse> getPollsVotedBy(@PathVariable(value = "username") String username,
//                                                       @CurrentUser UserPrincipal currentUser,
//                                                       @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
//                                                       @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size) {
//        return pollService.getPollsVotedBy(username, currentUser, page, size);
//    }

}
