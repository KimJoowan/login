-- FUNCTION: public.fn_manage_account_lock()

-- DROP FUNCTION IF EXISTS public.fn_manage_account_lock();

CREATE OR REPLACE FUNCTION public.fn_manage_account_lock()
    RETURNS trigger
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE NOT LEAKPROOF
AS $BODY$

BEGIN
    -- 1. 회원가입 시 (INSERT): 초기 계정 잠금 정보 등록
    IF (TG_OP = 'INSERT') THEN
        INSERT INTO public.account_lock ("number", fail_count, locked_until, active)
        VALUES (NEW."number", 0, NULL, true);
        RETURN NEW;

    -- 2. 회원정보 수정 시 (UPDATE): 회원 탈퇴 처리 시 계정 비활성화
    ELSIF (TG_OP = 'UPDATE') THEN
        IF (OLD.role != 'WITHDRAWN' AND NEW.role = 'WITHDRAWN') THEN
            UPDATE public.account_lock
            SET active = false -- true에서 false로 수정
            WHERE "number" = NEW."number";
        END IF;
        RETURN NEW;
    END IF;

    RETURN NULL;
END;
$BODY$;

ALTER FUNCTION public.fn_manage_account_lock()
    OWNER TO postgres;
