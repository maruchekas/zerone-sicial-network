ALTER TABLE notification_type RENAME TO user_notification_settings;

ALTER TYPE notification_name RENAME TO notification_type;

ALTER TABLE notifications ADD COLUMN type notification_type;
ALTER TABLE notifications ADD COLUMN entity_id BIGINT;

INSERT INTO user_notification_settings (person_id, post, post_comment, comment_comment, friends_request, message, friends_birthday)
VALUES (31, true, true, true, true, true, true);

INSERT INTO persons (first_name, last_name, reg_date, birth_date, email, phone, password, photo, about, town, country,
                     confirmation_code, is_approved, messages_permission, user_type, last_online_time, is_blocked)
VALUES ('Sergey', 'Sergeev', '2021-07-30 23:30:03', '1995-08-15', 'mwittey1@ovh.net', '+7 (917) 836-9756', '$2a$12$xdO/.HMmiJV1SUTRAhSgAeYTPZaSxi0mwi0tnoeug2mQmCu1bghue',
        'https://robohash.org/velsaepeea.png?size=50x50&set=set1',
        'Для того, чтобы служить публике, надо иметь смелость и быть готовым ей не нравиться.', 'Курск', 'Россия',
        '1899', 1, 'FRIENDS', 'USER', '2021-12-16 16:57:08', 0);

INSERT INTO posts (time, author_id, title, post_text, block_id, is_blocked)
VALUES ('2021-12-12 11:43:24', 31, 'что такое текст-рыба',
        'Веб-разработчик знает, что такое текст-рыба веб-разработчик. Все же лучше использовать в xvi веке. Возникнуть небольшие проблемы: в различных. Демонстрации внешнего вида контента, просмотра шрифтов, абзацев, отступов. Сегодня существует несколько вариантов lorem считается, что впервые. Книгопечатник вырвал отдельные фразы и смысловую нагрузку ему нести совсем необязательно кириллице. Цицерону, ведь именно из его трактата о пределах добра. Напрашивается вывод, что все же лучше использовать в длине наиболее распространенных слов. К обитателям водоемов контент – написание символов. Варианты текста сыграет на сайтах и смысловую нагрузку ему нести совсем.',
        null, 0);

INSERT INTO post_comments (time, post_id, parent_id, author_id, comment_text, block_id, is_blocked)
VALUES ('2021-12-15 05:42:32', 31, null, 31, 'tincidunt nulla mollis molestie lorem quisque ut erat curabitur gravida', null, 0);

INSERT INTO post_comments (time, post_id, parent_id, author_id, comment_text, block_id, is_blocked)
VALUES ('2021-12-15 05:42:32', 31, 27, 31, 'tincidunt nulla mollis molestie lorem quisque ut erat curabitur gravida', null, 0);

INSERT INTO friendship_statuses (time, name, code)
VALUES (NOW(), 'REQUEST', 10);
INSERT INTO friendship (status_id, src_person_id, dst_person_id)
VALUES (19, 1, 31);

INSERT INTO messages (time, author_id, recipient_id, message_text, read_status)
VALUES (NOW(), 1, 31,'integer ac neque duis bibendum morbi non quam nec dui luctus rutrum nulla tellus in sagittis dui vel nisl duis', 'SENT');

INSERT INTO notifications (sent_time, contact, person_id, type, entity_id)
        VALUES (NOW(), 'Contact', 31, 'POST_COMMENT', 31),
               (NOW(), 'Contact', 31, 'COMMENT_COMMENT', 27),
               (NOW(), 'Contact', 31, 'FRIEND_REQUEST', 19),
               (NOW(), 'Contact', 31, 'MESSAGE', 43);
