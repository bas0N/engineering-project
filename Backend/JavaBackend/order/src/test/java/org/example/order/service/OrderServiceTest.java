package org.example.order.service;

import jakarta.transaction.Transactional;
import org.example.order.dto.ListBasketItemDto;
import org.example.order.dto.request.DeliverRequest;
import org.example.order.dto.request.OrderRequest;
import org.example.order.dto.request.UpdateStatusRequest;
import org.example.order.dto.response.OrderResponse;
import org.example.order.entity.Deliver;
import org.example.order.entity.Order;
import org.example.order.entity.OrderItems;
import org.example.order.enums.Status;
import org.example.order.service.impl.OrderProccessingServiceImpl;
import org.example.order.repository.DeliverRepository;
import org.example.order.repository.ItemRepository;
import org.example.order.repository.OrderRepository;
import org.example.order.service.impl.BasketServiceImpl;
import org.example.order.service.impl.DeliverServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest(properties = {
        "spring.kafka.bootstrap-servers=",
        "spring.kafka.listener.auto-startup=false"
})
@ActiveProfiles("test")
@Transactional
public class OrderServiceTest {
    @MockBean
    private org.example.order.service.impl.OrderServiceImpl orderService;
    @Autowired
    private OrderProccessingServiceImpl orderProccessingServiceImpl;
    @Autowired
    private DeliverServiceImpl deliverService;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private DeliverRepository deliverRepository;
    @MockBean
    private BasketServiceImpl basketService;
    @MockBean(name = "basketItemsKafkaTemplate")
    private KafkaTemplate<String, String> basketItemsKafkaTemplate;

    @MockBean(name = "basketRemoveKafkaTemplate")
    private KafkaTemplate<String, String> basketRemoveKafkaTemplate;

    @MockBean(name = "userKafkaTemplate")
    private KafkaTemplate<String, String> userKafkaTemplate;


    @MockBean
    private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    private String userId;
    private String basketId;

    @BeforeEach
    void setUp() {
        userId = "user-123";
        basketId = "basket-123";
    }

    @Test
    void createOrder_Success() {
        // Arrange
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setBasketId(basketId);
        orderRequest.setDeliverId("deliver-123");

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        request.addHeader("userId", userId);

        OrderResponse mockOrderResponse = new OrderResponse();
        mockOrderResponse.setUuid("order-123");
        mockOrderResponse.setClientSecret("client-secret-123");

        when(basketService.getBasket(basketId)).thenReturn(new ListBasketItemDto());
        when(orderService.createOrder(orderRequest, request, response)).thenReturn(mockOrderResponse);
        when(orderService.createStripePayment(mockOrderResponse)).thenReturn("client-secret-123");

        // Act
        ResponseEntity<?> responseEntity = orderProccessingServiceImpl.createOrder(orderRequest, request, response);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(200, responseEntity.getStatusCodeValue());
        OrderResponse responseBody = (OrderResponse) responseEntity.getBody();
        assertNotNull(responseBody);
        assertEquals("order-123", responseBody.getUuid());
        assertEquals("client-secret-123", responseBody.getClientSecret());
    }

    @Test
    void createOrder_Failure_ExceptionCheck() {
        // Arrange
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setBasketId(null); // Invalid case: Missing basketId
        orderRequest.setDeliverId("deliver-123");

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.addHeader("userId", ""); // Invalid case: Empty userId

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> {
            orderProccessingServiceImpl.createOrder(orderRequest, request, response);
        });

