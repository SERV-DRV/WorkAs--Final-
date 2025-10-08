-- ===================================
-- INSERCIÓN DE DATOS DE EJEMPLO
-- ===================================

-- 1. Insertar Categorías (10 tuplas)
call sp_agregarcategoria('Desarrollo Web', 'Creación de sitios web y aplicaciones web.');
call sp_agregarcategoria('Diseño Gráfico', 'Diseño de logos, banners, y material visual.');
call sp_agregarcategoria('Redacción y Traducción', 'Creación de contenido escrito y traducción de textos.');
call sp_agregarcategoria('Marketing Digital', 'Estrategias de SEO, SEM y gestión de redes sociales.');
call sp_agregarcategoria('Desarrollo Móvil', 'Creación de aplicaciones para iOS y Android.');
call sp_agregarcategoria('Edición de Video', 'Producción y post-producción de material audiovisual.');
call sp_agregarcategoria('Asistencia Virtual', 'Soporte administrativo, gestión de agenda y correos.');
call sp_agregarcategoria('Consultoría de Negocios', 'Asesoramiento estratégico para empresas.');
call sp_agregarcategoria('Análisis de Datos', 'Interpretación y visualización de datos.');
call sp_agregarcategoria('Soporte Técnico IT', 'Resolución de problemas de hardware y software.');

-- 2. Insertar Clientes (10 tuplas)
call sp_agregarcliente('Ana', 'García', 'ana.garcia@email.com', 'cliente123', '555-0101');
call sp_agregarcliente('Carlos', 'Martinez', 'carlos.m@email.com', 'cliente123', '555-0102');
call sp_agregarcliente('Laura', 'Rodriguez', 'laura.r@email.com', 'cliente123', '555-0103');
call sp_agregarcliente('Javier', 'Hernandez', 'javier.h@email.com', 'cliente123', '555-0104');
call sp_agregarcliente('Sofia', 'Lopez', 'sofia.l@email.com', 'cliente123', '555-0105');
call sp_agregarcliente('David', 'Perez', 'david.p@email.com', 'cliente123', '555-0106');
call sp_agregarcliente('Maria', 'Sanchez', 'maria.s@email.com', 'cliente123', '555-0107');
call sp_agregarcliente('Daniel', 'Ramirez', 'daniel.r@email.com', 'cliente123', '555-0108');
call sp_agregarcliente('Paula', 'Gomez', 'paula.g@email.com', 'cliente123', '555-0109');
call sp_agregarcliente('Alejandro', 'Diaz', 'alejandro.d@email.com', 'cliente123', '555-0110');

-- 3. Insertar Freelancers (10 tuplas)
call sp_agregarfreelancer('Miguel', 'Torres', 'miguel.torres@freelance.com', 'freelancer123', '555-0201', 'Desarrollo Web', 'http://migueltorres.dev');
call sp_agregarfreelancer('Isabel', 'Flores', 'isabel.flores@freelance.com', 'freelancer123', '555-0202', 'Diseño Gráfico', 'http://isabelflores.design');
call sp_agregarfreelancer('Roberto', 'Castillo', 'roberto.c@freelance.com', 'freelancer123', '555-0203', 'Redacción y Traducción', 'http://robertocastillo.writer');
call sp_agregarfreelancer('Carmen', 'Ruiz', 'carmen.ruiz@freelance.com', 'freelancer123', '555-0204', 'Marketing Digital', 'http://carmenruiz.marketing');
call sp_agregarfreelancer('Fernando', 'Morales', 'fernando.m@freelance.com', 'freelancer123', '555-0205', 'Desarrollo Móvil', 'http://fernandomorales.app');
call sp_agregarfreelancer('Elena', 'Vazquez', 'elena.v@freelance.com', 'freelancer123', '555-0206', 'Edición de Video', 'http://elenavazquez.video');
call sp_agregarfreelancer('Ricardo', 'Jimenez', 'ricardo.j@freelance.com', 'freelancer123', '555-0207', 'Asistencia Virtual', 'http://ricardojimenez.va');
call sp_agregarfreelancer('Beatriz', 'Moreno', 'beatriz.m@freelance.com', 'freelancer123', '555-0208', 'Consultoría de Negocios', 'http://beatrizmoreno.consulting');
call sp_agregarfreelancer('Jorge', 'Ortega', 'jorge.o@freelance.com', 'freelancer123', '555-0209', 'Análisis de Datos', 'http://jorgeortega.data');
call sp_agregarfreelancer('Silvia', 'Romero', 'silvia.r@freelance.com', 'freelancer123', '555-0210', 'Soporte Técnico IT', 'http://silviaromero.tech');

