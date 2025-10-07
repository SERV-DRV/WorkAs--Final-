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
    id_freelancer int, -- se llena solo cuando se acepta una postulación
    presupuesto decimal(10,2), -- presupuesto máximo propuesto por el cliente
    monto_acordado decimal(10,2), -- se llena cuando se acepta una propuesta
    estado enum('publicado', 'en curso', 'finalizado', 'cancelado') default 'publicado',
    fecha_creacion datetime default current_timestamp,
    fecha_entrega date,
    foreign key (id_categoria) references categorias(id_categoria),
    foreign key (id_cliente) references clientes(id_cliente),
    foreign key (id_freelancer) references freelancers(id_freelancer)
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
    foreign key (id_proyecto) references proyectos(id_proyecto),
    foreign key (id_freelancer) references freelancers(id_freelancer)
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
    foreign key (id_proyecto) references proyectos(id_proyecto),
    foreign key (id_freelancer) references freelancers(id_freelancer)
);
 
-- Facturas emitidas a clientes
create table Facturas (
    id_factura int primary key auto_increment,
    id_cliente int,
    fecha datetime default current_timestamp,
    total decimal(10,2),
    estado enum('pendiente', 'pagado', 'cancelado') default 'pendiente',
    foreign key (id_cliente) references clientes(id_cliente)
);
 
-- Detalles de cada factura
create table Detalle_factura (
    id_detalle int primary key auto_increment,
    id_factura int,
    descripcion varchar(255),
    cantidad int,
    precio_unitario decimal(10,2),
    subtotal decimal(10,2),
    foreign key (id_factura) references facturas(id_factura)
);
 
-- Tabla de entregas
create table entregas (
    id_entrega int primary key auto_increment,
    id_proyecto int,
    id_freelancer int,
    archivo_url varchar(255),
    descripcion text,
    fecha_entrega datetime default current_timestamp,
 
    foreign key (id_proyecto) references proyectos(id_proyecto),
    foreign key (id_freelancer) references freelancers(id_freelancer)
);

-- ============================================
-- PROCEDIMIENTOS ALMACENADOS
-- ============================================

-- CRUD CLIENTES
DELIMITER //
	create procedure sp_AgregarCliente(
		in p_nombre varchar(100), 
		in p_correo varchar(100), 
		in p_telefono varchar(20), 
		in p_direccion varchar(200))
		begin
			insert into Clientes (nombre, correo, telefono, direccion) 
			values (p_nombre, p_correo, p_telefono, p_direccion);
		end //
DELIMITER //

DELIMITER //
	create procedure sp_ActualizarCliente(
		in p_id int, 
        in p_nombre varchar(100), 
        in p_correo varchar(100), 
        in p_telefono varchar(20), 
        in p_direccion varchar(200))
		begin
			update Clientes 
			set nombre = p_nombre, correo = p_correo, telefono = p_telefono, direccion = p_direccion 
			where idCliente = p_id;
		end //
DELIMITER //

DELIMITER //
	create procedure sp_EliminarCliente(in p_id int)
	begin
		delete from Clientes 
        where idCliente = p_id;
	end //
DELIMITER //

DELIMITER //
	create procedure sp_ListarClientes()
	begin
		select * from Clientes;
	end //
DELIMITER ;

-- CRUD FREELANCERS
DELIMITER //
	create procedure sp_AgregarFreelancer(
		in p_nombre varchar(100), 
		in p_correo varchar(100), 
		in p_telefono varchar(20), 
		in p_especialidad varchar(100), 
		in p_tarifa decimal(10,2))
		begin
			insert into Freelancers (nombre, correo, telefono, especialidad, tarifaHora) 
			values (p_nombre, p_correo, p_telefono, p_especialidad, p_tarifa);
		end //
DELIMITER //

DELIMITER //
	create procedure sp_ActualizarFreelancer(
		in p_id int, 
        in p_nombre varchar(100), 
        in p_correo varchar(100), 
        in p_telefono varchar(20), 
        in p_especialidad varchar(100), 
        in p_tarifa decimal(10,2))
		begin
			update Freelancers 
            set nombre = p_nombre, correo = p_correo, telefono = p_telefono, especialidad = p_especialidad, tarifaHora = p_tarifa 
			where idFreelancer = p_id;
		end //
DELIMITER //

