package ass.java6.ass.Config;

public class VNPayConfig {
    public static final String VNP_TMNCODE = "OCC0RW3C"; // Thay bằng vnp_TmnCode từ VNPay
    public static final String VNP_HASHSECRET = "UBXVHFIUDE9WF8JFBZAW9WSWF0IZ7B7T"; // Thay bằng vnp_HashSecret từ VNPay
    public static final String VNP_PAYURL = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html"; // URL Sandbox
    public static final String VNP_RETURNURL = "http://localhost:8080/api/vnpay/callback"; // Thay bằng URL
}
