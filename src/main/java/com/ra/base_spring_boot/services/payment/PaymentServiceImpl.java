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
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements IPaymentService {

    private final IPaymentRepository paymentRepository;
    private final BookingServiceClient bookingServiceClient;

    @Value("${vnpay.tmn-code}") private String tmnCode;
    @Value("${vnpay.hash-secret}") private String hashSecret;
    @Value("${vnpay.payment-url}") private String paymentUrl;
    @Value("${vnpay.return-url}") private String returnUrl;

    @Override
    public Page<PaymentResponse> findAllForAdmin(Pageable pageable) {
        return paymentRepository.findAll(pageable).map(PaymentResponse::new);
    }

    @Override
    public String createVnPayOrder(Long paymentId, BigDecimal amount, String description, HttpServletRequest request) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new HttpNotFound("Payment not found with id: " + paymentId));

        String vnp_TxnRef = String.valueOf(paymentId);
        long amountInVND = amount.multiply(new BigDecimal("100")).longValue();
        String vnp_IpAddr = VnPayConfig.getIpAddress(request);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());

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

        String hashData = VnPayConfig.hashAllFields(vnp_Params, hashSecret);
        vnp_Params.put("vnp_SecureHash", hashData);

        StringJoiner queryUrl = new StringJoiner("&");
        vnp_Params.forEach((key, value) -> {
            queryUrl.add(URLEncoder.encode(key, StandardCharsets.US_ASCII) + "=" + URLEncoder.encode(value, StandardCharsets.US_ASCII));
        });

        payment.setTransactionCode(vnp_TxnRef);
        paymentRepository.save(payment);

        return paymentUrl + "?" + queryUrl.toString();
    }

    @Override
    @Transactional
    public int handleVnPayIPN(Map<String, String> vnpayParams) {
        try {
            String vnp_TxnRef = vnpayParams.get("vnp_TxnRef");
            String vnp_ResponseCode = vnpayParams.get("vnp_ResponseCode");
            String vnp_SecureHash = vnpayParams.get("vnp_SecureHash");

            vnpayParams.remove("vnp_SecureHashType");
            vnpayParams.remove("vnp_SecureHash");

            String calculatedHash = VnPayConfig.hashAllFields(vnpayParams, hashSecret);
            if (!calculatedHash.equals(vnp_SecureHash)) {
                log.error("VNPAY IPN: Checksum failed!");
                return 2; // Invalid signature
            }

            Payment payment = paymentRepository.findByTransactionCode(vnp_TxnRef)
                    .orElse(null);
            if (payment == null) {
                log.error("VNPAY IPN: Order not found with id {}", vnp_TxnRef);
                return 1;
            }

            if (payment.getStatus() == PaymentStatus.COMPLETED || payment.getStatus() == PaymentStatus.FAILED) {
                log.warn("VNPAY IPN: Order already updated. Status: {}", payment.getStatus());
                return 0;
            }

            if ("00".equals(vnp_ResponseCode)) {
                payment.setStatus(PaymentStatus.COMPLETED);
                log.info("Updated Payment ID: {} to COMPLETED.", payment.getId());

                bookingServiceClient.finalizeTicketCreation(payment.getId());
            } else {
                payment.setStatus(PaymentStatus.FAILED);
                log.warn("Updated Payment ID: {} to FAILED. ResponseCode: {}", payment.getId(), vnp_ResponseCode);
            }

            paymentRepository.save(payment);
            return 0;
        } catch (Exception e) {
            log.error("Error handling VNPAY IPN: {}", e.getMessage());
            return 99;
        }
    }

}