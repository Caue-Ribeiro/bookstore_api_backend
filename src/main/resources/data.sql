INSERT INTO bs_book(id, title, isbn, release_date, stock, price, description, cover_image_url) VALUES(gen_random_uuid(), 'Harry Potter and the Socerer''s Stone', '978-0590353403', to_date('31/10/1998','DD/MM/YYYY'),100,20,'Turning the envelope over, his hand trembling, Harry saw a purple wax seal bearing a coat of arms; a lion, an eagle, a badger and a snake surrounding a large letter ''H''.Harry Potter has never even heard of Hogwarts when the letters start dropping on the doormat at number four, Privet Drive. Addressed in green ink on yellowish parchment with a purple seal, they are swiftly confiscated by his grisly aunt and uncle. Then, on Harry''s eleventh birthday, a great beetle-eyed giant of a man called Rubeus Hagrid bursts in with some astonishing news: Harry Potter is a wizard, and he has a place at Hogwarts School of Witchcraft and Wizardry. An incredible adventure is about to begin!', 'https://prodimage.images-bn.com/pimages/9781546148500_p0_v4_s600x595.jpg') ON CONFLICT (id) DO NOTHING;

INSERT INTO bs_book(id, title, isbn, release_date, stock, price, description, cover_image_url) VALUES(gen_random_uuid(), 'The Fellowship of the Ring', '978-0544003415', to_date('29/07/1954','DD/MM/YYYY'), 50, 25.50, 'The first part of J.R.R. Tolkien''s epic masterpiece The Lord of the Rings. A young hobbit is entrusted with a quest to destroy the One Ring.','https://m.media-amazon.com/images/I/61mn09OvTQL._AC_UF1000,1000_QL80_.jpg') ON CONFLICT (id) DO NOTHING;

INSERT INTO bs_book(id, title, isbn, release_date, stock, price, description, cover_image_url) VALUES(gen_random_uuid(), 'A Game of Thrones', '978-0553103540', to_date('01/08/1996','DD/MM/YYYY'), 75, 22.00, 'Summers span decades. Winter can last a lifetime. And the struggle for the Iron Throne has begun. It will stretch from the south, where heat breeds plots, to the vast and frozen north.','https://m.media-amazon.com/images/I/71Jzezm8CBL.jpg') ON CONFLICT (id) DO NOTHING;

INSERT INTO bs_book(id, title, isbn, release_date, stock, price, description, cover_image_url) VALUES(gen_random_uuid(), 'The Shining', '978-0385121675', to_date('28/01/1977','DD/MM/YYYY'), 60, 18.99, 'Jack Torrance''s new job at the Overlook Hotel is the perfect chance for a fresh start. But as the harsh winter weather sets in, the idyllic location feels ever more remote... and more sinister.','https://m.media-amazon.com/images/I/81zqohMOk-L._AC_UF1000,1000_QL80_.jpg') ON CONFLICT (id) DO NOTHING;

INSERT INTO bs_book(id, title, isbn, release_date, stock, price, description, cover_image_url) VALUES(gen_random_uuid(), 'Pride and Prejudice', '978-0141439518', to_date('28/01/1813','DD/MM/YYYY'), 120, 12.50, 'A classic romance novel that charts the emotional development of the protagonist, Elizabeth Bennet, who learns the error of making hasty judgments.','https://m.media-amazon.com/images/I/712P0p5cXIL._AC_UF1000,1000_QL80_.jpg') ON CONFLICT (id) DO NOTHING;

INSERT INTO bs_book(id, title, isbn, release_date, stock, price, description, cover_image_url) VALUES(gen_random_uuid(), 'Foundation', '978-0553293357', to_date('01/05/1951','DD/MM/YYYY'), 45, 15.00, 'For twelve thousand years the Galactic Empire has ruled supreme. Now it is dying. Only Hari Seldon, creator of the revolutionary science of psychohistory, can see into the future.','https://m.media-amazon.com/images/I/81J4LjdqQFL._AC_UF1000,1000_QL80_.jpg') ON CONFLICT (id) DO NOTHING;

