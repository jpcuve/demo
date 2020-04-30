insert into banks(id, name, opening, closing, settlement_completion_target, minimum_pay_in) values
(1, 'TEST-01', '00:15', '23:00', '10:00', '{}');
insert into currency_groups(id, name, priority) values
(1, 'ASIA', 1),
(2, 'EUROPE', 2),
(3, 'AMERICA', 3);
insert into currencies(id, bank_id, coin, currency_group_id, opening, funding_completion_target, closing, close) values
(1, 1, 'EUR', 2, '07:00', '09:00', '17:30', '18:00'),
(2, 1, 'GBP', 2, '06:00', '08:00', '14:30', '15:00'),
(3, 1, 'USD', 3, '06:30', '08:30', '21:00', '21:30'),
(4, 1, 'JPY', 1, '03:00', '05:00', '13:30', '13:00');
insert into accounts(id, bank_id, name, short_position_limit) values
(1, 1, '__MIRROR__', ''),
(2, 1, 'a', ''),
(3, 1, 'b', ''),
(4, 1, 'c', '');
insert into users(id, account_id, email, name, pass) values
(1, 1, 'jpcuvelliez@gmail.com', 'Jean-Pierre Cuvelliez', '$2a$10$w26AJvSlOdQZjKRNo9WNSeqJ1EthOge97bzwrIzodv2ooLBaT32xa');
insert into instructions(id, bank_id, moment, instruction_type, principal, counterparty, reference, amount) values
(1, 1, '08:00', 'PAY', 'a', 'b', 'TEST', '{}'),
(2, 1, '09:30', 'SETTLEMENT', 'b', 'a', 'Settlement 1', '{EUR=70,USD=90}'),
(3, 1, '09:30', 'SETTLEMENT', 'a', 'b', 'Settlement 2', '{EUR=80,USD=100}'),
(4, 1, '09:30', 'PAY_IN', '__MIRROR__', 'a', 'Pay-in 1', '{EUR=100,USD=200}');
