package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.dto.ResponseWrapper;
import com.ra.base_spring_boot.dto.payment.PaymentResponse;
import com.ra.base_spring_boot.services.payment.IPaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentCallbackController {

    private final IPaymentService paymentService;

    @Value("${frontend.url:http://localhost:5173}")
    private String frontendUrl;

    @GetMapping("/vnpay-ipn")
    public ResponseEntity<String> handleVnPayIPN(@RequestParam Map<String, String> allParams) {
        log.info("⚡ Received VNPay IPN callback");
        log.debug("IPN Parameters: {}", allParams);

        int result = paymentService.handleVnPayIPN(allParams);

        String jsonResponse;
        switch (result) {
            case 0:
                jsonResponse = "{\"RspCode\":\"00\",\"Message\":\"Confirm Success\"}";
                log.info("✅ VNPay IPN: Success");
                break;
            case 1:
                jsonResponse = "{\"RspCode\":\"01\",\"Message\":\"Order not found\"}";
                log.warn("⚠️ VNPay IPN: Order not found");
                break;
            case 2:
                jsonResponse = "{\"RspCode\":\"97\",\"Message\":\"Invalid Checksum\"}";
                log.error("❌ VNPay IPN: Invalid checksum");
                break;
            default:
                jsonResponse = "{\"RspCode\":\"99\",\"Message\":\"Unknown error\"}";
                log.error("❌ VNPay IPN: Unknown error");
        }

        return ResponseEntity.ok()
                .header("Content-Type", "application/json")
                .body(jsonResponse);
    }

    @GetMapping("/vnpay-return")
    public ResponseEntity<Void> handleVnPayReturn(@RequestParam Map<String, String> allParams) {
        log.info("🔙 Received VNPay return callback");
        log.debug("Return Parameters: {}", allParams);

        try {
            PaymentResponse paymentResponse = paymentService.handleVnPayReturn(allParams);

            String vnp_ResponseCode = allParams.get("vnp_ResponseCode");
            String vnp_TxnRef = allParams.get("vnp_TxnRef");

            String redirectUrl;

            if ("00".equals(vnp_ResponseCode)) {
                // Payment success - redirect to success page
                redirectUrl = String.format("%s/payment/success?paymentId=%d&txnRef=%s",
                        frontendUrl, paymentResponse.getId(), vnp_TxnRef);
                log.info("✅ VNPay Return: Payment successful. Redirecting to: {}", redirectUrl);
            } else {
                // Payment failed - redirect to error page
                redirectUrl = String.format("%s/payment/error?code=%s&txnRef=%s&message=%s",
                        frontendUrl, vnp_ResponseCode, vnp_TxnRef,
                        getVnPayErrorMessage(vnp_ResponseCode));
                log.warn("⚠️ VNPay Return: Payment failed. Code: {}. Redirecting to: {}",
                        vnp_ResponseCode, redirectUrl);
            }

            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create(redirectUrl))
                    .build();

        } catch (Exception e) {
            log.error(" VNPay Return: Error processing return: {}", e.getMessage(), e);

            // Redirect to generic error page
            String errorUrl = String.format("%s/payment/error?message=%s",
                    frontendUrl, "Processing error");

            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create(errorUrl))
                    .build();
        }
    }

    @GetMapping("/{id}/status")
    public ResponseEntity<ResponseWrapper<PaymentResponse>> getPaymentStatus(@PathVariable Long id) {
        log.info("📊 Getting payment status for ID: {}", id);

        PaymentResponse payment = paymentService.getPaymentStatus(id);

        return ResponseEntity.ok(
                ResponseWrapper.<PaymentResponse>builder()
                        .status(HttpStatus.OK)
                        .data(payment)
                        .build()
        );
    }

    private String getVnPayErrorMessage(String responseCode) {
        Map<String, String> errorMessages = Map.ofEntries(
                Map.entry("07", "Trừ tiền thành công. Giao dịch bị nghi ngờ (liên quan tới lừa đảo, giao dịch bất thường)."),
                Map.entry("09", "Giao dịch không thành công do: Thẻ/Tài khoản của khách hàng chưa đăng ký dịch vụ InternetBanking tại ngân hàng."),
                Map.entry("10", "Giao dịch không thành công do: Khách hàng xác thực thông tin thẻ/tài khoản không đúng quá 3 lần"),
                Map.entry("11", "Giao dịch không thành công do: Đã hết hạn chờ thanh toán. Xin quý khách vui lòng thực hiện lại giao dịch."),
                Map.entry("12", "Giao dịch không thành công do: Thẻ/Tài khoản của khách hàng bị khóa."),
                Map.entry("13", "Giao dịch không thành công do Quý khách nhập sai mật khẩu xác thực giao dịch (OTP). Xin quý khách vui lòng thực hiện lại giao dịch."),
                Map.entry("24", "Giao dịch không thành công do: Khách hàng hủy giao dịch"),
                Map.entry("51", "Giao dịch không thành công do: Tài khoản của quý khách không đủ số dư để thực hiện giao dịch."),
                Map.entry("65", "Giao dịch không thành công do: Tài khoản của Quý khách đã vượt quá hạn mức giao dịch trong ngày."),
                Map.entry("75", "Ngân hàng thanh toán đang bảo trì."),
                Map.entry("79", "Giao dịch không thành công do: KH nhập sai mật khẩu thanh toán quá số lần quy định. Xin quý khách vui lòng thực hiện lại giao dịch"),
                Map.entry("99", "Các lỗi khác (lỗi còn lại, không có trong danh sách mã lỗi đã liệt kê)")
        );

        return errorMessages.getOrDefault(responseCode, "Giao dịch không thành công");
    }
}