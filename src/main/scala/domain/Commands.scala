package domain

case class UserCommand(fullName: String, email: String, password: String)
case class ProductCommand(name: String, userId: Long)
