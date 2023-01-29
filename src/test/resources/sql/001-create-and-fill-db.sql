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

create table if not exists snippet
(
    id serial,
    page_id int4,
    lemmas character varying[],
    words character varying[],
    primary key (id)
);

insert into field (name, selector, weight) values
    ('title','title', 1.0),
    ('body','body', 0.8);
	
insert into site (name, url, last_error, status_time, status) values ('TestSite', 'https://test.ru/', 'none', '2023-01-28', 'INDEXED');

insert into page (path, code, content, site_id) values ('/', '200', '<html></html>', '1'), ('/test', '200', '<html></html>', '1');

insert into lemma (lemma, frequency, site_id) values 
	('ехать', '2', '1'), 
	('грека', '2', '1'), 
	('через', '2', '1'), 
	('река', '2', '1'), 
	('видеть', '2', '1'), 
	('река', '2', '1'), 
	('рак', '1', '1'),
	('сунуть', '1', '1'),
	('рука', '1', '1'),
	('цап', '1', '1');

insert into index_rank (page_id, lemma_id, rank) values 
	('1', '1', '5'),
	('1', '2', '5'),
	('1', '3', '5'),
	('1', '4', '5'),
	('1', '5', '5'),
	('1', '6', '5'),
	('1', '7', '5'),
	('1', '8', '5'),
	('1', '9', '5'),
	('1', '10', '5'),
	('2', '1', '2');
	
insert into snippet (page_id, lemmas, words) values
	('1', '{Ехать, грека, через, река, видеть, грека, в, река, рак, за, рука, грека, цап}', '{Ехал, Грека, через, реку, видит, Грека, в, реке, рак, за, руку, Греку, цап}'),
	('2', '{Ехать, грека, через, река, видеть, грека, в, река}', '{Ехал, Грека, через, реку, видит, Грека, в, реке}');