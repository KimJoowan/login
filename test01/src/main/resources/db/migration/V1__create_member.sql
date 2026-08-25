-- Table: public.member
CREATE TABLE IF NOT EXISTS public.member
(
    "number" bigint NOT NULL GENERATED ALWAYS AS IDENTITY,
    "userName" character varying(50) COLLATE pg_catalog."default" NOT NULL,
    email character varying(100) COLLATE pg_catalog."default" UNIQUE  NOT NULL,
    created_at timestamp with time zone DEFAULT now(),
    password character varying(255) COLLATE pg_catalog."default" NOT NULL,
    id character varying(50) COLLATE pg_catalog."default" NOT NULL,
    role character varying(20) COLLATE pg_catalog."default" DEFAULT 'USER'::character varying,
    CONSTRAINT member_pkey PRIMARY KEY ("number"),
    CONSTRAINT uk_member_id UNIQUE (id)
) 
TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.member
    OWNER to postgres;