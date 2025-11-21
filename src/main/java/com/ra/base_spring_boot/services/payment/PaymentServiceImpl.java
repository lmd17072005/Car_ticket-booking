package com.ra.base_spring_boot.services.payment;

import com.ra.base_spring_boot.client.BookingServiceClient;
import com.ra.base_spring_boot.config.VnPayConfig;
import com.ra.base_spring_boot.dto.payment.PaymentResponse;
import com.ra.base_spring_boot.exception.HttpBadRequest;
import com.ra.base_spring_boot.exception.HttpNotFound;
import com.ra.base_spring_boot.model.constants.PaymentStatus;
import com.ra.base_spring_boot.model.payment.Payment;
import com.ra.base_spring_boot.repository.payment.IPaymentRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;


@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements IPaymentService {

    private final IPaymentRepository paymentRepository;
    private final BookingServiceClient bookingServiceClient;

    @Value("${vnpay.tmn-code}")
    private String tmnCode;

    @Value("${vnpay.hash-secret}")
    private String hashSecret;

    @Value("${vnpay.payment-url}")
    private String paymentUrl;

    @Value("${vnpay.return-url}")
    private String returnUrl;

    @Value("${vnpay.api-url:https://sandbox.vnpayment.vn/merchant_webapi/api/transaction}")
    private String apiUrl;

    @Override
    public Page<PaymentResponse> findAllForAdmin(Pageable pageable) {
        return paymentRepository.findAll(pageable).map(PaymentResponse::new);
    }

    @Override
    public PaymentResponse getPaymentStatus(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new HttpNotFound("Payment not found with id: " + paymentId));
        return new PaymentResponse(payment);
    }

    @Override
    @Transactional
    public String createVnPayOrder(Long paymentId, BigDecimal amount, String description, HttpServletRequest request) {
        log.info("Creating VNPay order for Payment ID: {}, Amount: {}", paymentId, amount);

        // Validate payment exists
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new HttpNotFound("Payment not found with id: " + paymentId));

        // Validate payment status
        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new HttpBadRequest("Payment is not in PENDING status. Current status: " + payment.getStatus());
        }

        // Generate transaction reference
        String vnp_TxnRef = String.valueOf(paymentId);

        // Convert amount to VND cents (VNPay requires amount in smallest unit)
        long amountInVND = amount.multiply(new BigDecimal("100")).longValue();

        // Get client IP
        String vnp_IpAddr = VnPayConfig.getIpAddress(request);

        // Generate timestamp
        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());

        // Expiration time (15 minutes from now)
        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());

        // Build VNPay parameters
        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", "2.1.0");
        vnp_Params.put("vnp_Command", "pay");
        vnp_Params.put("vnp_TmnCode", tmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amountInVND));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", description);
        vnp_Params.put("vnp_OrderType", "other");
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", returnUrl);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        // Generate secure hash
        String hashData = VnPayConfig.hashAllFields(vnp_Params, hashSecret);
        vnp_Params.put("vnp_SecureHash", hashData);

        // Build query string
        String queryUrl = VnPayConfig.buildQueryString(vnp_Params);

        // Update payment with transaction code
        payment.setTransactionCode(vnp_TxnRef);
        paymentRepository.save(payment);

        String fullPaymentUrl = paymentUrl + "?" + queryUrl;
        log.info("Generated VNPay payment URL for Payment ID: {}", paymentId);

        return fullPaymentUrl;
    }

    @Override
    @Transactional
    public int handleVnPayIPN(Map<String, String> vnpayParams) {
        log.info("Handling VNPay IPN callback");

        try {
            // Extract important parameters
            String vnp_TxnRef = vnpayParams.get("vnp_TxnRef");
            String vnp_ResponseCode = vnpayParams.get("vnp_ResponseCode");
            String vnp_TransactionNo = vnpayParams.get("vnp_TransactionNo");
            String vnp_Amount = vnpayParams.get("vnp_Amount");
            String vnp_BankCode = vnpayParams.get("vnp_BankCode");
            String vnp_PayDate = vnpayParams.get("vnp_PayDate");

            // Validate signature
            if (!VnPayConfig.validateSignature(vnpayParams, hashSecret)) {
                log.error("VNPay IPN: Invalid signature for TxnRef: {}", vnp_TxnRef);
                return 2; // Invalid signature
            }

            // Find payment by transaction code
            Payment payment = paymentRepository.findByTransactionCode(vnp_TxnRef)
                    .orElse(null);

            if (payment == null) {
                log.error("VNPay IPN: Payment not found for TxnRef: {}", vnp_TxnRef);
                return 1; // Order not found
            }

            // Check if payment already processed
            if (payment.getStatus() == PaymentStatus.COMPLETED ||
                    payment.getStatus() == PaymentStatus.FAILED) {
                log.warn("VNPay IPN: Payment already processed. ID: {}, Status: {}",
                        payment.getId(), payment.getStatus());
                return 0; // Already processed
            }

            // Process based on response code
            if ("00".equals(vnp_ResponseCode)) {
                // Payment successful
                payment.setStatus(PaymentStatus.COMPLETED);

                // Store additional VNPay transaction details
                Map<String, Object> transactionDetails = new HashMap<>();
                transactionDetails.put("vnp_TransactionNo", vnp_TransactionNo);
                transactionDetails.put("vnp_BankCode", vnp_BankCode);
                transactionDetails.put("vnp_PayDate", vnp_PayDate);
                transactionDetails.put("vnp_Amount", vnp_Amount);
                // You can store this in a JSON field or separate table

                log.info("VNPay IPN: Payment COMPLETED. ID: {}, TransactionNo: {}",
                        payment.getId(), vnp_TransactionNo);

                // Call booking service to finalize ticket creation
                try {
                    bookingServiceClient.finalizeTicketCreation(payment.getId());
                    log.info("Successfully called booking service for Payment ID: {}", payment.getId());
                } catch (Exception e) {
                    log.error("Failed to finalize ticket creation for Payment ID: {}. Error: {}",
                            payment.getId(), e.getMessage());
                    // Note: Payment is still marked as COMPLETED
                    // Manual intervention may be needed
                }

            } else {
                // Payment failed
                payment.setStatus(PaymentStatus.FAILED);
                log.warn("VNPay IPN: Payment FAILED. ID: {}, ResponseCode: {}",
                        payment.getId(), vnp_ResponseCode);
            }

            // Save payment
            paymentRepository.save(payment);

            return 0; // Success

        } catch (Exception e) {
            log.error("VNPay IPN: Unexpected error: {}", e.getMessage(), e);
            return 99; // Unknown error
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse handleVnPayReturn(Map<String, String> vnpayParams) {
        log.info("Handling VNPay return URL");

        // Validate signature
        if (!VnPayConfig.validateSignature(vnpayParams, hashSecret)) {
            log.error("VNPay Return: Invalid signature");
            throw new HttpBadRequest("Invalid payment signature");
        }

        // Extract parameters
        String vnp_TxnRef = vnpayParams.get("vnp_TxnRef");
        String vnp_ResponseCode = vnpayParams.get("vnp_ResponseCode");

        // Find payment
        Payment payment = paymentRepository.findByTransactionCode(vnp_TxnRef)
                .orElseThrow(() -> new HttpNotFound("Payment not found for transaction: " + vnp_TxnRef));

        log.info("VNPay Return: Payment ID: {}, Status: {}, ResponseCode: {}",
                payment.getId(), payment.getStatus(), vnp_ResponseCode);

        return new PaymentResponse(payment);
    }
}