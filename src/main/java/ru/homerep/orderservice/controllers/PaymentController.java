package ru.homerep.orderservice.controllers;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.tool.schema.spi.SqlScriptException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.homerep.orderservice.models.Category;
import ru.homerep.orderservice.models.PaymentType;
import ru.homerep.orderservice.models.dto.DefaultResponse;
import ru.homerep.orderservice.repositories.CategoryRepository;
import ru.homerep.orderservice.repositories.PaymentTypeRepository;
import ru.homerep.orderservice.services.PaymentService;


import java.util.Optional;
@Slf4j
@RestController
@RequestMapping("/payments")
public class PaymentController {
    private final PaymentTypeRepository paymentTypeRepository;
    private final PaymentService paymentService;
    public PaymentController(PaymentTypeRepository paymentTypeRepository, PaymentService paymentService) {
        this.paymentTypeRepository = paymentTypeRepository;
        this.paymentService = paymentService;
    }

    @GetMapping
    public ResponseEntity<Iterable<PaymentType>> getAllPayments() {
        return ResponseEntity.ok(paymentTypeRepository.findAll());
    }
    @PostMapping
    public ResponseEntity<DefaultResponse<PaymentType,String>> createPayment(@RequestBody PaymentType paymentType) {
        try{
            PaymentType payment = paymentTypeRepository.save(paymentType);
            return ResponseEntity.ok(new DefaultResponse<>(payment, "Success"));
        } catch (DataIntegrityViolationException e){
            return ResponseEntity.badRequest()
                    .body(new DefaultResponse<>(paymentType, "Error: " + e.getMessage()));
        }
    }
    @PatchMapping("/activate")
    public ResponseEntity<DefaultResponse<PaymentType,String>> activatePayment(@RequestParam String paymentName) {
        try{
            PaymentType type = paymentService.activatePayment(paymentName);
            log.info("Payment {} activated", type.getName());
            return ResponseEntity.ok(new DefaultResponse<>(type, "Success"));
        }
        catch (RuntimeException e){
            return ResponseEntity.badRequest()
                    .body(new DefaultResponse<>(null, "Error: " + e.getMessage()));
        }
    }
    @PatchMapping("/deactivate")
    public ResponseEntity<DefaultResponse<PaymentType,String>> deactivatePayment(@RequestParam String paymentName) {
        try{
            PaymentType type = paymentService.deactivatePayment(paymentName);
            return ResponseEntity.ok(new DefaultResponse<>(type, "Success"));
        }
        catch (RuntimeException e){
            return ResponseEntity.badRequest()
                    .body(new DefaultResponse<>(null, "Error: " + e.getMessage()));
        }
    }

    @DeleteMapping()
    public ResponseEntity<DefaultResponse<PaymentType,String>> deletePayment(@RequestParam String paymentTypeName) {
        try {
            PaymentType toDel = paymentTypeRepository.findByName(paymentTypeName).orElseThrow(() -> new RuntimeException("Payment type does not exist"));
            paymentTypeRepository.delete(toDel);
            return ResponseEntity.ok(new DefaultResponse<>(toDel, "Success"));
        } catch (RuntimeException e){
            return ResponseEntity.badRequest()
                    .body(new DefaultResponse<>(null, "Error: " + e.getMessage()));
        }
    }


}