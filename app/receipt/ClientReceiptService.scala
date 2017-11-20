package receipt

class ClientReceiptService(implicit ec: ExecutionContext) {

  def toJson(receipt: Receipt): JsValue = {
    implicit val lineItemFormatter = Json.format[LineItem]
    implicit val receiptFormatter = Json.format[Receipt]
    return Json.toJson(receipt)
  }

  def toJson(result: Future[Receipt]): Future[JsValue] = {
    result.map(toJson(_))
  }

}
