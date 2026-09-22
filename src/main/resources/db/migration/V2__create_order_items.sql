CREATE TABLE order_items (
     id UUID PRIMARY KEY,
     order_id UUID NOT NULL,
     product_id UUID NOT NULL,
     quantity INTEGER NOT NULL,
     unit_price NUMERIC(19, 2) NOT NULL,

     CONSTRAINT fk_order_items_order
         FOREIGN KEY (order_id)
             REFERENCES orders(id)
);

CREATE INDEX idx_order_items_order_id
    ON order_items(order_id);