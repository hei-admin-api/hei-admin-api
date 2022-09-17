create extension if not exists "uuid-ossp";

create table if not exists "event" (
    id                varchar
    constraint event_pk primary key                 default uuid_generate_v4(),
    name               varchar                not null,
    place_id             varchar                   not null,
    start_date          timestamp with time zone not null,
    end_date         timestamp with time zone         not null,
   );