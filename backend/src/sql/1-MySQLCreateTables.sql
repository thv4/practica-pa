DROP TABLE IF EXISTS Compra;
DROP TABLE IF EXISTS Sesion;


DROP TABLE IF EXISTS User;
DROP TABLE IF EXISTS Pelicula;
DROP TABLE IF EXISTS Sala;

CREATE TABLE User (
  id BIGINT NOT NULL AUTO_INCREMENT,
  userName VARCHAR(60) COLLATE latin1_bin NOT NULL,
  password VARCHAR(60) NOT NULL,
  firstName VARCHAR(60) NOT NULL,
  lastName VARCHAR(60) NOT NULL,
  email VARCHAR(60) NOT NULL,
  role TINYINT NOT NULL,
  CONSTRAINT UserPK PRIMARY KEY (id),
  CONSTRAINT UserNameUniqueKey UNIQUE (userName)
) ENGINE = InnoDB;

CREATE INDEX UserIndexByUserName ON User (userName);

-- 1. Tabla Pelicula
CREATE TABLE Pelicula (
    id BIGINT NOT NULL AUTO_INCREMENT,
    titulo VARCHAR(255) NOT NULL,
    resumen VARCHAR(2000),
    duracion INT NOT NULL,
    CONSTRAINT PeliculaPK PRIMARY KEY (id)
) ENGINE=InnoDB;

-- 2. Tabla Sala
CREATE TABLE Sala (
  id BIGINT NOT NULL AUTO_INCREMENT,
  nombre VARCHAR(255) NOT NULL,
  capacidad INT NOT NULL,
  CONSTRAINT SalaPK PRIMARY KEY (id)
) ENGINE=InnoDB;

-- 3. Tabla Sesion
-- Nota: Sesion usa anotaciones en campos, por defecto busca nombres de columnas igual que los atributos
CREATE TABLE Sesion (
    id BIGINT NOT NULL AUTO_INCREMENT,
    sala_id BIGINT NOT NULL,
    pelicula_id BIGINT NOT NULL,
    fechaHora DATETIME NOT NULL,
    precio DECIMAL(11, 2) NOT NULL,
    localidadesLibres BIGINT NOT NULL,
    CONSTRAINT SesionPK PRIMARY KEY (id),
    CONSTRAINT SesionSalaIdFK FOREIGN KEY (sala_id) REFERENCES Sala(id),
    CONSTRAINT SesionPeliculaIdFK FOREIGN KEY (pelicula_id) REFERENCES Pelicula(id)
) ENGINE=InnoDB;

-- 4. Tabla Compra
-- Nota: Aquí tus compañeros especificaron @JoinColumn(name = "userId") y "sesionId"
CREATE TABLE Compra (
    compraId BIGINT NOT NULL AUTO_INCREMENT,
    userId BIGINT NOT NULL,
    sesionId BIGINT NOT NULL,
    fechaRegistroCompra DATETIME NOT NULL,
    numLocalidades INT NOT NULL,
    tarjetaBancaria VARCHAR(16) NOT NULL,
    entregada TINYINT(1) NOT NULL, -- boolean en MySQL es TINYINT
    CONSTRAINT CompraPK PRIMARY KEY (compraId),
    CONSTRAINT CompraUserIdFK FOREIGN KEY (userId) REFERENCES User(id),
    CONSTRAINT CompraSesionIdFK FOREIGN KEY (sesionId) REFERENCES Sesion(id)
) ENGINE=InnoDB;
