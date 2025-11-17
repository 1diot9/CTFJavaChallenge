<?php
$pageTitle = 'Settings';
$pageCSS = ['pages/settings'];
$pageJS = ['pages/settings'];
$navType = 'settings';
include __DIR__ . '/../layouts/header.php';
$user = Auth::user();
?>

<div class="settings-container">
    
    <div class="settings-wrapper">
        <div class="settings-sidebar">
            <nav class="settings-nav">
                <a href="#profile" class="settings-nav-item active" onclick="showSection('profile')">
                    <i class="fas fa-user"></i>
                    <span>Profile</span>
                </a>
                <a href="#account" class="settings-nav-item" onclick="showSection('account')">
                    <i class="fas fa-lock"></i>
                    <span>Account & Security</span>
                </a>
            </nav>
        </div>
        
        <div class="settings-main">
            <div id="section-profile" class="settings-content-section active">
                <div class="settings-section">
                    <h2>Avatar</h2>
                    <div class="avatar-setting">
                        <img src="<?= e($user['avatar_url']) ?>" alt="Avatar" class="current-avatar" id="currentAvatar">
                        <div>
                            <button class="btn-secondary" onclick="document.getElementById('avatarUpload').click()">
                                <i class="fas fa-upload"></i> Upload New Avatar
                            </button>
                            <input type="file" id="avatarUpload" accept="image/*" style="display: none;">
                            <p class="help-text">Supports JPG, PNG, GIF, WebP, max 10MB</p>
                        </div>
                    </div>
                </div>
                
                <div class="settings-section">
                    <h2>Background</h2>
                    <p class="help-text">Select one of your uploaded photos as your profile background</p>
                    <div style="margin-top: 16px;">
                        <button class="btn-secondary" onclick="openBackgroundSelector()">
                            <i class="fas fa-image"></i> Select Background
                        </button>
                        <?php if (!empty($user['background_photo_id'])): ?>
                            <button class="btn-secondary" onclick="removeBackground()" style="margin-left: 12px;">
                                <i class="fas fa-times"></i> Remove Background
                            </button>
                        <?php endif; ?>
                    </div>
                </div>
                
                <div class="settings-section">
                    <h2>Bio</h2>
                    <form id="bioForm">
                        <input type="hidden" name="csrf_token" value="<?= e(Auth::generateCSRFToken()) ?>">
                        <div class="form-group">
                            <textarea name="bio" rows="4" placeholder="Tell us about yourself..."><?= e($user['bio']) ?></textarea>
                        </div>
                        <button type="submit" class="btn-primary">Save</button>
                    </form>
                </div>
            </div>
            
            <div id="section-account" class="settings-content-section">
                <div class="settings-section">
                    <div class="section-header">
                        <h2>Username <span class="header-subtitle">- Set your username</span></h2>
                    </div>
                    <form id="usernameForm" class="form-inline">
                        <input type="hidden" name="csrf_token" value="<?= e(Auth::generateCSRFToken()) ?>">
                        <div class="form-inline-group">
                            <input type="text" name="username" value="<?= e($user['username']) ?>" placeholder="Enter username" required>
                            <button type="submit" class="btn-save">Save</button>
                        </div>
                    </form>
                </div>
                
                <div class="settings-section">
                    <div class="section-header">
                        <h2>Change Password <span class="header-subtitle">- Regularly changing your password improves account security</span></h2>
                    </div>
                    <form id="passwordForm">
                        <input type="hidden" name="csrf_token" value="<?= e(Auth::generateCSRFToken()) ?>">
                        <div class="form-group">
                            <label for="old_password">Current Password</label>
                            <input type="password" name="old_password" id="old_password" placeholder="Enter current password" required>
                        </div>
                        <div class="form-group">
                            <label for="new_password">New Password</label>
                            <input type="password" name="new_password" id="new_password" placeholder="At least 6 characters" required minlength="6">
                        </div>
                        <button type="submit" class="btn-save">Change Password</button>
                    </form>
                </div>
                
                <div class="settings-section">
                    <h2>Account Information</h2>
                    <div class="info-item">
                        <div class="info-label">Email</div>
                        <div class="info-value"><?= e($user['email']) ?></div>
                    </div>
                    <div class="info-item">
                        <div class="info-label">User Level</div>
                        <div class="info-value">Lv.<?= $user['level'] ?> <?= getUserLevelTitle($user['level']) ?></div>
                    </div>
                </div>
            </div>
            
        </div>
    </div>
</div>

<div id="backgroundModal" class="modal">
    <div class="modal-content modal-large">
        <div class="modal-header">
            <h2>Select Background</h2>
            <button class="modal-close" onclick="closeBackgroundModal()">&times;</button>
        </div>
        <div id="photoGallery" class="photo-gallery-modal"></div>
    </div>
</div>

<script>
const csrfToken = '<?= e(Auth::generateCSRFToken()) ?>';
</script>

<?php include __DIR__ . '/../layouts/footer.php'; ?>
