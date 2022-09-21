create type status_type as enum ('INDEXING', 'INDEXED', 'FAILED');

create table if not exists field (
    id serial,
    name varchar(255),
    selector varchar(255),
    weight float,
    primary key (id)
);

create table if not exists index_rank (
    id serial,
    page_id int4,
    lemma_id int4,
    rank float,
    primary key (id)
);

create table if not exists lemma (
    id serial,
    lemma varchar(255),
    frequency int4,
    site_id int4,
    primary key (id)
);

create table if not exists page (
    id serial,
    path varchar(255),
    code int4,
    content varchar,
    site_id int4,
    primary key (id)
);

create table if not exists site (
    id serial,
    status status_type,
    status_time timestamp without time zone,
    last_error varchar(255),
    url varchar(255),
    name varchar(255),
    primary key (id)
);

insert into field (name, selector, weight) values
    ('title','title', 1.0),
    ('body','body', 0.8);