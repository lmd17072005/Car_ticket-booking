package com.ra.base_spring_boot.services.payment;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ra.base_spring_boot.client.BookingServiceClient;
import com.ra.base_spring_boot.dto.payment.ZaloPayCallbackRequest;
import com.ra.base_spring_boot.dto.payment.CreateOrderRequest;
import com.ra.base_spring_boot.dto.payment.PaymentResponse;
import com.ra.base_spring_boot.dto.payment.ZaloPayCreateOrderResponse;
import com.ra.base_spring_boot.exception.HttpBadRequest;
import com.ra.base_spring_boot.exception.HttpNotFound;
import com.ra.base_spring_boot.model.constants.PaymentStatus;
import com.ra.base_spring_boot.model.payment.Payment;
import com.ra.base_spring_boot.repository.payment.IPaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import java.util.HexFormat;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements IPaymentService {

    private final IPaymentRepository paymentRepository;
    private final BookingServiceClient bookingServiceClient;
    private final RestClient restClient = RestClient.create();
    private final ObjectMapper objectMapper = new ObjectMapper();


    @Value("${zalopay.app-id}")
    private String appId;
    @Value("${zalopay.key1}")
    private String key1;
    @Value("${zalopay.create-order-url}")
    private String createOrderUrl;
    @Value("${zalopay.callback-url}")
    private String callbackUrl;
    @Value("${zalopay.key2}")
    private String key2;

    @Override
    @Transactional
    public boolean handleZaloPayCallback(String jsonStr) {
        try {
            ZaloPayCallbackRequest callbackRequest = objectMapper.readValue(jsonStr, ZaloPayCallbackRequest.class);
            String mac = hmacSha256(key2, callbackRequest.getData());
            if (!mac.equals(callbackRequest.getMac())) {
                log.error("ZaloPay Callback: MAC authentication failed!");
                return false;
            }

            Map<String, Object> data = objectMapper.readValue(callbackRequest.getData(), new TypeReference<Map<String, Object>>() {});
            String appTransId = (String) data.get("app_trans_id");
            log.info("ZaloPay Callback received for app_trans_id: {}", appTransId);

            Payment payment = paymentRepository.findByTransactionCode(appTransId)
                    .orElseThrow(() -> new HttpNotFound("Payment not found with transaction code: " + appTransId));

            if (payment.getStatus() == PaymentStatus.PENDING) {
                payment.setStatus(PaymentStatus.COMPLETED);
                paymentRepository.save(payment);
                log.info("Updated Payment ID: {} to COMPLETED.", payment.getId());

                try {
                    bookingServiceClient.finalizeTicketCreation(payment.getId());
                    log.info("Successfully called finalizeTicketCreation for Booking Service.");
                } catch (Exception e) {
                    log.error("Error calling finalizeTicketCreation for paymentId {}: {}", payment.getId(), e.getMessage());
                }
            } else {
                log.warn("Payment ID {} already has status {}, skipping callback processing.", payment.getId(), payment.getStatus());
            }
            return true;
        } catch (Exception e) {
            log.error("Error handling ZaloPay callback: {}", e.getMessage(), e);
            return false;
        }
    }



    @Override
    public String createZaloPayOrder(CreateOrderRequest request) {
        Payment payment = paymentRepository.findById(request.getPaymentId())
                .orElseThrow(() -> new HttpNotFound("Payment not found with id: " + request.getPaymentId()));

        try {
            long appTime = System.currentTimeMillis();
            String appTransId = new SimpleDateFormat("yyMMdd").format(new Date()) + "_" +
                    UUID.randomUUID().toString().substring(0,8);
            String appUser = payment.getUser().getId().toString();
            String embedData = "{}";
            String item = "[]";

            String data = appId + "|" + appTransId + "|" + appUser + "|" + request.getAmount().longValue()
                    + "|" + appTime + "|" + embedData + "|" + item;

            String mac = hmacSha256(key1, data);

            MultiValueMap<String, String> orderParams = new LinkedMultiValueMap<>();
            orderParams.add("app_id", appId);
            orderParams.add("app_user", appUser);
            orderParams.add("app_trans_id", appTransId);
            orderParams.add("app_time", String.valueOf(appTime));
            orderParams.add("amount", String.valueOf(request.getAmount().longValue()));
            orderParams.add("description", request.getDescription());
            orderParams.add("bank_code", "");
            orderParams.add("item", item);
            orderParams.add("embed_data", embedData);
            orderParams.add("callback_url", callbackUrl);
            orderParams.add("mac", mac);

            ZaloPayCreateOrderResponse zaloPayResponse = restClient.post()
                    .uri(createOrderUrl)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(orderParams)
                    .retrieve()
                    .body(ZaloPayCreateOrderResponse.class);

            if (zaloPayResponse == null || zaloPayResponse.getReturnCode() != 1) {
                throw new HttpBadRequest("Failed to create ZaloPay order" +
                        (zaloPayResponse != null ? zaloPayResponse.getReturnMessage() : "Response is null"));
            }

            payment.setTransactionCode(appTransId);
            paymentRepository.save(payment);

            return zaloPayResponse.getOrderUrl();
        } catch (Exception e) {
            log.error("Exception creating zaloPay order {}", e.getMessage());
            throw new RuntimeException("Exception creating zaloPay order", e);
        }
    }



    private String hmacSha256(String key, String data) {
        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(key.getBytes("UTF-8"), "HmacSHA256");
            sha256_HMAC.init(secret_key);
            byte [] hash = sha256_HMAC.doFinal(data.getBytes("UTF-8"));
            return HexFormat.of().formatHex(hash);
        } catch (Exception e) {
            throw new  IllegalStateException("Failed to generate HMAC_SHA256 signature", e);
        }
    }

    @Override
    public Page<PaymentResponse> findAllForAdmin(Pageable pageable) {
        return paymentRepository.findAll(pageable).map(PaymentResponse::new);
    }
}