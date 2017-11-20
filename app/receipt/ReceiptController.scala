package receipt

class ReceiptController /*@Inject()(components: ControllerComponents)(implicit ec: ExecutionContext)
  extends AbstractController(components)*/ {

  /*val clientService = new ClientReceiptService
  val receiptService = new ReceiptService
  val TMP_RECEIPT_DIR = "tmp/receipts/"

  (new File(TMP_RECEIPT_DIR)).mkdir()*/

  /*def browse = Action { implicit request =>
    Ok(views.html.browse())
  }

  def upload = Action(parse.multipartFormData) { request =>
    request.body.file("img").map { picture =>


      val filename = picture.filename
      val contentType = picture.contentType
      //val tmpFile = new File(s"$TMP_RECEIPT_DIR$filename")
      //picture.ref.moveTo(tmpFile)

      val result = receiptService.processReceipt(picture.ref.file)
      Ok(clientService.toJson( result ))
    }.getOrElse {
      Redirect("/").flashing(
        "error" -> "Missing file")
    }
  }*/
}
