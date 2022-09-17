do
$$
    begin
        if not exists(select from pg_type where typname = 'status') then
            create type status as enum ('EXPECTED', 'HERE', 'MISSING');
        end if;
    end
$$;

create extension if not exists "uuid-ossp";

create table if not exists "event_participant" (
    id                varchar
    constraint event_participant_pk primary key                 default uuid_generate_v4(),
    user_id              varchar                  not null,
    status               status                not null,
    event_id            varchar                 not null
   );