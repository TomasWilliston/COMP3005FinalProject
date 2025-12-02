CREATE TABLE public.members
(
    id serial,
    first_name text NOT NULL,
    last_name text NOT NULL,
    email text NOT NULL,
    password text NOT NULL,
    PRIMARY KEY (id),
    UNIQUE (email)
);

CREATE TABLE public.health_logs
(
    m_id integer NOT NULL,
    time timestamp(0) without time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    heart_rate integer,
    weight integer,
    PRIMARY KEY (m_id, time),
    FOREIGN KEY (m_id)
        REFERENCES public.members (id) MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE CASCADE
);

CREATE TABLE public.trainers
(
    id serial NOT NULL,
    first_name text NOT NULL,
    last_name text NOT NULL,
    password text NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE public.staff
(
    id serial NOT NULL,
    first_name text NOT NULL,
    last_name text NOT NULL,
    password text NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE public.rooms
(
    r_number integer NOT NULL,
    PRIMARY KEY (r_number),
    UNIQUE (r_number)
);

CREATE TABLE public.maintenance_logs
(
    id serial NOT NULL,
    details text NOT NULL,
    room_number integer,
    status text NOT NULL,
    s_id integer,
    PRIMARY KEY (id),
    FOREIGN KEY (s_id)
        REFERENCES public.staff (id) MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE NO ACTION,
    FOREIGN KEY (room_number)
        REFERENCES public.rooms (r_number) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

CREATE TABLE public.training_sessions
(
    t_id integer NOT NULL,
    date date NOT NULL,
    start_time time(0) without time zone NOT NULL,
    end_time time(0) without time zone NOT NULL,
    m_id integer,
    room integer,
    PRIMARY KEY (t_id, date, start_time),
    FOREIGN KEY (t_id)
        REFERENCES public.trainers (id) MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    FOREIGN KEY (m_id)
        REFERENCES public.members (id) MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE SET NULL,
    FOREIGN KEY (room)
        REFERENCES public.rooms (r_number) MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE SET NULL
);

CREATE TABLE public.unassigned_sessions
(
    t_id integer,
    date date,
    "time" time(0) without time zone,
    PRIMARY KEY (t_id, date, "time"),
    FOREIGN KEY (t_id, date, "time")
        REFERENCES public.training_sessions (t_id, date, start_time) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);