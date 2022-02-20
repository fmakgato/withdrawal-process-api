create sequence if not exists investor_sequence start with 1 increment by 1;
create sequence if not exists product_sequence start with 1 increment by 1;
create sequence if not exists withdrawal_sequence start with 1 increment by 1;
create table if not exists investor (investor_id bigint not null, address varchar(255) not null, date_of_birth date not null, email_address varchar(255) not null, first_name varchar(255) not null, mobile_number varchar(255) not null, last_name varchar(255) not null, primary key (investor_id));
create table if not exists product (product_id bigint not null, current_balance double not null, product_name varchar(255) not null, product_type TEXT not null, primary key (product_id));
create table if not exists withdrawal (withdrawal_id bigint not null, status varchar(255) not null, withdrawal_amount double not null, investor_id bigint, product_id bigint, primary key (withdrawal_id));
alter table investor add constraint if not exists investor_email_address unique (email_address);
alter table withdrawal add constraint if not exists FKrw9skegmoluax62ekb88qy3kh foreign key (investor_id) references investor;
alter table withdrawal add constraint if not exists FK7m21ddd9p8849di9u73l1rt36 foreign key (product_id) references product;

insert into investor (investor_id, address, date_of_birth, email_address, mobile_number, first_name, last_name)
values (1, '123 Fake Street, Polokwane, 0700', parsedatetime('1921-01-01', 'yyyy-MM-dd'), 'sfmakgato@gmail.com', '0680067725', 'France', 'Mkhonto');
insert into investor (investor_id, address, date_of_birth, email_address, mobile_number, first_name, last_name)
values (2, '123 Mokomene Ga-Ramokgopa 0811', parsedatetime('2019-01-01', 'yyyy-MM-dd'), 'makgatosf@gmail.com', '0680067725', 'Sethuwane', 'Makgato');

insert into product (product_id, current_balance, product_name, product_type)
values (1, 500000.00, 'RetirementProduct', 'RETIREMENT');
insert into product (product_id, current_balance, product_name, product_type)
values (2, 36000.00, 'SavingsProduct', 'SAVINGS');
