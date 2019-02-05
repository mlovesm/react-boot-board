package com.example.boot.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.boot.domain.ContentCategory;
import com.example.boot.domain.VodRepo;
import com.example.boot.repository.ContentCategoryRepository;
import com.example.boot.repository.VODRepository;

@Service
public class PollService {

    @Autowired
    private ContentCategoryRepository contentCategoryRepository;

    @Autowired
    private VODRepository vodRepository;
    
    private static final Logger logger = LoggerFactory.getLogger(PollService.class);

    
    public Page<VodRepo> getVodRepoList(Pageable pageable) {
    	System.out.println("service pageable="+ pageable.getSort());
        pageable = PageRequest.of(pageable.getPageNumber() <= 0 ? 0 : pageable.getPageNumber() - 1, pageable.getPageSize()
        		, pageable.getSort());

    	Page<VodRepo> list= vodRepository.findAll(pageable);
    	logger.debug("list="+ list); 
    	return list;
    }
    
    public Page<VodRepo> getIdxVodRepoList(int categoryIdx, Pageable pageable) {
        pageable = PageRequest.of(pageable.getPageNumber() <= 0 ? 0 : pageable.getPageNumber() - 1, pageable.getPageSize()
        		, pageable.getSort());
    	
    	ContentCategory category = getContentCategoryItem(categoryIdx);
    	Page<VodRepo> list= vodRepository.findByContentCategory(category, pageable);
        
    	return list;
    }
    
    public List<HashMap<String, Object>> getContentCategory(long idx) {
        List<ContentCategory> parentCategoryList = contentCategoryRepository.findByParentIdOrderByIdx((int)idx);
        List<HashMap<String, Object>> mapList = new ArrayList<>();

        for (int i = 0; i < parentCategoryList.size(); i++) {
        	LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        	map.put("value", parentCategoryList.get(i).getIdx());
        	long parentIdx= parentCategoryList.get(i).getIdx();
        	map.put("label", parentCategoryList.get(i).getCategoryName());
        	map.put("parentId", parentCategoryList.get(i).getParentId());
        	map.put("vodSize", parentCategoryList.get(i).getVodRepository().size());	
        	
    		List<HashMap<String, Object>> childMapList = new ArrayList<>();
    		childMapList = getContentCategory((int)parentIdx);
    		if(childMapList.size() > 0) {
    			map.put("children", childMapList);
    		}
        	mapList.add(i, map);
		}
        return mapList; 
    }
    
    // 카테고리 아이템
    public ContentCategory getContentCategoryItem(int idx) {
    	ContentCategory category = contentCategoryRepository.findById((long) idx).orElse(new ContentCategory());

    	return category;
    }
    
    // parentId별 카테고리 count
    public ContentCategory getContentCategoryMaxPosition(int parentId) {
    	ContentCategory maxPosotionItem = contentCategoryRepository.findTopByParentIdOrderByPositionDesc(parentId);

    	return maxPosotionItem;
    }
    
