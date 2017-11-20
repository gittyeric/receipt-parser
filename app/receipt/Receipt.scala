package receipt

case class LineItem(val quantity: Int, val name: String, val price: Double) {
  override def hashCode(): Int = name.hashCode + quantity * 97 + price.toInt * 87

  override def equals(obj: scala.Any): Boolean = Objects.equals(name, if (obj.isInstanceOf[LineItem]) (obj.asInstanceOf[LineItem]).name else null)
}

case class Receipt(val date: String, val uploadDate: Long, val total: Double, val store: String, val rawText: String, val items: List[LineItem]) {

}
