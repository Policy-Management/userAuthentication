package com.user.entity;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "policy",url = "https://djrjhdm4vg.execute-api.ap-south-1.amazonaws.com/Prod")
public interface PolicyProxy {
    @PostMapping("/save")
    public ResponseEntity<?> savePolicy(@RequestBody PolicyEntity policy);
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable(name = "id") String id);
    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable(name="id") String id,@RequestBody PolicyEntity policyEntity);
    @GetMapping("/get/{id}")
    public ResponseEntity<?> getPolicy(@PathVariable String id);
    @GetMapping("/getall")
    public ResponseEntity<?> getPolicy();
}