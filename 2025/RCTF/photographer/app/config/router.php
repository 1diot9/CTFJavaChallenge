<?php

return function(Router $router) {
    
    $router->get('/', 'HomeController@index');
    
    $router->get('/login', 'AuthController@showLogin');
    $router->post('/api/login', 'AuthController@login');
    $router->get('/register', 'AuthController@showRegister');
    $router->post('/api/register', 'AuthController@register');
    $router->get('/logout', 'AuthController@logout');
    
    $router->get('/space', 'UserController@space');
    $router->get('/space/posts', 'UserController@spacePosts');
    $router->get('/space/photos', 'UserController@spacePhotos');
    
    $router->get('/settings', 'UserController@showSettings');
    $router->post('/api/user/username', 'UserController@updateUsername');
    $router->post('/api/user/password', 'UserController@updatePassword');
    $router->post('/api/user/bio', 'UserController@updateBio');
    $router->post('/api/user/avatar', 'UserController@updateAvatar');
    
    $router->get('/api/user/photos', 'UserController@getPhotos');
    $router->post('/api/user/background', 'UserController@setBackground');
    
    $router->get('/compose', 'PostController@compose');
    $router->get('/post/{id}', 'PostController@show');
    $router->post('/api/posts/create', 'PostController@create');
    $router->post('/api/posts/delete', 'PostController@delete');
    
    $router->post('/api/photos/upload', 'PhotoController@upload');
    $router->get('/api/photos/{id}/info', 'PhotoController@info');
    $router->post('/api/photos/delete', 'PhotoController@delete');
    
    $router->notFound(function() {
        http_response_code(404);
        echo '404 - Page Not Found';
    });
};
