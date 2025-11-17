<?php
return [
    'db' => [
        'path' => __DIR__ . '/../../database/photographer.db'
    ],
    'app' => [
        'name' => 'Photography Sharing Platform'
    ],
    'upload' => [
        'path' => __DIR__ . '/../../public/uploads/',
        'max_size' => 1 * 1024 * 1024,
        'allowed_extensions' => ['jpg', 'jpeg', 'png', 'gif', 'webp'],
    ],
    'session' => [
        'name' => 'PHOTOGRAPHER_SESSION'
    ],
    'user_types' => [
        'admin' => 0,
        'auditor' => 1,
        'user' => 2
    ],
    'user_levels' => [
        1 => 'Novice Photographer',
        2 => 'Amateur Photographer',
        3 => 'Photography Enthusiast',
        4 => 'Advanced Photographer',
        5 => 'Professional Photographer',
        6 => 'Master Photographer',
        7 => 'Grandmaster Photographer',
        8 => 'Legendary Photographer',
        9 => 'Photography Deity',
        10 => 'Photography Supreme'
    ],
    'default_value' => [
        'user' => [
            'avatar_url' => '/assets/img/default-avatar.png',
            'type' => 2,
            'level' => 1
        ]
    ]
];

