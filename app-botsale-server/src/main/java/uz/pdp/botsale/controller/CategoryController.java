package uz.pdp.botsale.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.pdp.botsale.payload.ApiResponse;
import uz.pdp.botsale.payload.ApiResponseModel;
import uz.pdp.botsale.payload.ReqCategory;
import uz.pdp.botsale.service.CategoryService;
import uz.pdp.botsale.utils.AppConstants;

@RestController
@RequestMapping("/api/category")
public class CategoryController {
    @Autowired
    CategoryService categoryService;

    @PostMapping
    public HttpEntity<?> saveOrEdit(@RequestBody ReqCategory reqCategory) {
        ApiResponse response = categoryService.saveOrEdit(reqCategory);
        return ResponseEntity.status(response.isSuccess() ? response.getMessage().equals("Saved") ? HttpStatus.CREATED : HttpStatus.ACCEPTED : HttpStatus.CONFLICT).body(response);
    }

    @GetMapping("/all")
    public ApiResponseModel getAll() {
        return categoryService.getAll();
    }

    @GetMapping("/getAllCategoriesByPageable")
    public HttpEntity<?> getAllByPageable(@RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) Integer page,
                                          @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) Integer size) {
        return ResponseEntity.ok(categoryService.getAllByPageable(page, size));
    }

    @DeleteMapping("/{id}")
    public HttpEntity<?> removeCategory(@PathVariable Integer id) {
        ApiResponse response = categoryService.removeCategory(id);
        return ResponseEntity.status(response.isSuccess() ? HttpStatus.OK : HttpStatus.CONFLICT).body(response);
    }

}
