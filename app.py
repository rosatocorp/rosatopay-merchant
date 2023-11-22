from flask import Flask, render_template
import random
import hashlib
import string

app = Flask(__name__)

APP_ID = 'YOUR_APP_ID_HERE'
SECRET_KEY = 'YOUR_SECRET_KEY_HERE'


def generate_order_id():
    chars = string.ascii_uppercase + '23456789'
    random_string = ''.join(random.choice(chars) for _ in range(6))
    return 'order_' + random_string


def signature_request(data, secret_key):
    sorted_data = sorted(data.items())
    signature_data = ''.join([f"{key}{value}" for key, value in sorted_data])
    return hashlib.sha256(signature_data.encode('utf-8')).hexdigest()


@app.route('/')
def index():
    return render_template('index.html')


@app.route('/pay')
def pay():
    form_obj = {
        "appId": APP_ID,
        "orderId": generate_order_id(),
        "orderAmount": '10',
        "orderCurrency": 'USD',
        "orderNote": 'Add money to your wallet',
        "customerName": 'User',
        "customerPhone": '(607) 836-4935',
        "customerEmail": 'john@doe.com',
        "returnUrl": '',  # PG returns to this URL after payment, Send Empty if not needed.
        "notifyUrl": ''  # PG sends a webhook to this URL after payment, Send Empty if not needed.
    }

    form_obj["signature"] = signature_request(form_obj, SECRET_KEY)

    return render_template('pay.html', form=form_obj)


if __name__ == '__main__':
    app.run(host='localhost', port=3399)