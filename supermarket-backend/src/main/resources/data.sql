INSERT INTO employee (username, password, role, name) VALUES ('cashier1', '123456', 'CASHIER', '收银员张三');
INSERT INTO employee (username, password, role, name) VALUES ('purchaser1', '123456', 'PURCHASER', '采购员李四');
INSERT INTO employee (username, password, role, name) VALUES ('stock1', '123456', 'STOCK_KEEPER', '库存员王五');
INSERT INTO employee (username, password, role, name) VALUES ('manager1', '123456', 'MANAGER', '经理赵六');
INSERT INTO category (name) VALUES ('食品'), ('饮料'), ('日用品');
INSERT INTO supplier (name, contact_person, phone, address) VALUES ('统一供应商', '刘先生', '13800001111', '北京市朝阳区');
INSERT INTO product (name, category_id, price, stock, min_stock, barcode, supplier_id) VALUES ('康师傅方便面', 1, 4.50, 50, 10, '6901234567890', 1);
INSERT INTO product (name, category_id, price, stock, min_stock, barcode, supplier_id) VALUES ('可口可乐', 2, 3.00, 30, 10, '6901234567891', 1);
INSERT INTO product (name, category_id, price, stock, min_stock, barcode, supplier_id) VALUES ('心相印纸巾', 3, 5.00, 5, 10, '6901234567892', 1);