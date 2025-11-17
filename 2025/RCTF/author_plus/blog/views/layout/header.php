<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title><?php echo $pageTitle ?? 'ROIS Blog'; ?></title>
<?php if (isset($pageAuthor)): ?>
    <meta name="author" content=<?php echo $pageAuthor; ?>>
<?php else: ?>
    <meta name="csrf-token" content=<?php echo CsrfProtection::getToken(); ?>>
    <script src="/assets/js/csrf.js"></script>
<?php endif; ?>
    <link rel="stylesheet" href="/assets/css/style.css">

</head>
<body>
    <nav class="navbar">
        <div class="container">
            <div class="nav-brand">
                <a href="/">ROIS Blog</a>
            </div>
            <div class="nav-links">
                <?php if (isset($_SESSION['user_id'])): ?>
                    <a href="/dashboard">My Articles</a>
                    <a href="/articles/create" class="btn-write">Write</a>
                    <span class="nav-user">Hello, <?php echo $_SESSION['username']; ?></span>
                    <a href="/logout">Logout</a>
                <?php else: ?>
                    <a href="/login">Login</a>
                    <a href="/register" class="btn-primary">Register</a>
                <?php endif; ?>
            </div>
        </div>
    </nav>

