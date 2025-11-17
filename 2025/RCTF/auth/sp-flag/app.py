from flask import Flask, request, render_template, session, redirect, url_for, render_template_string
import os

from saml2 import SAMLResponseParser, AuthnRequestBuilder
from config import saml_config

app = Flask(__name__)
app.secret_key = os.urandom(24)

SP_ENTITY_ID = saml_config.sp_entity_id
SP_ACS_URL = saml_config.sp_acs_url
IDP_SSO_URL = saml_config.idp_sso_url
IDP_CERT = saml_config.get_idp_cert()

processed_assertion_ids = set()


@app.route('/')
def index():
    if 'email' in session:
        return redirect(url_for('admin'))
    return render_template('index.html', config=saml_config)


@app.route('/saml/login')
def saml_login():
    relay_state = request.args.get('next', '/')
    
    builder = AuthnRequestBuilder(
        sp_entity_id=SP_ENTITY_ID,
        acs_url=SP_ACS_URL,
        idp_sso_url=IDP_SSO_URL
    )
    
    redirect_url = builder.get_redirect_url(relay_state=relay_state)
    
    return redirect(redirect_url)


@app.route('/saml/acs', methods=['POST'])
def saml_acs():
    try:
        saml_response = request.form.get('SAMLResponse')
        relay_state = request.form.get('RelayState', '/admin')
        
        if not saml_response:
            return render_template('error.html', error='No SAML response received'), 400
        
        parser = SAMLResponseParser(
            saml_response, 
            IDP_CERT,
            validate_time=saml_config.validate_time,
            validate_audience=saml_config.validate_audience,
            expected_audience=SP_ENTITY_ID,
            validate_destination=saml_config.validate_destination,
            expected_destination=SP_ACS_URL,
            time_tolerance=saml_config.time_tolerance
        )
        
        if not parser.is_valid():
            return render_template('error.html', error='SAML response validation failed'), 401
        
        assertion_ids = parser.get_assertion_ids()
        for assertion_id in assertion_ids:
            if assertion_id in processed_assertion_ids:
                return render_template('error.html', error='Replay attack detected'), 401
            processed_assertion_ids.add(assertion_id)
        
        nameid = parser.get_nameid()
        
        if not nameid:
            return render_template('error.html', error='Unable to get user identifier'), 401
        
        session['email'] = nameid
        
        return redirect(url_for('admin'))
        
    except Exception as e:
        return render_template('error.html', error=f'Error processing SAML response: {str(e)}'), 500


@app.route('/logout')
def logout():
    session.clear()
    return redirect(url_for('index'))


@app.route('/admin')
def admin():
    if 'email' not in session:
        return redirect(url_for('saml_login'))
    
    if session.get('email') != 'admin@rois.team':
        return render_template('error.html', error='Insufficient permissions, admin access only'), 403
    
    return render_template_string(os.getenv("FLAG","RCTF{test_flag}"))


if __name__ == '__main__':
    app.run(host='0.0.0.0', port=26000, debug=False)
