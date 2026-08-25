-- V4__create_account_lock_trigger.sql

CREATE OR REPLACE TRIGGER trg_member_account_lock
    AFTER INSERT OR UPDATE 
    ON public.member
    FOR EACH ROW
    EXECUTE FUNCTION public.fn_manage_account_lock();