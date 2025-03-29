package ass.java6.ass.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
public class MomoService {
    private static final String PARTNER_CODE = "MOMO";
    private static final String ACCESS_KEY = "F8BBA842ECF85";
    private static final String SECRET_KEY = "K951B6PE1waDMi640xX08PD3vg6EkVlz";
    private static final String REDIRECT_URL = "http://localhost:8080/thanhtoan/momo/return";
    private static final String IPN_URL = "http://localhost:8080/thanhtoan/momo/ipn";
    private static final String REQUEST_TYPE = "captureWallet";

    public static class PaymentResponse {
        private String payUrl;
        private String qrCodeUrl;
        private int resultCode;
        private String message;

        public PaymentResponse(String payUrl, String qrCodeUrl, int resultCode, String message) {
            this.payUrl = payUrl;
            this.qrCodeUrl = qrCodeUrl;
            this.resultCode = resultCode;
            this.message = message;
        }

        public String getPayUrl() { return payUrl; }
        public String getQrCodeUrl() { return qrCodeUrl; }
        public int getResultCode() { return resultCode; }
        public String getMessage() { return message; }
    }

    public PaymentResponse createPaymentRequest(String amount) {
        try {
            String requestId = PARTNER_CODE + System.currentTimeMillis();
            String orderId = requestId;
            String orderInfo = "Thanh toán đơn hàng";
            String extraData = "";

            String rawSignature = String.format(
                    "accessKey=%s&amount=%s&extraData=%s&ipnUrl=%s&orderId=%s&orderInfo=%s&partnerCode=%s&redirectUrl=%s&requestId=%s&requestType=%s",
                    ACCESS_KEY, amount, extraData, IPN_URL, orderId, orderInfo, PARTNER_CODE, REDIRECT_URL,
                    requestId, REQUEST_TYPE);
            String signature = signHmacSHA256(rawSignature, SECRET_KEY);

            JSONObject requestBody = new JSONObject();
            requestBody.put("partnerCode", PARTNER_CODE);
            requestBody.put("accessKey", ACCESS_KEY);
            requestBody.put("requestId", requestId);
            requestBody.put("amount", amount);
            requestBody.put("orderId", orderId);
            requestBody.put("orderInfo", orderInfo);
            requestBody.put("redirectUrl", REDIRECT_URL);
            requestBody.put("ipnUrl", IPN_URL);
            requestBody.put("extraData", extraData);
            requestBody.put("requestType", REQUEST_TYPE);
            requestBody.put("signature", signature);
            requestBody.put("lang", "vi");

            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost("https://test-payment.momo.vn/v2/gateway/api/create");
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setEntity(new StringEntity(requestBody.toString(), StandardCharsets.UTF_8));

            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(response.getEntity().getContent(), StandardCharsets.UTF_8));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                JSONObject jsonResponse = new JSONObject(result.toString());
                String payUrl = jsonResponse.getString("payUrl");
                String qrCodeUrl = jsonResponse.getString("qrCodeUrl");
                int resultCode = jsonResponse.getInt("resultCode");
                String message = jsonResponse.getString("message");

                return new PaymentResponse(payUrl, qrCodeUrl, resultCode, message);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new PaymentResponse(null, null, -1, "Failed to create payment request: " + e.getMessage());
        }
    }

    private static String signHmacSHA256(String data, String key) throws Exception {
        Mac hmacSHA256 = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        hmacSHA256.init(secretKey);
        byte[] hash = hmacSHA256.doFinal(data.getBytes(StandardCharsets.UTF_8));
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}