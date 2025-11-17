<?php
require_once __DIR__ . '/../app/config/autoload.php';

Auth::init();

$user_types = config('user_types');


if (Auth::check() && Auth::type() < $user_types['admin']) {
    echo getenv('FLAG') ?: 'RCTF{test_flag}';
}else{
    header('Location: /');
}