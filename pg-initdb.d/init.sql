DO
$$
    BEGIN
        CREATE TYPE post_status AS ENUM ( 'DRAFT', 'PENDING_MODERATION', 'PUBLISHED');
    EXCEPTION
        WHEN duplicate_object THEN null;
    END
$$;

CREATE CAST ( varchar AS post_status ) WITH INOUT AS IMPLICIT;