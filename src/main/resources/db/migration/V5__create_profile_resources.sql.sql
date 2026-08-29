 CREATE TABLE profile_resources (
                                   id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

                                   profile_id UUID NOT NULL,

                                   resource_id UUID NOT NULL,

                                   created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                   CONSTRAINT fk_profile_resources_profile
                                       FOREIGN KEY (profile_id)
                                           REFERENCES profiles (id),

                                   CONSTRAINT fk_profile_resources_resource
                                       FOREIGN KEY (resource_id)
                                           REFERENCES resources (id),

                                   CONSTRAINT uk_profile_resource
                                       UNIQUE (profile_id, resource_id)
);

 INSERT INTO public.profile_resources
 (id, profile_id, resource_id, created_at)
 VALUES('c368acbf-af77-4513-a9e9-48f8831f7ac1'::uuid, 'a747e317-12b2-4e97-82ac-d583ea704141'::uuid, 'f66f9494-0f1d-460d-a29c-288b99a5ca44'::uuid, '2026-08-20 18:36:40.527');
 INSERT INTO public.profile_resources
 (id, profile_id, resource_id, created_at)
 VALUES('4f48133e-2cc5-425f-9a1a-4a8de5cb038c'::uuid, 'a747e317-12b2-4e97-82ac-d583ea704141'::uuid, '380664a0-be26-44c5-b54e-3016e36c076c'::uuid, '2026-08-20 17:18:55.458');
 INSERT INTO public.profile_resources
 (id, profile_id, resource_id, created_at)
 VALUES('be6bf59b-88d1-4f9b-8ac0-3eb5d6bd4668'::uuid, 'a747e317-12b2-4e97-82ac-d583ea704141'::uuid, '99258ad9-42d6-44ae-a9bb-4177bec0b837'::uuid, '2026-08-20 17:18:55.458');
 INSERT INTO public.profile_resources
 (id, profile_id, resource_id, created_at)
 VALUES('d1e80411-a511-4b85-b904-49e02c98b76e'::uuid, 'a747e317-12b2-4e97-82ac-d583ea704141'::uuid, '6670892a-3b3c-42a5-b7f8-6008367632e2'::uuid, '2026-08-20 17:18:55.458');