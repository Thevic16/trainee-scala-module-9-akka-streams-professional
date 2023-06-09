package com.lightbend.akkassembly

import akka.stream.Materializer
import akka.stream.scaladsl.Sink

import scala.concurrent.Future

class Factory(bodyShop: BodyShop, paintShop: PaintShop, engineShop: EngineShop,
  wheelShop: WheelShop, qualityAssurance: QualityAssurance, upgradeShop: UpgradeShop)(implicit materializer: Materializer){

  def orderCars(quantity: Int): Future[Seq[Car]] = {
    bodyShop.cars.via(paintShop.paint.named("paint-stage")) // 4.00 ms
      .async
      .via(engineShop.installEngine.named("install-engine-stage")) // 6.00 ms + 11.14 ms = 17.14 ms
      .async
      .via(wheelShop.installWheels.named("install-wheels-stage")) // 0 ms
      .via(upgradeShop.installUpgrades.named("install-upgrades-stage")) // 1 ms
      .via(qualityAssurance.inspect.named("inspect-stage"))
      .take(quantity)
      .runWith(Sink.seq)
  }

}
