drop database if exists workas_db;
create database workas_db;
use workas_db;
 
--- Tabla de clientes
create table Clientes (
    id_cliente int primary key auto_increment,
    nombre varchar(100),
    apellido varchar(100),
    email varchar(100) unique not null,
    contraseña varchar(100) not null,
    telefono varchar(20),
    fecha_registro datetime default current_timestamp
);
 
-- Tabla de freelancers
create table Freelancers (
    id_freelancer int primary key auto_increment,
    nombre varchar(100),
    apellido varchar(100),
    email varchar(100) unique not null,
    contraseña varchar(100) not null,
    telefono varchar(20),
    especialidad varchar(100),
    portafolio_url varchar(255),
    fecha_registro datetime default current_timestamp
);
 
-- Categorías de proyectos
create table Categorias (
    id_categoria int primary key auto_increment,
    nombre varchar(100) not null,
    descripcion text
);
 
-- Tabla de proyectos
create table Proyectos (
    id_proyecto int primary key auto_increment,
    titulo varchar(150) not null,
    descripcion text,
    id_categoria int,
    id_cliente int,
    id_freelancer int,
    presupuesto decimal(10,2),
    monto_acordado decimal(10,2),
    estado enum('publicado', 'en curso', 'finalizado', 'cancelado') default 'publicado',
    fecha_creacion datetime default current_timestamp,
    fecha_entrega date,
    foreign key (id_categoria) references categorias(id_categoria) on delete cascade,
    foreign key (id_cliente) references clientes(id_cliente) on delete cascade,
    foreign key (id_freelancer) references freelancers(id_freelancer) on delete cascade
);
 
-- Tabla de postulaciones
create table Postulaciones (
    id_postulacion int primary key auto_increment,
    id_proyecto int,
    id_freelancer int,
    mensaje text,
    monto_ofrecido decimal(10,2), -- cuánto desea cobrar el freelancer
    estado enum('pendiente', 'aceptado', 'rechazado') default 'pendiente',
    fecha_postulacion datetime default current_timestamp,
    foreign key (id_proyecto) references proyectos(id_proyecto) on delete cascade,
    foreign key (id_freelancer) references freelancers(id_freelancer) on delete cascade
);
 
-- Pagos realizados a freelancers
create table Pagos (
    id_pago int primary key auto_increment,
    id_proyecto int,
    id_freelancer int,
    monto decimal(10,2),
    fecha_pago datetime default current_timestamp,
    metodo_pago varchar(50),
    estado enum('pendiente', 'completado', 'fallido') default 'pendiente',
    foreign key (id_proyecto) references proyectos(id_proyecto) on delete cascade, 
    foreign key (id_freelancer) references freelancers(id_freelancer)on delete cascade
);
 
-- Facturas emitidas a clientes
create table Facturas (
    id_factura int primary key auto_increment,
    id_cliente int,
    fecha datetime default current_timestamp,
    total decimal(10,2),
    estado enum('pendiente', 'pagado', 'cancelado') default 'pendiente',
    foreign key (id_cliente) references clientes(id_cliente)on delete cascade
);
 
-- Detalles de cada factura
create table Detalle_factura (
    id_detalle int primary key auto_increment,
    id_factura int,
    descripcion varchar(255),
    cantidad int,
    precio_unitario decimal(10,2),
    subtotal decimal(10,2),
    foreign key (id_factura) references facturas(id_factura)on delete cascade
);
 
-- Tabla de entregas
create table entregas (
    id_entrega int primary key auto_increment,
    id_proyecto int,
    id_freelancer int,
    archivo_url varchar(255),
    descripcion text,
    fecha_entrega datetime default current_timestamp,
 
    foreign key (id_proyecto) references proyectos(id_proyecto)on delete cascade,
    foreign key (id_freelancer) references freelancers(id_freelancer)on delete cascade
);

-- =============================
-- PROCEDIMIENTOS ALMACENADOS
-- =============================

-- CRUD CLIENTES
delimiter //
create procedure sp_agregarcliente(
    in p_nombre varchar(100),
    in p_apellido varchar(100),
    in p_email varchar(100),
    in p_contraseña varchar(100),
    in p_telefono varchar(20)
)
begin
    insert into clientes (nombre, apellido, email, contraseña, telefono)
    values (p_nombre, p_apellido, p_email, p_contraseña, p_telefono);
end //
delimiter ;

delimiter //
create procedure sp_actualizarcliente(
    in p_id int,
    in p_nombre varchar(100),
    in p_apellido varchar(100),
    in p_email varchar(100),
    in p_contraseña varchar(100),
    in p_telefono varchar(20)
)
begin
    update clientes
    set nombre = p_nombre,
        apellido = p_apellido,
        email = p_email,
        contraseña = p_contraseña,
        telefono = p_telefono
    where id_cliente = p_id;
end //
delimiter ;

delimiter //
create procedure sp_eliminarcliente(in p_id int)
begin
    delete from clientes where id_cliente = p_id;
end //
delimiter ;

delimiter //
create procedure sp_listarclientes()
begin
    select * from clientes;
