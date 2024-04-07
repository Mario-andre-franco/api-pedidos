CREATE TABLE Pedidos (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         numeroControle VARCHAR(255) NOT NULL,
                         dataCadastro DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         nome VARCHAR(255) NOT NULL,
                         valor DECIMAL(19,2) NOT NULL,
                         quantidade INT DEFAULT 1,
                         codigoCliente BIGINT NOT NULL
);