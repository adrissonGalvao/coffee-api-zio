package domain

case class User(id: Long, fullName: String, email: String, password: String)
case class Product(id: Long, name: String, user: User)
