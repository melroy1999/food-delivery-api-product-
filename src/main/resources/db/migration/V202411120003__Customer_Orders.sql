CREATE TABLE orders (
    id BIGINT AUTO_INCREMENT,
    customer_id BIGINT NOT NULL,
    restaurant_id BIGINT NOT NULL,
    order_status VARCHAR(50) NOT NULL,
    order_details JSON NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    is_deleted BOOLEAN NOT NULL DEFAULT false,
    is_archived BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_customer_orders_customer_id` (`customer_id`),
    KEY `idx_customer_orders_restaurant_id` (`restaurant_id`),
    CONSTRAINT `fk_customer_orders_customer_id` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`id`),
    CONSTRAINT `fk_customer_orders_restaurant_id` FOREIGN KEY (`restaurant_id`) REFERENCES `restaurants` (`id`)
);