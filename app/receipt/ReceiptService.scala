package receipt

class ReceiptService {

  def processReceipt(receipt: File): Receipt = {
    val tess = new Tesseract()
    tess.setLanguage("eng+Merchant")
    tess.setDatapath("C:\\Program Files (x86)\\Tesseract-OCR\\tessdata")
    ImageIO.scanForPlugins()
    val rawText = tess.doOCR(receipt)
    val sanitized = safeSanitize(rawText)

    parseReceipt(sanitized)
  }

  def parseReceipt(rawText: String): Receipt = {
    val lineItemsTextPos = Array(0, rawText.length)

    val store = rawText.substring(0, Math.max(rawText.indexOf("\n"), 0))

    val date = parsePatterns(rawText,
      """(\d{1,2}\s?/\s?\d{1,2}\s?/\s?\d{2,4})""".r,
      """(\d{1,2}\s?-\s?\d{1,2}\s?-\s?\d{2,4})""".r)
      .replaceAll("-", "/")
      .replaceAll(" ", "")

    //val PHONE_REGEX = """[\n\s]1?[\s.-]?(\(?\d{3}\)?[\s.-]?\d{3}[\s.-]?\d{4})[\n\s]""".r
    //val phone = parsePatterns(rawText, PHONE_REGEX).trim

    val ADDRESS_REGEX = """(?i)\n(.*\s(blvd|st|street|ave|center|circle|court|drive|hwy|route).{0,50},\s*[A-Z][a-zA-Z]\s+\d{5})""".r
    val address = parsePatterns(rawText, ADDRESS_REGEX)

    val TOTAL_REGEX = """(?i)[\n\s]tota[l1]:?\s+\$?\s?(\d{0,6}[\.,\s]\d{2})""".r
    val totalStr = parsePatterns(rawText, TOTAL_REGEX)
      .replaceAll("""[,\s]""", ".")

    var total = 0.0
    if (!totalStr.isEmpty) {
      total = totalStr.toDouble
      lineItemsTextPos(1) = getFirstPatternPos(rawText, TOTAL_REGEX) - 3

      if (!address.isEmpty) {
        val addressPos = getFirstPatternPos(rawText, ADDRESS_REGEX)
        if (addressPos < lineItemsTextPos(1)) {
          lineItemsTextPos(0) = addressPos
        }
      }
    }

    val lineItems = parseLineItems(rawText.substring(lineItemsTextPos(0), lineItemsTextPos(1)))
    return new Receipt(date, System.currentTimeMillis(), total, store, rawText, lineItems)
  }

  private def safeSanitize(rawText: String): String = {
    return rawText
      .replaceAll("-+", "-")
      .replaceAll("=+", "=")
      .replaceAll("""\*+""", "*")
      .replaceAll("""\.+""", ".")
  }

  private def parseLineItems(rawText: String): List[LineItem] = {
    var items = parseEasyLines(rawText)

    if (items.isEmpty) {
      items = parseNoQuantityLines(rawText)
    }
    items
  }

  private def parseEasyLines(rawText: String): List[LineItem] = {
    val maxLineChars = rawText.split("\n").foldLeft(0)((max: Int, str2: String) => Math.max(max, str2.length))
    val lineReg = ("""\n\s*(\d{1,4})\s+(.{1,""" + maxLineChars + """})\s+\$?(\d{0,6}[\.,\s]\d{2})\s?[AF-]?\s*\n""").r
    val matches = lineReg.findAllMatchIn(rawText)
    matches.map(m =>
      //TODO: Add look-ahead to extend item name for multi-line cases
      LineItem(m.group(1).toInt, m.group(2).toUpperCase, m.group(3).replaceAll("""[,\s]""", ".").toDouble)
    ).toList ++ parseWeightedLines(rawText)
  }

  private def parseWeightedLines(rawText: String): List[LineItem] = {
    val lineReg = """(?i)\n\s*(WT)\s+([^\n]{1,200})\s+\$?(\d{0,6}[\.,\s]\d{2})\s?[AF-]?\s*\n""".r
    val matches = lineReg.findAllMatchIn(rawText)
    matches.map(m =>
      //TODO: Add look-ahead to extend item name for multi-line cases
      LineItem(0, m.group(2).toUpperCase, m.group(3).replaceAll("""[,\s]""", ".").toDouble)
    ).toList
  }

  private def parseNoQuantityLines(rawText: String): List[LineItem] = {
    val maxLineChars = rawText.split("\n").foldLeft(0)((max: Int, str2: String) => Math.max(max, str2.length))

    val lineReg = ("""\n\s*([^\n]{1,""" + maxLineChars + """})\s+\$?(\d{0,6}[\.,\s]\d{2})\s?[AF-]?\s*\n""").r
    val matches = lineReg.findAllMatchIn(rawText)
    val noQuantity = matches.map(m =>
      LineItem(0,
        m.group(1).toUpperCase
          .replaceAll("""\n""", " ")
          .replaceAll("""\s+""", " "),
        m.group(2)
          .replaceAll("""[,\s]""", ".").toDouble)
    ).filter(item =>
      !item.name.matches("""(TOTAL|CASH|CHANGE|SAVED\s|CARD\sSAV|.*\sTAX|BALANCE|BAL):?\s?$""") && //Exclude totals n stuff
        !item.name.matches("""(?i)\n\s*(WT)\s+([^\n]{1,200})""")).toList //Exclude weighted products

    return noQuantity ++ parseWeightedLines(rawText)
  }

  private def parsePatterns(rawText: String, regexes: Regex*): String = {
    regexes.foreach(reg => {
      val found = reg.findFirstMatchIn(rawText)
      if (found.isDefined) {
        return found.get.group(1)
      }
    })

    ""
  }

  private def getFirstPatternPos(rawText: String, regexes: Regex*): Int = {
    regexes.foreach(reg => {
      val found = reg.findFirstMatchIn(rawText)
      if (found.isDefined) {
        return found.get.end
      }
    })

    0
  }

}