DELIMITER //
	create procedure sp_EliminarFreelancer(in p_id int)
	begin
		delete from Freelancers 
        where idFreelancer = p_id;
	end //
DELIMITER //

DELIMITER //
	create procedure sp_ListarFreelancers()
	begin
		select * from Freelancers;
	end //
DELIMITER ;

-- CRUD CATEGORIAS
DELIMITER //
	create procedure sp_AgregarCategoria(
    in p_nombre varchar(100), 
    in p_descripcion text)
	begin
		insert into Categorias (nombre, descripcion) 
        values (p_nombre, p_descripcion);
	end //
DELIMITER //

DELIMITER //
	create procedure sp_ActualizarCategoria(
		in p_id int, 
        in p_nombre varchar(100), 
        in p_descripcion text)
		begin
			update Categorias 
            set nombre = p_nombre, descripcion = p_descripcion 
            where idCategoria = p_id;
		end //
DELIMITER //

DELIMITER //
	create procedure sp_EliminarCategoria(in p_id int)
		begin
			delete from Categorias 
            where idCategoria = p_id;
		end //
DELIMITER //

DELIMITER //
	create procedure sp_ListarCategorias()
	begin
		select * from Categorias;
	end //
DELIMITER ;

-- CRUD PROYECTOS
DELIMITER //
	create procedure sp_AgregarProyecto(
		in p_idCliente int,
        in p_idCategoria int,
        in p_titulo varchar(150),
        in p_descripcion text,
        in p_presupuesto decimal(10,2),
        in p_fechainicio date,
        in p_fechaFin date,
        in p_estado varchar(50))
		begin
			insert into Proyectos (idCliente, idCategoria, titulo, descripcion, presupuesto, fechainicio, fechaFin, estado) 
            values (p_idCliente, p_idCategoria, p_titulo, p_descripcion, p_presupuesto, p_fechainicio, p_fechaFin, p_estado);
		end //
DELIMITER //

DELIMITER //
	create procedure sp_ActualizarProyecto(
		in p_id int,
        in p_titulo varchar(150),
        in p_descripcion text,
        in p_presupuesto decimal(10,2),
        in p_fechainicio date,
        in p_fechaFin date,
        in p_estado varchar(50))
		begin
			update Proyectos 
            set titulo = p_titulo, descripcion = p_descripcion, presupuesto = p_presupuesto, fechainicio = p_fechainicio, fechaFin = p_fechaFin, estado = p_estado 
            where idProyecto = p_id;
		end //
DELIMITER //

DELIMITER //
	create procedure sp_EliminarProyecto(in p_id int)
		begin
			delete from Proyectos 
            where idProyecto = p_id;
		end //
DELIMITER //

DELIMITER //
	create procedure sp_ListarProyectos()
	begin
		select * from Proyectos;
	end //
DELIMITER //


-- CRUD POSTULACIONES
DELIMITER //
	create procedure sp_AgregarPostulacion(
		in p_idProyecto int,
        in p_idFreelancer int,
        in p_propuesta decimal(10,2),
        in p_mensaje text,
        in p_fecha date,
        in p_estado varchar(50))
		begin
			insert into Postulaciones (idProyecto, idFreelancer, propuesta, mensaje, fechaPostulacion, estado)
            values (p_idProyecto, p_idFreelancer, p_propuesta, p_mensaje, p_fecha, p_estado);
		end //
DELIMITER //

DELIMITER //
	create procedure sp_ActualizarPostulacion(
		in p_id int,
        in p_propuesta decimal(10,2),
        in p_mensaje text,
        in p_estado varchar(50))
		begin
			update Postulaciones 
            set propuesta = p_propuesta, mensaje = p_mensaje, estado = p_estado 
            where idPostulacion = p_id;
		end //
DELIMITER //

DELIMITER //
	create procedure sp_EliminarPostulacion(in p_id int)
		begin
			delete from Postulaciones 
            where idPostulacion = p_id;
		end //
DELIMITER //

DELIMITER //
	create procedure sp_ListarPostulaciones()
	begin
		select * from Postulaciones;
	end //
DELIMITER //


-- CRUD PAGOS
DELIMITER //
	create procedure sp_AgregarPago(
		in p_idProyecto int,
        in p_monto decimal(10,2),
        in p_fecha date,
        in p_metodo varchar(50))
		begin
			insert into Pagos (idProyecto, monto, fechaPago, metodoPago) 
            values (p_idProyecto, p_monto, p_fecha, p_metodo);
		end //
