package uz.pdp.botsale.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import uz.pdp.botsale.entity.Category;
import uz.pdp.botsale.exception.ResourceNotFoundException;
import uz.pdp.botsale.payload.*;
import uz.pdp.botsale.repository.CategoryRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class    CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public ApiResponse saveOrEdit(ReqCategory reqCategory) {
        ApiResponse apiResponse = new ApiResponse();
        try {
            apiResponse.setSuccess(true);
            apiResponse.setMessage("Saved");
            Category category = new Category();
            if (reqCategory.getId() != null) {
                category = categoryRepository.findById(reqCategory.getId()).orElseThrow(() -> new ResourceNotFoundException("category", "id", reqCategory.getId()));
                apiResponse.setMessage("Edited");
            }
            if (reqCategory.getParentId() != null) {
                category.setParent(categoryRepository.findById(reqCategory.getParentId()).orElseThrow(() -> new ResourceNotFoundException("parentCategory", "id", reqCategory.getParentId())));
            }
            category.setNameUz(reqCategory.getNameUz());
            category.setNameRu(reqCategory.getNameRu());
            categoryRepository.save(category);
        } catch (Exception e) {
            apiResponse.setMessage("Error");
            apiResponse.setSuccess(false);
        }
        return apiResponse;
    }

    public ResPageable getAllByPageable(Integer page, Integer size) {
        List<ResCategory> resCategoryList = new ArrayList<>();
        Page<Category> categoryPage = categoryRepository.findAll(PageRequest.of(page, size, Sort.Direction.DESC, "id"));
        for (Category category : categoryPage) {
            resCategoryList.add(getResCategory(category));
        }
        return new ResPageable(resCategoryList, categoryPage.getTotalElements(), page);
    }


    public ResCategory getResCategory(Category category) {
        ResCategory resCategory = new ResCategory();
        resCategory.setId(category.getId());
        resCategory.setNameUz(category.getNameUz());
        resCategory.setNameRu(category.getNameRu());
        if (category.getParent() != null) {
            resCategory.setParentId(category.getParent().getId());
            resCategory.setParentNameUz(category.getParent().getNameUz());
            resCategory.setParentNameRu(category.getParent().getNameRu());
        }
        return resCategory;
    }


    public ApiResponse removeCategory(Integer id) {
        try {
            categoryRepository.deleteById(id);
            return new ApiResponse("Deleted", true);
        } catch (Exception e) {
            return new ApiResponse("Error", false);
        }
    }

    public ApiResponseModel getAll() {
        List<ResCategory> resCategoryList = new ArrayList<>();
        List<Category> all = categoryRepository.findAll();
        for (Category category : all) {
            resCategoryList.add(getResCategory(category));
        }
        return new ApiResponseModel(true, "Ok", resCategoryList);
    }
}