end //
delimiter ;

-- CRUD FREELANCERS
delimiter //
create procedure sp_agregarfreelancer(
    in p_nombre varchar(100),
    in p_apellido varchar(100),
    in p_email varchar(100),
    in p_contraseña varchar(100),
    in p_telefono varchar(20),
    in p_especialidad varchar(100),
    in p_portafolio varchar(255)
)
begin
    insert into freelancers (nombre, apellido, email, contraseña, telefono, especialidad, portafolio_url)
    values (p_nombre, p_apellido, p_email, p_contraseña, p_telefono, p_especialidad, p_portafolio);
end //
delimiter ;

delimiter //
create procedure sp_actualizarfreelancer(
    in p_id int,
    in p_nombre varchar(100),
    in p_apellido varchar(100),
    in p_email varchar(100),
    in p_contraseña varchar(100),
    in p_telefono varchar(20),
    in p_especialidad varchar(100),
    in p_portafolio varchar(255)
)
begin
    update freelancers
    set nombre = p_nombre,
        apellido = p_apellido,
        email = p_email,
        contraseña = p_contraseña,
        telefono = p_telefono,
        especialidad = p_especialidad,
        portafolio_url = p_portafolio
    where id_freelancer = p_id;
end //
delimiter ;

delimiter //
create procedure sp_eliminarfreelancer(in p_id int)
begin
    delete from freelancers where id_freelancer = p_id;
end //
delimiter ;

delimiter //
create procedure sp_listarfreelancers()
begin
    select * from freelancers;
end //
delimiter ;

-- CRUD CATEGORIAS
delimiter //
create procedure sp_agregarcategoria(
    in p_nombre varchar(100),
    in p_descripcion text
)
begin
    insert into categorias (nombre, descripcion)
    values (p_nombre, p_descripcion);
end //
delimiter ;

delimiter //
create procedure sp_actualizarcategoria(
    in p_id int,
    in p_nombre varchar(100),
    in p_descripcion text
)
begin
    update categorias
    set nombre = p_nombre,
        descripcion = p_descripcion
    where id_categoria = p_id;
end //
delimiter ;

delimiter //
create procedure sp_eliminarcategoria(in p_id int)
begin
    delete from categorias where id_categoria = p_id;
end //
delimiter ;

delimiter //
create procedure sp_listarcategorias()
begin
    select * from categorias;
end //
delimiter ;

-- CRUD PROYECTOS
delimiter //
create procedure sp_agregarproyecto(
    in p_idcliente int,
    in p_idcategoria int,
    in p_titulo varchar(150),
    in p_descripcion text,
    in p_presupuesto decimal(10,2),
    in p_fechaentrega date,
    in p_estado enum('publicado','en curso','finalizado','cancelado')
)
begin
    insert into proyectos (id_cliente, id_categoria, titulo, descripcion, presupuesto, fecha_entrega, estado)
    values (p_idcliente, p_idcategoria, p_titulo, p_descripcion, p_presupuesto, p_fechaentrega, p_estado);
end //
delimiter ;

delimiter //
create procedure sp_actualizarproyecto(
    in p_id int,
    in p_titulo varchar(150),
    in p_descripcion text,
    in p_presupuesto decimal(10,2),
    in p_fechaentrega date,
    in p_estado enum('publicado','en curso','finalizado','cancelado')
)
begin
    update proyectos
    set titulo = p_titulo,
        descripcion = p_descripcion,
        presupuesto = p_presupuesto,
        fecha_entrega = p_fechaentrega,
        estado = p_estado
    where id_proyecto = p_id;
end //
delimiter ;

delimiter //
create procedure sp_eliminarproyecto(in p_id int)
begin
    delete from proyectos where id_proyecto = p_id;
end //
delimiter ;

delimiter //
create procedure sp_listarproyectos()
begin
    select * from proyectos;
end //
delimiter ;

-- CRUD POSTULACIONES
delimiter //
create procedure sp_agregarpostulacion(
    in p_idproyecto int,
    in p_idfreelancer int,
    in p_montoofrecido decimal(10,2),
    in p_mensaje text,
    in p_estado enum('pendiente','aceptado','rechazado')
)
begin
    insert into postulaciones (id_proyecto, id_freelancer, monto_ofrecido, mensaje, estado)
    values (p_idproyecto, p_idfreelancer, p_montoofrecido, p_mensaje, p_estado);
end //
delimiter ;

delimiter //
create procedure sp_actualizarpostulacion(
    in p_id int,
    in p_montoofrecido decimal(10,2),
    in p_mensaje text,
    in p_estado enum('pendiente','aceptado','rechazado')
)
begin
    update postulaciones
    set monto_ofrecido = p_montoofrecido,
        mensaje = p_mensaje,
        estado = p_estado
    where id_postulacion = p_id;
end //
delimiter ;

delimiter //
create procedure sp_eliminarpostulacion(in p_id int)
begin
    delete from postulaciones where id_postulacion = p_id;
end //
delimiter ;

delimiter //
create procedure sp_listarpostulaciones()
begin
    select * from postulaciones;
