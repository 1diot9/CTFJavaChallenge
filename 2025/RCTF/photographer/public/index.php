<?php
require_once __DIR__ . '/../app/config/autoload.php';

Auth::init();

$router = new Router();

$routeLoader = require __DIR__ . '/../app/config/router.php';
$routeLoader($router);

$router->dispatch();
