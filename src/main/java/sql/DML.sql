CREATE FUNCTION public.new_pt_session()
    RETURNS trigger
    LANGUAGE 'plpgsql'
    COST null
    VOLATILE NOT LEAKPROOF
AS $BODY$
begin
    INSERT INTO unassigned_sessions
    VALUES(NEW.t_ID, NEW.date, NEW.start_time);
    RETURN NEW;
end;
$BODY$;

ALTER FUNCTION public.new_pt_session()
    OWNER TO postgres;

COMMENT ON FUNCTION public.new_pt_session()
    IS 'null';

CREATE OR REPLACE TRIGGER new_session
    AFTER INSERT
    ON public.training_sessions
    FOR EACH ROW
    EXECUTE FUNCTION public.new_pt_session();