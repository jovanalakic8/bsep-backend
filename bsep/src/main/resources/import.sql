INSERT INTO roles (id,name) VALUES (-1,'client');
INSERT INTO roles (id,name) VALUES (-2,'employee');
INSERT INTO roles (id,name) VALUES (-3,'admin');

INSERT INTO public.users(enabled, id, address, city, country, name, password, phone, surname, username, blocked, activation_pending,using2FA,secret2FA) VALUES (true, -1, 'glcAJwg8FenNhTqAq0OGVw==', '7N8PVIl22Z6QZlIJsszG/g==', 'OOQoFfq3TuqqXYhgbddYyA==', '7b9yk7lCOTI2u1Ykdnhq+A==', '$2a$10$e0GCtqUSjhH0nGaRZIgiDeiyK0cecH19N92SibYOQyfhobvtNHrUS', 'w7XMX5uRGHdomS9u8D0m0w==', '9rYBaPvzS+pwgthoMpPPzA==', 'jovana.lakic@gmail.com', null, false, false, null);

INSERT INTO public.users(enabled, id, address, city, country, name, password, phone, surname, username, blocked, activation_pending,using2FA,secret2FA) VALUES (true, -2, '0tZZGRhuRqzG+U0ALcJ4iA==', '7N8PVIl22Z6QZlIJsszG/g==', 'OOQoFfq3TuqqXYhgbddYyA==', 'mNgxi+89SEwDu+Xr/PCoWQ==', '$2a$10$e0GCtqUSjhH0nGaRZIgiDeiyK0cecH19N92SibYOQyfhobvtNHrUS', 'dZZnC3EavLAN6arlr72O8w==', '4Ga0xMHRCJizLLbK1PAHWw==', 'employee@gmail.com', null, false, false, null);

INSERT INTO public.users(enabled, id, address, city, country, name, password, phone, surname, username, blocked, activation_pending, using2FA,secret2FA) VALUES (true, -3, '0tZZGRhuRqzG+U0ALcJ4iA==', '7N8PVIl22Z6QZlIJsszG/g==', 'OOQoFfq3TuqqXYhgbddYyA==', 'bdLnWLWQFglIMuBbqT5yqA==', '$2a$10$e0GCtqUSjhH0nGaRZIgiDeiyK0cecH19N92SibYOQyfhobvtNHrUS', '3SpMG1pKCCuH2TlXjUgUUg==', 'rFMUNUlxhvg6LyGeHeQoNQ==', 'ana.tomic@gmail.com', null, false, false, null);

INSERT INTO public.clients(client_type, service_package, client_id) VALUES (0, 1, -3);

INSERT INTO public.users(enabled, id, address, city, country, name, password, phone, surname, username, blocked, activation_pending, using2FA,secret2FA) VALUES (false, -4, '0tZZGRhuRqzG+U0ALcJ4iA==', '7N8PVIl22Z6QZlIJsszG/g==', 'OOQoFfq3TuqqXYhgbddYyA==', 'M7xatuhEw26eHEQ4oAoYow==', '$2a$10$e0GCtqUSjhH0nGaRZIgiDeiyK0cecH19N92SibYOQyfhobvtNHrUS', '3SpMG1pKCCuH2TlXjUgUUg==', 'C8rdnAKy/CmDKhkSRJWiXw==', 'kacadrakulic@gmail.com', null, false, false, null);

INSERT INTO public.clients(client_type, service_package, client_id) VALUES (0, 2, -4);

INSERT INTO public.users(enabled, id, address, city, country, password, phone, username, blocked, activation_pending, using2FA,secret2FA) VALUES (false, -5, '0tZZGRhuRqzG+U0ALcJ4iA==', '7N8PVIl22Z6QZlIJsszG/g==', 'OOQoFfq3TuqqXYhgbddYyA==',  '$2a$10$e0GCtqUSjhH0nGaRZIgiDeiyK0cecH19N92SibYOQyfhobvtNHrUS', '3SpMG1pKCCuH2TlXjUgUUg==', 'cli5@gmail.com', null, false, false, null);

