create table if not exists gruppendto
(
    id      serial primary key,
    erstellername varchar(300) not null,
    beschreibung varchar(255) not null,
    isgeschlossen boolean,
    mitglieder text ARRAY not null,
    version integer default null
);

create table if not exists ausgabendto
(
    id   serial primary key,
    ersteller varchar(300),
    saldo   decimal,
    verwendungszweck varchar(300),
    teilhaber   text ARRAY not null,
    gruppenid integer references gruppendto (id)
);