package com.example.munkoback.Service;

import com.example.munkoback.Model.InvalidArgumentsException;
import com.example.munkoback.Model.Order.Status;
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
    private final OrderService orderService;

    public Order createPayPalOrder(Integer userId, String prise) throws IOException {
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.checkoutPaymentIntent("CAPTURE");

        User user = service.findById(userId);
        if (user != null) {
            String referenceId = UUID.randomUUID().toString();
            String customId = "CUST-" + user.getEmail() + "-" + UUID.randomUUID();
            Address userAddress = user.getAddress();

            if (userAddress == null ||
                    userAddress.getDistrict() == null ||
                    userAddress.getCity() == null ||
                    userAddress.getPostalCode() == null ||
                    userAddress.getStreet() == null ||
                    userAddress.getCountryCode() == null) {
                throw new InvalidArgumentsException("Wrong arguments!");
            }

            ShippingDetail shippingDetail = new ShippingDetail()
                    .name(new Name().fullName(user.getFirstName() + " " + user.getLastName()))
                    .addressPortable(new AddressPortable()
                            .addressLine1(userAddress.getDistrict())
                            .addressLine2(userAddress.getCity())
                            .postalCode(userAddress.getPostalCode())
                            .adminArea2(userAddress.getStreet())
                            .countryCode(userAddress.getCountryCode()));

            ApplicationContext applicationContext = new ApplicationContext()
                    .brandName("Funko Market")
                    .landingPage("BILLING")
                    .cancelUrl("https://munko-front.vercel.app/cart")
                    .returnUrl("https://munko-front.vercel.app/cart")
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

    public Order completePayPalOrder(String token, String payerId) throws IOException {
        OrdersCaptureRequest request = new OrdersCaptureRequest(token);

        OrdersGetRequest getRequest = new OrdersGetRequest(token);
        HttpResponse<Order> getResponse = payPalHttpClient.execute(getRequest);
        String customId = getResponse.result().purchaseUnits().get(0).customId();
        String userEmail = customId.split("-")[1];
        User authUser = service.getAutentificatedUser();
        if (authUser.getEmail().equals(userEmail)) {
            HttpResponse<Order> response = payPalHttpClient.execute(request);
            if(response.statusCode() == 201 && payerId != null){
                com.example.munkoback.Model.Order.Order order = orderService.findByStatus(authUser, Status.PENDING);
                if(order != null){
                    orderService.updateOrderStatus(order);
                }
            }
            return response.result();
        } else {
            throw new InvalidArgumentsException("Wrong arguments!");
        }
    }
}
