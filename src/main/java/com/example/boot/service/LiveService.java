package com.example.boot.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.boot.domain.LiveCategory;
import com.example.boot.domain.LiveRepo;
import com.example.boot.repository.live.LiveCategoryRepository;
import com.example.boot.repository.live.LiveRepository;

@Service
public class LiveService {

    @Autowired
    private LiveCategoryRepository liveCategoryRepository;

    @Autowired
    private LiveRepository liveRepository;

    
    public Page<LiveRepo> getLiveRepoList(Pageable pageable) {
    	System.out.println("service pageable="+ pageable.getSort());
        pageable = PageRequest.of(pageable.getPageNumber() <= 0 ? 0 : pageable.getPageNumber() - 1, pageable.getPageSize()
        		, pageable.getSort());

        return liveRepository.findAll(pageable);
    }
    
    public Page<LiveRepo> getIdxLiveRepoList(int categoryIdx, Pageable pageable) {
        pageable = PageRequest.of(pageable.getPageNumber() <= 0 ? 0 : pageable.getPageNumber() - 1, pageable.getPageSize()
        		, pageable.getSort());
        
        //배열 카테고리 테스트
        List<Long> categoryIdxList = getContentCategoryAllChildIdx(categoryIdx);
        List<LiveCategory> categoryList = liveCategoryRepository.findByIdxIn(categoryIdxList);
    	return liveRepository.findByContentCategoryIn(categoryList, pageable);
    }
    
    public List<HashMap<String, Object>> getLiveCategory(long idx) {
        List<LiveCategory> parentCategoryList = liveCategoryRepository.findByParentIdOrderByIdx((int)idx);
        List<HashMap<String, Object>> mapList = new ArrayList<>();

        for (int i = 0; i < parentCategoryList.size(); i++) {
        	LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        	long parentIdx= parentCategoryList.get(i).getIdx();
        	map.put("value", parentIdx);
        	map.put("label", parentCategoryList.get(i).getCategoryName());
        	map.put("parentId", parentCategoryList.get(i).getParentId());
        	map.put("vodSize", parentCategoryList.get(i).getLiveRepository().size());	
        	
    		List<HashMap<String, Object>> childMapList = new ArrayList<>();
    		childMapList = getLiveCategory((int)parentIdx);
    		if(childMapList.size() > 0) {
    			map.put("children", childMapList);
    		}
        	mapList.add(i, map);
		}
        return mapList; 
    }
    
    // 카테고리 아이템
    public LiveCategory getContentCategoryItem(long idx) {
    	return liveCategoryRepository.findById(idx).orElse(new LiveCategory());
    }
    
    // ContentCategory 마지막 노드의 IDX
	public int getContentCategoryLastNodeIdx() {
		int categoryIdx = 0;
		
		while(true) {
			LiveCategory lastNodeItem = liveCategoryRepository.findTopByParentIdOrderByIdxDesc(categoryIdx);
			int itemIdx = Optional.ofNullable(lastNodeItem)
					.map(LiveCategory::getIdx)
					.map(Long::intValue).orElse(0);
				
			if(itemIdx != 0) {
				categoryIdx = itemIdx;
			}else {
				break;
			}
		}
		System.out.println("categoryIdx="+categoryIdx);
    	return categoryIdx;
    }
    
    // parentId별 카테고리 count
    public LiveCategory getContentCategoryMaxPosition(int parentId) {
    	return liveCategoryRepository.findTopByParentIdOrderByPositionDesc(parentId);
    }
    
    // 해당 카테고리의 자식노드가 있는지 확인 ( 0이면 없음 )
    public int getContentCategoryChildrenIdx(long idx) {   	
    	LiveCategory contentCategory = liveCategoryRepository.findTopByParentIdOrderByIdxDesc((int)idx);
		int itemIdx = Optional.ofNullable(contentCategory)
				.map(LiveCategory::getIdx)
				.map(Long::intValue).orElse(0);
		
		return itemIdx;
    }
    
    // 해당 카테고리가 속한 그룹 노드 (배열 값)
    public ArrayList<Integer> getContentCategoryGroupIdx(long idx) {
    	ArrayList<Integer> categoryIdxList = new ArrayList<>();
    	
		while(true) {
	    	int parentId = liveCategoryRepository.findById(idx)
	    			.map(LiveCategory::getParentId).orElse(0);
			
	    	categoryIdxList.add((int)idx);
			if(parentId != 0) {
				idx = parentId;
			}else {
				break;
			}
		}
		return categoryIdxList;
    }
    
    // 해당 카테고리가 속한 모든 하위 노드 (배열 값) 트리 클릭 시 적용
    public ArrayList<Long> getContentCategoryAllChildIdx(int parentId) {
    	ArrayList<Long> categoryIdxList = new ArrayList<>();
    	categoryIdxList.add((long)parentId);
    	while(true) {
        	List<LiveCategory> parentIdList = liveCategoryRepository.findByParentIdOrderByPosition(parentId);
        	for (LiveCategory contentCategory : parentIdList) {
    			parentId = contentCategory.getIdx().intValue();
    			categoryIdxList.add((long)parentId);
    		}
        	if(parentIdList.size() == 0) break;
    	}
		return categoryIdxList;
    }
    
    // 카테고리 삭제
    public void removeContentCategory(long idx) { 
    	LiveCategory category = liveCategoryRepository.findById(idx).orElse(new LiveCategory());
    	if(category.getIdx() != null) {
    		liveCategoryRepository.deleteById(idx);	// 상위 삭제
    		System.out.println("삭제 idx="+idx);
    	}
    	List<LiveCategory> categoryList = liveCategoryRepository.findByParentIdOrderByPosition((int)idx);
    	if(categoryList.size() > 0) {	// 하위 카테고리가 있으면 같이 삭제
    		liveCategoryRepository.deleteByParentId((int)idx);	// 하위 삭제
    		System.out.println("삭제 idx="+categoryList.get(0).getIdx());
    		
    		idx = categoryList.get(0).getIdx();
    		// 반복
    		removeContentCategory(idx);
    	}
    }
    
	// LiveRepo 상세
    public LiveRepo getLiveRepoItem(long idx) {
    	return liveRepository.findById(idx).orElse(new LiveRepo());
    }

}
