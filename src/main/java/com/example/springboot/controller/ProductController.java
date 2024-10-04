package com.example.springboot.controller;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.springboot.dtos.ProductRecordDto;
import com.example.springboot.model.ProductModel;
import com.example.springboot.repositories.ProductRepository;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import jakarta.validation.Valid;

@RestController
public class ProductController {
	
	@Autowired
	ProductRepository productRepository;
	
	@GetMapping("/products")
	public ResponseEntity<List<ProductModel>> getAllProducts() {
	  List<ProductModel> productList =
	  productRepository.findAll();
			
		if (!productList.isEmpty()) {
			for(ProductModel product : productList) {
				UUID id = product.getIdProduct();
			product.add(linkTo(methodOn(ProductController.class).getAllProducts()).withRel("Product List:"));				
			}
		}		
		return ResponseEntity.status(HttpStatus.OK)
      .body(productList);				
	}
	
	@GetMapping("/products/{id}")
	public ResponseEntity<Object> getOneProduct(@PathVariable(value="id") UUID id) {
		Optional<ProductModel> productO =
	  productRepository.findById(id);
		if (productO.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body("Product not found.");					
		}
	  productO.get().add(linkTo(methodOn(ProductController.class).getOneProduct(id)).withRel("One Product:"));	
	    return ResponseEntity.status(HttpStatus.OK)
      .body(productO.get());	    						
	}
	
	@PostMapping("/products")
	public ResponseEntity<ProductModel>
	   saveProduct(@RequestBody @Valid ProductRecordDto produRecordDto){
		var productModel = new ProductModel();
		
		BeanUtils.copyProperties(produRecordDto, productModel);
		
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(productRepository.save(productModel));
	}
	
	@DeleteMapping("/products/{id}")
	public ResponseEntity<Object>
	deleteProduct(@PathVariable (value="id") UUID id) {
		Optional<ProductModel> productO =
	productRepository.findById(id);
		if (productO.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
		.body("Product not found.");
		}
		productRepository.delete(productO.get());
		return ResponseEntity.status(HttpStatus.OK)
	.body("Product deleted sucessfully");
		
	}
	
	@PutMapping("/products/{id}")
	public ResponseEntity<Object>
	updateProduct(@PathVariable (value="id") UUID id,
		@RequestBody @Valid ProductRecordDto productRecordDto) {
		Optional<ProductModel> productO =
	productRepository.findById(id);
		if (productO.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
	     .body("Product not found.");
		}
		
	   var productModel = productO.get();
	   BeanUtils.copyProperties(productRecordDto, productModel);
		
	   return ResponseEntity.status(HttpStatus.OK)
	.body(productRepository.save(productModel));
	}
	
 	
}
