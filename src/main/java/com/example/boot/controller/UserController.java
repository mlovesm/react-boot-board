package com.example.boot.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.boot.domain.ContentCategory;
import com.example.boot.domain.VodRepo;
import com.example.boot.payload.UserIdentityAvailability;
import com.example.boot.payload.UserSummary;
import com.example.boot.repository.ContentCategoryRepository;
import com.example.boot.repository.UserRepository;
import com.example.boot.repository.VODRepository;
import com.example.boot.security.CurrentUser;
import com.example.boot.security.UserPrincipal;
import com.example.boot.service.PollService;

import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;

@Slf4j
@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserRepository userRepository;
    
	@Autowired
	private VODRepository vodRepository;
	
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
	
	@GetMapping("/vod/list/2")
	public Page<VodRepo> getIdxCategory(Pageable pageable) {
		JSONObject jsonObject = new JSONObject();
		pageable = PageRequest.of(0, 10);
		
		Page<VodRepo> list= pollService.getIdxVodRepoList("타이틀17", pageable);
		try {
//			List<ContentCategory> list= pollService.getIdxVodRepoList(categoryIdx, pageable);
			
			jsonObject.put("data", list);
			jsonObject.put("status", "success");
		} catch (Exception e) {
			e.printStackTrace();
			jsonObject.put("status", "failed");
		}
		return list;
//		return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.OK); 
	}
  
	@GetMapping("/vod/contentCategory")
	public ResponseEntity<JSONObject> getCategory() {
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