INSERT INTO public.clients( client_type, service_package, client_id, company_name, tin) VALUES (1, 0, -5, 'Kompanija 1', '3846296229');

INSERT INTO public.users(enabled, id, address, city, country, password, phone, username, blocked, activation_pending, using2FA,secret2FA) VALUES (true, -6, '0tZZGRhuRqzG+U0ALcJ4iA==', '7N8PVIl22Z6QZlIJsszG/g==', 'OOQoFfq3TuqqXYhgbddYyA==',  '$2a$10$e0GCtqUSjhH0nGaRZIgiDeiyK0cecH19N92SibYOQyfhobvtNHrUS', '3SpMG1pKCCuH2TlXjUgUUg==', 'cli6@gmail.com', null, false, false, null);

INSERT INTO public.clients( client_type, service_package, client_id, company_name, tin) VALUES (1, 0, -6, 'Kompanija 2', '3856296222');

INSERT INTO public.user_role (user_id, role_id) VALUES (-1, -3);
INSERT INTO public.user_role (user_id, role_id) VALUES (-2, -2);
INSERT INTO public.user_role (user_id, role_id) VALUES (-3, -1);
INSERT INTO public.user_role (user_id, role_id) VALUES (-4, -1);
INSERT INTO public.user_role (user_id, role_id) VALUES (-5, -1);
INSERT INTO public.user_role (user_id, role_id) VALUES (-6, -1);

INSERT INTO public.permissions(id, description, name) VALUES (-1, 'Deskripcija', 'Brisanje');
INSERT INTO public.permissions(id, description, name) VALUES (-2, 'Deskripcija123', 'Pisanje');
INSERT INTO public.permissions(id, description, name) VALUES (-3, 'Deskripcija123', 'SveVezanoZaDozvole');
INSERT INTO public.permissions(id, description, name) VALUES (-4, 'Deskripcija123', 'SveVezanoZaRole');
INSERT INTO public.permissions(id, description, name) VALUES (-5, 'Deskripcija123', 'SveVezanoZaKlijente');

INSERT INTO public.permission_role(permission_id, role_id) VALUES (-3, -3);
INSERT INTO public.permission_role(permission_id, role_id) VALUES (-5, -3);
INSERT INTO public.permission_role(permission_id, role_id) VALUES (-2, -3);
INSERT INTO public.permission_role(permission_id, role_id) VALUES (-1, -3);
INSERT INTO public.permission_role(permission_id, role_id) VALUES (-4, -3);
INSERT INTO public.permission_role(permission_id, role_id) VALUES (-3, -2);

INSERT INTO public.requests(id, client_id, deadline, active_from, active_to, description) VALUES (-1, -5, '25-05-2024', '26-05-2024', '29-05-2024', 'Opis zahtjeva...');

INSERT INTO public.requests(id, client_id, deadline, active_from, active_to, description) VALUES (-2, -6, '20-05-2024', '21-05-2024', '30-05-2024', 'Opis zahtjeva...');

INSERT INTO public.employees(employee_id, has_changed_password) VALUES (-2, false);

INSERT INTO public.users(enabled, id, address, city, country, password, phone, username, blocked, activation_pending, using2FA,secret2FA, name, surname) VALUES (true, -7, '0tZZGRhuRqzG+U0ALcJ4iA==', '7N8PVIl22Z6QZlIJsszG/g==', 'OOQoFfq3TuqqXYhgbddYyA==',  '$2a$10$e0GCtqUSjhH0nGaRZIgiDeiyK0cecH19N92SibYOQyfhobvtNHrUS', '3SpMG1pKCCuH2TlXjUgUUg==', 'employee1@gmail.com', null, false, false, null, 'Tdf/hDLWSSuhaAu84lqefw==', 'GgoJE+f+2+9huIllamD/zA==');

INSERT INTO public.user_role (user_id, role_id) VALUES (-7, -2);

INSERT INTO public.employees(employee_id, has_changed_password) VALUES (-7, false);