create table "user" (
    id                  serial primary key,
    first_name          varchar(128),
    last_name           varchar(128),
    email               varchar(128) unique,
    image_url           varchar(256)

);

create table Provider (
    id                  serial primary key,
    user_id             int not null,
    provider_id         varchar(128),
    provider            int default 0,

    constraint unique_user_per_provider unique (provider_id, provider),
    constraint fk_provider_user FOREIGN KEY(user_id) REFERENCES "user"(id)

);

create table Item (
    id                  serial primary key,
    user_id             int not null,
    name                varchar(128),
    current             int default 1,
    target              int default 1,

    constraint fk_item_user FOREIGN KEY(user_id) REFERENCES "user"(id)

);



insert into "user" (first_name, last_name, email, image_url) values ('John', 'Doe', 'test@test.no', 'http://www.test.no');
insert into "user" (first_name, last_name, email, image_url) values ('Jane', 'Doe', 'jane@test.no', 'http://www.test.no');
insert into Provider (user_id, provider_id, provider) values (1, '123', 0);
insert into Provider (user_id, provider_id, provider) values (1, '456', 1);
insert into Provider (user_id, provider_id, provider) values (2, '567', 1);

insert into Item (name, user_id, current, target) values ('Item 1', 1, 1, 1);
insert into Item (name, user_id, current, target) values ('Item 2', 1, 1, 1);
insert into Item (name, user_id, current, target) values ('Item 3', 1, 1, 1);
insert into Item (name, user_id, current, target) values ('Item 4', 1, 1, 1);
