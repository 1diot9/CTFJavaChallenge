package org.springframework.web.util;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/util/HtmlCharacterEntityDecoder.class */
class HtmlCharacterEntityDecoder {
    private static final int MAX_REFERENCE_SIZE = 10;
    private final HtmlCharacterEntityReferences characterEntityReferences;
    private final String originalMessage;
    private final StringBuilder decodedMessage;
    private int currentPosition = 0;
    private int nextPotentialReferencePosition = -1;
    private int nextSemicolonPosition = -2;

    public HtmlCharacterEntityDecoder(HtmlCharacterEntityReferences characterEntityReferences, String original) {
        this.characterEntityReferences = characterEntityReferences;
        this.originalMessage = original;
        this.decodedMessage = new StringBuilder(original.length());
    }

    public String decode() {
        while (this.currentPosition < this.originalMessage.length()) {
            findNextPotentialReference(this.currentPosition);
            copyCharactersTillPotentialReference();
            processPossibleReference();
        }
        return this.decodedMessage.toString();
    }

    private void findNextPotentialReference(int startPosition) {
        this.nextPotentialReferencePosition = Math.max(startPosition, this.nextSemicolonPosition - 10);
        do {
            this.nextPotentialReferencePosition = this.originalMessage.indexOf(38, this.nextPotentialReferencePosition);
            if (this.nextSemicolonPosition != -1 && this.nextSemicolonPosition < this.nextPotentialReferencePosition) {
                this.nextSemicolonPosition = this.originalMessage.indexOf(59, this.nextPotentialReferencePosition + 1);
            }
            if (this.nextPotentialReferencePosition != -1) {
                if (this.nextSemicolonPosition == -1) {
                    this.nextPotentialReferencePosition = -1;
                    return;
                } else if (this.nextSemicolonPosition - this.nextPotentialReferencePosition >= 10) {
                    this.nextPotentialReferencePosition++;
                } else {
                    return;
                }
            } else {
                return;
            }
        } while (this.nextPotentialReferencePosition != -1);
    }

    private void copyCharactersTillPotentialReference() {
        if (this.nextPotentialReferencePosition != this.currentPosition) {
            int skipUntilIndex = this.nextPotentialReferencePosition != -1 ? this.nextPotentialReferencePosition : this.originalMessage.length();
            if (skipUntilIndex - this.currentPosition > 3) {
                this.decodedMessage.append((CharSequence) this.originalMessage, this.currentPosition, skipUntilIndex);
                this.currentPosition = skipUntilIndex;
                return;
            }
            while (this.currentPosition < skipUntilIndex) {
                StringBuilder sb = this.decodedMessage;
                String str = this.originalMessage;
                int i = this.currentPosition;
                this.currentPosition = i + 1;
                sb.append(str.charAt(i));
            }
        }
    }

    private void processPossibleReference() {
        if (this.nextPotentialReferencePosition != -1) {
            boolean isNumberedReference = this.originalMessage.charAt(this.currentPosition + 1) == '#';
            boolean wasProcessable = isNumberedReference ? processNumberedReference() : processNamedReference();
            if (wasProcessable) {
                this.currentPosition = this.nextSemicolonPosition + 1;
                return;
            }
            char currentChar = this.originalMessage.charAt(this.currentPosition);
            this.decodedMessage.append(currentChar);
            this.currentPosition++;
        }
    }

    private boolean processNumberedReference() {
        int parseInt;
        char referenceChar = this.originalMessage.charAt(this.nextPotentialReferencePosition + 2);
        boolean isHexNumberedReference = referenceChar == 'x' || referenceChar == 'X';
        try {
            if (!isHexNumberedReference) {
                parseInt = Integer.parseInt(getReferenceSubstring(2));
            } else {
                parseInt = Integer.parseInt(getReferenceSubstring(3), 16);
            }
            int value = parseInt;
            this.decodedMessage.append((char) value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean processNamedReference() {
        String referenceName = getReferenceSubstring(1);
        char mappedCharacter = this.characterEntityReferences.convertToCharacter(referenceName);
        if (mappedCharacter != 65535) {
            this.decodedMessage.append(mappedCharacter);
            return true;
        }
        return false;
    }

    private String getReferenceSubstring(int referenceOffset) {
        return this.originalMessage.substring(this.nextPotentialReferencePosition + referenceOffset, this.nextSemicolonPosition);
    }
}
