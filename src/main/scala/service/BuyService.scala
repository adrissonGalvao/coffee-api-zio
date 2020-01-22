//package service
//
//import domain.BuyCommand
//import environment.Environments.BuyEnvironment
//import zio.ZIO
//
//object BuyService {
//  def createBuy(bc: BuyCommand): ZIO[BuyEnvironment, Any, Unit] = ZIO.accessM{ env =>
//    for{
//      //buscar o user existe <*> buscar o o produto exite
//      //vericar se abos existem
//      //criar Buy
//    } yield ???
//  }
//}
