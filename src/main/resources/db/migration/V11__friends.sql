INSERT INTO friendship_statuses (id, time, name, code)
VALUES (777, '2021-12-10 12:44:34', 'FRIEND', 10),
       (778, '2021-12-10 12:58:07', 'FRIEND', 11);

INSERT INTO friendship (id, status_id, src_person_id, dst_person_id)
VALUES (333, 777, 777, 666),
       (334, 778, 666, 777);