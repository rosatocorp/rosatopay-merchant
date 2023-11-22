import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.MessageDigest;
import java.util.Random;

@Controller
public class PaymentController {

    private final String appId;
    private final String secretKey;

    public PaymentController(@Value("${app.id}") String appId,
                             @Value("${secret.key}") String secretKey) {
        this.appId = appId;
        this.secretKey = secretKey;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/pay")
    public String pay(Model model) {
        PaymentForm formObj = new PaymentForm();
        formObj.setAppId(appId);
        formObj.setOrderId(generateOrderId());
        formObj.setOrderAmount("10");
        formObj.setOrderCurrency("USD");
        formObj.setOrderNote("Add money to your wallet");
        formObj.setCustomerName("User");
        formObj.setCustomerPhone("(607) 836-4935");
        formObj.setCustomerEmail("john@doe.com");
        formObj.setReturnUrl(""); // PG returns to this URL after payment, Send Empty if not needed.
        formObj.setNotifyUrl(""); // PG sends a webhook to this URL after payment, Send Empty if not needed.
        formObj.setSignature(signatureRequest(formObj));

        model.addAttribute("form", formObj);
        return "pay";
    }

    private String generateOrderId() {
        String chars = "ABCDEFGHJKLMNOPQRSTUVWXYZ23456789";
        int stringLength = 6;
        StringBuilder randomString = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < stringLength; i++) {
            int rnum = random.nextInt(chars.length());
            randomString.append(chars.charAt(rnum));
        }

        return "order_" + randomString.toString();
    }

    private String signatureRequest(PaymentForm data) {
        String[] fields = new String[] {
                "appId", "orderId", "orderAmount", "orderCurrency", "orderNote",
                "customerName", "customerEmail", "customerPhone", "returnUrl", "notifyUrl"
        };

        StringBuilder signatureData = new StringBuilder();
        for (String field : fields) {
            try {
                String value = BeanUtils.getProperty(data, field);
                signatureData.append(value);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        String combinedData = secretKey + signatureData.toString();

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(combinedData.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
