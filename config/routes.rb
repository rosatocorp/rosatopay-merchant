Rails.application.routes.draw do
  get 'payment/index'
  get 'payment/pay'
  # Define other routes as needed
  root 'payment#index'
end
