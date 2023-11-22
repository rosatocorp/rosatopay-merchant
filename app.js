var express = require('express');
var crypto = require('crypto');
var app = express();
var APP_ID = 'YOUR_APP_ID_HERE';
var SECRET_KEY = 'YOUR_SECRET_KEY_HERE';

function generateOrderId() {
   let chars = "ABCDEFGHJKLMNOPQRSTUVWXYZ23456789";
    let string_length = 6;
    let randomstring = '';
    for (let i=0; i<string_length; i++) {
        let rnum = Math.floor(Math.random() * chars.length);
        randomstring += chars.substring(rnum,rnum+1);
    }
    return 'order_' + randomstring;
}

function signatureRequest( data, secretKey ) {
  var keys = Object.keys(data);
  keys.sort();
  signatureData = "";
  keys.forEach((key)=>{
      signatureData += key+data[key];
  });

  return crypto.createHmac('sha256',secretKey).update(signatureData).digest('base64');
}

// set the view engine to ejs
app.set('view engine', 'ejs');

// use res.render to load up an ejs view file

// index page
app.get('/', function(req, res) {
  res.render('pages/index');
});

// about page
app.get('/pay', function(req, res) {
  let formObj = {
    "appId": APP_ID,
    "orderId": generateOrderId(),
    "orderAmount": '10',
    "orderCurrency": 'USD',
    "orderNote": 'Add money to your wallet',
    "customerName": 'User',
    "customerPhone": '(607) 836-4935',
    "customerEmail": 'john@doe.com',
    // "returnUrl": '', //Optional, PG returns to this URL after payment.
    // "notifyUrl": '' //Optional, PG sends a webhook to this URL after payment.
  };

  formObj.signature = signatureRequest( formObj, SECRET_KEY );
  res.render('pages/pay', {
    form: formObj
  });
});

app.listen(3399);
console.log('Server is running on http://localhost:3399');
