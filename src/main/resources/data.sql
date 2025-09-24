INSERT INTO menu(name, description, price, image) VALUES
('Chicken Biriyani','Aromatic basmati rice layered with spiced chicken, fried onions, and herbs.',220.00,'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSUkFl4-e90gihmLEM7TZZ-LTbwkWQ47VaLMQ&s'),
('Chicken Mandi','Smoky, slow-cooked chicken over fragrant mandi rice with house spice blend.',450.00,'https://d1uz88p17r663j.cloudfront.net/original/10c05681081b47950fe2f90e73968e7d_Chicken-Mandi.jpg'),
('Alfaham Mandi','Char-grilled alfaham chicken served on mandi rice with garlic dip.',480.00,'https://media-assets.swiggy.com/swiggy/image/upload/f_auto,q_auto,fl_lossy/xmgalxauqajgfp50dsyx'),
('Porotta and Beef','Flaky Kerala porotta served with spicy beef roast (ularthiyathu).',180.00,'https://b.zmtcdn.com/data/dish_photos/ca9/d33aba09def42da8cf1d34edd0051ca9.jpg'),
('Porotta and Chicken Curry','Kerala porotta with homestyle coconut-based chicken curry.',160.00,'https://media-assets.swiggy.com/swiggy/image/upload/f_auto,q_auto,fl_lossy/is92qt9hzytksqiratll'),
('Ghee Rice and Chicken Curry','Fragrant ghee rice paired with creamy chicken curry.',190.00,'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRh09YPNgoWFBh7fFokbCCQnHaw-jXHs9ulgw&s'),
('Kerala Parotta (2 pcs)','Layered, flaky flatbread. Perfect with curries. (2 pieces)',60.00,'https://www.whiskaffair.com/wp-content/uploads/2020/04/Kerala-Parotta-3.jpg'),
('Malabar Fish Curry','Tangy and spicy fish curry simmered in kokum and coconut gravy.',240.00,'https://www.nestleprofessional.in/sites/default/files/2022-07/Malabar-Fish-Curry.jpg'),
('Appam and Vegetable Stew','Soft appams served with lightly spiced coconut milk vegetable stew.',160.00,'https://blogger.googleusercontent.com/img/b/R29vZ2xl/AVvXsEgRjWMXQNDdzxQYhPF__uIidHNeAVI8zJzD60GmhzCQMK2eOUp_IymMSuJvrkmalOuScxOrrVW7piqTXO1B-2gi6riFQFGWcuTfN6g7pWONaDPqVXa6MCAo9L3PEj4HzAhw4cxLzqzhbsgT/s1600/Kerala+vegetable+stew+recipe....jpg'),
('Kerala Beef Fry','Peppery, dry-roasted beef with coconut slivers and curry leaves.',150.00,'https://i.pinimg.com/736x/15/76/cc/1576cc178d1bd79f7caa005d2b838436.jpg');

-- Cleanup job (dev): remove reservations older than today
DELETE FROM reservations WHERE date < CURRENT_DATE;

-- Seed default admin (username: admin / password: admin123) for dev profile
-- Seed default kitchen user (username: kitchen / password: kitchen123) for dev profile
INSERT INTO users(username, password_hash, role)
VALUES ('admin', '$2a$12$JHmd7moCS40VjD6iwepP/eD5n4w5d9YLWERw4uzah8Ws.gJJHTgfy', 'ADMIN'),
       ('kitchen', '$2a$12$v7KWpfxbg38fIOyO70Z12.25FA8q0qKHWhglvGcbO2XIQ3ZPjObUu', 'KITCHEN');