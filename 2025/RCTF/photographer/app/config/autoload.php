<?php

date_default_timezone_set('Asia/Shanghai');

require_once __DIR__ . '/../../framework/helpers.php';
require_once __DIR__ . '/../../framework/DB.php';
require_once __DIR__ . '/../../framework/Snowflake.php';
require_once __DIR__ . '/../../framework/Router.php';

require_once __DIR__ . '/../middlewares/Auth.php';

require_once __DIR__ . '/../models/User.php';
require_once __DIR__ . '/../models/Post.php';
require_once __DIR__ . '/../models/Photo.php';

require_once __DIR__ . '/../controllers/HomeController.php';
require_once __DIR__ . '/../controllers/AuthController.php';
require_once __DIR__ . '/../controllers/UserController.php';
require_once __DIR__ . '/../controllers/PostController.php';
require_once __DIR__ . '/../controllers/PhotoController.php';