end //
delimiter ;

-- CRUD PAGOS
delimiter //
create procedure sp_agregarpago(
    in p_idproyecto int,
    in p_idfreelancer int,
    in p_monto decimal(10,2),
    in p_metodo_pago varchar(50),
    in p_estado enum('pendiente','completado','fallido')
)
begin
    insert into pagos (id_proyecto, id_freelancer, monto, metodo_pago, estado)
    values (p_idproyecto, p_idfreelancer, p_monto, p_metodo_pago, p_estado);
end //
delimiter ;

delimiter //
create procedure sp_actualizarpago(
    in p_id int,
    in p_monto decimal(10,2),
    in p_metodo_pago varchar(50),
    in p_estado enum('pendiente','completado','fallido')
)
begin
    update pagos
    set monto = p_monto,
        metodo_pago = p_metodo_pago,
        estado = p_estado
    where id_pago = p_id;
end //
delimiter ;

delimiter //
create procedure sp_eliminarpago(in p_id int)
begin
    delete from pagos where id_pago = p_id;
end //
delimiter ;

delimiter //
create procedure sp_listarpagos()
begin
    select * from pagos;
end //
delimiter ;

-- CRUD FACTURAS
delimiter //
create procedure sp_agregarfactura(
    in p_idcliente int,
    in p_total decimal(10,2),
    in p_estado enum('pendiente','pagado','cancelado')
)
begin
    insert into facturas (id_cliente, total, estado)
    values (p_idcliente, p_total, p_estado);
end //
delimiter ;

delimiter //
create procedure sp_actualizarfactura(
    in p_id int,
    in p_total decimal(10,2),
    in p_estado enum('pendiente','pagado','cancelado')
)
begin
    update facturas
    set total = p_total,
        estado = p_estado
    where id_factura = p_id;
end //
delimiter ;

delimiter //
create procedure sp_eliminarfactura(in p_id int)
begin
    delete from facturas where id_factura = p_id;
end //
delimiter ;

delimiter //
create procedure sp_listarfacturas()
begin
    select * from facturas;
end //
delimiter ;

-- CRUD DETALLE FACTURA
delimiter //
create procedure sp_agregardetallefactura(
    in p_idfactura int,
    in p_descripcion varchar(255),
    in p_cantidad int,
    in p_precio_unitario decimal(10,2)
)
begin
    insert into detalle_factura (id_factura, descripcion, cantidad, precio_unitario, subtotal)
    values (p_idfactura, p_descripcion, p_cantidad, p_precio_unitario, p_cantidad * p_precio_unitario);
end //
delimiter ;

delimiter //
create procedure sp_actualizardetallefactura(
    in p_id int,
    in p_descripcion varchar(255),
    in p_cantidad int,
    in p_precio_unitario decimal(10,2)
)
begin
    update detalle_factura
    set descripcion = p_descripcion,
        cantidad = p_cantidad,
        precio_unitario = p_precio_unitario,
        subtotal = p_cantidad * p_precio_unitario
    where id_detalle = p_id;
end //
delimiter ;

delimiter //
create procedure sp_eliminardetallefactura(in p_id int)
begin
    delete from detalle_factura where id_detalle = p_id;
end //
delimiter ;

delimiter //
create procedure sp_listardetallefactura()
begin
    select * from detalle_factura;
end //
delimiter ;

-- CRUD ENTREGAS
delimiter //
create procedure sp_agregarantrega(
    in p_idproyecto int,
    in p_idfreelancer int,
    in p_descripcion text,
    in p_archivo_url varchar(255)
)
begin
    insert into entregas (id_proyecto, id_freelancer, descripcion, archivo_url)
    values (p_idproyecto, p_idfreelancer, p_descripcion, p_archivo_url);
end //
delimiter ;

delimiter //
create procedure sp_actualizarentrega(
    in p_id int,
    in p_descripcion text,
    in p_archivo_url varchar(255)
)
begin
    update entregas
    set descripcion = p_descripcion,
        archivo_url = p_archivo_url
    where id_entrega = p_id;
end //
delimiter ;

delimiter //
create procedure sp_eliminarentrega(in p_id int)
begin
    delete from entregas where id_entrega = p_id;
end //
delimiter ;

delimiter //
create procedure sp_listarentregas()
begin
    select * from entregas;
end //
delimiter ;

delimiter $$
	create procedure sp_iniciarsesionunificado(
		in p_email varchar(100),
		in p_contraseña varchar(100)
	)
	begin
		declare is_client_found boolean default false;
		
		select count(*) into is_client_found
		from clientes
		where email = p_email and contraseña = p_contraseña;

		if is_client_found then
			select
				id_cliente as id_usuario,
				nombre,
				email,
				'CLIENTE' as rol
			from clientes
			where email = p_email and contraseña = p_contraseña
			limit 1;
		else
			select
				id_freelancer as id_usuario,
				nombre,
				email,
				'FREELANCER' as rol
			from freelancers
			where email = p_email and contraseña = p_contraseña
			limit 1;
		end if;
	end$$
delimiter ;