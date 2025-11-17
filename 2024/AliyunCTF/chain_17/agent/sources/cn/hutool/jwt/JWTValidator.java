package cn.hutool.jwt;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.exceptions.ValidateException;
import cn.hutool.core.util.StrUtil;
import cn.hutool.jwt.signers.JWTSigner;
import cn.hutool.jwt.signers.NoneJWTSigner;
import java.util.Date;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/jwt/JWTValidator.class */
public class JWTValidator {
    private final JWT jwt;

    public static JWTValidator of(String token) {
        return new JWTValidator(JWT.of(token));
    }

    public static JWTValidator of(JWT jwt) {
        return new JWTValidator(jwt);
    }

    public JWTValidator(JWT jwt) {
        this.jwt = jwt;
    }

    public JWTValidator validateAlgorithm() throws ValidateException {
        return validateAlgorithm(null);
    }

    public JWTValidator validateAlgorithm(JWTSigner signer) throws ValidateException {
        validateAlgorithm(this.jwt, signer);
        return this;
    }

    public JWTValidator validateDate() throws ValidateException {
        return validateDate(DateUtil.beginOfSecond(DateUtil.date()));
    }

    public JWTValidator validateDate(Date dateToCheck) throws ValidateException {
        validateDate(this.jwt.getPayload(), dateToCheck, 0L);
        return this;
    }

    public JWTValidator validateDate(Date dateToCheck, long leeway) throws ValidateException {
        validateDate(this.jwt.getPayload(), dateToCheck, leeway);
        return this;
    }

    private static void validateAlgorithm(JWT jwt, JWTSigner signer) throws ValidateException {
        String algorithmId = jwt.getAlgorithm();
        if (null == signer) {
            signer = jwt.getSigner();
        }
        if (StrUtil.isEmpty(algorithmId)) {
            if (null == signer || (signer instanceof NoneJWTSigner)) {
            } else {
                throw new ValidateException("No algorithm defined in header!");
            }
        } else {
            if (null == signer) {
                throw new IllegalArgumentException("No Signer for validate algorithm!");
            }
            String algorithmIdInSigner = signer.getAlgorithmId();
            if (false == StrUtil.equals(algorithmId, algorithmIdInSigner)) {
                throw new ValidateException("Algorithm [{}] defined in header doesn't match to [{}]!", algorithmId, algorithmIdInSigner);
            }
            if (false == jwt.verify(signer)) {
                throw new ValidateException("Signature verification failed!");
            }
        }
    }

    private static void validateDate(JWTPayload payload, Date now, long leeway) throws ValidateException {
        if (null == now) {
            now = DateUtil.date();
            now.setTime((now.getTime() / 1000) * 1000);
        }
        Date notBefore = payload.getClaimsJson().getDate(RegisteredPayload.NOT_BEFORE);
        validateNotAfter(RegisteredPayload.NOT_BEFORE, notBefore, now, leeway);
        Date expiresAt = payload.getClaimsJson().getDate(RegisteredPayload.EXPIRES_AT);
        validateNotBefore(RegisteredPayload.EXPIRES_AT, expiresAt, now, leeway);
        Date issueAt = payload.getClaimsJson().getDate(RegisteredPayload.ISSUED_AT);
        validateNotAfter(RegisteredPayload.ISSUED_AT, issueAt, now, leeway);
    }

    private static void validateNotAfter(String fieldName, Date dateToCheck, Date now, long leeway) throws ValidateException {
        if (null == dateToCheck) {
            return;
        }
        if (leeway > 0) {
            now = DateUtil.date(now.getTime() + (leeway * 1000));
        }
        if (dateToCheck.after(now)) {
            throw new ValidateException("'{}':[{}] is after now:[{}]", fieldName, DateUtil.date(dateToCheck), DateUtil.date(now));
        }
    }

    private static void validateNotBefore(String fieldName, Date dateToCheck, Date now, long leeway) throws ValidateException {
        if (null == dateToCheck) {
            return;
        }
        if (leeway > 0) {
            now = DateUtil.date(now.getTime() - (leeway * 1000));
        }
        if (dateToCheck.before(now)) {
            throw new ValidateException("'{}':[{}] is before now:[{}]", fieldName, DateUtil.date(dateToCheck), DateUtil.date(now));
        }
    }
}
