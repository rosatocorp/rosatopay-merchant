class PaymentController < ApplicationController
  def index
    # Your code for the index page here (if needed)
  end

  def pay
    @form_obj = {
      "appId" => 'YOUR_APP_ID_HERE',
      "orderId" => generate_order_id,
      "orderAmount" => '10.00', # Always pass 2 fixed decimals, like 1.00 or 2.00 or 1.46 and not 1 or 2.
      "orderCurrency" => 'USD',
      "orderNote" => 'Add money to your wallet',
      "customerName" => 'User',
      "customerPhone" => '(607) 836-4935',
      "customerEmail" => 'john@doe.com',
      "returnUrl" => '', # PG returns to this URL after payment, Send Empty if not needed.
      "notifyUrl" => '' # PG sends a webhook to this URL after payment, Send Empty if not needed.
    }

    @form_obj["signature"] = signature_request(@form_obj, 'YOUR_SECRET_KEY_HERE')
  end

  private

  def generate_order_id
    chars = "ABCDEFGHJKLMNOPQRSTUVWXYZ23456789"
    string_length = 6
    random_string = (0...string_length).map { chars[rand(chars.length)] }.join
    'order_' + random_string
  end

  def signature_request(data, secret_key)
    sorted_data = data.sort.to_h
    signature_data = sorted_data.map { |key, value| "#{key}#{value}" }.join
    Digest::SHA256.hexdigest("#{secret_key}#{signature_data}")
  end
end
