
insert into user_tb(username, password, email, created_at) values('ssar', '1234', 'ssar@nate.com', now());
insert into user_tb(username, password, email, created_at) values('cos', '1234', 'cos@nate.com', now());


insert into board_tb(title, content, user_id, created_at) values('java란', 'OOP인 뭘까?', 1, now());
insert into board_tb(title, content, user_id, created_at) values('Spring과 spring boot란', '프레임워크란?', 1, now());
insert into board_tb(title, content, user_id, created_at) values('SSR', 'WAS에 대한 개념', 1, now());
insert into board_tb(title, content, user_id, created_at) values('제목4', '내용4', 2, now());


insert into reply_tb(comment, board_id, user_id, created_at) values('댓글1', 1, 1, now());
insert into reply_tb(comment, board_id, user_id, created_at) values('댓글2', 4, 1, now());
insert into reply_tb(comment, board_id, user_id, created_at) values('댓글3', 4, 1, now());
insert into reply_tb(comment, board_id, user_id, created_at) values('댓글4', 4, 2, now());