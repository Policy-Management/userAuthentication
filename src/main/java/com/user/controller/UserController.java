package com.user.controller;

import com.user.entity.PolicyEntity;
import com.user.entity.PolicyProxy;
import com.user.entity.UserEntity;
import com.user.repository.UserRepository;
import com.user.security.CustomUserDetailsService;
import com.user.security.JwtResponse;
import com.user.security.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@Slf4j
public class UserController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private CustomUserDetailsService customUserDetailsService;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private PolicyProxy policyProxy;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/register/policy")
    public ResponseEntity<?> savePolicy(@RequestBody PolicyEntity policyEntity){
        log.info("creating policy");
        try {
            return new ResponseEntity<>(policyProxy.savePolicy(policyEntity),HttpStatus.CREATED);
        }catch (Exception e){
            log.error("policy not saved");
            return new ResponseEntity<>("Policy not created " , HttpStatus.BAD_REQUEST);
        }

    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable String id)throws Exception{
        ResponseEntity<?> response = new ResponseEntity<>("sample",HttpStatus.OK);
        try{
            log.info("deleting policy");
            response = policyProxy.delete(id);
            log.info((String) response.getBody());
            return new ResponseEntity<>(response,HttpStatus.ACCEPTED);
        }catch (Exception e){
            e.printStackTrace();
            log.info((String) response.getBody());
            log.error("policy not deleted");
            return new ResponseEntity<>("Invalid id or id not exist " , HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable String id,@RequestBody PolicyEntity policyEntity)throws Exception{
        try{
            log.info("updating policy");
            return policyProxy.update(id,policyEntity);
        }catch (Exception e){
            log.error("policy not updated");
            return new ResponseEntity<>("Invalid id or id not exist " , HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> get(@PathVariable String id) throws Exception{
        try{
            log.info("reading policy by id");
            return new ResponseEntity<>(policyProxy.getPolicy(id),HttpStatus.FOUND);
        }catch (Exception e){
            log.error("policy does not exist");
            return new ResponseEntity<>("Invalid id or id not exist " , HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/getall")
    public ResponseEntity<?> getall(){
        try{
            log.info("reading policy");
            return policyProxy.getPolicy();
        }catch (Exception e){
            log.error("policy does not exist");
            return new ResponseEntity<>("Invalid id or id not exist " , HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserEntity userEntity){
        log.info("registering user");
        try {
            userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));
            userEntity.setRoles("ROLE_USER");
            return new ResponseEntity<>(userRepository.save(userEntity), HttpStatus.CREATED);
        } catch (Exception e) {
            log.info("user not registered");
            return new ResponseEntity<>("error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserEntity userEntity) throws Exception {
        log.info("login user");
        String username = userEntity.getUsername();
        String password = userEntity.getPassword();
        try{
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username,password));
            log.debug("successfully logged in with userName: {}", username);
        }catch(Exception e){
            log.info("user not logged in");
            return new ResponseEntity<>("Bad Credentials " + username, HttpStatus.UNAUTHORIZED);
        }
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(userEntity.getUsername());
        JwtResponse jwtResponse = new JwtResponse();
        jwtResponse.setToken(jwtUtil.generateToken(userDetails));
        jwtResponse.setRole(userRepository.findById(userEntity.getUsername()).get().getRoles());
        return new ResponseEntity<>(jwtResponse,HttpStatus.OK);

    }

}