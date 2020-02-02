
create database coffee_bd;

CREATE TABLE IF NOT EXISTS `coffee_bd`.`user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `full_name` VARCHAR(254) NOT NULL,
  `email` VARCHAR(254) NOT NULL,
  `password` VARCHAR(254) NOT NULL,
  `date_created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `active` BIT NOT NULL DEFAULT 1,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS `coffee_bd`.`product` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NOT NULL,
  `user_id` BIGINT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_product_user1_idx` (`user_id` ASC) VISIBLE,
  CONSTRAINT `fk_product_user1`
    FOREIGN KEY (`user_id`)
    REFERENCES `coffee_bd`.`user` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS `coffee_bd`.`buy` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `data_buy` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `value_freight` BIGINT NOT NULL,
  `quantity` BIGINT NOT NULL,
  `user_buy` BIGINT NOT NULL,
  `product_id` BIGINT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_buy_user_idx` (`user_buy` ASC) VISIBLE,
  INDEX `fk_buy_product1_idx` (`product_id` ASC) VISIBLE,
  CONSTRAINT `fk_buy_user`
    FOREIGN KEY (`user_buy`)
    REFERENCES `coffee_bd`.`user` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_buy_product1`
    FOREIGN KEY (`product_id`)
    REFERENCES `coffee_bd`.`product` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

