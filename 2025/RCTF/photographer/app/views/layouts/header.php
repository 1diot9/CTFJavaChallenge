<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><?= $pageTitle ?? 'Photography Sharing Platform' ?></title>
    <link rel="stylesheet" href="/assets/css/base.css">
    <link rel="stylesheet" href="/assets/css/components.css">
    <?php if (isset($pageCSS)): ?>
        <?php foreach ((array)$pageCSS as $css): ?>
            <link rel="stylesheet" href="/assets/css/<?= $css ?>.css">
        <?php endforeach; ?>
    <?php endif; ?>
    <link rel="stylesheet" href="/assets/css/all.min.css">
</head>
<body<?= isset($bodyClass) ? ' class="' . $bodyClass . '"' : '' ?>>

<?php
$navType = $navType ?? 'none';
$isLoggedIn = Auth::check();
$currentUser = $isLoggedIn ? Auth::user() : null;

if ($navType !== 'none'):
    if (!$isLoggedIn && $navType === 'home'):
?>
    <nav class="home-nav">
        <div class="nav-container">
            <div class="nav-logo">
                <i class="fas fa-camera"></i>
                <span>Photographer Platform</span>
            </div>
            <div class="nav-actions">
                <a href="/login" class="btn-login">Login</a>
                <a href="/register" class="btn-register">Register</a>
            </div>
        </div>
    </nav>
<?php
    elseif ($isLoggedIn):
?>
    <nav class="top-nav">
        <div class="nav-content">
            <div class="nav-left">
                <?php if ($navType === 'space'): ?>
                    <h2><i class="fas fa-camera-retro"></i> Photography Works</h2>
                <?php elseif ($navType === 'compose'): ?>
                    <a href="/space" class="nav-link">
                        <i class="fas fa-times"></i>
                        <span>Close</span>
                    </a>
                    <h2>Publish Work</h2>
                <?php elseif ($navType === 'settings'): ?>
                    <a href="/space" class="nav-link">
                        <i class="fas fa-arrow-left"></i>
                        <span>Back</span>
                    </a>
                    <h2>Settings</h2>
                <?php elseif ($navType === 'post'): ?>
                    <a href="/space" class="nav-link">
                        <i class="fas fa-arrow-left"></i>
                        <span>Back</span>
                    </a>
                <?php endif; ?>
            </div>
            <div class="nav-right">
                <?php if ($navType === 'compose'): ?>
                    <button id="publishBtn" class="btn-publish" disabled>
                        Publish
                    </button>
                <?php elseif ($navType === 'post' && isset($isOwner) && $isOwner): ?>
                    <button class="nav-link btn-delete-post" onclick="deleteCurrentPost()" style="background: none; border: none; cursor: pointer; margin-right: 12px;">
                        <i class="fas fa-trash"></i>
                        <span>Delete</span>
                    </button>
                <?php endif; ?>
                <div class="user-menu">
                    <img src="<?= e($currentUser['avatar_url']) ?>" alt="Avatar" class="nav-avatar" onclick="toggleUserMenu(event)">
                    <div class="user-dropdown" id="userDropdown">
                        <?php if ($navType !== 'space'): ?>
                            <a href="/space" class="dropdown-item">
                                <i class="fas fa-home"></i>
                                <span>My Space</span>
                            </a>
                        <?php endif; ?>
                        <?php if ($navType !== 'settings'): ?>
                            <a href="/settings" class="dropdown-item">
                                <i class="fas fa-cog"></i>
                                <span>Settings</span>
                            </a>
                        <?php endif; ?>
                        <a href="/logout" class="dropdown-item">
                            <i class="fas fa-sign-out-alt"></i>
                            <span>Logout</span>
                        </a>
                    </div>
                </div>
            </div>
        </div>
    </nav>
<?php
    endif;
endif;
?>

