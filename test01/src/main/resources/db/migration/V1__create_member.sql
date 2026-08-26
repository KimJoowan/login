-- Table: public.member

-- DROP TABLE IF EXISTS public.member;

CREATE TABLE IF NOT EXISTS public.member
(
    "number" bigint NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 9223372036854775807 CACHE 1 ),
    "userName" character varying(50) COLLATE pg_catalog."default" NOT NULL,
    email character varying(100) COLLATE pg_catalog."default" NOT NULL,
    created_at timestamp with time zone DEFAULT now(),
    password character varying(255) COLLATE pg_catalog."default" NOT NULL,
    id character varying(50) COLLATE pg_catalog."default" NOT NULL,
    role character varying(20) COLLATE pg_catalog."default" DEFAULT 'USER'::character varying,
    CONSTRAINT member_pkey PRIMARY KEY ("number"),
    CONSTRAINT uk_member_id UNIQUE (id),
    CONSTRAINT chk_member_role CHECK (role::text = ANY (ARRAY['USER'::character varying, 'ADMIN'::character varying, 'WITHDRAWN'::character varying]::text[]))
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.member
    OWNER to postgres;
