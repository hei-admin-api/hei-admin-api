create table if not exists "place"
(
    id                varchar
        constraint place_pk primary key,
    name              varchar                  not null,
        constraint place_ref_unique unique,
    address           varchar
);
