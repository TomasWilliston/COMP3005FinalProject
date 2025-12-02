CREATE FUNCTION public.new_pt_session()
    RETURNS trigger
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE NOT LEAKPROOF
AS $BODY$
begin
    INSERT INTO unassigned_sessions
    VALUES(NEW.t_ID, NEW.date, NEW.start_time);
    RETURN NEW;
end;
$BODY$;

CREATE OR REPLACE VIEW public.user_summary
AS
SELECT id, first_name, last_name, heart_rate AS latest_hr, weight AS latest_w FROM members t
    JOIN
    (SELECT h.m_id,h.heart_rate,h.weight,m."time"
     FROM ( SELECT health_logs.m_id,max(health_logs."time") AS "time"
            FROM health_logs
            GROUP BY health_logs.m_id) m
         JOIN health_logs h ON h.m_id = m.m_id AND m."time" = h."time"
) f ON t.id = f.m_id;

ALTER FUNCTION public.new_pt_session()
    OWNER TO postgres;

COMMENT ON FUNCTION public.new_pt_session()
    IS 'null';

CREATE OR REPLACE TRIGGER new_session
    AFTER INSERT
    ON public.training_sessions
    FOR EACH ROW
    EXECUTE FUNCTION public.new_pt_session();

CREATE INDEX training_ids ON training_sessions(t_id);

insert into members (first_name, last_name, email, password)
values ('Tomas', 'Williston', 'TW@example.com', 'password');

insert into members (first_name, last_name, email, password)
values ('Fake', 'Person', 'fake@email.com', 'password2');

insert into health_logs (m_id, heart_rate, weight)
values (1, 100, 80);

insert into trainers (first_name, last_name, password)
values ('John', 'Trainer', 'I<3training');

insert into trainers (first_name, last_name, password)
values ('Jane', 'Trainer', 'trainingTime');

insert into staff (first_name, last_name, password)
values ('Bob', 'Staff',  'admin');

insert into rooms values (1);

insert into rooms values (10);

insert into rooms values (12);