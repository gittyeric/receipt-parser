# Receipt Parser

This is a minimalistic REST API for uploading a receipt and getting information back using Scala/Play and Tesseract 4.  You must have Tesseract 4 installed on the system you run the Play API on.

You should also consider training your Tesseract engine against receipt or typewriter fonts for best results.  I made training files for this and will post it if you force me to find them.

### Running

You need to download and install sbt for this application to run.

Once you have sbt installed, the following at the command prompt will start up Play in development mode:

```
sbt run
```

Play will start up on the HTTP port at http://localhost:9000/.   You don't need to reploy or reload anything -- changing any source code while the server is running will automatically recompile and hot-reload the application on the next HTTP request. 

### Usage

Check out ReceiptController.scala and post your image as multi-part form POST with parameter name "img".

```
curl -X POST http://localhost:9000/receipt/upload
```

