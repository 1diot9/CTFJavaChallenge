package org.springframework.boot.autoconfigure.amqp;

import org.springframework.amqp.rabbit.connection.RabbitConnectionFactoryBean;
import org.springframework.boot.ssl.SslBundle;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/amqp/SslBundleRabbitConnectionFactoryBean.class */
class SslBundleRabbitConnectionFactoryBean extends RabbitConnectionFactoryBean {
    private SslBundle sslBundle;
    private boolean enableHostnameVerification;

    protected void setUpSSL() {
        if (this.sslBundle != null) {
            this.connectionFactory.useSslProtocol(this.sslBundle.createSslContext());
            if (this.enableHostnameVerification) {
                this.connectionFactory.enableHostnameVerification();
                return;
            }
            return;
        }
        super.setUpSSL();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setSslBundle(SslBundle sslBundle) {
        this.sslBundle = sslBundle;
    }

    public void setEnableHostnameVerification(boolean enable) {
        this.enableHostnameVerification = enable;
        super.setEnableHostnameVerification(enable);
    }
}
