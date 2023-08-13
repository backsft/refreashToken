package com.Test.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.Test.dto.JwtResponse;
import com.Test.dto.Product;
import com.Test.entity.RefreshToken;
import com.Test.entity.UserInfo;
import com.Test.repository.UserInfoRepository;

import jakarta.annotation.PostConstruct;

@Service
public class ProductService {

    List<Product> productList = null;

    @Autowired
    private UserInfoRepository repository;
    @Autowired
    JwtService jwtService;
    @Autowired
    RefreshTokenService refreshTokenService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    public void loadProductsFromDB() {
        productList = new ArrayList<>();
        
        for (int i = 1; i <= 100; i++) {
            Product product = new Product();
            product.setProductId(i);
            product.setName("product " + i);
            product.setQty(new Random().nextInt(10));
            product.setPrice(new Random().nextInt(5000));
            
            productList.add(product);
        }
    }



    public List<Product> getProducts() {
        return productList;
    }

    public Product getProduct(int id) {
        return productList.stream()
                .filter(product -> product.getProductId() == id)
                .findAny()
                .orElseThrow(() -> new RuntimeException("product " + id + " not found"));
    }


    public JwtResponse addUser(UserInfo userInfo) {
        userInfo.setPassword(passwordEncoder.encode(userInfo.getPassword()));
        repository.save(userInfo);
        
        //when create a new account it will create automatically access token and also refresh token at a time
        String accessToken = jwtService.generateToken(userInfo.getName());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userInfo.getName());
        JwtResponse jwtResponse=new JwtResponse();
        jwtResponse.setAccessToken(accessToken);
        jwtResponse.setToken(refreshToken.getToken());
        
        
        return jwtResponse;
    }
}
