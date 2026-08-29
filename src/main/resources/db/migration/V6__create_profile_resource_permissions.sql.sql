CREATE TABLE profile_resource_permissions (
                                                profile_resource_id UUID NOT NULL,
                                                permission_id UUID NOT NULL,
                                                created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                                CONSTRAINT pk_profile_resource_permissions
                                                    PRIMARY KEY (
                                                                 profile_resource_id,
                                                                 permission_id
                                                        ),
                                                CONSTRAINT fk_prp_profile_resource
                                                    FOREIGN KEY (profile_resource_id)
                                                        REFERENCES profile_resources (id)
                                                        ON DELETE CASCADE,
                                                CONSTRAINT fk_prp_permission
                                                    FOREIGN KEY (permission_id)
                                                        REFERENCES permissions (id)
  );

INSERT INTO public.profile_resource_permissions
(profile_resource_id, permission_id, created_at)
VALUES('c368acbf-af77-4513-a9e9-48f8831f7ac1'::uuid, '948ee5dc-e134-44df-a0a6-cc5e02524e18'::uuid, '2026-08-25 14:50:51.418');
INSERT INTO public.profile_resource_permissions
(profile_resource_id, permission_id, created_at)
VALUES('4f48133e-2cc5-425f-9a1a-4a8de5cb038c'::uuid, 'bf29ea49-b5d5-4f10-8ce7-bd01aa32172b'::uuid, '2026-08-25 14:50:51.418');
INSERT INTO public.profile_resource_permissions
(profile_resource_id, permission_id, created_at)
VALUES('be6bf59b-88d1-4f9b-8ac0-3eb5d6bd4668'::uuid, '63a8171b-67c3-4b6b-b69e-6ad2d09fe04d'::uuid, '2026-08-25 14:50:51.418');
INSERT INTO public.profile_resource_permissions
(profile_resource_id, permission_id, created_at)
VALUES('d1e80411-a511-4b85-b904-49e02c98b76e'::uuid, '42d0a8ac-11f5-4435-a1c6-bf810a56f4f2'::uuid, '2026-08-25 14:50:51.418');
INSERT INTO public.profile_resource_permissions
(profile_resource_id, permission_id, created_at)
VALUES('c368acbf-af77-4513-a9e9-48f8831f7ac1'::uuid, 'bf29ea49-b5d5-4f10-8ce7-bd01aa32172b'::uuid, '2026-08-25 14:50:51.418');
INSERT INTO public.profile_resource_permissions
(profile_resource_id, permission_id, created_at)
VALUES('c368acbf-af77-4513-a9e9-48f8831f7ac1'::uuid, '63a8171b-67c3-4b6b-b69e-6ad2d09fe04d'::uuid, '2026-08-25 14:50:51.418');
INSERT INTO public.profile_resource_permissions
(profile_resource_id, permission_id, created_at)
VALUES('c368acbf-af77-4513-a9e9-48f8831f7ac1'::uuid, '42d0a8ac-11f5-4435-a1c6-bf810a56f4f2'::uuid, '2026-08-25 14:50:51.418');
INSERT INTO public.profile_resource_permissions
(profile_resource_id, permission_id, created_at)
VALUES('4f48133e-2cc5-425f-9a1a-4a8de5cb038c'::uuid, '948ee5dc-e134-44df-a0a6-cc5e02524e18'::uuid, '2026-08-25 14:50:51.418');
INSERT INTO public.profile_resource_permissions
(profile_resource_id, permission_id, created_at)
VALUES('4f48133e-2cc5-425f-9a1a-4a8de5cb038c'::uuid, '42d0a8ac-11f5-4435-a1c6-bf810a56f4f2'::uuid, '2026-08-25 14:50:51.418');
INSERT INTO public.profile_resource_permissions
(profile_resource_id, permission_id, created_at)
VALUES('4f48133e-2cc5-425f-9a1a-4a8de5cb038c'::uuid, '63a8171b-67c3-4b6b-b69e-6ad2d09fe04d'::uuid, '2026-08-25 14:50:51.418');
INSERT INTO public.profile_resource_permissions
(profile_resource_id, permission_id, created_at)
VALUES('d1e80411-a511-4b85-b904-49e02c98b76e'::uuid, 'bf29ea49-b5d5-4f10-8ce7-bd01aa32172b'::uuid, '2026-08-25 14:50:51.418');
INSERT INTO public.profile_resource_permissions
(profile_resource_id, permission_id, created_at)
VALUES('d1e80411-a511-4b85-b904-49e02c98b76e'::uuid, '948ee5dc-e134-44df-a0a6-cc5e02524e18'::uuid, '2026-08-25 14:50:51.418');
INSERT INTO public.profile_resource_permissions
(profile_resource_id, permission_id, created_at)
VALUES('d1e80411-a511-4b85-b904-49e02c98b76e'::uuid, '63a8171b-67c3-4b6b-b69e-6ad2d09fe04d'::uuid, '2026-08-25 14:50:51.418');
INSERT INTO public.profile_resource_permissions
(profile_resource_id, permission_id, created_at)
VALUES('be6bf59b-88d1-4f9b-8ac0-3eb5d6bd4668'::uuid, '42d0a8ac-11f5-4435-a1c6-bf810a56f4f2'::uuid, '2026-08-25 14:50:51.418');
INSERT INTO public.profile_resource_permissions
(profile_resource_id, permission_id, created_at)
VALUES('be6bf59b-88d1-4f9b-8ac0-3eb5d6bd4668'::uuid, '948ee5dc-e134-44df-a0a6-cc5e02524e18'::uuid, '2026-08-25 14:50:51.418');
INSERT INTO public.profile_resource_permissions
(profile_resource_id, permission_id, created_at)
VALUES('be6bf59b-88d1-4f9b-8ac0-3eb5d6bd4668'::uuid, 'bf29ea49-b5d5-4f10-8ce7-bd01aa32172b'::uuid, '2026-08-25 14:50:51.418');