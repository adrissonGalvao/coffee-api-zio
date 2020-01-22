package domain

case class UserCommand(fullName: String, email: String, password: String)
case class ProductCommand(name: String, userId: Long)
case class BuyCommand(valueFreight: Long, quantity: Long, userBuy: Long, productId: Long)
