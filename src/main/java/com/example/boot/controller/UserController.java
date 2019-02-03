package com.example.boot.controller;

import java.util.HashMap;
import java.util.List;

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
import com.example.boot.domain.VodRepo;
import com.example.boot.payload.ApiResponse;
import com.example.boot.payload.ContentCategoryRequest;
import com.example.boot.payload.UserIdentityAvailability;
import com.example.boot.payload.UserSummary;
import com.example.boot.repository.ContentCategoryRepository;
import com.example.boot.repository.UserRepository;
import com.example.boot.security.CurrentUser;
import com.example.boot.security.UserPrincipal;
import com.example.boot.service.PollService;

import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;

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
	public Page<VodRepo> getTest(@PageableDefault Pageable pageable) {
		System.out.println("pageable= "+pageable);

//	  Iterable<VodRepo> list= pollService.getVodRepoList();
//	  Iterator<VodRepo> itr = list.iterator();
//      while (itr.hasNext()) {
//          //System.out.println(itr.next());
//    	  VodRepo mc = itr.next();
//          System.out.println("id: " + mc.getIdx());
//          System.out.println("name: " + mc.getVodTitle());
//      } // end while
	  
		Page<VodRepo> list= pollService.getVodRepoList(pageable);
		return list;
	}
	
	@GetMapping("/vod/list/{categoryIdx}")
	public ResponseEntity<?> contentCategory(@PathVariable(value = "categoryIdx") int categoryIdx, Pageable pageable) {
		JSONObject jsonObject = new JSONObject();
	  
		Page<VodRepo> categoryList = pollService.getIdxVodRepoList(categoryIdx, pageable);
		
		try {
			jsonObject.put("status", "success");
		} catch (Exception e) {
			e.printStackTrace();
			jsonObject.put("status", "failed");
		}
		return new ResponseEntity<Page<VodRepo>>(categoryList, HttpStatus.OK); 
	}
  
	@GetMapping("/vod/contentCategory")
	public ResponseEntity<JSONObject> contentCategory() {
		JSONObject jsonObject = new JSONObject();
	  
		try {
			List<HashMap<String, Object>> categoryList = pollService.getContentCategory();
			jsonObject.put("data", categoryList);
			jsonObject.put("status", "success");
		} catch (Exception e) {
			e.printStackTrace();
			jsonObject.put("data", null);
			jsonObject.put("status", "failed");
		}

		return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.OK); 
	}
	
	@GetMapping("/vod/contentCategory/{idx}")
	public ContentCategory contentCategoryItem(@PathVariable(value = "idx") int idx) {	  
		ContentCategory category = pollService.getContentCategoryItem(idx);
		return category; 
	}
	
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
