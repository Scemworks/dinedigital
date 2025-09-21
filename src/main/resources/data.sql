INSERT INTO menu(name, description, price, image) VALUES
('Pan-Seared Scallops','Delicately seared, served with a saffron risotto and a hint of citrus foam.',32.00,'https://images.unsplash.com/photo-1548943487-a2e4e43b4853?auto=format&fit=crop&w=800&q=80'),
('Prime Wagyu Filet','A5 Wagyu, grilled to perfection, accompanied by truffle mashed potatoes and asparagus.',75.00,'https://images.unsplash.com/photo-1598511757337-de2a7db69a88?auto=format&fit=crop&w=800&q=80'),
('Lobster Thermidor','A classic preparation with a creamy brandy sauce, baked in the shell.',58.00,'https://images.unsplash.com/photo-1621996346565-e326e20f547c?auto=format&fit=crop&w=800&q=80');

-- Cleanup job (dev): remove reservations older than today
DELETE FROM reservations WHERE date < CURRENT_DATE;

-- Seed default admin (username: admin / password: admin123) for dev profile
-- Seed default kitchen user (username: kitchen / password: kitchen123) for dev profile
INSERT INTO users(username, password_hash, role)
VALUES ('admin', '$2a$12$JHmd7moCS40VjD6iwepP/eD5n4w5d9YLWERw4uzah8Ws.gJJHTgfy', 'ADMIN'),
       ('kitchen', '$2a$12$v7KWpfxbg38fIOyO70Z12.25FA8q0qKHWhglvGcbO2XIQ3ZPjObUu', 'KITCHEN');

