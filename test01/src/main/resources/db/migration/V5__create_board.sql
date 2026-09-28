-- Table: public.board

-- DROP TABLE IF EXISTS public.board;

CREATE TABLE IF NOT EXISTS public.board
(
    bcode bigint NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 9223372036854775807 CACHE 1 ),
    "idNumber" bigint NOT NULL,
    title character varying(200) COLLATE pg_catalog."default" NOT NULL,
    content text COLLATE pg_catalog."default" NOT NULL,
    "createdAt" timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT board_pkey PRIMARY KEY (bcode),
    CONSTRAINT board_member_fk FOREIGN KEY ("idNumber")
        REFERENCES public.member ("number") MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.board
    OWNER to postgres;