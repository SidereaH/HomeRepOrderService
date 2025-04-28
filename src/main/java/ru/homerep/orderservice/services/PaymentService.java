package ru.homerep.orderservice.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.homerep.orderservice.models.PaymentType;
import ru.homerep.orderservice.repositories.PaymentTypeRepository;
@Service
@Transactional
public class PaymentService {
    private final PaymentTypeRepository paymentTypeRepository;

    public PaymentService(PaymentTypeRepository paymentTypeRepository) {
        this.paymentTypeRepository = paymentTypeRepository;
    }

    @Transactional
    public PaymentType activatePayment(String paymentName){
        PaymentType paymentType = paymentTypeRepository.findByName(paymentName).orElseThrow(() -> new RuntimeException("Error while finding payment type with name: " + paymentName));
        paymentType.setIsActive(true);
        paymentTypeRepository.saveAndFlush(paymentType);
        return paymentType;
    }
    @Transactional
    public PaymentType deactivatePayment(String paymentName){
        PaymentType paymentType = paymentTypeRepository.findByName(paymentName).orElseThrow(() -> new RuntimeException("Error while finding payment type with name: " + paymentName));
        paymentType.setIsActive(false);
        paymentTypeRepository.saveAndFlush(paymentType);
        return paymentType;
    }
}