-- 4. Insertar Proyectos (10 tuplas)
call sp_agregarproyecto(1, 1, 'Creación de Landing Page para Startup', 'Diseñar y desarrollar una página de aterrizaje responsiva.', 500.00, '2025-11-15', 'publicado');
call sp_agregarproyecto(2, 2, 'Diseño de Logo para Cafetería', 'Crear un logo memorable y moderno para una nueva cafetería.', 250.00, '2025-11-01', 'publicado');
call sp_agregarproyecto(3, 4, 'Campaña de Redes Sociales', 'Gestionar la campaña de lanzamiento en Instagram y Facebook.', 800.00, '2025-12-01', 'en curso');
call sp_agregarproyecto(4, 5, 'App de Lista de Tareas para iOS', 'Desarrollar una aplicación nativa para iPhone.', 2500.00, '2026-02-01', 'publicado');
call sp_agregarproyecto(5, 3, 'Traducción de Documento Técnico', 'Traducir un manual de 50 páginas de inglés a español.', 300.00, '2025-10-25', 'finalizado');
call sp_agregarproyecto(1, 6, 'Edición de Video Promocional', 'Editar un video de 2 minutos para un producto.', 400.00, '2025-11-10', 'en curso');
call sp_agregarproyecto(6, 1, 'E-commerce con WordPress', 'Crear una tienda en línea completa con WooCommerce.', 1500.00, '2026-01-15', 'publicado');
call sp_agregarproyecto(7, 8, 'Plan de Negocios para Restaurante', 'Desarrollar un plan de negocios detallado.', 1200.00, '2025-12-20', 'publicado');
call sp_agregarproyecto(8, 9, 'Dashboard de Ventas en Power BI', 'Crear un dashboard interactivo para visualizar ventas.', 600.00, '2025-11-22', 'en curso');
call sp_agregarproyecto(9, 7, 'Gestión de Agenda y Correos', 'Asistencia virtual para un ejecutivo durante un mes.', 700.00, '2025-11-30', 'publicado');

-- 5. Insertar Postulaciones (10 tuplas)
call sp_agregarpostulacion(1, 1, 480.00, 'Tengo experiencia en React y puedo entregar la landing page rápidamente.', 'pendiente');
call sp_agregarpostulacion(2, 2, 250.00, 'Me especializo en diseño de marcas para el sector gastronómico.', 'aceptado');
call sp_agregarpostulacion(3, 4, 750.00, 'He gestionado campañas con más de 1 millón de impresiones.', 'pendiente');
call sp_agregarpostulacion(4, 5, 2300.00, 'Desarrollador iOS con 5 años de experiencia y apps publicadas.', 'pendiente');
call sp_agregarpostulacion(6, 6, 380.00, 'Puedo agregar efectos visuales y corrección de color profesional.', 'pendiente');
call sp_agregarpostulacion(7, 1, 1450.00, 'Experto en WordPress y WooCommerce, garantizo una tienda optimizada para SEO.', 'aceptado');
call sp_agregarpostulacion(8, 8, 1100.00, 'He ayudado a más de 20 startups a lanzar sus planes de negocio.', 'pendiente');
call sp_agregarpostulacion(9, 9, 550.00, 'Cuento con certificación en Power BI y experiencia en el sector retail.', 'pendiente');
call sp_agregarpostulacion(10, 7, 680.00, 'Organizada y proactiva, disponible a tiempo completo.', 'rechazado');
call sp_agregarpostulacion(1, 5, 500.00, 'Puedo hacer el frontend y backend de la aplicación web.', 'pendiente');

