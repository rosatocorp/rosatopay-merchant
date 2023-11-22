<?php
use Slim\Factory\AppFactory;
use Psr\Http\Message\ResponseInterface as Response;
use Psr\Http\Message\ServerRequestInterface as Request;

require __DIR__ . '/vendor/autoload.php';

$APP_ID = 'YOUR_APP_ID_HERE';
$SECRET_KEY = 'YOUR_SECRET_KEY_HERE';

function generateOrderId() {
    $chars = "ABCDEFGHJKLMNOPQRSTUVWXYZ23456789";
    $randomstring = '';
    $string_length = 6;
    for ($i = 0; $i < $string_length; $i++) {
        $rnum = rand(0, strlen($chars) - 1);
        $randomstring .= $chars[$rnum];
    }
    return 'order_' . $randomstring;
}

function signatureRequest($data, $secretKey) {
    ksort($data);
    $signatureData = "";
    foreach ($data as $key => $value) {
        $signatureData .= $key . $value;
    }
    return base64_encode(hash_hmac('sha256', $signatureData, $secretKey, true));
}

$app = AppFactory::create();

// Set up view rendering using Twig
$loader = new \Twig\Loader\FilesystemLoader(__DIR__ . '/templates');
$twig = new \Twig\Environment($loader, [
    'cache' => false, // Set to 'path/to/cache' for production
]);

// Define route for the home page
$app->get('/', function (Request $request, Response $response, $args) use ($twig) {
    $content = $twig->render('index.twig');
    $response->getBody()->write($content);
    return $response;
});

// Define route for payment page
$app->get('/pay', function (Request $request, Response $response, $args) use ($APP_ID, $SECRET_KEY, $twig) {
    $formObj = [
        "appId" => $APP_ID,
        "orderId" => generateOrderId(),
        "orderAmount" => '10',
        "orderCurrency" => 'USD',
        "orderNote" => 'Add money to your wallet',
        "customerName" => 'User',
        "customerPhone" => '(607) 836-4935',
        "customerEmail" => 'john@doe.com',
        "returnUrl" => '', // PG returns to this URL after payment, Send Empty if not needed.
        "notifyUrl" => '' // PG sends a webhook to this URL after payment, Send Empty if not needed.
    ];

    $formObj["signature"] = signatureRequest($formObj, $SECRET_KEY);

    $content = $twig->render('pay.twig', ["form" => $formObj]);
    $response->getBody()->write($content);
    return $response;
});

// Run the Slim app
$app->run();
