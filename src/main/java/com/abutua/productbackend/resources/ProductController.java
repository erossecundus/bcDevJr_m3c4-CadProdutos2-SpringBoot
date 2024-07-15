package com.abutua.productbackend.resources;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.abutua.productbackend.models.Category;
import com.abutua.productbackend.models.Product;
import com.abutua.productbackend.repositories.CategoryRepository;
import com.abutua.productbackend.repositories.ProductRepository;

@RestController
@CrossOrigin
public class ProductController {

  // injetando dependencias
  @Autowired
  private ProductRepository productRepository;

  @Autowired
  private CategoryRepository categoryRepository;

  // salvar um produto
  @PostMapping("products")
  public ResponseEntity<Product> save(@RequestBody Product product){

    product = productRepository.save(product);

    // gerando o URI para o location - criando o produto
    URI location = ServletUriComponentsBuilder
      .fromCurrentRequest()
      .path("/{id}")
      .buildAndExpand(product.getId())
      .toUri();

    return ResponseEntity.created(location).body(product);
  }

  // buscar um produto
  @GetMapping("products/{id}")
  public ResponseEntity<Product> getProduct(@PathVariable int id) {  
    Product product = productRepository.findById(id)
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

    return ResponseEntity.ok(product);
  }

  // buscar todos os produtos
  @GetMapping("products")
  public List<Product> getProducts() {
    return productRepository.findAll();
  }
  
  // remover um produto
  @DeleteMapping("products/{id}")
  public ResponseEntity<Product> deleteProduct(@PathVariable int id) {  
    Product product = productRepository.findById(id)
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
    productRepository.delete(product);

    return ResponseEntity.noContent().build();
  }

  // atualizar um produto
  @PutMapping("products/{id}")
  public ResponseEntity<Product> updateProduct(@PathVariable int id, @RequestBody Product productUpdate) {  
    Product product = productRepository.findById(id)
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

    if(productUpdate.getCategory() == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Category can not be empty");
    }

    Category category = categoryRepository.findById(productUpdate.getCategory().getId())
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Category not found"));

    product.setName(productUpdate.getName());
    product.setDescription(productUpdate.getDescription());
    product.setPrice(productUpdate.getPrice());
    product.setPromotion(productUpdate.isPromotion());
    product.setNewProduct(productUpdate.isNewProduct());
    product.setCategory(category);

    productRepository.save(product);

    return ResponseEntity.ok().build();
  }
}
