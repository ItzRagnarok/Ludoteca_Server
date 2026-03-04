--select 1 from dual;

INSERT INTO category(name) VALUES ('Eurogames');
INSERT INTO category(name) VALUES ('Ameritrash');
INSERT INTO category(name) VALUES ('Familiar');


INSERT INTO author(name, nationality) VALUES ('Alan R. Moon', 'US');
INSERT INTO author(name, nationality) VALUES ('Vital Lacerda', 'PT');
INSERT INTO author(name, nationality) VALUES ('Simone Luciani', 'IT');
INSERT INTO author(name, nationality) VALUES ('Perepau Llistosella', 'ES');
INSERT INTO author(name, nationality) VALUES ('Michael Kiesling', 'DE');
INSERT INTO author(name, nationality) VALUES ('Phil Walker-Harding', 'US');

INSERT INTO game(title, age, category_id, author_id) VALUES ('On Mars', '14', 1, 2);
INSERT INTO game(title, age, category_id, author_id) VALUES ('Aventureros al tren', '8', 3, 1);
INSERT INTO game(title, age, category_id, author_id) VALUES ('1920: Wall Street', '12', 1, 4);
INSERT INTO game(title, age, category_id, author_id) VALUES ('Barrage', '14', 1, 3);
INSERT INTO game(title, age, category_id, author_id) VALUES ('Los viajes de Marco Polo', '12', 1, 3);
INSERT INTO game(title, age, category_id, author_id) VALUES ('Azul', '8', 3, 5);


INSERT INTO client(name) VALUES ('Pedro');
INSERT INTO client(name) VALUES ('Pablo');
INSERT INTO client(name) VALUES ('Juan');
INSERT INTO client(name) VALUES ('Alba');


INSERT INTO loan(game_id, client_id, loan_date, return_date) VALUES (1, 1, '2026-03-01', '2026-03-10');
INSERT INTO loan(game_id, client_id, loan_date, return_date) VALUES (2, 2, '2026-03-12', '2026-03-22');
INSERT INTO loan(game_id, client_id, loan_date, return_date) VALUES (6, 1, '2026-04-01', '2026-04-05');
INSERT INTO loan(game_id, client_id, loan_date, return_date) VALUES (5, 4, '2026-04-10', '2026-04-20');