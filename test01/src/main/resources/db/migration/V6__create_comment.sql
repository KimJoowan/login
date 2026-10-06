-- Comments belong to a board post and an existing member.
CREATE TABLE public.comment
(
    ccode bigint GENERATED ALWAYS AS IDENTITY,
    bcode bigint NOT NULL,
    "idNumber" bigint NOT NULL,
    content text NOT NULL,
    "createdAt" timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updatedAt" timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT comment_pkey PRIMARY KEY (ccode),
    CONSTRAINT comment_board_fk FOREIGN KEY (bcode)
        REFERENCES public.board (bcode)
        ON DELETE CASCADE,
    CONSTRAINT comment_member_fk FOREIGN KEY ("idNumber")
        REFERENCES public.member ("number")
        ON DELETE NO ACTION,
    CONSTRAINT comment_content_not_blank CHECK (content ~ '[^[:space:]]')
);

-- Supports listing and paginating comments for a post in comment-number order.
CREATE INDEX comment_board_ccode_idx ON public.comment (bcode, ccode);

-- Supports looking up a member's comments and checking the member foreign key.
CREATE INDEX comment_member_idx ON public.comment ("idNumber");

-- Keep the modification timestamp current for updates from any DB client.
CREATE FUNCTION public.set_comment_updated_at()
RETURNS trigger
LANGUAGE plpgsql
AS $$
BEGIN
    NEW."updatedAt" := CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$;

CREATE TRIGGER comment_updated_at_trigger
BEFORE UPDATE ON public.comment
FOR EACH ROW
EXECUTE FUNCTION public.set_comment_updated_at();
