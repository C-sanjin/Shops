package com.shopx.service.product.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shopx.common.exception.BizException;
import com.shopx.common.result.ResultCode;
import com.shopx.dao.mapper.ProductMapper;
import com.shopx.dao.mapper.ProductStoreMapper;
import com.shopx.dao.mapper.StoreMapper;
import com.shopx.model.dto.ProductDTO;
import com.shopx.model.entity.Product;
import com.shopx.model.entity.ProductStore;
import com.shopx.model.entity.Store;
import com.shopx.model.vo.ProductVO;
import com.shopx.model.vo.StoreVO;
import com.shopx.service.product.ProductService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductMapper productMapper;
    private final ProductStoreMapper productStoreMapper;
    private final StoreMapper storeMapper;

    public ProductServiceImpl(ProductMapper productMapper,
                              ProductStoreMapper productStoreMapper,
                              StoreMapper storeMapper) {
        this.productMapper = productMapper;
        this.productStoreMapper = productStoreMapper;
        this.storeMapper = storeMapper;
    }

    @Override
    public IPage<ProductVO> listProducts(Integer status, int page, int size) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(Product::getStatus, status);
        }
        wrapper.eq(Product::getDeleted, 0);
        wrapper.orderByDesc(Product::getCreatedAt);

        Page<Product> productPage = new Page<>(page, size);
        IPage<Product> result = productMapper.selectPage(productPage, wrapper);

        return result.convert(product -> {
            ProductVO vo = new ProductVO();
            BeanUtils.copyProperties(product, vo);
            return vo;
        });
    }

    @Override
    public ProductVO getProductDetail(Long productId) {
        Product product = productMapper.selectById(productId);
        if (product == null || product.getDeleted() == 1) {
            throw new BizException(ResultCode.PRODUCT_NOT_FOUND);
        }

        ProductVO vo = new ProductVO();
        BeanUtils.copyProperties(product, vo);

        List<Long> storeIds = productStoreMapper.selectStoreIdsByProductId(productId);
        if (!storeIds.isEmpty()) {
            LambdaQueryWrapper<Store> storeWrapper = new LambdaQueryWrapper<>();
            storeWrapper.in(Store::getId, storeIds);
            storeWrapper.eq(Store::getDeleted, 0);
            List<Store> stores = storeMapper.selectList(storeWrapper);
            List<StoreVO> storeVOList = stores.stream().map(store -> {
                StoreVO storeVO = new StoreVO();
                BeanUtils.copyProperties(store, storeVO);
                return storeVO;
            }).collect(Collectors.toList());
            vo.setStores(storeVOList);
        } else {
            vo.setStores(new ArrayList<>());
        }

        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createProduct(ProductDTO dto) {
        Product product = new Product();
        BeanUtils.copyProperties(dto, product);
        product.setStatus(1);
        product.setDeleted(0);
        productMapper.insert(product);

        if (dto.getStoreScope() != null && dto.getStoreScope() == 1 && dto.getStoreIds() != null) {
            for (Long storeId : dto.getStoreIds()) {
                ProductStore ps = new ProductStore();
                ps.setProductId(product.getId());
                ps.setStoreId(storeId);
                productStoreMapper.insert(ps);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProduct(Long productId, ProductDTO dto) {
        Product product = productMapper.selectById(productId);
        if (product == null || product.getDeleted() == 1) {
            throw new BizException(ResultCode.PRODUCT_NOT_FOUND);
        }

        product.setProductName(dto.getProductName());
        product.setPrice(dto.getPrice());
        product.setOriginalPrice(dto.getOriginalPrice());
        product.setStock(dto.getStock());
        product.setDescription(dto.getDescription());
        product.setImages(dto.getImages());
        product.setStoreScope(dto.getStoreScope());
        productMapper.updateById(product);

        if (dto.getStoreScope() != null && dto.getStoreScope() == 1 && dto.getStoreIds() != null) {
            productStoreMapper.deleteByProductId(productId);
            for (Long storeId : dto.getStoreIds()) {
                ProductStore ps = new ProductStore();
                ps.setProductId(productId);
                ps.setStoreId(storeId);
                productStoreMapper.insert(ps);
            }
        }
    }

    @Override
    public void bindStore(Long productId, List<Long> storeIds) {
        for (Long storeId : storeIds) {
            LambdaQueryWrapper<ProductStore> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ProductStore::getProductId, productId);
            wrapper.eq(ProductStore::getStoreId, storeId);
            ProductStore existing = productStoreMapper.selectOne(wrapper);
            if (existing == null) {
                ProductStore ps = new ProductStore();
                ps.setProductId(productId);
                ps.setStoreId(storeId);
                productStoreMapper.insert(ps);
            }
        }
    }

    @Override
    public void unbindStore(Long productId, List<Long> storeIds) {
        for (Long storeId : storeIds) {
            LambdaQueryWrapper<ProductStore> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ProductStore::getProductId, productId);
            wrapper.eq(ProductStore::getStoreId, storeId);
            productStoreMapper.delete(wrapper);
        }
    }
}
