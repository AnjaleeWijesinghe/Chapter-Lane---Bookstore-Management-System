// Order Management Module - U.L.G.Maduwantha - IT19010618
package com.bookstore.management.dto;

import com.bookstore.management.model.OrderStatus;
import jakarta.validation.constraints.NotNull;

public class OrderStatusForm {

    @NotNull(message = "Status is required")
    private OrderStatus status;

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }
}