        assertNotNull(exception);
    }

    @Test
    void updateStatus_Failure_ExceptionCheck() {
        // Arrange
        UpdateStatusRequest updateStatusRequest = new UpdateStatusRequest();
        updateStatusRequest.setOrderId("non-existent-order");
        updateStatusRequest.setStatus(Status.COMPLETED);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("userId", userId);

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> {
            orderProccessingServiceImpl.updateStatus(updateStatusRequest, request);
        });


        assertNotNull(exception);
    }



    @Test
    void getOrderById_Success() {
        // Arrange
        String orderUuid = "order-123";
        Order mockOrder = new Order();
        mockOrder.setUuid(orderUuid);
        mockOrder.setClient("user-123");
        mockOrder.setId(1L);

        OrderItems mockItem1 = new OrderItems();
        mockItem1.setPriceSummary(100.0);

        OrderItems mockItem2 = new OrderItems();
        mockItem2.setPriceSummary(150.0);

        List<OrderItems> itemsList = List.of(mockItem1, mockItem2);

        Order order = orderRepository.save(mockOrder);
        mockItem1.setOrder(order);
        mockItem2.setOrder(order);
        itemRepository.saveAll(itemsList);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("userId", userId);
        ResponseEntity<?> responseEntity = orderProccessingServiceImpl.getOrderById(orderUuid, request);

        // Act

        // Assert
        assertNotNull(responseEntity);
        assertEquals(200, responseEntity.getStatusCodeValue());
        OrderResponse responseBody = (OrderResponse) responseEntity.getBody();
        assertNotNull(responseBody);
        assertEquals(250.0, responseBody.getSummaryPrice());
    }

    @Test
    void getOrdersByClient_Success() {
        // Arrange
        Deliver mockDeliver = new Deliver();
        mockDeliver.setUuid("deliver-123");
        mockDeliver.setName("deliver1");
        mockDeliver.setPrice(10.0);
        deliverRepository.save(mockDeliver);

        Order mockOrder1 = new Order();
        mockOrder1.setClient("user-123");
        mockOrder1.setUuid("order-123");
        mockOrder1.setSummaryPrice(100.0);
        mockOrder1.setDeliver(mockDeliver);


        Order mockOrder2 = new Order();
        mockOrder2.setClient("user-123");
        mockOrder2.setUuid("order-456");
        mockOrder2.setSummaryPrice(150.0);
        mockOrder2.setDeliver(mockDeliver);

        orderRepository.save(mockOrder1);
        orderRepository.save(mockOrder2);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("userId", userId);

        // Act
        ResponseEntity<?> responseEntity = orderProccessingServiceImpl.getOrdersByClient(request);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(200, responseEntity.getStatusCodeValue());
        List<OrderResponse> responseBody = (List<OrderResponse>) responseEntity.getBody();
        assertNotNull(responseBody);
        assertEquals(2, responseBody.size());
    }

    @Test
    void updateStatus_Success() {
        // Arrange
        String orderUuid = "order-123";
        Order mockOrder = new Order();
        mockOrder.setUuid(orderUuid);
        mockOrder.setClient("user-123");
        mockOrder.setId(1L);
        orderRepository.save(mockOrder);

        UpdateStatusRequest updateStatusRequest = new UpdateStatusRequest();
        updateStatusRequest.setOrderId(orderUuid);
        updateStatusRequest.setStatus(Status.COMPLETED);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("userId", userId);

        // Act
        ResponseEntity<?> responseEntity = orderProccessingServiceImpl.updateStatus(updateStatusRequest, request);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(200, responseEntity.getStatusCodeValue());
    }

    @Test
    void getAllDeliver_Success(){
        // Arrange
        Deliver mockDeliver = new Deliver();
        mockDeliver.setUuid("deliver-123");
        mockDeliver.setName("deliver1");
        mockDeliver.setPrice(10.0);
        deliverRepository.save(mockDeliver);

        // Act
        ResponseEntity<?> responseEntity = deliverService.getAllDeliver();

        // Assert
        assertNotNull(responseEntity);
        assertEquals(200, responseEntity.getStatusCodeValue());
    }

    @Test
    void createDeliverOrder_Success() {
        // Arrange
        Deliver mockDeliver = new Deliver();
        mockDeliver.setUuid("deliver-123");
        mockDeliver.setName("deliver1");
        mockDeliver.setPrice(10.0);

        DeliverRequest deliverRequest = new DeliverRequest();
        deliverRequest.setName("deliver1");
        deliverRequest.setPrice(10.0);
        // Act
        ResponseEntity<?> responseEntity = deliverService.createDeliverOrder(deliverRequest);
        assertNotNull(responseEntity);
        assertEquals(200, responseEntity.getStatusCodeValue());
    }
}