INSERT INTO bs_book(id, title, isbn, release_date, stock, price, description, cover_image_url) VALUES(gen_random_uuid(), 'Murder on the Orient Express', '978-0062073501', to_date('01/01/1934','DD/MM/YYYY'), 80, 14.99, 'Just after midnight, the famous Orient Express is stopped in its tracks by a snowdrift. By morning, the millionaire Samuel Edward Ratchett lies dead in his compartment, stabbed a dozen times.','https://m.media-amazon.com/images/I/61rRmiz9HvL._AC_UF1000,1000_QL80_.jpg') ON CONFLICT (id) DO NOTHING;

INSERT INTO bs_book(id, title, isbn, release_date, stock, price, description, cover_image_url) VALUES(gen_random_uuid(), 'Sapiens: A Brief History of Humankind', '978-0062316097', to_date('04/09/2011','DD/MM/YYYY'), 150, 24.99, 'Dr. Yuval Noah Harari breaks the mold with this highly original book that begins about 70,000 years ago with the appearance of modern cognition.','https://m.media-amazon.com/images/I/716E6dQ4BXL._AC_UF1000,1000_QL80_.jpg') ON CONFLICT (id) DO NOTHING;

INSERT INTO bs_book(id, title, isbn, release_date, stock, price, description, cover_image_url) VALUES(gen_random_uuid(), 'Thus Spoke Zarathustra', '978-0140441185', to_date('01/01/1883','DD/MM/YYYY'), 30, 16.00, 'A philosophical novel that deals with ideas such as the ''eternal recurrence of the same'', the parable on the ''death of God'', and the ''prophecy'' of the Übermensch.','https://m.media-amazon.com/images/I/613ZVoVVeXL.jpg') ON CONFLICT (id) DO NOTHING;

INSERT INTO bs_book(id, title, isbn, release_date, stock, price, description, cover_image_url) VALUES(gen_random_uuid(), 'The Hound of the Baskervilles', '978-0451528018', to_date('01/04/1902','DD/MM/YYYY'), 90, 10.99, 'Sherlock Holmes and Dr. Watson investigate the legend of a supernatural hound, a beast that may be stalking a young heir on the fog-shrouded moorland.','https://m.media-amazon.com/images/I/711gFGlF2iL.jpg') ON CONFLICT (id) DO NOTHING;



INSERT INTO bs_category(id, type) VALUES(1, 'FANTASY') ON CONFLICT (id) DO NOTHING;
INSERT INTO bs_category(id, type) VALUES(2, 'ROMANCE') ON CONFLICT (id) DO NOTHING;
INSERT INTO bs_category(id, type) VALUES(3, 'HORROR') ON CONFLICT (id) DO NOTHING;
INSERT INTO bs_category(id, type) VALUES(4, 'ADVENTURE') ON CONFLICT (id) DO NOTHING;
INSERT INTO bs_category(id, type) VALUES(5, 'HISTORY') ON CONFLICT (id) DO NOTHING;
INSERT INTO bs_category(id, type) VALUES(6, 'SUSPENSE') ON CONFLICT (id) DO NOTHING;
INSERT INTO bs_category(id, type) VALUES(7, 'PHILOSOPHY') ON CONFLICT (id) DO NOTHING;
INSERT INTO bs_category(id, type) VALUES(8, 'DIDACTIC') ON CONFLICT (id) DO NOTHING;
INSERT INTO bs_category(id, type) VALUES(9, 'SCIFI') ON CONFLICT (id) DO NOTHING;
INSERT INTO bs_category(id, type) VALUES(10, 'MYSTERY') ON CONFLICT (id) DO NOTHING;

