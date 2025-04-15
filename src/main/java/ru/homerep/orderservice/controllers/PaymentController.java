package ru.homerep.orderservice.controllers;

import org.hibernate.tool.schema.spi.SqlScriptException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.homerep.orderservice.models.Category;
import ru.homerep.orderservice.models.PaymentType;
import ru.homerep.orderservice.models.dto.DefaultResponse;
import ru.homerep.orderservice.repositories.CategoryRepository;
import ru.homerep.orderservice.repositories.PaymentTypeRepository;


import java.util.Optional;

@RestController
@RequestMapping("/payments")
public class PaymentController {
    private final PaymentTypeRepository paymentTypeRepository;

    public PaymentController(PaymentTypeRepository paymentTypeRepository) {
        this.paymentTypeRepository = paymentTypeRepository;
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
            PaymentType paymentType = paymentTypeRepository.findByName(paymentName).orElseThrow(() -> new RuntimeException("Error while finding payment type with name: " + paymentName));
            paymentType.setIsActive(true);
            paymentTypeRepository.save(paymentType);
            return ResponseEntity.ok(new DefaultResponse<>(paymentType, "Success"));
        }
        catch (RuntimeException e){
            return ResponseEntity.badRequest()
                    .body(new DefaultResponse<>(null, "Error: " + e.getMessage()));
        }
    }
    @PatchMapping("/deactivate")
    public ResponseEntity<DefaultResponse<PaymentType,String>> deactivatePayment(@RequestParam String paymentName) {
        try{
            PaymentType paymentType = paymentTypeRepository.findByName(paymentName).orElseThrow(() -> new RuntimeException("Error while finding payment type with name: " + paymentName));
            paymentType.setIsActive(false);
            //обновить состояние в бд
            paymentTypeRepository.save(paymentType);
            return ResponseEntity.ok(new DefaultResponse<>(paymentType, "Success"));
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