DELIMITER //

DELIMITER //
	create procedure sp_ActualizarPago(
		in p_id int,
        in p_monto decimal(10,2),
        in p_fecha date,
        in p_metodo varchar(50))
		begin
			update Pagos 
            set monto = p_monto, fechaPago = p_fecha, metodoPago = p_metodo 
            where idPago = p_id;
		end //
DELIMITER //

DELIMITER //
	create procedure sp_EliminarPago(in p_id int)
		begin
			delete from Pagos 
            where idPago = p_id;
		end //
DELIMITER //

DELIMITER //
	create procedure sp_ListarPagos()
	begin
		select * from Pagos;
	end //
DELIMITER //


-- CRUD FACTURAS
DELIMITER //
	create procedure sp_AgregarFactura(
		in p_idPago int,
        in p_numero varchar(50),
        in p_fecha date,
        in p_total decimal(10,2))
		begin
			insert into Facturas (idPago, numeroFactura, fechaEmision, total) 
            values (p_idPago, p_numero, p_fecha, p_total);
		end //
DELIMITER //

DELIMITER //
	create procedure sp_ActualizarFactura(
		in p_id int,
        in p_numero varchar(50),
        in p_fecha date,
        in p_total decimal(10,2))
		begin
			update Facturas 
            set numeroFactura = p_numero, fechaEmision = p_fecha, total = p_total 
            where idFactura = p_id;
		end //
DELIMITER //

DELIMITER //
	create procedure sp_EliminarFactura(in p_id int)
		begin
			delete from Facturas 
            where idFactura = p_id;
		end //
DELIMITER //

DELIMITER //
	create procedure sp_ListarFacturas()
	begin
		select * from Facturas;
	end //
DELIMITER //


-- CRUD DETALLE FACTURA
DELIMITER //
	create procedure sp_AgregarDetalleFactura(
		in p_idFactura int,
        in p_descripcion text,
        in p_cantidad int,
        in p_precio decimal(10,2))
		begin
			insert into DetalleFactura (idFactura, descripcion, cantidad, precioUnitario, subtotal)
            values (p_idFactura, p_descripcion, p_cantidad, p_precio, (p_cantidad * p_precio));
		end //
DELIMITER //

DELIMITER //
	create procedure sp_ActualizarDetalleFactura(
		in p_id int,
        in p_descripcion text,
        in p_cantidad int,
        in p_precio decimal(10,2))
		begin
			update DetalleFactura 
            set descripcion = p_descripcion, cantidad = p_cantidad, precioUnitario = p_precio, subtotal = (p_cantidad * p_precio) 
            where idDetalle = p_id;
		end //
DELIMITER //

DELIMITER //
	create procedure sp_EliminarDetalleFactura(in p_id int)
		begin
			delete from DetalleFactura 
            where idDetalle = p_id;
		end //
DELIMITER //

DELIMITER //
	create procedure sp_ListarDetalleFactura()
	begin
		select * from DetalleFactura;
	end //
DELIMITER //


-- CRUD ENTREGAS
DELIMITER //
	create procedure sp_AgregarEntrega(
		in p_idProyecto int,
        in p_idFreelancer int,
        in p_descripcion text,
        in p_archivo varchar(200),
        in p_fecha date,
        in p_estado varchar(50))
		begin
			insert into Entregas (idProyecto, idFreelancer, descripcion, archivo, fechaEntrega, estado)
            values (p_idProyecto, p_idFreelancer, p_descripcion, p_archivo, p_fecha, p_estado);
		end //
DELIMITER //

DELIMITER //
	create procedure sp_ActualizarEntrega(
		in p_id int,
        in p_descripcion text,
        in p_archivo varchar(200),
        in p_estado varchar(50))
		begin
			update Entregas 
            set descripcion = p_descripcion, archivo = p_archivo, estado = p_estado 
            where idEntrega = p_id;
		end //
DELIMITER //

DELIMITER //
	create procedure sp_EliminarEntrega(in p_id int)
		begin
			delete from Entregas 
            where idEntrega = p_id;
		end //
DELIMITER //

DELIMITER //
	create procedure sp_ListarEntregas()
	begin
		select * from Entregas;
	end //
DELIMITER ;