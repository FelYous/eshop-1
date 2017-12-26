package nano.project.eshop.services;


import nano.project.eshop.models.Product;
import nano.project.eshop.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

@Service("productService")
public class ProductService {

    private ProductRepository productRepository;

    @Autowired
    public ProductService(@Qualifier("productRepository") ProductRepository productRepository) {
        this.productRepository = productRepository;
    }


    public Product save(Product product){
        return productRepository.save(product);
    }

    public Page<Product> findByCategory(String category , Pageable pageable){
        return  productRepository.findByCategory(category,pageable);
    }

    public Product findById(Long id) {
        return productRepository.findOne(id);
    }


    public List<Product> findLast10(){
        return productRepository.findTop10ByOrderByIdDesc();
    }


    public List<Product> findAllProducts(){
        return (List<Product>) productRepository.findAll();
    }

    public List<Product> findByCategory(String category){
        return productRepository.findByCategory(category);
    }


    public long count() {
        return productRepository.count();
    }

    public Long countByCategory(String category){
        return  productRepository.countByCategory(category);
    }

    public void delete(long id) {
        productRepository.delete(id);
    }




}