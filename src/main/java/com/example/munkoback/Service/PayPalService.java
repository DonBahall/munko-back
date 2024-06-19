package com.example.munkoback.Service;

import com.example.munkoback.Model.User.Address;
import com.example.munkoback.Model.User.User;
import com.paypal.core.PayPalHttpClient;
import com.paypal.orders.*;
import com.paypal.http.HttpResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PayPalService {

    @Autowired
    private PayPalHttpClient payPalHttpClient;
    private final UserService service;

    public Order createOrder(Integer userId, String prise) throws IOException {
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.checkoutPaymentIntent("CAPTURE");

        User user = service.findById(userId);
        if (user != null) {
            String referenceId = UUID.randomUUID().toString();
            String customId = "CUST-" + UUID.randomUUID();
            Address userAddress = user.getAddress();

            ShippingDetail shippingDetail = new ShippingDetail()
                    .name(new Name().fullName(user.getFirstName() + " " + user.getLastName()))
                    .addressPortable(new AddressPortable()
                            .addressLine1(userAddress.getAddressLine1())
                            .addressLine2(userAddress.getAddressLine2())
                            .postalCode(userAddress.getPostalCode())
                            .adminArea1("CA")
                            .adminArea2("San Francisco")
                            .countryCode("US"));

            ApplicationContext applicationContext = new ApplicationContext()
                    .brandName("Funko Market")
                    .landingPage("BILLING")
                    .cancelUrl("https://munko-front.vercel.app")
                    .returnUrl("https://munko-front.vercel.app")
                    .userAction("PAY_NOW")
                    .shippingPreference("SET_PROVIDED_ADDRESS");
            orderRequest.applicationContext(applicationContext);

            List<PurchaseUnitRequest> purchaseUnitRequests = new ArrayList<>();
            PurchaseUnitRequest purchaseUnitRequest = new PurchaseUnitRequest()
                    .referenceId(referenceId)
                    .description("Paid for funko")
                    .customId(customId)
                    .softDescriptor("FunkoPurchase")
                    .amountWithBreakdown(new AmountWithBreakdown().currencyCode("USD").value(new BigDecimal(prise).toString()))
                    .shippingDetail(shippingDetail);
            purchaseUnitRequests.add(purchaseUnitRequest);
            orderRequest.purchaseUnits(purchaseUnitRequests);

            OrdersCreateRequest request = new OrdersCreateRequest().requestBody(orderRequest);

            HttpResponse<Order> response = payPalHttpClient.execute(request);
            return response.result();
        }
        return null;
    }
}