    // 카테고리 삭제
    public void removeContentCategory(long idx) { 
    	ContentCategory category = contentCategoryRepository.findById(idx).orElse(new ContentCategory());
    	if(category.getIdx() != null) {
    		contentCategoryRepository.deleteById(idx);	// 상위 삭제
    		System.out.println("삭제 idx="+idx);
    	}
    	List<ContentCategory> categoryList = contentCategoryRepository.findByParentIdOrderByPosition((int)idx);
    	if(categoryList.size() > 0) {	// 하위 카테고리가 있으면 같이 삭제
    		contentCategoryRepository.deleteByParentId((int)idx);	// 하위 삭제
    		System.out.println("삭제 idx="+categoryList.get(0).getIdx());
    		
    		idx = categoryList.get(0).getIdx();
    		// 반복
    		removeContentCategory(idx);
    	}
    }


//    public Poll createPoll(PollRequest pollRequest) {
//        Poll poll = new Poll();
//        poll.setQuestion(pollRequest.getQuestion());
//
//        pollRequest.getChoices().forEach(choiceRequest -> {
//            poll.addChoice(new Choice(choiceRequest.getText()));
//        });
//
//        Instant now = Instant.now();
//        Instant expirationDateTime = now.plus(Duration.ofDays(pollRequest.getPollLength().getDays()))
//                .plus(Duration.ofHours(pollRequest.getPollLength().getHours()));
//
//        poll.setExpirationDateTime(expirationDateTime);
//
//        return pollRepository.save(poll);
//    }
//
//    public PollResponse getPollById(Long pollId, UserPrincipal currentUser) {
//        Poll poll = pollRepository.findById(pollId).orElseThrow(
//                () -> new ResourceNotFoundException("Poll", "id", pollId));
//
//        // Retrieve Vote Counts of every choice belonging to the current poll
//        List<ChoiceVoteCount> votes = voteRepository.countByPollIdGroupByChoiceId(pollId);
//
//        Map<Long, Long> choiceVotesMap = votes.stream()
//                .collect(Collectors.toMap(ChoiceVoteCount::getChoiceId, ChoiceVoteCount::getVoteCount));
//
//        // Retrieve poll creator details
//        User creator = userRepository.findById(poll.getCreatedBy())
//                .orElseThrow(() -> new ResourceNotFoundException("User", "id", poll.getCreatedBy()));
//
//        // Retrieve vote done by logged in user
//        Vote userVote = null;
//        if(currentUser != null) {
//            userVote = voteRepository.findByUserIdAndPollId(currentUser.getId(), pollId);
//        }
//
//        return ModelMapper.mapPollToPollResponse(poll, choiceVotesMap,
//                creator, userVote != null ? userVote.getChoice().getId(): null);
//    }
//
//    public PollResponse castVoteAndGetUpdatedPoll(Long pollId, VoteRequest voteRequest, UserPrincipal currentUser) {
//        Poll poll = pollRepository.findById(pollId)
//                .orElseThrow(() -> new ResourceNotFoundException("Poll", "id", pollId));
//
//        if(poll.getExpirationDateTime().isBefore(Instant.now())) {
//            throw new BadRequestException("Sorry! This Poll has already expired");
//        }
//
//        User user = userRepository.getOne(currentUser.getId());
//
//        Choice selectedChoice = poll.getChoices().stream()
//                .filter(choice -> choice.getId().equals(voteRequest.getChoiceId()))
//                .findFirst()
//                .orElseThrow(() -> new ResourceNotFoundException("Choice", "id", voteRequest.getChoiceId()));
//
//        Vote vote = new Vote();
//        vote.setPoll(poll);
//        vote.setUser(user);
//        vote.setChoice(selectedChoice);
//
//        try {
//            vote = voteRepository.save(vote);
//        } catch (DataIntegrityViolationException ex) {
//            logger.info("User {} has already voted in Poll {}", currentUser.getId(), pollId);
//            throw new BadRequestException("Sorry! You have already cast your vote in this poll");
//        }
//
//        //-- Vote Saved, Return the updated Poll Response now --
//
//        // Retrieve Vote Counts of every choice belonging to the current poll
//        List<ChoiceVoteCount> votes = voteRepository.countByPollIdGroupByChoiceId(pollId);
//
//        Map<Long, Long> choiceVotesMap = votes.stream()
//                .collect(Collectors.toMap(ChoiceVoteCount::getChoiceId, ChoiceVoteCount::getVoteCount));
//
//        // Retrieve poll creator details
//        User creator = userRepository.findById(poll.getCreatedBy())
//                .orElseThrow(() -> new ResourceNotFoundException("User", "id", poll.getCreatedBy()));
//
//        return ModelMapper.mapPollToPollResponse(poll, choiceVotesMap, creator, vote.getChoice().getId());
//    }
//
//
//    private void validatePageNumberAndSize(int page, int size) {
//        if(page < 0) {
//            throw new BadRequestException("Page number cannot be less than zero.");
//        }
//
//        if(size > AppConstants.MAX_PAGE_SIZE) {
//            throw new BadRequestException("Page size must not be greater than " + AppConstants.MAX_PAGE_SIZE);
//        }
//    }
//
//    private Map<Long, Long> getChoiceVoteCountMap(List<Long> pollIds) {
//        // Retrieve Vote Counts of every Choice belonging to the given pollIds
//        List<ChoiceVoteCount> votes = voteRepository.countByPollIdInGroupByChoiceId(pollIds);
//
//        Map<Long, Long> choiceVotesMap = votes.stream()
//                .collect(Collectors.toMap(ChoiceVoteCount::getChoiceId, ChoiceVoteCount::getVoteCount));
//
//        return choiceVotesMap;
//    }
//
//    private Map<Long, Long> getPollUserVoteMap(UserPrincipal currentUser, List<Long> pollIds) {
//        // Retrieve Votes done by the logged in user to the given pollIds
//        Map<Long, Long> pollUserVoteMap = null;
//        if(currentUser != null) {
//            List<Vote> userVotes = voteRepository.findByUserIdAndPollIdIn(currentUser.getId(), pollIds);
//
//            pollUserVoteMap = userVotes.stream()
//                    .collect(Collectors.toMap(vote -> vote.getPoll().getId(), vote -> vote.getChoice().getId()));
//        }
//        return pollUserVoteMap;
//    }
//
//    Map<Long, User> getPollCreatorMap(List<Poll> polls) {
//        // Get Poll Creator details of the given list of polls
//        List<Long> creatorIds = polls.stream()
//                .map(Poll::getCreatedBy)
//                .distinct()
//                .collect(Collectors.toList());
//
//        List<User> creators = userRepository.findByIdIn(creatorIds);
//        Map<Long, User> creatorMap = creators.stream()
//                .collect(Collectors.toMap(User::getId, Function.identity()));
//
//        return creatorMap;
//    }
}
