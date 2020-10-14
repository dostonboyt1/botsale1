package uz.pdp.botsale.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import uz.pdp.botsale.entity.Brand;
import uz.pdp.botsale.entity.Product;
import uz.pdp.botsale.entity.ProductSize;
import uz.pdp.botsale.exception.ResourceNotFoundException;
import uz.pdp.botsale.payload.*;
import uz.pdp.botsale.repository.*;
import uz.pdp.botsale.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProductService {
    @Autowired
    ProductRepository productRepository;

    @Autowired
    AttachmentRepository attachmentRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    BrandRepository brandRepository;

    @Autowired
    ProductSizeRepository productSizeRepository;

    @Autowired
    CategoryService categoryService;


    public ApiResponse saveOrEdit(ReqProduct reqProduct) {
        ApiResponse apiResponse = new ApiResponse();
        try {
            apiResponse.setSuccess(true);
            apiResponse.setMessage("Saved");
            Product product = new Product();
            if (reqProduct.getId() != null) {
                apiResponse.setMessage("Edited");
                product = productRepository.findById(reqProduct.getId()).orElseThrow(() -> new ResourceNotFoundException("product", "id", reqProduct.getId()));
                List<ProductSize> newProductSizeList = new ArrayList<>();
                for (UUID uuid : reqProduct.getProductSizeList()) {
                    newProductSizeList.add(productSizeRepository.findById(uuid).orElseThrow(() -> new ResourceNotFoundException("productSize", "id", uuid)));
                }
                List<ProductSize> removeingList = new ArrayList<>();
                List<ProductSize> oldProductSizeList = product.getProductSizeList();
                for (ProductSize oldProductSize : oldProductSizeList) {
                    boolean isHave = false;
                    for (ProductSize newProductSize : newProductSizeList) {
                        if (oldProductSize.getId().equals(newProductSize.getId())) {
                            isHave = true;
                        }
                    }
                    if (!isHave) {
                        removeingList.add(oldProductSize);
                    }
                }
                for (ProductSize oldProductSize : removeingList) {
                    boolean isHave = true;
                    for (ProductSize old : oldProductSizeList) {
                        if (oldProductSize.getId().equals(old.getId())) {
                            isHave = false;
                        }
                    }
                    if (!isHave) {
                        oldProductSizeList.remove(oldProductSize);
                    }
                }
                for (ProductSize newProductSize : newProductSizeList) {
                    boolean isHave = false;
                    for (ProductSize oldProductSize : oldProductSizeList) {
                        if (oldProductSize.getId().equals(newProductSize.getId())) {
                            isHave = true;
                        }
                    }
                    if (!isHave) {
                        oldProductSizeList.add(newProductSize);
                    }
                }
                product.setProductSizeList(new ArrayList<>());
                product = productRepository.save(product);
                product.setProductSizeList(oldProductSizeList);

            } else {
                List<ProductSize> productSizeList = new ArrayList<>();
                for (UUID uuid : reqProduct.getProductSizeList()) {
                    productSizeList.add(productSizeRepository.findById(uuid).orElseThrow(() -> new ResourceNotFoundException("productSize", "id", uuid)));
                }
                product.setProductSizeList(productSizeList);
            }
            if (reqProduct.getPhotoId() != null) {
                product.setAttachment(attachmentRepository.findById(reqProduct.getPhotoId()).orElseThrow(() -> new ResourceNotFoundException("photo", "id", reqProduct.getPhotoId())));
            }

            product.setCategory(categoryRepository.findById(reqProduct.getCategoryId()).orElseThrow(() -> new ResourceNotFoundException("category", "id", reqProduct.getCategoryId())));
            product.setBrand(brandRepository.findById(reqProduct.getBrandId()).orElseThrow(() -> new ResourceNotFoundException("brand", "id", reqProduct.getBrandId())));
            product.setGender(reqProduct.getGender());
            product.setNameUz(reqProduct.getNameUz());
            product.setNameRu(reqProduct.getNameRu());
            product.setDescriptionUz(reqProduct.getDescriptionUz());
            product.setDescriptionRu(reqProduct.getDescriptionRu());
            product.setNorm(reqProduct.getNorm());
            product.setIncomePrice(reqProduct.getIncomePrice());
            product.setSalePrice(reqProduct.getSalePrice());
            productRepository.save(product);
        } catch (Exception e) {
            apiResponse.setSuccess(false);
            apiResponse.setMessage("Error");
        }
        return apiResponse;
    }

    public ApiResponse removeProduct(UUID id) {
        try {
            productRepository.deleteById(id);
            return new ApiResponse("Deleted", true);
        } catch (Exception e) {
            return new ApiResponse("Error", false);
        }
    }


    public ApiResponse changeActive(UUID id, boolean active) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            product.setActive(active);
            productRepository.save(product);
            return new ApiResponse(active ? "Activated" : "Blocked", true);
        }
        return new ApiResponse("Error", false);
    }

    public ResPageable getBySearch(Integer page, Integer size, String search) {
        List<ResProduct> resProductList = new ArrayList<>();
        long totalElement = 0;
        if (search.equals("all")) {
            Page<Product> productPage = productRepository.findAll(CommonUtils.getPageable(page, size));
            totalElement = productPage.getTotalElements();
            for (Product product : productPage) {
                resProductList.add(getResProduct(product));
            }
        } else {
            List<Product> bySearch = productRepository.findAllByNameUzStartingWithIgnoreCaseOrNameRuStartingWithIgnoreCaseOrBrandNameUzStartingWithIgnoreCaseOrBrandNameRuStartingWithIgnoreCaseOrCategoryNameUzStartingWithIgnoreCaseOrCategoryNameRuStartingWithIgnoreCase(search, search, search, search, search, search);
            for (Product product : bySearch) {
                resProductList.add(getResProduct(product));
            }
            totalElement = resProductList.size();
        }
        return new ResPageable(resProductList, totalElement, page);
    }

    public ResProduct getResProduct(Product product) {
        ResProduct resProduct = new ResProduct();
        resProduct.setId(product.getId());
        resProduct.setNameUz(product.getNameUz());
        resProduct.setNameRu(product.getNameRu());
        resProduct.setDescriptionUz(product.getDescriptionUz());
        resProduct.setDescriptionRu(product.getDescriptionRu());
        resProduct.setNorm(product.getNorm());
        resProduct.setGender(product.getGender());
        resProduct.setIncomePrice(product.getIncomePrice());
        resProduct.setSalePrice(product.getSalePrice());
        resProduct.setResCategory(categoryService.getResCategory(product.getCategory()));
        resProduct.setBrand(product.getBrand());
        resProduct.setProductSizeList(product.getProductSizeList());
        if (product.getAttachment() != null) {
            resProduct.setPhotoId(product.getAttachment().getId());
        }
        return resProduct;
    }

    public ApiResponseModel getByCatIdOrBrandId(Integer catId, Integer brandId) {
        List<ResProduct> resProductList = new ArrayList<>();
        if (catId != null && brandId != null) {
            resProductList = productRepository.findAllByCategoryIdAndBrandId(catId, brandId).stream().map(this::getResProduct).collect(Collectors.toList());
        } else if (catId != null && brandId == null) {
            resProductList = productRepository.findAllByCategoryId(catId).stream().map(this::getResProduct).collect(Collectors.toList());
        } else if (catId == null && brandId != null) {
            resProductList = productRepository.findAllByBrandId(brandId).stream().map(this::getResProduct).collect(Collectors.toList());
        }
        return new ApiResponseModel(true, "Ok", resProductList);
    }

    public ApiResponseModel sizeListByProduct(UUID id) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        return optionalProduct.map(product -> new ApiResponseModel(true, "ok", getResProduct(product))).orElseGet(() -> new ApiResponseModel(false, "Error", null));
    }
}
