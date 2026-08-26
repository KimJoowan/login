
-- Table: public.account_lock

-- DROP TABLE IF EXISTS public.account_lock;

CREATE TABLE IF NOT EXISTS public.account_lock
(
    "number" bigint NOT NULL,
    fail_count bigint NOT NULL DEFAULT 0,
    CONSTRAINT chk_account_lock_fail_count
    	CHECK (fail_count >= 0)
    
    active boolean NOT NULL DEFAULT true,
    locked_until timestamp without time zone,
    CONSTRAINT account_lock_pkey PRIMARY KEY ("number"),
    CONSTRAINT fk_account_lock_member FOREIGN KEY ("number")
        REFERENCES public.member ("number") MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE CASCADE
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.account_lock
    OWNER to postgres;