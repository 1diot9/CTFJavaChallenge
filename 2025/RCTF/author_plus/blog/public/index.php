<?php
ini_set('session.cookie_httponly', 1);
ini_set('session.use_only_cookies', 1);
ini_set('session.cookie_samesite', 'Strict');
session_start();

require_once __DIR__ . '/../config/database.php';
require_once __DIR__ . '/../app/Snowflake.php';
require_once __DIR__ . '/../app/Sanitizer.php';
require_once __DIR__ . '/../app/CsrfProtection.php';
require_once __DIR__ . '/../app/Router.php';
require_once __DIR__ . '/../app/controllers/HomeController.php';
require_once __DIR__ . '/../app/controllers/AuthController.php';
require_once __DIR__ . '/../app/controllers/ArticleController.php';

$router = new Router();

// Home routes
$router->add('GET', '/', 'HomeController@index');
$router->add('GET', '/article/{id}', 'HomeController@show');

// Authentication routes
$router->add('GET', '/login', 'AuthController@showLogin');
$router->add('POST', '/login', 'AuthController@login');
$router->add('GET', '/register', 'AuthController@showRegister');
$router->add('POST', '/register', 'AuthController@register');
$router->add('GET', '/logout', 'AuthController@logout');

// Article management routes
$router->add('GET', '/dashboard', 'ArticleController@dashboard');
$router->add('GET', '/articles/create', 'ArticleController@create');
$router->add('POST', '/articles/store', 'ArticleController@store');
$router->add('GET', '/articles/edit/{id}', 'ArticleController@edit');
$router->add('POST', '/articles/update/{id}', 'ArticleController@update');
$router->add('POST', '/articles/delete/{id}', 'ArticleController@delete');

// Article audit routes (admin only)
$router->add('GET', '/article/{id}/audit', 'ArticleController@audit');
$router->add('POST', '/articles/approve/{id}', 'ArticleController@approve');
$router->add('POST', '/articles/reject/{id}', 'ArticleController@reject');

// API routes - async loading
$router->add('GET', '/api/articles', 'ArticleController@apiList');
$router->add('GET', '/api/articles/{id}', 'ArticleController@apiShow');

// Dispatch routes
$router->dispatch();

