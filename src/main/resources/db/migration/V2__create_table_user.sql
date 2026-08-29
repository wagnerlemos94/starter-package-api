CREATE TABLE users (
                       id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                       cpf VARCHAR(11) UNIQUE NOT NULL,
                       password VARCHAR(255) NOT NULL,
                       name VARCHAR(150) NOT NULL,
                       profile_id UUID NOT NULL,
                       active BOOLEAN,
                       CONSTRAINT fk_users_profile
                           FOREIGN KEY (profile_id)
                            REFERENCES profiles (id)
);

INSERT INTO public.users
(id, cpf, "password", "name", active, profile_id)
VALUES('a2b2f256-9368-4fae-8669-7966bca28940'::uuid, '85806128539', '$2a$10$OWCFTMPGNDEmstnRog2Zx.VAYxa47cdpQteXz0eT6xV7GcJsyqYSC', 'wagner cupertino lemos', true, 'a747e317-12b2-4e97-82ac-d583ea704141'::uuid);