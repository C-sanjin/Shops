package com.shopx.service.product;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.shopx.model.dto.ProductDTO;
import com.shopx.model.vo.ProductVO;

import java.util.List;

public interface ProductService {

    IPage<ProductVO> listProducts(Integer status, int page, int size);

    ProductVO getProductDetail(Long productId);

    void createProduct(ProductDTO dto);

    void updateProduct(Long productId, ProductDTO dto);

    void bindStore(Long productId, List<Long> storeIds);

    void unbindStore(Long productId, List<Long> storeIds);
}
