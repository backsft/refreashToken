package com.Test.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Test.dto.AuthRequest;
import com.Test.dto.JwtResponse;
import com.Test.dto.Product;
import com.Test.dto.RefreshTokenRequest;
import com.Test.entity.RefreshToken;
import com.Test.entity.UserInfo;
import com.Test.service.JwtService;
import com.Test.service.ProductService;
import com.Test.service.RefreshTokenService;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService service;
    @Autowired
    private JwtService jwtService;
    
    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private AuthenticationManager authenticationManager;


    @PostMapping("/signUp")
    public JwtResponse addNewUser(@RequestBody UserInfo userInfo) {
        return service.addUser(userInfo);
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public List<Product> getAllTheProducts() {
        return service.getProducts();
    }
    
    @GetMapping("/onlyuser")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public String onlyuser() {
        return "onlyuser called";
    }
    

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public Product getProductById(@PathVariable int id) {
        return service.getProduct(id);
    }


    @PostMapping("/login")
    public JwtResponse authenticateAndGetToken(@RequestBody AuthRequest authRequest) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
        if (authentication.isAuthenticated()) {
        	//generate refresh token 
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(authRequest.getUsername());
            
            //generate access token            
            String accessToken = jwtService.generateToken(authRequest.getUsername());
            System.out.println("accessToken is "+accessToken);
            JwtResponse jwtResponse=new JwtResponse(accessToken, refreshToken.getToken());
            return jwtResponse;
            		
            		
        } else {
            throw new UsernameNotFoundException("invalid user request !");
        }
    }

    @PostMapping("/refreshToken")
    public JwtResponse refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
    	

    	
        return refreshTokenService.findByToken(refreshTokenRequest.getToken())
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUserInfo)
                .map(userInfo -> {
                    String accessToken = jwtService.generateToken(userInfo.getName());
                    
                    JwtResponse jwtResponse=new JwtResponse(accessToken, refreshTokenRequest.getToken());
                    return jwtResponse;
                    	
                    
                    	
                }).orElseThrow(() -> new RuntimeException(
                        "Refresh token is not in database!"));
    }


}