INSERT INTO bs_author(id, name, last_name) VALUES(1, 'J.K', 'Rowling') ON CONFLICT (id) DO NOTHING;
INSERT INTO bs_author(id, name, last_name) VALUES(2, 'J.R.R.', 'Tolkien') ON CONFLICT (id) DO NOTHING;
INSERT INTO bs_author(id, name, last_name) VALUES(3, 'George R.R.', 'Martin') ON CONFLICT (id) DO NOTHING;
INSERT INTO bs_author(id, name, last_name) VALUES(4, 'Stephen', 'King') ON CONFLICT (id) DO NOTHING;
INSERT INTO bs_author(id, name, last_name) VALUES(5, 'Jane', 'Austen') ON CONFLICT (id) DO NOTHING;
INSERT INTO bs_author(id, name, last_name) VALUES(6, 'Isaac', 'Asimov') ON CONFLICT (id) DO NOTHING;
INSERT INTO bs_author(id, name, last_name) VALUES(7, 'Agatha', 'Christie') ON CONFLICT (id) DO NOTHING;
INSERT INTO bs_author(id, name, last_name) VALUES(8, 'Yuval Noah', 'Harari') ON CONFLICT (id) DO NOTHING;
INSERT INTO bs_author(id, name, last_name) VALUES(9, 'Friedrich', 'Nietzsche') ON CONFLICT (id) DO NOTHING;
INSERT INTO bs_author(id, name, last_name) VALUES(10, 'Arthur Conan', 'Doyle') ON CONFLICT (id) DO NOTHING;


INSERT INTO bs_book_author(book_id, author_id) VALUES((SELECT id FROM bs_book WHERE title = 'Harry Potter and the Socerer''s Stone'), 1);
INSERT INTO bs_book_author(book_id, author_id) VALUES((SELECT id FROM bs_book WHERE title = 'The Fellowship of the Ring'), 2);
INSERT INTO bs_book_author(book_id, author_id) VALUES((SELECT id FROM bs_book WHERE title = 'A Game of Thrones'), 3);
INSERT INTO bs_book_author(book_id, author_id) VALUES((SELECT id FROM bs_book WHERE title = 'The Shining'), 4);
INSERT INTO bs_book_author(book_id, author_id) VALUES((SELECT id FROM bs_book WHERE title = 'Pride and Prejudice'), 5);
INSERT INTO bs_book_author(book_id, author_id) VALUES((SELECT id FROM bs_book WHERE title = 'Foundation'), 6);
INSERT INTO bs_book_author(book_id, author_id) VALUES((SELECT id FROM bs_book WHERE title = 'Murder on the Orient Express'), 7);
INSERT INTO bs_book_author(book_id, author_id) VALUES((SELECT id FROM bs_book WHERE title = 'Sapiens: A Brief History of Humankind'), 8);
INSERT INTO bs_book_author(book_id, author_id) VALUES((SELECT id FROM bs_book WHERE title = 'Thus Spoke Zarathustra'), 9);
INSERT INTO bs_book_author(book_id, author_id) VALUES((SELECT id FROM bs_book WHERE title = 'The Hound of the Baskervilles'), 10);


INSERT INTO bs_book_category(book_id,category_id) VALUES((SELECT id FROM bs_book WHERE title = 'Harry Potter and the Socerer''s Stone'), 1);
INSERT INTO bs_book_category(book_id,category_id) VALUES((SELECT id FROM bs_book WHERE title = 'Harry Potter and the Socerer''s Stone'), 4);
INSERT INTO bs_book_category(book_id,category_id)VALUES((SELECT id FROM bs_book WHERE title = 'The Fellowship of the Ring'), 1);
INSERT INTO bs_book_category(book_id,category_id) VALUES((SELECT id FROM bs_book WHERE title = 'A Game of Thrones'), 1);
INSERT INTO bs_book_category(book_id,category_id) VALUES((SELECT id FROM bs_book WHERE title = 'The Shining'), 3);
INSERT INTO bs_book_category(book_id,category_id) VALUES((SELECT id FROM bs_book WHERE title = 'Pride and Prejudice'), 7);
INSERT INTO bs_book_category(book_id,category_id) VALUES((SELECT id FROM bs_book WHERE title = 'Foundation'), 9);
INSERT INTO bs_book_category(book_id,category_id) VALUES((SELECT id FROM bs_book WHERE title = 'Murder on the Orient Express'),3);
INSERT INTO bs_book_category(book_id,category_id) VALUES((SELECT id FROM bs_book WHERE title = 'Sapiens: A Brief History of Humankind'), 5);
INSERT INTO bs_book_category(book_id,category_id) VALUES((SELECT id FROM bs_book WHERE title = 'Thus Spoke Zarathustra'), 7);
INSERT INTO bs_book_category(book_id,category_id) VALUES((SELECT id FROM bs_book WHERE title = 'The Hound of the Baskervilles'),10);

COMMIT;