-- 6. Insertar Pagos (10 tuplas)
call sp_agregarpago(5, 3, 300.00, 'paypal', 'completado');
call sp_agregarpago(2, 2, 125.00, 'tarjeta', 'pendiente'); -- Pago inicial del 50%
call sp_agregarpago(3, 4, 400.00, 'transferencia', 'completado'); -- Primer pago de la campaña
call sp_agregarpago(6, 6, 200.00, 'tarjeta', 'pendiente');
call sp_agregarpago(7, 1, 750.00, 'transferencia', 'completado');
call sp_agregarpago(1, 1, 100.00, 'paypal', 'fallido'); -- Intento de pago fallido
call sp_agregarpago(9, 9, 300.00, 'tarjeta', 'pendiente');
call sp_agregarpago(8, 8, 600.00, 'transferencia', 'completado');
call sp_agregarpago(4, 5, 500.00, 'paypal', 'pendiente');
call sp_agregarpago(10, 7, 350.00, 'tarjeta', 'completado');

-- 7. Insertar Entregas (10 tuplas)
call sp_agregarantrega(5, 3, 'Versión final del documento traducido.', 'http://example.com/entregas/doc_final.pdf');
call sp_agregarantrega(2, 2, 'Propuesta inicial de logos en formato PNG y AI.', 'http://example.com/entregas/logos_cafeteria.zip');
call sp_agregarantrega(3, 4, 'Reporte semanal de métricas de la campaña.', 'http://example.com/entregas/reporte_sem1.pdf');
call sp_agregarantrega(6, 6, 'Primer borrador del video promocional.', 'http://example.com/entregas/video_v1.mp4');
call sp_agregarantrega(7, 1, 'Acceso al sitio de desarrollo para revisión.', 'http://dev.tienda-wp.com');
call sp_agregarantrega(1, 1, 'Maqueta del diseño de la landing page.', 'http://example.com/entregas/maqueta_landing.fig');
call sp_agregarantrega(9, 9, 'Vínculo al dashboard publicado en Power BI Service.', 'http://powerbi.com/dashboard/ventas_xyz');
call sp_agregarantrega(8, 8, 'Borrador del plan de negocios, sección financiera.', 'http://example.com/entregas/plan_financiero.docx');
call sp_agregarantrega(4, 5, 'Prototipo funcional de la app en TestFlight.', 'http://testflight.apple.com/join/app_tareas');
call sp_agregarantrega(10, 7, 'Resumen diario de actividades y correos gestionados.', 'http://example.com/entregas/resumen_20251020.pdf');

-- 8. Insertar Facturas y sus Detalles (5 facturas con 10 detalles en total)
-- Factura 1
call sp_agregarfactura(1, 500.00, 'pendiente');
call sp_agregardetallefactura(1, 'Desarrollo Landing Page', 1, 500.00);

-- Factura 2
call sp_agregarfactura(2, 250.00, 'pagado');
call sp_agregardetallefactura(2, 'Diseño de Logotipo', 1, 250.00);

-- Factura 3
call sp_agregarfactura(3, 800.00, 'pendiente');
call sp_agregardetallefactura(3, 'Gestión de Campaña RRSS - Mes 1', 1, 800.00);

-- Factura 4
call sp_agregarfactura(5, 700.00, 'pagado');
call sp_agregardetallefactura(4, 'Traducción de Manual Técnico', 1, 300.00);
call sp_agregardetallefactura(4, 'Edición Video Promocional', 1, 400.00);

-- Factura 5
call sp_agregarfactura(6, 1500.00, 'pendiente');
call sp_agregardetallefactura(5, 'Desarrollo de Tienda Online (E-commerce)', 1, 1500.00);