(function() {
    'use strict';
    
    const typeSelect = document.getElementById('type');
    const invitationCodeGroup = document.getElementById('invitationCodeGroup');
    const invitationCodeInput = document.getElementById('invitationCode');

    function toggleInvitationCode() {
        if (typeSelect.value === '0') {
            invitationCodeGroup.style.display = 'block';
            invitationCodeInput.required = true;
        } else {
            invitationCodeGroup.style.display = 'none';
            invitationCodeInput.required = false;
            invitationCodeInput.value = '';
        }
    }

    if (typeSelect && invitationCodeGroup && invitationCodeInput) {
        typeSelect.addEventListener('change', toggleInvitationCode);
        toggleInvitationCode();
    }
})();

